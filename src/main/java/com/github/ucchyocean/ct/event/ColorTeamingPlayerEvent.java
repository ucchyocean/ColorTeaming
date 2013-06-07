/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.scoreboard.Team;

/**
 * プレイヤーのチーム参加/離脱に関する基底イベント
 * @author ucchy
 */
public abstract class ColorTeamingPlayerEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean isCancelled;

    /** 参加/離脱するプレイヤー */
    private Player player;

    /** 参加/離脱するチーム */
    private Team team;

    /**
     * コンストラクタ
     * @param player 参加/離脱するプレイヤー
     * @param team 参加/離脱するチーム
     */
    public ColorTeamingPlayerEvent(Player player, Team team) {
        this.player = player;
        this.team = team;
    }

    /**
     * @return 参加/離脱するプレイヤー
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return 参加/離脱するチーム
     */
    public Team getTeam() {
        return team;
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
