/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

/**
 * キル数がkillReachTrophyに到達したときに呼び出されるイベント
 * @author ucchy
 */
public class ColorTeamingTrophyKillReachEvent extends ColorTeamingTrophyEvent {

    public ColorTeamingTrophyKillReachEvent(Team team, Player player) {
        super(team, player);
    }
}
