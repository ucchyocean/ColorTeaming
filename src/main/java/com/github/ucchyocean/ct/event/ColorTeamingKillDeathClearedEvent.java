/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;

import org.bukkit.event.Cancellable;


/**
 * キルデス数のカウントがクリアされるときに呼び出されるイベント
 * @author ucchy
 */
public class ColorTeamingKillDeathClearedEvent extends ColorTeamingEvent implements Cancellable {

    private boolean isCancelled;

    /**
     * コンストラクタ
     */
    public ColorTeamingKillDeathClearedEvent() {
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
