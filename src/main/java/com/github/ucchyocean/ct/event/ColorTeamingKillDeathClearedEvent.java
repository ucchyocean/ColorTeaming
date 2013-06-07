/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * キルデス数のカウントがクリアされるときに呼び出されるイベント
 * @author ucchy
 */
public class ColorTeamingKillDeathClearedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    /**
     * コンストラクタ
     */
    public ColorTeamingKillDeathClearedEvent() {
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
