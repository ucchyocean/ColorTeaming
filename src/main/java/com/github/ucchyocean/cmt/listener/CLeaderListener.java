/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.cmt.listener;

import java.util.Enumeration;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.ucchyocean.cmt.ColorMeTeaming;

/**
 * @author ucchy
 * プレイヤーが死亡したりログアウトしたときに、大将だったかどうかを確認するクラス
 */
public class CLeaderListener implements Listener {

    private static final String PRENOTICE = ChatColor.LIGHT_PURPLE.toString();

    /**
     * Playerが死亡したときに発生するイベント
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player player = event.getEntity();

        // 死亡したプレイヤーは、どこかのグループのリーダーだったか、調べる。
        Enumeration<String> keys = ColorMeTeaming.leaders.keys();

        while ( keys.hasMoreElements() ) {

            String key = keys.nextElement();

            if ( ColorMeTeaming.leaders.get(key).contains(player) ) {
                String message = String.format(PRENOTICE + "%s チームの大将、%s が倒されました！",
                        key, player.getName());
                ColorMeTeaming.sendBroadcast(message);
                ColorMeTeaming.leaders.get(key).remove(player);

                if ( ColorMeTeaming.leaders.get(key).size() >= 1 ) {
                    message = String.format(PRENOTICE + "%s チームの残り大将は、あと %d 人です。",
                            key, ColorMeTeaming.leaders.get(key).size());
                } else {
                    message = String.format(PRENOTICE + "%s チームの大将は全滅しました！", key);
                }
                ColorMeTeaming.sendBroadcast(message);
            }
        }
    }

    /**
     * Playerがログアウトしたときに発生するイベント
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        // ログアウトしたプレイヤーは、どこかのグループのリーダーだったか、調べる。
        Enumeration<String> keys = ColorMeTeaming.leaders.keys();

        while ( keys.hasMoreElements() ) {

            String key = keys.nextElement();

            if ( ColorMeTeaming.leaders.get(key).contains(player) ) {
                String message = String.format(PRENOTICE + "%s チームの大将、%s は逃げ出した！",
                        key, player.getName());
                ColorMeTeaming.sendBroadcast(message);
                ColorMeTeaming.leaders.get(key).remove(player);

                if ( ColorMeTeaming.leaders.get(key).size() >= 1 ) {
                    message = String.format(PRENOTICE + "%s チームの残り大将は、あと %d 人です。",
                            key, ColorMeTeaming.leaders.get(key).size());
                } else {
                    message = String.format(PRENOTICE + "%s チームの大将は全滅しました！", key);
                }
                ColorMeTeaming.sendBroadcast(message);
            }
        }
    }
}
