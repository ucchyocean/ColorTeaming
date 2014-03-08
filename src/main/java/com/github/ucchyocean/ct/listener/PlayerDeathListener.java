/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.listener;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.ColorTeamingAPI;
import com.github.ucchyocean.ct.Utility;
import com.github.ucchyocean.ct.config.ClassData;
import com.github.ucchyocean.ct.config.ColorTeamingConfig;
import com.github.ucchyocean.ct.config.TeamNameSetting;
import com.github.ucchyocean.ct.event.ColorTeamingLeaderDefeatedEvent;
import com.github.ucchyocean.ct.event.ColorTeamingPlayerLeaveEvent.Reason;
import com.github.ucchyocean.ct.event.ColorTeamingTeamDefeatedEvent;
import com.github.ucchyocean.ct.event.ColorTeamingTrophyKillEvent;
import com.github.ucchyocean.ct.event.ColorTeamingTrophyKillReachEvent;
import com.github.ucchyocean.ct.event.ColorTeamingWonLeaderEvent;
import com.github.ucchyocean.ct.event.ColorTeamingWonTeamEvent;

/**
 * プレイヤーが死亡したときに、通知を受け取って処理するクラス
 * @author ucchy
 */
public class PlayerDeathListener implements Listener {

    private static final String PRENOTICE = ChatColor.LIGHT_PURPLE.toString();

    private ColorTeaming plugin;

    public PlayerDeathListener(ColorTeaming plugin) {
        this.plugin = plugin;
    }

