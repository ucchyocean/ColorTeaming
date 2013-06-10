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
public interface CustomScoreInterface {

    /**
     * 更新対象のobjectiveが渡される。このメソッドは、コマンドで表示を設定されたときに呼び出される。
     * @param objective 更新対象のobjective
     */
    public void setObjective(Objective objective);

    /**
     * スコアの単位を返す。BelowNameなどで表示される。
     * @return スコアの単位
     */
    public String getUnit();
}
