/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.RespawnConfiguration;

/**
 * プレイヤーがリスポーンしたときに、通知を受け取って処理するクラス
 * @author ucchy
 */
public class PlayerRespawnListener implements Listener {

    private ColorTeaming plugin;

    public PlayerRespawnListener(ColorTeaming plugin) {
        this.plugin = plugin;
    }

    /**
     * Playerがリスポーンしたときに発生するイベント
     * @param event
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        Player player = event.getPlayer();
        String color = plugin.getAPI().getPlayerTeamName(player);

        // リスポーンポイントを設定
        RespawnConfiguration respawnConfig = plugin.getAPI().getRespawnConfig();
        String respawnMapName = plugin.getAPI().getRespawnMapName();
        Location respawn = respawnConfig.get(color, respawnMapName);
        
//        if ( respawn == null && plugin.getCTConfig().isWorldSpawn() ) {
//            // チームリスポーンがない場合は、ワールドリスポーンを取得する
//            respawn = player.getWorld().getSpawnLocation();
//        }

        if ( respawn != null ) {
            respawn = respawn.add(0.5, 0, 0.5);
            event.setRespawnLocation(respawn);
            player.setNoDamageTicks(plugin.getCTConfig().getNoDamageSeconds() * 20);
        }
    }
}
