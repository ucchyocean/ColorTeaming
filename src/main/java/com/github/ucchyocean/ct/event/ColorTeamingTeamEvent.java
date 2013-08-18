/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * チームの作成/削除に関する基底イベント
 * @author ucchy
 */
public abstract class ColorTeamingTeamEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean isCancelled;

    /** チーム名 */
    private String teamName;

    /**
     * コンストラクタ
     * @param team チーム
     */
    public ColorTeamingTeamEvent(String teamName) {
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

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }
}
