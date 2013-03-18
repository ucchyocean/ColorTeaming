/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.scoreboard;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

/**
 * @author ucchy
 * サイドバーに表示するチーム項目クラス
 */
public class SidebarTeamScore implements OfflinePlayer {

    private Team team;

    /**
     * コンストラクタ
     * @param team チーム
     */
    public SidebarTeamScore(Team team) {
        this.team = team;
    }

    /**
     * @see org.bukkit.OfflinePlayer#getName()
     */
    public String getName() {
        return team.getDisplayName();
    }

    // 以下、OfflinePlayerインターフェイスのクラスで、使用しないメソッド。

    public boolean isOp() {
        return false;
    }

    public void setOp(boolean arg0) {
    }

    public Map<String, Object> serialize() {
        return null;
    }

    public Location getBedSpawnLocation() {
        return null;
    }

    public long getFirstPlayed() {
        return 0;
    }

    public long getLastPlayed() {
        return 0;
    }

    public Player getPlayer() {
        return null;
    }

    public boolean hasPlayedBefore() {
        return false;
    }

    public boolean isBanned() {
        return false;
    }

    public boolean isOnline() {
        return false;
    }

    public boolean isWhitelisted() {
        return true;
    }

    public void setBanned(boolean arg0) {
    }

    public void setWhitelisted(boolean arg0) {
    }
}
