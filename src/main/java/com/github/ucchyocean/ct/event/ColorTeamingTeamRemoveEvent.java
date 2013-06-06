/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;


/**
 * チームが削除されたときのイベント
 * @author ucchy
 */
public class ColorTeamingTeamRemoveEvent extends ColorTeamingTeamEvent {

    public ColorTeamingTeamRemoveEvent(String teamName) {
        super(teamName);
    }
}
