/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;

import com.github.ucchyocean.ct.config.TeamNameSetting;


/**
 * 大将戦勝利イベント。他のチームの大将が全滅したときに、大将が残っているのが残り1チームなら、このイベントが発生する。
 * @author ucchy
 *
 */
public class ColorTeamingWonLeaderEvent extends ColorTeamingEvent {

    /** チーム名 */
    private TeamNameSetting teamName;

    /** ColorTeamingLeaderDefeatedEvent */
    private ColorTeamingLeaderDefeatedEvent ldevent;

    /**
     * コンストラクタ
     * @param teamName 勝利チーム名
     * @param ldevent ColorTeamingLeaderDefeatedEvent
     */
    public ColorTeamingWonLeaderEvent(TeamNameSetting teamName, ColorTeamingLeaderDefeatedEvent ldevent) {
        this.teamName = teamName;
        this.ldevent = ldevent;
    }

    /**
     * @return 勝利チーム名
     */
    public TeamNameSetting getWonTeamName() {
        return teamName;
    }

    /**
     * @return LeaderDefeatedEventを取得する
     */
    public ColorTeamingLeaderDefeatedEvent getLeaderDefeatedEvent() {
        return ldevent;
    }
}
