/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.scoreboard.Team;

/**
 * キル数がkillReachTrophy/killTrophyに到達したときに呼び出される基底イベント
 * @author ucchy
 */
public abstract class ColorTeamingTrophyEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Team team;
    private Player killer;

    /**
     * コンストラクタ
     * @param team 到達したチーム
     * @param player 到達させたプレイヤー
     */
    public ColorTeamingTrophyEvent(Team team, Player killer) {
        this.team = team;
        this.killer = killer;
    }

    /**
     * 到達したチームを返す
     * @return 到達したチーム
     */
    public Team getTeam() {
        return team;
    }

    /**
     * 到達させたプレイヤーを返す
     * @return 到達させたプレイヤー
     */
    public Player getKiller() {
        return killer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
