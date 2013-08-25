/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;

import com.github.ucchyocean.ct.config.TeamNameSetting;


/**
 * チームが作成されたときのイベント
 * @author ucchy
 */
public class ColorTeamingTeamCreateEvent extends ColorTeamingTeamEvent {

    public ColorTeamingTeamCreateEvent(TeamNameSetting teamName) {
        super(teamName);
    }
}
