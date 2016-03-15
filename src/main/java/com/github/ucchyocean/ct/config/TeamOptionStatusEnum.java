/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2016
 */
package com.github.ucchyocean.ct.config;

import org.bukkit.scoreboard.Team;

/**
 * チーム間での有効無効設定の種類 (CB1.9以降用)
 * @author ucchy
 */
public enum TeamOptionStatusEnum {

    /**
     * 表示
     */
    ALWAYS,
    /**
     * 非表示
     */
    NEVER,
    /**
     * 他のチームメンバーから非表示
     */
    FOR_OTHER_TEAMS,
    /**
     * 自分のチームメンバーから非表示
     */
    FOR_OWN_TEAM;

    /**
     * 識別文字列から、TeamOptionStatusEnumを作成して返す。
     * 無効な文字列が指定された場合は、defで指定されたものが返される。
     * @param status 文字列
     * @param def デフォルト
     * @return 対応したTeamOptionStatusEnum
     */
    public static TeamOptionStatusEnum fromString(String status, TeamOptionStatusEnum def) {

        if ( status == null ) {
            return def;
        }

        for (TeamOptionStatusEnum value : TeamOptionStatusEnum.values()) {
            if ( value.toString().equalsIgnoreCase(status) ) {
                return value;
            }
        }

        return def;
    }

    /**
     * 対応するBukkitのTeam.OptionStatusに変換して返す。
     * @return Team.OptionStatus
     */
    public Team.OptionStatus getBukkit() {

        switch (this) {
        case ALWAYS:
            return Team.OptionStatus.ALWAYS;
        case NEVER:
            return Team.OptionStatus.NEVER;
        case FOR_OTHER_TEAMS:
            return Team.OptionStatus.FOR_OTHER_TEAMS;
        case FOR_OWN_TEAM:
            return Team.OptionStatus.FOR_OWN_TEAM;
        }
        return null;
    }

    /**
     * 対応するBukkitのNametagVisibilityEnumに変換して返す。
     * @return NametagVisibilityEnum
     */
    public NametagVisibilityEnum getNametagVisibility() {

        switch (this) {
        case ALWAYS:
            return NametagVisibilityEnum.ALWAYS;
        case NEVER:
            return NametagVisibilityEnum.NEVER;
        case FOR_OTHER_TEAMS:
            return NametagVisibilityEnum.HIDE_FOR_OTHER_TEAMS;
        case FOR_OWN_TEAM:
            return NametagVisibilityEnum.HIDE_FOR_OWN_TEAM;
        }
        return null;
    }
}
