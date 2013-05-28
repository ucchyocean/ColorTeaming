/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.scoreboard;

import org.bukkit.scoreboard.Criterias;


/**
 * @author ucchy
 * Playerに表示するスコアの種類
 */
public enum PlayerCriteria {

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

    /** カスタム 他のプラグインからの連携用 */
    CUSTOM("custom"),

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
    public static PlayerCriteria convert(Criterias criteria) {

        if ( criteria == null ) {
            return PlayerCriteria.NONE;
        } else if ( Criterias.PLAYER_KILLS.equals(criteria) ) {
            return PlayerCriteria.KILL_COUNT;
        } else if ( Criterias.TOTAL_KILLS.equals(criteria) ) {
            return PlayerCriteria.TOTAL_KILL_COUNT;
        } else if ( Criterias.DEATHS.equals(criteria) ) {
            return PlayerCriteria.DEATH_COUNT;
        } else if ( Criterias.HEALTH.equals(criteria) ) {
            return PlayerCriteria.HEALTH;
        }
        return PlayerCriteria.NONE;
    }

    /**
     * TabListCriteriaから、CraftBukkitのCriteriasへ変換する。
     * @param criteria
     * @return
     */
    public static String convert(PlayerCriteria criteria) {

        switch (criteria) {
        case TOTAL_KILL_COUNT:
            return Criterias.TOTAL_KILLS;
        case HEALTH:
            return Criterias.HEALTH;
        case KILL_COUNT:
        case DEATH_COUNT:
        case POINT:
        case CUSTOM:
        case NONE:
            return ""; // return dummy.
        }
        return "";
    }

    /**
     * 名前下の表示名部分に表示する文字列
     * @return 対応した文字列
     */
    public String getBelowNameTitle() {

        switch (this) {
        case KILL_COUNT:
            return "kill";
        case DEATH_COUNT:
            return "death";
        case POINT:
        case CUSTOM:
            return "point";
        case HEALTH:
            return "/ 20";
        case NONE:
        default:
            return "";
        }
    }
}
