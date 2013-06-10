/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * チームのリーダーが全滅したときのイベント
 * @author ucchy
 */
public class ColorTeamingLeaderDefeatedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    /** チーム名 */
    private String teamName;

    /** 最後に倒したプレイヤー名 */
    private String killer;

    /** 最後に倒されたプレイヤー名 */
    private String deader;

    /**
     * コンストラクタ
     * @param team チーム
     * @param killer 最後に倒したプレイヤー名
     * @param deader 最後に倒されたプレイヤー名
     */
    public ColorTeamingLeaderDefeatedEvent(String teamName, String killer, String deader) {
        this.teamName = teamName;
        this.killer = killer;
        this.deader = deader;
    }

    /**
     * @return チーム名
     */
    public String getTeamName() {
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
