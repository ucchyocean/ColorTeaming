/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.config;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;


/**
 * チーム名とチームカラーのセット
 * @author ucchy
 */
public class TeamNameSetting {

    /** チームID */
    private String id;
    /** チーム名 */
    private String name;
    /** チームカラー */
    private ChatColor color;
    
    /**
     * コンストラクタ
     * @param id チームID
     * @param name チーム名
     * @param color チームカラー
     */
    public TeamNameSetting(String id, String name, ChatColor color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    /**
     * @return チームIDを返す
     */
    public String getID() {
        return id;
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
    
    /**
     * @return スコアボード用の項目名（ダミーのオフラインプレイヤー）を返す
     */
    public OfflinePlayer getScoreItem() {
        return Bukkit.getOfflinePlayer(toString());
    }

    /**
     * color + name を返す
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if ( color != null ) return color + name;
        return name;
    }
}
