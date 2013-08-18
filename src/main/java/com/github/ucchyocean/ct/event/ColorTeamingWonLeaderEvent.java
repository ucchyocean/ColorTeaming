/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 大将戦勝利イベント。他のチームの大将が全滅したときに、大将が残っているのが残り1チームなら、このイベントが発生する。
 * @author ucchy
 *
 */
public class ColorTeamingWonLeaderEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    /** チーム名 */
    private String teamName;

    /** ColorTeamingLeaderDefeatedEvent */
    private ColorTeamingLeaderDefeatedEvent ldevent;

    /**
     * コンストラクタ
     * @param teamName 勝利チーム名
     * @param ldevent ColorTeamingLeaderDefeatedEvent
     */
    public ColorTeamingWonLeaderEvent(String teamName, ColorTeamingLeaderDefeatedEvent ldevent) {
        this.teamName = teamName;
        this.ldevent = ldevent;
    }

    /**
     * @return 勝利チーム名
     */
    public String getWonTeamName() {
        return teamName;
    }

    /**
     * @return LeaderDefeatedEventを取得する
     */
    public ColorTeamingLeaderDefeatedEvent getLeaderDefeatedEvent() {
        return ldevent;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
