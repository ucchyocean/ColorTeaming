/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * チーム戦勝利イベント。他のチームが全滅したときに、残り1チームなら、このイベントが発生する。
 * @author ucchy
 */
public class ColorTeamingWonTeamEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    /** チーム名 */
    private String teamName;

    /** ColorTeamingTeamDefeatedEvent */
    private ColorTeamingTeamDefeatedEvent tdevent;

    /**
     * コンストラクタ
     * @param teamName 勝利したチーム名
     * @param tdevent ColorTeamingTeamDefeatedEvent
     */
    public ColorTeamingWonTeamEvent(String teamName, ColorTeamingTeamDefeatedEvent tdevent) {
        this.teamName = teamName;
        this.tdevent = tdevent;
    }

    /**
     * @return 勝利したチーム名
     */
    public String getWonTeamName() {
        return teamName;
    }

    /**
     * @return TeamDefeatedEventを取得する
     */
    public ColorTeamingTeamDefeatedEvent getTeamDefeatedEvent() {
        return tdevent;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
