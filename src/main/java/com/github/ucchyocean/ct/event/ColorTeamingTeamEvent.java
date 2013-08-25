/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;

import org.bukkit.event.Cancellable;

import com.github.ucchyocean.ct.config.TeamNameSetting;

/**
 * チームの作成/削除に関する基底イベント
 * @author ucchy
 */
public abstract class ColorTeamingTeamEvent extends ColorTeamingEvent implements Cancellable {

    private boolean isCancelled;

    /** チーム名 */
    private TeamNameSetting teamName;

    /**
     * コンストラクタ
     * @param team チーム
     */
    public ColorTeamingTeamEvent(TeamNameSetting teamName) {
        this.teamName = teamName;
    }

    /**
     * @return チーム名
     */
    public TeamNameSetting getTeamName() {
        return teamName;
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
