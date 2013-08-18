/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * チームチャットイベント
 * @author ucchy
 */
public class ColorTeamingTeamChatEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean isCancelled;

    /** 発言したプレイヤー */
    private Player player;

    /** 発言したメッセージ */
    private String message;

    /**
     * コンストラクタ
     * @param player 発言したプレイヤー
     * @param message 発言したメッセージ
     */
    public ColorTeamingTeamChatEvent(Player player, String message) {
        this.player = player;
        this.message = message;
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
     * @param message メッセージを設定する
     */
    public void setMessage(String message) {
        this.message = message;
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
