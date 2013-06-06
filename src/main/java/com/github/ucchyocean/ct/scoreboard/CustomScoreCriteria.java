/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.scoreboard;

import org.bukkit.scoreboard.Objective;

/**
 * 外部連携用スコアインターフェイス
 * @author ucchy
 */
public interface CustomScoreCriteria {

    /**
     * スコアを更新する必要があるときに、上位から呼び出されるメソッド
     * @param objective 更新対象のobjective
     */
    public void refreshScore(Objective objective);

    /**
     * スコアの単位を返す。BelowNameなどで表示される。
     * @return スコアの単位
     */
    public String getUnit();
}
