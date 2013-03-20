/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.scoreboard;

import org.bukkit.scoreboard.Objective.CRITERIA;

/**
 * @author ucchy
 * TabListに表示するスコアの種類
 */
public enum TabListCriteria {

    /** キル数 */
    KILL_COUNT("kill"),

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

    public static TabListCriteria convert(CRITERIA criteria) {

        switch (criteria) {
        case PLAYER_KILL_COUNT:
            return TabListCriteria.KILL_COUNT;
        case DEATH_COUNT:
            return TabListCriteria.DEATH_COUNT;
        case HEALTH:
            return TabListCriteria.HEALTH;
        }
        return TabListCriteria.NONE;
    }

    public static CRITERIA convert(TabListCriteria criteria) {

        switch (criteria) {
        case KILL_COUNT:
            return CRITERIA.PLAYER_KILL_COUNT;
        case DEATH_COUNT:
            return CRITERIA.DEATH_COUNT;
        case HEALTH:
            return CRITERIA.HEALTH;
        }
        return CRITERIA.DUMMY;
    }
}
