/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.scoreboard;

import org.bukkit.scoreboard.Criterias;


/**
 * Playerに表示するスコアの種類
 * @author ucchy
 */
public enum PlayerCriteria {

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
    PlayerCriteria (String criteria) {
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
    public static PlayerCriteria fromString(String criteria) {

        if ( criteria == null ) {
            return PlayerCriteria.NONE;
        }

        for (PlayerCriteria value : PlayerCriteria.values()) {
            if (criteria.equalsIgnoreCase(value.criteria)) {
                return value;
            }
        }

        return PlayerCriteria.NONE;
    }

    /**
     * CraftBukkitのCriteriasから、TabListCriteriaへ変換する
     * @param criteria
     * @return
     */
    public static PlayerCriteria convert(String criteria) {

        if ( criteria == null ) {
            return PlayerCriteria.NONE;
        } else if ( Criterias.PLAYER_KILLS.equals(criteria) ) {
            return PlayerCriteria.KILL_COUNT;
        } else if ( Criterias.DEATHS.equals(criteria) ) {
            return PlayerCriteria.DEATH_COUNT;
        } else if ( Criterias.HEALTH.equals(criteria) ) {
            return PlayerCriteria.HEALTH;
        }
        return PlayerCriteria.NONE;
    }
}
