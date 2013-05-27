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
public interface CTScoreInterface {

    public void refreshScore(Objective objective);
}
