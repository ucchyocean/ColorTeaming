/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;

import com.github.ucchyocean.ct.config.TeamNameSetting;


/**
 * チームのリーダーが全滅したときのイベント
 * @author ucchy
 */
public class ColorTeamingLeaderDefeatedEvent extends ColorTeamingEvent {

    /** チーム名 */
    private TeamNameSetting teamName;

    /** 最後に倒したプレイヤー名 */
    private String killer;

    /** 最後に倒されたプレイヤー名 */
    private String deader;

    /**
     * コンストラクタ
     * @param team チーム
     * @param killer 最後に倒したプレイヤー名
     * @param deader 最後に倒されたプレイヤー名
     */
    public ColorTeamingLeaderDefeatedEvent(TeamNameSetting teamName, String killer, String deader) {
        this.teamName = teamName;
        this.killer = killer;
        this.deader = deader;
    }

    /**
     * @return チーム名
     */
    public TeamNameSetting getTeamName() {
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
