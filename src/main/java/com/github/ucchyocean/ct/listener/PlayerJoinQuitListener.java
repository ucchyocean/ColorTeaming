/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.listener;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.scoreboard.SidebarCriteria;

/**
 * @author ucchy
 * プレイヤーがログアウトしたときに、通知を受け取って処理するクラス
 */
public class PlayerJoinQuitListener implements Listener {

    private static final String PRENOTICE = ChatColor.LIGHT_PURPLE.toString();

    /**
     * プレイヤーがログインしたときに発生するイベント
     * @param event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        // クライテリアが残り人数に設定されているなら、
        if ( ColorTeaming.getCTConfig().getSideCriteria() == SidebarCriteria.REST_PLAYER ) {
            // サイドバーを更新する
            ColorTeaming.refreshSidebarScore();
        }

        // worldRespawn が設定されていて、チームに所属していないプレイヤーは
        // ワールドリスポーン地点に飛ばす
        Player player = event.getPlayer();
        if ( ColorTeaming.getCTConfig().isWorldSpawn() &&
                ColorTeaming.getPlayerColor(player).equals("") ) {
            Location location = player.getWorld().getSpawnLocation();
            if ( location != null ) {
                player.teleport(location, TeleportCause.PLUGIN);
            }
        }
    }


    /**
     * Playerがログアウトしたときに発生するイベント
     * @param event
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        // サイドバーを更新する
        ColorTeaming.refreshSidebarScore();

        // colorRemoveOnQuitがfalseなら、以降の処理は何もしない。
        if ( !ColorTeaming.getCTConfig().isColorRemoveOnQuit() ) {
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
