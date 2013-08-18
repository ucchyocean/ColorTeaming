/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * チーム全滅イベント
 * @author ucchy
 */
public class ColorTeamingTeamDefeatedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    /** チーム名 */
    private String teamName;

    /** 最後に倒したプレイヤー名 */
    private String killer;

    /** 最後に倒されたプレイヤー名 */
    private String deader;

    /**
     * コンストラクタ
     * @param teamName 全滅したチーム名
     * @param killer 最後に倒したプレイヤー名
     * @param deader 最後に倒されたプレイヤー名
     */
    public ColorTeamingTeamDefeatedEvent(String teamName, String killer, String deader) {
        this.teamName = teamName;
        this.killer = killer;
        this.deader = deader;
    }

    /**
     * @return 全滅したチーム名
     */
    public String getLoseTeamName() {
        return teamName;
    }

    /**
     * @return 最後に倒したプレイヤー名、自殺だった場合はnullになることに注意
     */
    public String getKiller() {
        return killer;
    }

    /**
     * @return 最後に倒されたプレイヤー名
     */
    public String getDeader() {
        return deader;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
