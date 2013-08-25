/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;

import com.github.ucchyocean.ct.config.TeamNameSetting;


/**
 * チーム全滅イベント
 * @author ucchy
 */
public class ColorTeamingTeamDefeatedEvent extends ColorTeamingEvent {

    /** チーム名 */
    private TeamNameSetting teamName;

    /** 最後に倒したプレイヤー名 */
    private String killer;

    /** 最後に倒されたプレイヤー名 */
    private String deader;

    /**
     * コンストラクタ
     * @param teamName 全滅したチーム名
     * @param killer 最後に倒したプレイヤー名
     * @param deader 最後に倒されたプレイヤー名
     */
    public ColorTeamingTeamDefeatedEvent(TeamNameSetting teamName, String killer, String deader) {
        this.teamName = teamName;
        this.killer = killer;
        this.deader = deader;
    }

    /**
     * @return 全滅したチーム名
     */
    public TeamNameSetting getLoseTeamName() {
        return teamName;
    }

    /**
     * @return 最後に倒したプレイヤー名、自殺だった場合はnullになることに注意
     */
    public String getKiller() {
        return killer;
    }

    /**
     * @return 最後に倒されたプレイヤー名
     */
    public String getDeader() {
        return deader;
    }
}
