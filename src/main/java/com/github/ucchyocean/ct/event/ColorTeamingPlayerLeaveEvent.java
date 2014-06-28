/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.event;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

/**
 * プレイヤーがチームから離脱するときに呼び出されるイベント
 * @author ucchy
 */
public class ColorTeamingPlayerLeaveEvent extends ColorTeamingPlayerEvent {

    /**
     * チームを離脱した理由
     * @author ucchy
     */
    public enum Reason {

        /** 死亡による離脱 */
        DEAD,

        /** チーム削除による離脱 */
        TEAM_REMOVED,

        /** cleaveコマンドによる自己離脱 */
        SELF,

        /** ct leave コマンドによる強制離脱 */
        ADMIN_COMMAND,

        /** 不明 */
        UNKNOWN,
    };

    private Reason reason;

    /**
     * コンストラクタ
     * @param player プレイヤー
     * @param team チーム
     * @param reason 離脱の理由
     */
    public ColorTeamingPlayerLeaveEvent(Player player, Team team, Reason reason) {
        super(player, team);
        this.reason = reason;
    }

    /**
     * 離脱理由を取得する
     * @return 離脱理由
     */
    public Reason getReason() {
        return reason;
    }
}
