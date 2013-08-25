/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.config;

import org.bukkit.ChatColor;

/**
 * チーム名とチームカラーのセット
 * @author ucchy
 */
public class TeamName {

    /** チーム名 */
    private String name;
    /** チームカラー */
    private ChatColor color;
    
    /**
     * コンストラクタ
     * @param name チーム名
     * @param color チームカラー
     */
    public TeamName (String name, ChatColor color) {
        this.name = name;
        this.color = color;
    }

    /**
     * @return チーム名を返す
     */
    public String getName() {
        return name;
    }

    /**
     * @return チームカラーを返す
     */
    public ChatColor getColor() {
        return color;
    }
}
