/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;


/**
 * チームが作成されたときのイベント
 * @author ucchy
 */
public class ColorTeamingTeamCreateEvent extends ColorTeamingTeamEvent {

    public ColorTeamingTeamCreateEvent(String teamName) {
        super(teamName);
    }
}
