/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.config.RespawnConfiguration;
import com.github.ucchyocean.ct.config.TeamNameSetting;

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
        TeamNameSetting tns = plugin.getAPI().getPlayerTeamName(player);

        // （死亡したあとも）チームに所属している場合
        if ( tns != null ) {

            // チームのリスポーン場所に設定
            RespawnConfiguration respawnConfig = plugin.getAPI().getRespawnConfig();
            String respawnMapName = plugin.getAPI().getRespawnMapName();
            Location respawn = respawnConfig.get(tns.getID(), respawnMapName);
            
            if ( respawn != null ) {
                respawn = respawn.add(0.5, 0, 0.5);
                event.setRespawnLocation(respawn);
            }
            
            // 無敵時間を設定
            player.setNoDamageTicks(plugin.getCTConfig().getNoDamageSeconds() * 20);
        }
    }
}
