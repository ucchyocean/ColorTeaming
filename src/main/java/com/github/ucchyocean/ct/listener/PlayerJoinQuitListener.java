/*
 * Copyright ucchy 2013
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.ColorTeamingAPI;
import com.github.ucchyocean.ct.ColorTeamingConfig;
import com.github.ucchyocean.ct.scoreboard.SidebarCriteria;

/**
 * プレイヤーがログアウトしたときに、通知を受け取って処理するクラス
 * @author ucchy
 */
public class PlayerJoinQuitListener implements Listener {

    private static final String PRENOTICE = ChatColor.LIGHT_PURPLE.toString();

    private ColorTeaming plugin;

    public PlayerJoinQuitListener(ColorTeaming plugin) {
        this.plugin = plugin;
    }

    /**
     * プレイヤーがログインしたときに発生するイベント
     * @param event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        ColorTeamingConfig config = plugin.getCTConfig();
        ColorTeamingAPI api = plugin.getAPI();

        // クライテリアが残り人数に設定されているなら、
        if ( config.getSideCriteria() == SidebarCriteria.REST_PLAYER ) {
            // サイドバーを更新する
            api.refreshSidebarScore();
        }

        // worldRespawn が設定されていて、初参加のプレイヤーや、
        // チームが無くてベッドリスポーンが設定されていないプレイヤーは、
        // ワールドリスポーン地点に飛ばす
        Player player = event.getPlayer();
        if ( config.isWorldSpawn() ) {

            if ( !player.hasPlayedBefore() ||
                    (api.getPlayerColor(player).equals("") &&
                            player.getBedSpawnLocation() == null) ) {
                Location location = player.getWorld().getSpawnLocation();
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

        ColorTeamingConfig config = plugin.getCTConfig();
        ColorTeamingAPI api = plugin.getAPI();

        // サイドバーを更新する
        api.refreshSidebarScore();

        // colorRemoveOnQuitがfalseなら、以降の処理は何もしない。
        if ( !config.isColorRemoveOnQuit() ) {
            return;
        }

        Player player = event.getPlayer();
        String color = api.getPlayerColor(player);

        // ログアウトしたプレイヤーが、大将だった場合、逃げたことを全体に通知する。
        HashMap<String, ArrayList<String>> leaders = api.getLeaders();
        if ( leaders.containsKey(color) &&
                leaders.get(color).contains(player.getName()) ) {
            String message = String.format(PRENOTICE + "%s チームの大将、%s は逃げ出した！",
                    color, player.getName());
            Bukkit.broadcastMessage(message);
            leaders.get(color).remove(player.getName());

            if ( leaders.get(color).size() >= 1 ) {
                message = String.format(PRENOTICE + "%s チームの残り大将は、あと %d 人です。",
                        color, leaders.get(color).size());
            } else {
                message = String.format(PRENOTICE + "%s チームの大将は全滅しました！", color);
            }
            Bukkit.broadcastMessage(message);
        }

        // 色設定を削除する
        api.leavePlayerTeam(player);
    }

}
