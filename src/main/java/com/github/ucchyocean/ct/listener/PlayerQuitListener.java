/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.ColorTeamingConfig;

/**
 * @author ucchy
 * プレイヤーがログアウトしたときに、通知を受け取って処理するクラス
 */
public class PlayerQuitListener implements Listener {

    private static final String PRENOTICE = ChatColor.LIGHT_PURPLE.toString();

    /**
     * Playerがログアウトしたときに発生するイベント
     * @param event
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        // colorRemoveOnQuitがfalseなら、ここでは何もしない。
        if ( !ColorTeamingConfig.colorRemoveOnQuit ) {
            return;
        }

        Player player = event.getPlayer();
        String color = ColorTeaming.getPlayerColor(player);

        // ログアウトしたプレイヤーが、大将だった場合、逃げたことを全体に通知する。
        if ( ColorTeaming.leaders.containsKey(color) &&
                ColorTeaming.leaders.get(color).contains(player.getName()) ) {
            String message = String.format(PRENOTICE + "%s チームの大将、%s は逃げ出した！",
                    color, player.getName());
            ColorTeaming.sendBroadcast(message);
            ColorTeaming.leaders.get(color).remove(player.getName());

            if ( ColorTeaming.leaders.get(color).size() >= 1 ) {
                message = String.format(PRENOTICE + "%s チームの残り大将は、あと %d 人です。",
                        color, ColorTeaming.leaders.get(color).size());
            } else {
                message = String.format(PRENOTICE + "%s チームの大将は全滅しました！", color);
            }
            ColorTeaming.sendBroadcast(message);
        }

        // 色設定を削除する
        ColorTeaming.leavePlayerTeam(player);
    }

}
