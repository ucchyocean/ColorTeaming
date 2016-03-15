/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.listener;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.ColorTeamingAPI;
import com.github.ucchyocean.ct.config.ColorTeamingConfig;
import com.github.ucchyocean.ct.config.ColorTeamingMessages;
import com.github.ucchyocean.ct.config.TeamNameSetting;
import com.github.ucchyocean.ct.event.ColorTeamingLeaderDefeatedEvent;
import com.github.ucchyocean.ct.event.ColorTeamingPlayerLeaveEvent.Reason;
import com.github.ucchyocean.ct.event.ColorTeamingTeamDefeatedEvent;
import com.github.ucchyocean.ct.event.ColorTeamingWonLeaderEvent;
import com.github.ucchyocean.ct.event.ColorTeamingWonTeamEvent;

/**
 * プレイヤーがログアウトしたときに、通知を受け取って処理するクラス
 * @author ucchy
 */
public class PlayerJoinQuitListener implements Listener {

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

        // チーム人数を更新する
        api.refreshRestTeamMemberScore();

        // worldRespawn が設定されていて、初参加のプレイヤーや、
        // チームが無くてベッドリスポーンが設定されていないプレイヤーは、
        // ワールドリスポーン地点に飛ばす
        Player player = event.getPlayer();
        if ( config.isWorldSpawn() ) {

            if ( !player.hasPlayedBefore() ||
                    (api.getPlayerTeamName(player) == null &&
                            player.getBedSpawnLocation() == null) ) {
                Location location = player.getWorld().getSpawnLocation();
                player.teleport(location, TeleportCause.PLUGIN);
            }
        }

        // チームに所属しているなら、チーム所属パーミッションを与える
        TeamNameSetting tns = api.getPlayerTeamName(player);
        if ( tns != null ) {
            plugin.addMemberPermission(player, tns.getID());
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

        // チーム人数を更新する
        api.refreshRestTeamMemberScore();

        // colorRemoveOnQuitがtrueなら処理する
        if ( config.isColorRemoveOnQuit() ) {
            leaveTeam(event.getPlayer());
        }
    }

    /**
     * プレイヤーがワールドを変更したときに発生するイベント
     * @param event
     */
    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent event) {

        ColorTeamingConfig config = plugin.getCTConfig();
        ColorTeamingAPI api = plugin.getAPI();
        String wname = event.getPlayer().getWorld().getName();

        // colorRemoveOnQuitがtrueなら処理する
        if ( config.isColorRemoveOnChangeWorld() &&
                !config.getWorldNames().contains(wname) ) {

            leaveTeam(event.getPlayer());

            // チーム人数を更新する
            api.refreshRestTeamMemberScore();
        }
    }

    /**
     * 指定したプレイヤーをチームから離脱させる
     * @param player
     */
    private void leaveTeam(Player player) {

        ColorTeamingAPI api = plugin.getAPI();

        TeamNameSetting tns = api.getPlayerTeamName(player);

        // チームに所属していないプレイヤーなら、処理しない
        if ( tns == null ) {
            return;
        }

        // ログアウトしたプレイヤーが、大将だった場合、逃げたことを全体に通知する。
        HashMap<String, ArrayList<String>> leaders = api.getLeaders();
        if ( leaders.containsKey(tns.getID()) &&
                leaders.get(tns.getID()).contains(player.getName()) ) {

            String message = ColorTeamingMessages.getLeaderDefeatedMessage(
                    tns.toString(), player.getName());
            if ( message != null ) {
                Bukkit.broadcastMessage(message);
            }

            leaders.get(tns.getID()).remove(player.getName());

            if ( leaders.get(tns.getID()).size() >= 1 ) {
                message = ColorTeamingMessages.getLeaderDefeatedRemainMessage(
                        tns.toString(), leaders.get(tns.getID()).size());
                if ( message != null ) {
                    Bukkit.broadcastMessage(message);
                }

            } else {
                message = ColorTeamingMessages.getLeaderDefeatedAllMessage(tns.toString());
                if ( message != null ) {
                    Bukkit.broadcastMessage(message);
                }
                leaders.remove(tns.getID());

                // チームリーダー全滅イベントのコール
                ColorTeamingLeaderDefeatedEvent event2 =
                        new ColorTeamingLeaderDefeatedEvent(tns, null, player.getName());
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

        // 色設定を削除する
        api.leavePlayerTeam(player, Reason.DEAD);

        // チームがなくなっていたなら、チーム全滅イベントをコール
        ColorTeamingTeamDefeatedEvent event2 =
                new ColorTeamingTeamDefeatedEvent(tns, null, player.getName());
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
}
