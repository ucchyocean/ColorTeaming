/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.listener;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Team;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.ColorTeamingAPI;
import com.github.ucchyocean.ct.ColorTeamingConfig;
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
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {

        // 倒された人を取得
        Player deader = event.getEntity();
        ColorTeamingConfig config = plugin.getCTConfig();
        ColorTeamingAPI api = plugin.getAPI();
        HashMap<String, int[]> killDeathCounts = api.getKillDeathCounts();
        HashMap<String, int[]> killDeathUserCounts = api.getKillDeathUserCounts();
        HashMap<String, ArrayList<String>> leaders = api.getLeaders();
        String color = api.getPlayerTeamName(deader);

        // 倒したプレイヤーを取得
        // 直接攻撃で倒された場合は、killerをそのまま使う
        // 間接攻撃で倒された場合は、shooterを取得して使う
        Player killer = deader.getKiller();
        EntityDamageEvent cause = event.getEntity().getLastDamageCause();
        if ( cause != null && cause instanceof EntityDamageByEntityEvent ) {
            Entity damager = ((EntityDamageByEntityEvent)cause).getDamager();
            if ( damager instanceof Projectile ) {
                LivingEntity shooter = ((Projectile) damager).getShooter();
                if ( shooter instanceof Player ) {
                    killer = (Player)shooter;
                }
            }
        }
        String killerName = null;
        if ( killer != null ) {
            killerName = killer.getName();
        }

        // Death数を加算

        // グループへ加算
        if ( !killDeathCounts.containsKey(color) ) {
            killDeathCounts.put(color, new int[3]);
        }
        killDeathCounts.get(color)[1]++;
        // ユーザーへ加算
        if ( !killDeathUserCounts.containsKey(deader.getName()) ) {
            killDeathUserCounts.put(deader.getName(), new int[3]);
        }
        killDeathUserCounts.get(deader.getName())[1]++;

        // 死亡したプレイヤーが、大将だった場合、倒されたことを全体に通知する。
        if ( leaders.containsKey(color) &&
                leaders.get(color).contains(deader.getName()) ) {
            String message = String.format(PRENOTICE + "%s チームの大将、%s が倒されました！",
                    color, deader.getName());
            Bukkit.broadcastMessage(message);
            leaders.get(color).remove(deader.getName());

            if ( leaders.get(color).size() >= 1 ) {
                message = String.format(PRENOTICE + "%s チームの残り大将は、あと %d 人です。",
                        color, leaders.get(color).size());
                Bukkit.broadcastMessage(message);
            } else {

                message = String.format(PRENOTICE + "%s チームの大将は全滅しました！", color);
                Bukkit.broadcastMessage(message);
                leaders.remove(color);

                // チームリーダー全滅イベントのコール
                ColorTeamingLeaderDefeatedEvent event2 =
                        new ColorTeamingLeaderDefeatedEvent(color, killerName, deader.getName());
                Bukkit.getServer().getPluginManager().callEvent(event2);

                // リーダーが残っているチームがあと1チームなら、勝利イベントを更にコール
                if ( leaders.size() == 1 ) {
                    String wonColor = "";
                    for ( String t : leaders.keySet() ) {
                        wonColor = t;
                    }
                    ColorTeamingWonLeaderEvent event3 =
                            new ColorTeamingWonLeaderEvent(wonColor, event2);
                    Bukkit.getServer().getPluginManager().callEvent(event3);
                }
            }
        }

        // 倒したプレイヤー側の処理
        if ( killer != null ) {
            String colorKiller = api.getPlayerTeamName(killer);

            // Kill数を加算

            // グループへ加算
            if ( !killDeathCounts.containsKey(colorKiller) ) {
                killDeathCounts.put(colorKiller, new int[3]);
            }
            if ( color.equals(colorKiller) ) // 同じグループだった場合のペナルティ
                killDeathCounts.get(colorKiller)[2]++;
            else
                killDeathCounts.get(colorKiller)[0]++;
            // ユーザーへ加算
            if ( !killDeathUserCounts.containsKey(killer.getName()) ) {
                killDeathUserCounts.put(killer.getName(), new int[3]);
            }
            if ( color.equals(colorKiller) ) // 同じグループだった場合のペナルティ
                killDeathUserCounts.get(killer.getName())[2]++;
            else
                killDeathUserCounts.get(killer.getName())[0]++;

            // killReachTrophyが設定されていたら、超えたかどうかを判定する
            if ( config.getKillReachTrophy() > 0 &&
                    leaders.size() == 0 ) {

                if ( killDeathCounts.get(colorKiller)[0] ==
                        config.getKillReachTrophy() ) {
                    int rest = config.getKillTrophy() - config.getKillReachTrophy();
                    String message = String.format(
                            PRENOTICE + "%s チームが、%d キルまでもう少しです(あと %d キル)。",
                            colorKiller, config.getKillTrophy(), rest);
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

                if ( killDeathCounts.get(colorKiller)[0] ==
                        config.getKillTrophy() ) {

                    // 全体通知
                    String message = String.format(
                            PRENOTICE + "%s チームは、%d キルを達成しました！",
                            colorKiller, config.getKillTrophy());
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
                    new ColorTeamingTeamDefeatedEvent(color, killerName, deader.getName());
            Bukkit.getServer().getPluginManager().callEvent(event2);

            // 残っているチームがあと1チームなら、勝利イベントを更にコール
            ArrayList<String> teamNames = api.getAllTeamNames();
            if ( teamNames.size() == 1 ) {
                String wonColor = "";
                for ( String t : leaders.keySet() ) {
                    wonColor = t;
                }
                ColorTeamingWonTeamEvent event3 =
                        new ColorTeamingWonTeamEvent(wonColor, event2);
                Bukkit.getServer().getPluginManager().callEvent(event3);
            }
        }

        // スコア表示を更新する
        api.refreshSidebarScore();
        api.refreshTabkeyListScore();
        api.refreshBelowNameScore();
    }
}
