/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.listener;

import java.util.ArrayList;
import java.util.Hashtable;

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

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.ColorTeamingConfig;
import com.github.ucchyocean.ct.GameGoalKind;

/**
 * @author ucchy
 * プレイヤーが死亡したときに、通知を受け取って処理するクラス
 */
public class PlayerDeathListener implements Listener {

    private static final String PRENOTICE = ChatColor.LIGHT_PURPLE.toString();

    /**
     * Playerが死亡したときに発生するイベント
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player player = event.getEntity();
        String color = ColorTeaming.getPlayerColor(player);
        boolean isGameEnd = false;

        // Death数を加算

        // グループへ加算
        if ( !ColorTeaming.killDeathCounts.containsKey(color) ) {
            ColorTeaming.killDeathCounts.put(color, new int[3]);
        }
        ColorTeaming.killDeathCounts.get(color)[1]++;
        // ユーザーへ加算
        if ( !ColorTeaming.killDeathUserCounts.containsKey(player.getName()) ) {
            ColorTeaming.killDeathUserCounts.put(player.getName(), new int[3]);
        }
        ColorTeaming.killDeathUserCounts.get(player.getName())[1]++;

        // 死亡したプレイヤーが、大将だった場合、倒されたことを全体に通知する。
        if ( ColorTeaming.leaders.containsKey(color) &&
                ColorTeaming.leaders.get(color).contains(player.getName()) ) {
            String message = String.format(PRENOTICE + "%s チームの大将、%s が倒されました！",
                    color, player.getName());
            ColorTeaming.sendBroadcast(message);
            ColorTeaming.leaders.get(color).remove(player.getName());

            if ( ColorTeaming.leaders.get(color).size() >= 1 ) {
                message = String.format(PRENOTICE + "%s チームの残り大将は、あと %d 人です。",
                        color, ColorTeaming.leaders.get(color).size());
                ColorTeaming.sendBroadcast(message);
            } else {
                message = String.format(PRENOTICE + "%s チームの大将は全滅しました！", color);
                ColorTeaming.sendBroadcast(message);

                ColorTeaming.leaders.remove(color);

                // ゲーム終了が設定されていたら、ゲーム終了する
                if ( ColorTeamingConfig.gameGoal == GameGoalKind.LEADER &&
                        (ColorTeaming.leaders.size() == 1) ) {

                    message = String.format(PRENOTICE + "%s チームの勝利です！",
                            ColorTeaming.leaders.keys().nextElement());
                    ColorTeaming.sendBroadcast(message);
                    isGameEnd = true;
                }
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
            String colorKiller = ColorTeaming.getPlayerColor(killer);

            // Kill数を加算

            // グループへ加算
            if ( !ColorTeaming.killDeathCounts.containsKey(colorKiller) ) {
                ColorTeaming.killDeathCounts.put(colorKiller, new int[3]);
            }
            if ( color.equals(colorKiller) ) // 同じグループだった場合のペナルティ
                ColorTeaming.killDeathCounts.get(colorKiller)[2]++;
            else
                ColorTeaming.killDeathCounts.get(colorKiller)[0]++;
            // ユーザーへ加算
            if ( !ColorTeaming.killDeathUserCounts.containsKey(killer.getName()) ) {
                ColorTeaming.killDeathUserCounts.put(killer.getName(), new int[3]);
            }
            if ( color.equals(colorKiller) ) // 同じグループだった場合のペナルティ
                ColorTeaming.killDeathUserCounts.get(killer.getName())[2]++;
            else
                ColorTeaming.killDeathUserCounts.get(killer.getName())[0]++;

            // killReachTrophyが設定されていたら、超えたかどうかを判定する
            if ( ColorTeamingConfig.killReachTrophy > 0 &&
                    ColorTeaming.leaders.size() == 0 ) {

                if ( ColorTeaming.killDeathCounts.get(colorKiller)[0] ==
                        ColorTeamingConfig.killReachTrophy ) {
                    int least = ColorTeamingConfig.killTrophy -
                            ColorTeamingConfig.killReachTrophy;
                    String message = String.format(
                            PRENOTICE + "%s チームが、%d キルまでもう少しです(あと %d キル)。",
                            colorKiller, ColorTeamingConfig.killTrophy, least);
                    ColorTeaming.sendBroadcast(message);
                }
            }

            // killTrophyが設定されていたら、超えたかどうかを判定する
            if ( ColorTeamingConfig.killTrophy > 0 &&
                    ColorTeaming.leaders.size() == 0 ) {

                if ( ColorTeaming.killDeathCounts.get(colorKiller)[0] ==
                        ColorTeamingConfig.killTrophy ) {

                    // 全体通知
                    String message = String.format(
                            PRENOTICE + "%s チームは、%d キルを達成しました！",
                            colorKiller, ColorTeamingConfig.killTrophy);
                    ColorTeaming.sendBroadcast(message);

                    // ゲーム終了が設定されていたら、ゲーム終了する
                    if ( ColorTeamingConfig.gameGoal == GameGoalKind.KILL ) {
                        message = String.format(PRENOTICE + "%s チームの勝利です！", colorKiller);
                        ColorTeaming.sendBroadcast(message);
                        isGameEnd = true;
                    }
                }
            }
        }

        // 色設定を削除する
        if ( ColorTeamingConfig.colorRemoveOnDeath ) {
            ColorTeaming.leavePlayerTeam(player);
        }

        // スコア表示を更新する
        ColorTeaming.refreshSidebarScore();
        ColorTeaming.refreshTabkeyListScore();
        ColorTeaming.refreshBelowNameScore();

        // チームが全滅したかどうかを確認する
        if ( ColorTeamingConfig.gameGoal == GameGoalKind.DEFEAT ) {
            Hashtable<String, ArrayList<Player>> members = ColorTeaming.getAllTeamMembers();
            int teamNum = 0;
            String teamName = "";
            for ( String group : members.keySet() ) {
                if ( members.get(group).size() >= 1 ) {
                    teamNum++;
                    teamName = group;
                }
            }
            if ( teamNum == 1 ) {

                String message = String.format(PRENOTICE + "%s チームの勝利です！", teamName);
                ColorTeaming.sendBroadcast(message);
                isGameEnd = true;
            }
        }

        // ゲームの終了を実行する
        if ( isGameEnd ) {
            ColorTeaming.endGame();
        }
    }
}
