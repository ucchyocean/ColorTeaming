/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.scoreboard;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.github.ucchyocean.ct.ColorTeaming;

/**
 * @author ucchy
 *
 */
public abstract class ScoreDisplayBase {

    protected Objective objective;
    private CTScoreInterface customScore;

    /**
     * コンストラクタ。
     */
    public ScoreDisplayBase() {

        Scoreboard scoreboard = ColorTeaming.instance.getScoreboard();
        String criteria = PlayerCriteria.convert(getConfigData());

        objective = scoreboard.getObjective(getObjectiveName());
        if ( objective == null ) {
            objective = scoreboard.registerNewObjective(getObjectiveName(), criteria);
        } else {
            // スコアを消去して使いまわす
            for ( Player player : ColorTeaming.instance.getAllPlayers() ) {
                objective.getScore(player).setScore(0);
            }
        }

        refreshDisplayName();

        objective.setDisplaySlot(getDisplaySlot());

        refreshScore();
    }

    /**
     * スコアを更新する。
     */
    public void refreshScore() {

        // NOTE: Health、TotalKillは、自動更新させる。Noneは何もしない。
        if ( getConfigData() == PlayerCriteria.HEALTH ||
                getConfigData() == PlayerCriteria.TOTAL_KILL_COUNT ||
                getConfigData() == PlayerCriteria.NONE ) {
            return;
        }

        // Customは、customScoreの更新メソッドを呼び出す。
        if ( getConfigData() == PlayerCriteria.CUSTOM ) {
            if ( customScore != null ) {
                customScore.refreshScore(objective);
            }
            return;
        }

        ArrayList<Player> players = ColorTeaming.instance.getAllPlayers();
        for ( Player player : players ) {

            int point = 0;
            if ( ColorTeaming.killDeathUserCounts.containsKey(player.getName()) ) {
                int[] data = ColorTeaming.killDeathUserCounts.get(player.getName());
                if ( getConfigData() == PlayerCriteria.KILL_COUNT ) {
                    point = data[0];
                } else if ( getConfigData() == PlayerCriteria.DEATH_COUNT ) {
                    point = data[1];
                } else if ( getConfigData() == PlayerCriteria.POINT ) {
                    point = data[0] * ColorTeaming.instance.getCTConfig().getKillPoint() +
                            data[1] * ColorTeaming.instance.getCTConfig().getDeathPoint() +
                            data[2] * ColorTeaming.instance.getCTConfig().getTkPoint();
                }
            }
            if ( point == 0 ) {
                // NOTE: 0 を設定すると項目が消えるので、まず1を設定する
                objective.getScore(player).setScore(1);
            }
            objective.getScore(player).setScore(point);
        }
    }

    /**
     * スコア表示を削除する。
     */
    public void remove() {
        if ( ColorTeaming.instance.getScoreboard().getObjective(getObjectiveName()) != null ) {
            objective.unregister();
        }
    }

    public void setCustomScore(CTScoreInterface customScore) {
        this.customScore = customScore;
    }

    public abstract PlayerCriteria getConfigData();

    public abstract String getObjectiveName();

    public abstract DisplaySlot getDisplaySlot();

    public abstract void refreshDisplayName();
}
