/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2015
 */
package com.github.ucchyocean.ct.config;

import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;


/**
 * ネームタグの表示非表示の種類(CB1.8互換用)
 * @author ucchy
 */
@SuppressWarnings("deprecation")
public enum NametagVisibilityEnum {

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
    HIDE_FOR_OTHER_TEAMS,
    /**
     * 自分のチームメンバーから非表示
     */
    HIDE_FOR_OWN_TEAM;

    /**
     * 識別文字列から、NametagVisibilityEnumを作成して返す。
     * 無効な文字列が指定された場合は、defで指定されたものが返される。
     * @param visibility
     * @return 対応したNametagVisibilityEnum
     */
    public static NametagVisibilityEnum fromString(String visibility, NametagVisibilityEnum def) {

        if ( visibility == null ) {
            return def;
        }

        for (NametagVisibilityEnum value : NametagVisibilityEnum.values()) {
            if ( value.toString().equalsIgnoreCase(visibility) ) {
                return value;
            }
        }

        return def;
    }

    /**
     * 対応するBukkitのNameTagVisibilityに変換して返す。
     * @return NameTagVisibility
     */
    public NameTagVisibility getBukkit() {

        switch (this) {
        case ALWAYS:
            return NameTagVisibility.ALWAYS;
        case NEVER:
            return NameTagVisibility.NEVER;
        case HIDE_FOR_OTHER_TEAMS:
            return NameTagVisibility.HIDE_FOR_OTHER_TEAMS;
        case HIDE_FOR_OWN_TEAM:
            return NameTagVisibility.HIDE_FOR_OWN_TEAM;
        }
        return null;
    }

    /**
     * 対応するBukkitのTeam.OptionStatusに変換して返す。
     * @return
     */
    public Team.OptionStatus getBukkitOptionStatus() {

        switch (this) {
        case ALWAYS:
            return Team.OptionStatus.ALWAYS;
        case NEVER:
            return Team.OptionStatus.NEVER;
        case HIDE_FOR_OTHER_TEAMS:
            return Team.OptionStatus.FOR_OTHER_TEAMS;
        case HIDE_FOR_OWN_TEAM:
            return Team.OptionStatus.FOR_OWN_TEAM;
        }
        return null;
    }
}
