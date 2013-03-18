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
public enum TeamCriteria {

    /** キル数 */
    KILL_COUNT,

    /** デス数 */
    DEATH_COUNT,

    /** ポイント */
    POINT,

    /** 残り人数 */
    LEAST_PLAYER,

    /** 非表示 */
    NONE,
}
