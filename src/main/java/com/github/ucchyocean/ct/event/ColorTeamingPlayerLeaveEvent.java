/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

/**
 * プレイヤーがチームから離脱するときに呼び出されるイベント
 * @author ucchy
 */
public class ColorTeamingPlayerLeaveEvent extends ColorTeamingPlayerEvent {

    public ColorTeamingPlayerLeaveEvent(Player player, Team team) {
        super(player, team);
    }
}
