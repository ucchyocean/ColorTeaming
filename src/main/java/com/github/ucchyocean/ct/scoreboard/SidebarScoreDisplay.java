/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.ColorTeamingConfig;

/**
 * サイドバーにスコアを表示するためのAPIクラス
 * @author ucchy
 */
public class SidebarScoreDisplay {

    private Objective objective;
    private Hashtable<String, SidebarTeamScore> teamscores;

    private ColorTeaming plugin;

    /**
     * コンストラクタ。コンストラクト時に、現在のチーム状況を取得し、
     * サイドバーを初期化、表示する。
     */
    public SidebarScoreDisplay(ColorTeaming plugin) {

        this.plugin = plugin;

        // Scoreboardからobjective取得。null の場合は再作成する。
        Scoreboard scoreboard = plugin.getAPI().getScoreboard();
        objective = scoreboard.getObjective("teamscore");
        if ( objective == null ) {
            objective = scoreboard.registerNewObjective("teamscore", "");
            objective.setDisplayName("チームスコア");
        }
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // 項目を初期化
        teamscores = new Hashtable<String, SidebarTeamScore>();

        HashMap<String, ArrayList<Player>> members = plugin.getAPI().getAllTeamMembers();
        for ( String key : members.keySet() ) {
            Team team = scoreboard.getTeam(key);
            SidebarTeamScore ts = new SidebarTeamScore(team);
            objective.getScore(ts).setScore(0);
            teamscores.put(key, ts);
        }

        // スコアを消去
        // NOTE: 全部0を設定すると非表示になってしまうので、1を設定してから0を設定する
        for ( String key : teamscores.keySet() ) {
            objective.getScore(teamscores.get(key)).setScore(1);
            objective.getScore(teamscores.get(key)).setScore(0);
        }

        refreshCriteria();
    }

    /**
     * サイドバーのクライテリアを、ColorTeamingConfigから取得し、更新する。
     */
    private void refreshCriteria() {

        SidebarCriteria criteria = plugin.getCTConfig().getSideCriteria();

        if ( criteria == SidebarCriteria.NONE ) {
            plugin.getAPI().removeSidebarScore();
            return;
        }

        objective.setDisplayName(
                ChatColor.ITALIC.toString() + ChatColor.YELLOW.toString() +
                criteria.getSidebarTitle() + ChatColor.RESET);

        refreshScore();
    }

    /**
     * スコアを再取得し、表示更新する。
     * スコアが更新されるタイミング（プレイヤー死亡時、ログアウト時）に、
     * 本メソッドを呼び出してスコア表示を更新すること。
     */
    public void refreshScore() {

        switch (plugin.getCTConfig().getSideCriteria()) {
        case KILL_COUNT:
            refreshScoreByKillOrDeathCount(SidebarCriteria.KILL_COUNT);
            break;
        case DEATH_COUNT:
            refreshScoreByKillOrDeathCount(SidebarCriteria.DEATH_COUNT);
            break;
        case POINT:
            refreshScoreByPoint();
            break;
        case REST_PLAYER:
            refreshScoreByRestPlayerCount();
            break;
        case NONE:
            break; // do nothing.
        }
    }

    /**
     * キル数、または、デス数による、スコア更新を行う
     * @param criteria
     */
    private void refreshScoreByKillOrDeathCount(SidebarCriteria criteria) {

        int index;
        if ( criteria == SidebarCriteria.KILL_COUNT ) {
            index = 0;
        } else {
            index = 1;
        }

        HashMap<String, ArrayList<Player>> members =
                plugin.getAPI().getAllTeamMembers();
        HashMap<String, int[]> killDeathCounts =
                plugin.getAPI().getKillDeathCounts();
        for ( String key : members.keySet() ) {
            if ( teamscores.containsKey(key) ) {
                SidebarTeamScore team = teamscores.get(key);
                if ( killDeathCounts.containsKey(key) ) {
                    int[] data = killDeathCounts.get(key);
                    objective.getScore(team).setScore(data[index]);
                } else {
                    objective.getScore(team).setScore(0);
                }
            }
        }
    }

    /**
     * ポイントによるスコア更新を行う
     */
    private void refreshScoreByPoint() {

        ColorTeamingConfig config = plugin.getCTConfig();
        HashMap<String, ArrayList<Player>> members =
                plugin.getAPI().getAllTeamMembers();
        HashMap<String, int[]> killDeathCounts =
                plugin.getAPI().getKillDeathCounts();
        for ( String key : members.keySet() ) {
            if ( teamscores.containsKey(key) ) {
                SidebarTeamScore team = teamscores.get(key);
                if ( killDeathCounts.containsKey(key) ) {
                    int[] data = killDeathCounts.get(key);
                    int point = data[0] * config.getKillPoint() +
                                data[1] * config.getDeathPoint() +
                                data[2] * config.getTkPoint();
                    objective.getScore(team).setScore(point);
                } else {
                    objective.getScore(team).setScore(0);
                }
            }
        }
    }

    /**
     * 残り人数によるスコア更新を行う
     */
    private void refreshScoreByRestPlayerCount() {
        HashMap<String, ArrayList<Player>> members =
                plugin.getAPI().getAllTeamMembers();
        for ( String key : members.keySet() ) {
            if ( teamscores.containsKey(key) ) {
                SidebarTeamScore team = teamscores.get(key);
                int rest = members.get(key).size();
                objective.getScore(team).setScore(rest);
            }
        }
    }

    /**
     * サイドバーの表示を消去する。
     */
    public void remove() {
        if ( plugin.getAPI().getScoreboard().getObjective("teamscore") != null ) {
            objective.unregister();
        }
    }
}
