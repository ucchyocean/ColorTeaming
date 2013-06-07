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
import com.github.ucchyocean.ct.event.ColorTeamingPlayerLeaveEvent.Reason;
import com.github.ucchyocean.ct.event.ColorTeamingTeamDefeatedEvent;
import com.github.ucchyocean.ct.event.ColorTeamingTrophyKillEvent;
import com.github.ucchyocean.ct.event.ColorTeamingTrophyKillReachEvent;

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

        Player player = event.getEntity();
        ColorTeamingConfig config = plugin.getCTConfig();
        ColorTeamingAPI api = plugin.getAPI();
        HashMap<String, int[]> killDeathCounts = api.getKillDeathCounts();
        HashMap<String, int[]> killDeathUserCounts = api.getKillDeathUserCounts();
        HashMap<String, ArrayList<String>> leaders = api.getLeaders();

        String color = api.getPlayerTeamName(player);

        // Death数を加算

        // グループへ加算
        if ( !killDeathCounts.containsKey(color) ) {
            killDeathCounts.put(color, new int[3]);
        }
        killDeathCounts.get(color)[1]++;
        // ユーザーへ加算
        if ( !killDeathUserCounts.containsKey(player.getName()) ) {
            killDeathUserCounts.put(player.getName(), new int[3]);
        }
        killDeathUserCounts.get(player.getName())[1]++;

        // 死亡したプレイヤーが、大将だった場合、倒されたことを全体に通知する。
        if ( leaders.containsKey(color) &&
                leaders.get(color).contains(player.getName()) ) {
            String message = String.format(PRENOTICE + "%s チームの大将、%s が倒されました！",
                    color, player.getName());
            Bukkit.broadcastMessage(message);
            leaders.get(color).remove(player.getName());

            if ( leaders.get(color).size() >= 1 ) {
                message = String.format(PRENOTICE + "%s チームの残り大将は、あと %d 人です。",
                        color, leaders.get(color).size());
                Bukkit.broadcastMessage(message);
            } else {

                // イベントコール
                ColorTeamingTeamDefeatedEvent event2 =
                        new ColorTeamingTeamDefeatedEvent(color);
                Bukkit.getServer().getPluginManager().callEvent(event2);

                message = String.format(PRENOTICE + "%s チームの大将は全滅しました！", color);
                Bukkit.broadcastMessage(message);
                leaders.remove(color);
            }

        }

        // 倒したプレイヤーを取得
        // 直接攻撃で倒された場合は、killerをそのまま使う
        // 間接攻撃で倒された場合は、shooterを取得して使う
        Player killer = player.getKiller();
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

                    // イベントコール
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

                    // イベントコール
                    Team killerTeam = api.getPlayerTeam(killer);
                    ColorTeamingTrophyKillEvent event2 =
                            new ColorTeamingTrophyKillEvent(killerTeam, killer);
                    Bukkit.getServer().getPluginManager().callEvent(event2);
                }
            }
        }

        // 色設定を削除する
        if ( config.isColorRemoveOnDeath() ) {
            api.leavePlayerTeam(player, Reason.DEAD);
        }

        // スコア表示を更新する
        api.refreshSidebarScore();
        api.refreshTabkeyListScore();
        api.refreshBelowNameScore();
    }
}
