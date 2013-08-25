/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;

import com.github.ucchyocean.ct.config.TeamNameSetting;


/**
 * チーム戦勝利イベント。他のチームが全滅したときに、残り1チームなら、このイベントが発生する。
 * @author ucchy
 */
public class ColorTeamingWonTeamEvent extends ColorTeamingEvent {

    /** チーム名 */
    private TeamNameSetting teamName;

    /** ColorTeamingTeamDefeatedEvent */
    private ColorTeamingTeamDefeatedEvent tdevent;

    /**
     * コンストラクタ
     * @param teamName 勝利したチーム名
     * @param tdevent ColorTeamingTeamDefeatedEvent
     */
    public ColorTeamingWonTeamEvent(TeamNameSetting teamName, ColorTeamingTeamDefeatedEvent tdevent) {
        this.teamName = teamName;
        this.tdevent = tdevent;
    }

    /**
     * @return 勝利したチーム名
     */
    public TeamNameSetting getWonTeamName() {
        return teamName;
    }

    /**
     * @return TeamDefeatedEventを取得する
     */
    public ColorTeamingTeamDefeatedEvent getTeamDefeatedEvent() {
        return tdevent;
    }
}
