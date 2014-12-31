/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2014
 */
package com.github.ucchyocean.ct.event;

import com.github.ucchyocean.ct.config.TeamNameSetting;

/**
 * チームスコアが変化したときに発生するイベント
 * @author ucchy
 */
public class ColorTeamingTeamscoreChangeEvent extends ColorTeamingEvent {
    
    private TeamNameSetting team;
    private int pointBefore;
    private int pointAfter;
    
    /**
     * コンストラクタ
     * @param team チーム
     * @param pointBefore 変化前のポイント
     * @param pointAfter 変化後のポイント
     */
    public ColorTeamingTeamscoreChangeEvent(TeamNameSetting team, int pointBefore, int pointAfter) {
        this.team = team;
        this.pointBefore = pointBefore;
        this.pointAfter = pointAfter;
    }

    /**
     * @return team
     */
    public TeamNameSetting getTeam() {
        return team;
    }

    /**
     * @return pointBefore
     */
    public int getPointBefore() {
        return pointBefore;
    }

    /**
     * @return pointAfter
     */
    public int getPointAfter() {
        return pointAfter;
    }
}
