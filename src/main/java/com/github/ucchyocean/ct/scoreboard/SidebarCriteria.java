/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.scoreboard;

/**
 * @author ucchy
 * サイドバーに表示するスコアの種類
 */
public enum SidebarCriteria {

    /** キル数 */
    KILL_COUNT("kill"),

    /** デス数 */
    DEATH_COUNT("death"),

    /** ポイント */
    POINT("point"),

    /** 残り人数 */
    REST_PLAYER("rest"),

    /** 非表示 */
    NONE("none");

    private final String criteria;

    /**
     * コンストラクタ
     * @param criteria 識別文字列
     */
    SidebarCriteria (String criteria) {
        this.criteria = criteria;
    }

    /**
     * 識別文字列を返す
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return this.criteria;
    }

    /**
     * 識別文字列から、TeamCriteriaを作成して返す。
     * 無効な文字列が指定された場合は、TeamCriteria.NONEが返される。
     * @param criteria 識別文字列
     * @return 対応したTeamCriteria
     */
    public static SidebarCriteria fromString(String criteria) {

        if ( criteria == null ) {
            return SidebarCriteria.NONE;
        }

        for (SidebarCriteria value : SidebarCriteria.values()) {
            if (criteria.equalsIgnoreCase(value.criteria)) {
                return value;
            }
        }

        return SidebarCriteria.NONE;
    }

    /**
     * サイドバーのタイトル部分に表示する文字列
     * @return 対応したタイトル文字列
     */
    public String getSidebarTitle() {

        switch (this) {
        case KILL_COUNT:
            return "スコア(キル数)";
        case DEATH_COUNT:
            return "スコア(デス数)";
        case POINT:
            return "チームスコア";
        case REST_PLAYER:
            return "チーム人数";
        case NONE:
        default:
            return "";
        }
    }
}
