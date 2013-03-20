/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.scoreboard;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Objective.CRITERIA;
import org.bukkit.scoreboard.Objective.DISPLAY;
import org.bukkit.scoreboard.Scoreboard;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.ColorTeamingConfig;

/**
 * @author ucchy
 * TABキーリストのスコア表示を管理するクラス
 */
public class TabListScoreDisplay {

    private Objective objective;

    /**
     * コンストラクタ。コンストラクト時に、TABキーリストのスコアを初期化し表示する。
     */
    public TabListScoreDisplay() {

        Scoreboard scoreboard = ColorTeaming.getScoreboard();
        CRITERIA criteria = TabListCriteria.convert(ColorTeamingConfig.listCriteria);
        objective =
                scoreboard.createObjective("listscore", criteria, "listscore");
        objective.setDisplaySlot(DISPLAY.LIST);

        refreshScore();
    }

    /**
     * スコアを更新する。
     * ただし、POINTを指定しているとき以外は、サーバーが自動更新するので、何もしない。
     */
    public void refreshScore() {

        // NOTE: 独自仕様のPOINT以外は、全て自動更新されるため、
        //       POINT以外の場合は、何もしないで終了する。
        if ( ColorTeamingConfig.listCriteria != TabListCriteria.POINT ) {
            return;
        }

        ArrayList<Player> players = ColorTeaming.getAllPlayers();
        for ( Player player : players ) {

            if ( ColorTeaming.killDeathUserCounts.containsKey(player.getName()) ) {
                int[] data = ColorTeaming.killDeathUserCounts.get(player.getName());
                int point = data[0] * ColorTeamingConfig.killPoint +
                        data[1] * ColorTeamingConfig.deathPoint +
                        data[2] * ColorTeamingConfig.tkPoint;
                objective.setScore(player, point);
            } else {
                objective.setScore(player, 0);
            }
        }
    }

    /**
     * スコア表示を削除する。
     */
    public void remove() {

        objective.setDisplaySlot(DISPLAY.NONE);

        ColorTeaming.getScoreboard().removeObjective(objective);
    }
}
