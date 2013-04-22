/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.scoreboard;

import org.bukkit.scoreboard.Criterias;


/**
 * @author ucchy
 * TabListに表示するスコアの種類
 */
public enum TabListCriteria {

    /** キル数 */
    KILL_COUNT("kill"),

    /** キル数(MOB含む) */
    TOTAL_KILL_COUNT("total_kill"),

    /** デス数 */
    DEATH_COUNT("death"),

    /** ポイント */
    POINT("point"),

    /** 残り体力 */
    HEALTH("health"),

    /** 非表示 */
    NONE("none");

    private final String criteria;

    /**
     * コンストラクタ
     * @param criteria 識別文字列
     */
    TabListCriteria (String criteria) {
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
     * 識別文字列から、TabListCriteriaを作成して返す。
     * 無効な文字列が指定された場合は、TabListCriteria.NONEが返される。
     * @param criteria 識別文字列
     * @return 対応したTabListCriteria
     */
    public static TabListCriteria fromString(String criteria) {

        if ( criteria == null ) {
            return TabListCriteria.NONE;
        }

        for (TabListCriteria value : TabListCriteria.values()) {
            if (criteria.equalsIgnoreCase(value.criteria)) {
                return value;
            }
        }

        return TabListCriteria.NONE;
    }

    public static TabListCriteria convert(Criterias criteria) {

        if ( criteria == null ) {
            return TabListCriteria.NONE;
        } else if ( Criterias.PLAYER_KILLS.equals(criteria) ) {
            return TabListCriteria.KILL_COUNT;
        } else if ( Criterias.TOTAL_KILLS.equals(criteria) ) {
            return TabListCriteria.TOTAL_KILL_COUNT;
        } else if ( Criterias.DEATHS.equals(criteria) ) {
            return TabListCriteria.DEATH_COUNT;
        } else if ( Criterias.HEALTH.equals(criteria) ) {
            return TabListCriteria.HEALTH;
        }
        return TabListCriteria.NONE;
    }

    public static String convert(TabListCriteria criteria) {

        switch (criteria) {
        case KILL_COUNT:
            return Criterias.PLAYER_KILLS;
        case TOTAL_KILL_COUNT:
            return Criterias.TOTAL_KILLS;
        case DEATH_COUNT:
            return Criterias.DEATHS;
        case HEALTH:
            return Criterias.HEALTH;
        case POINT:
        case NONE:
            return ""; // return dummy.
        }
        return "";
    }
}
