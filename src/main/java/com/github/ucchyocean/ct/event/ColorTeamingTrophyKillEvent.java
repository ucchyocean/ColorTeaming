/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

/**
 * キル数がkillTrophyに到達したときに呼び出されるイベント
 * @author ucchy
 */
public class ColorTeamingTrophyKillEvent extends ColorTeamingTrophyEvent {

    public ColorTeamingTrophyKillEvent(Team team, Player player) {
        super(team, player);
    }
}