    /**
     * Playerが死亡したときに発生するイベント
     * @param event
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        // 倒された人を取得
        Player deader = event.getEntity();
        ColorTeamingConfig config = plugin.getCTConfig();
        ColorTeamingAPI api = plugin.getAPI();
        
        HashMap<String, int[]> killDeathUserCounts = api.getKillDeathUserCounts();
        HashMap<String, ArrayList<String>> leaders = api.getLeaders();
        TeamNameSetting tnsDeader = api.getPlayerTeamName(deader);
        
        if ( tnsDeader != null ) {
            String teamDeader = tnsDeader.getID();

            // 倒したプレイヤーを取得
            Player killer = deader.getKiller();
            String killerName = null;
            if ( killer != null ) {
                killerName = killer.getName();
            }

            // Death数を加算

            // チームへ加算
            api.addTeamPoint(teamDeader, getDeathPoint(deader));
            
            // ユーザーへ加算
            if ( !killDeathUserCounts.containsKey(deader.getName()) ) {
                killDeathUserCounts.put(deader.getName(), new int[3]);
            }
            killDeathUserCounts.get(deader.getName())[1]++;

            // 死亡したプレイヤーが、大将だった場合、倒されたことを全体に通知する。
            if ( leaders.containsKey(teamDeader) &&
                    leaders.get(teamDeader).contains(deader.getName()) ) {
                String message = String.format(PRENOTICE + "%s チームの大将、%s が倒されました！",
                        tnsDeader.getName(), deader.getName());
                Bukkit.broadcastMessage(message);
                leaders.get(teamDeader).remove(deader.getName());

                if ( leaders.get(teamDeader).size() >= 1 ) {
                    message = String.format(PRENOTICE + "%s チームの残り大将は、あと %d 人です。",
                            tnsDeader.getName(), leaders.get(teamDeader).size());
                    Bukkit.broadcastMessage(message);
                } else {

                    message = String.format(PRENOTICE + "%s チームの大将は全滅しました！", 
                            tnsDeader.getName());
                    Bukkit.broadcastMessage(message);
                    leaders.remove(teamDeader);

                    // チームリーダー全滅イベントのコール
                    ColorTeamingLeaderDefeatedEvent event2 =
                            new ColorTeamingLeaderDefeatedEvent(tnsDeader, killerName, deader.getName());
                    Bukkit.getServer().getPluginManager().callEvent(event2);

                    // リーダーが残っているチームがあと1チームなら、勝利イベントを更にコール
                    if ( leaders.size() == 1 ) {
                        TeamNameSetting wonTeam = null;
                        for ( String t : leaders.keySet() ) {
                            wonTeam = api.getTeamNameFromID(t);
                        }
                        ColorTeamingWonLeaderEvent event3 =
                                new ColorTeamingWonLeaderEvent(wonTeam, event2);
                        Bukkit.getServer().getPluginManager().callEvent(event3);
                    }
                }
            }

            // 倒したプレイヤー側の処理
            TeamNameSetting tnsKiller = null;
            String teamKiller = null;
            if ( killer != null ) {
                tnsKiller = api.getPlayerTeamName(killer);
                teamKiller = tnsKiller.getID();
            }
            
            if ( tnsKiller != null ) {

                // Kill数を加算

                // チームへ加算
                if ( teamDeader.equals(teamKiller) ) // 同じチームだった場合のペナルティ
                    api.addTeamPoint(teamKiller, config.getCTTKPoint());
                else
                    api.addTeamPoint(teamKiller, getKillPoint(deader));
                // ユーザーへ加算
                if ( !killDeathUserCounts.containsKey(killer.getName()) ) {
                    killDeathUserCounts.put(killer.getName(), new int[3]);
                }
                if ( teamDeader.equals(teamKiller) ) // 同じチームだった場合のペナルティ
                    killDeathUserCounts.get(killer.getName())[2]++;
                else
                    killDeathUserCounts.get(killer.getName())[0]++;

                // killReachTrophyが設定されていたら、超えたかどうかを判定する
                HashMap<String, int[]> killDeathCounts = api.getKillDeathCounts();
                
                if ( config.getKillReachTrophy() > 0 &&
                        leaders.size() == 0 ) {

                    if ( killDeathCounts.get(teamKiller)[0] ==
                            config.getKillReachTrophy() ) {
                        int rest = config.getKillTrophy() - config.getKillReachTrophy();
                        String message = String.format(
                                PRENOTICE + "%s チームが、%d キルまでもう少しです(あと %d キル)。",
                                tnsKiller.getName(), config.getKillTrophy(), rest);
                        Bukkit.broadcastMessage(message);

                        // キル数達成イベントのコール
                        Team killerTeam = api.getPlayerTeam(killer);
                        ColorTeamingTrophyKillReachEvent event2 =
                                new ColorTeamingTrophyKillReachEvent(killerTeam, killer);
                        Bukkit.getServer().getPluginManager().callEvent(event2);
                    }
                }

                // killTrophyが設定されていたら、超えたかどうかを判定する
                if ( config.getKillTrophy() > 0 &&
                        leaders.size() == 0 ) {

                    if ( killDeathCounts.get(teamKiller)[0] ==
                            config.getKillTrophy() ) {

                        // 全体通知
                        String message = String.format(
                                PRENOTICE + "%s チームは、%d キルを達成しました！",
                                tnsKiller.getName(), config.getKillTrophy());
                        Bukkit.broadcastMessage(message);

                        // キル数リーチイベントのコール
                        Team killerTeam = api.getPlayerTeam(killer);
                        ColorTeamingTrophyKillEvent event2 =
                                new ColorTeamingTrophyKillEvent(killerTeam, killer);
                        Bukkit.getServer().getPluginManager().callEvent(event2);
                    }
                }
            }

            // 色設定を削除する
            if ( config.isColorRemoveOnDeath() ) {
                api.leavePlayerTeam(deader, Reason.DEAD);

                // チームがなくなっていたなら、チーム全滅イベントをコール
                ColorTeamingTeamDefeatedEvent event2 =
                        new ColorTeamingTeamDefeatedEvent(tnsDeader, killerName, deader.getName());
                Bukkit.getServer().getPluginManager().callEvent(event2);

                // 残っているチームがあと1チームなら、勝利イベントを更にコール
                ArrayList<TeamNameSetting> teamNames = api.getAllTeamNames();
                if ( teamNames.size() == 1 ) {
                    TeamNameSetting wonTeam = null;
                    for ( TeamNameSetting t : teamNames ) {
                        wonTeam = t;
                    }
                    ColorTeamingWonTeamEvent event3 =
                            new ColorTeamingWonTeamEvent(wonTeam, event2);
                    Bukkit.getServer().getPluginManager().callEvent(event3);
                }
            }
            
            // スコア表示を更新する
            api.refreshSidebarScore();
            api.refreshTabkeyListScore();
            api.refreshBelowNameScore();
            
            // ゲームオーバー画面をスキップする
            if ( config.isSkipGameover() ) {
                
                // NOTE: 回復するとゲームオーバー画面が表示されない
                Utility.heal(deader);
                
                // リスポーンイベントを呼び出す
                Location respawnLocation = deader.getBedSpawnLocation();
                if ( respawnLocation == null ) {
                    respawnLocation = deader.getWorld().getSpawnLocation();
                    // TODO: ワールドの初期設定によっては、リスポーン後に埋まることがある
                }
                PlayerRespawnEvent respawnEvent = 
                        new PlayerRespawnEvent(deader, respawnLocation, true);
                Bukkit.getServer().getPluginManager().callEvent(respawnEvent);
                
                respawnLocation = respawnEvent.getRespawnLocation();
                if ( respawnLocation != null ) {
                    
                    // 移送する場合は、経験値やインベントリのアイテムを落とさない
                    event.setDroppedExp(0);
                    event.getDrops().clear();
                    
                    // リスポーン場所へテレポートする
                    deader.teleport(respawnLocation, TeleportCause.PLUGIN);
                    // ノックバックの除去
                    deader.setVelocity(new Vector()); 
                }
            }
        }
    }
    
    private int getDeathPoint(Player deader) {
        
        int point = plugin.getCTConfig().getCTDeathPoint();
        if ( deader.hasMetadata(ClassData.DEATH_POINT_NAME) ) {
            point = deader.getMetadata(ClassData.DEATH_POINT_NAME).get(0).asInt();
            deader.removeMetadata(ClassData.DEATH_POINT_NAME, ColorTeaming.instance);
        }
        return point;
    }
    
    private int getKillPoint(Player deader) {
        
        int point = plugin.getCTConfig().getCTKillPoint();
        if ( deader.hasMetadata(ClassData.KILL_POINT_NAME) ) {
            point = deader.getMetadata(ClassData.KILL_POINT_NAME).get(0).asInt();
            deader.removeMetadata(ClassData.KILL_POINT_NAME, ColorTeaming.instance);
        }
        return point;
    }
}
