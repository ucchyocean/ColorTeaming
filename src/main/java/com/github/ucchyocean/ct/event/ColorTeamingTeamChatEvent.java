/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.scoreboard.Team;

/**
 * チームチャットイベント
 * @author ucchy
 */
public class ColorTeamingTeamChatEvent extends ColorTeamingEvent implements Cancellable {

    private boolean isCancelled;

    /** 発言した人 */
    private CommandSender sender;

    /** 発言したメッセージ */
    private String message;
    
    /** 発言先チーム */
    private Team team;

    /**
     * コンストラクタ
     * @param sender 発言した人
     * @param message 発言したメッセージ
     * @param team 発言先チーム
     */
    public ColorTeamingTeamChatEvent(CommandSender sender, String message, Team team) {
        this.sender = sender;
        this.message = message;
        this.team = team;
    }

    /**
     * @return 発言したプレイヤー
     */
    public CommandSender getSender() {
        return sender;
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
