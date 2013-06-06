/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

/**
 * チームにプレイヤーが追加されたときに呼び出されるメソッド
 * @author ucchy
 */
public class ColorTeamingPlayerAddEvent extends ColorTeamingPlayerEvent {

    public ColorTeamingPlayerAddEvent(Player player, Team team) {
        super(player, team);
    }
}
