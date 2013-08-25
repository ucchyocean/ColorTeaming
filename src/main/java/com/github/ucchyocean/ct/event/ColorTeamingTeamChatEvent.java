/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.scoreboard.Team;

/**
 * チームチャットイベント
 * @author ucchy
 */
public class ColorTeamingTeamChatEvent extends ColorTeamingEvent implements Cancellable {

    private boolean isCancelled;

    /** 発言したプレイヤー */
    private Player player;

    /** 発言したメッセージ */
    private String message;
    
    /** 発言先チーム */
    private Team team;

    /**
     * コンストラクタ
     * @param player 発言したプレイヤー
     * @param message 発言したメッセージ
     * @param team 発言先チーム
     */
    public ColorTeamingTeamChatEvent(Player player, String message, Team team) {
        this.player = player;
        this.message = message;
        this.team = team;
    }

    /**
     * @return 発言したプレイヤー
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return 発言したメッセージ
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * @return 発言先チーム
     */
    public Team getTeam() {
        return team;
    }

    /**
     * @param message メッセージを設定する
     */
    public void setMessage(String message) {
        this.message = message;
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
