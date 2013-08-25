/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;

import com.github.ucchyocean.ct.config.TeamNameSetting;


/**
 * チームが削除されたときのイベント
 * @author ucchy
 */
public class ColorTeamingTeamRemoveEvent extends ColorTeamingTeamEvent {

    public ColorTeamingTeamRemoveEvent(TeamNameSetting teamName) {
        super(teamName);
    }
}
