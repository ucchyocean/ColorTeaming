/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * チームのリーダーが全滅したときのイベント
 * @author ucchy
 */
public class ColorTeamingLeaderDefeatedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    /** チーム名 */
    private String teamName;

    /**
     * コンストラクタ
     * @param team チーム
     */
    public ColorTeamingLeaderDefeatedEvent(String teamName) {
        this.teamName = teamName;
    }

    /**
     * @return チーム名
     */
    public String getTeamName() {
        return teamName;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
