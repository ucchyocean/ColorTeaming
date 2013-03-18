/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.scoreboard;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Objective.CRITERIA;
import org.bukkit.scoreboard.Objective.DISPLAY;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.ColorTeamingConfig;
import com.github.ucchyocean.ct.Utility;

/**
 * @author ucchy
 * サイドバーにスコアを表示するためのAPIクラス
 */
public class SidebarDisplay {

    private Objective objective;
    private Hashtable<String, SidebarTeamScore> teamscores;

    public SidebarDisplay() {

        Scoreboard scoreboard = ColorTeaming.getScoreboard();
        objective = scoreboard.createObjective(
                "teamscore", CRITERIA.DUMMY, "チームスコア");

        teamscores = new Hashtable<String, SidebarTeamScore>();

        Hashtable<String, ArrayList<Player>> members = ColorTeaming.getAllTeamMembers();
        Enumeration<String> keys = members.keys();
        while ( keys.hasMoreElements() ) {
            String key = keys.nextElement();
            Team team = scoreboard.getTeam(key);
            if ( team == null ) {
                team = scoreboard.createTeam(
                        key, Utility.replaceColorCode(key) + key + ChatColor.RESET);
            }
            SidebarTeamScore ts = new SidebarTeamScore(team);
            objective.setScore(ts, 0);
            teamscores.put(key, ts);
        }

        objective.setDisplaySlot(DISPLAY.SIDEBAR);

        refreshCriteria();
    }

    public void refreshCriteria() {

        TeamCriteria criteria = ColorTeamingConfig.teamCriteria;

        if ( criteria == TeamCriteria.NONE ) {
            if ( ColorTeaming.sidebar != null ) {
                ColorTeaming.sidebar.remove();
                ColorTeaming.sidebar = null;
                return;
            }
        }

        objective.setDisplayName(
                ChatColor.ITALIC.toString() + ChatColor.BLUE.toString() +
                getSidebarTitle(criteria) + ChatColor.RESET);

        refreshScore();
    }

    public void refreshScore() {

        switch (ColorTeamingConfig.teamCriteria) {
        case KILL_COUNT:
        case DEATH_COUNT:
            refreshScoreByKillOrDeathCount(ColorTeamingConfig.teamCriteria);
            break;
        case POINT:
            refreshScoreByPoint();
            break;
        case LEAST_PLAYER:
            refreshScoreByLeastPlayerCount();
            break;
        }
    }

    private void refreshScoreByKillOrDeathCount(TeamCriteria criteria) {

        int index;
        if ( criteria == TeamCriteria.KILL_COUNT ) {
            index = 0;
        } else {
            index = 1;
        }

        Hashtable<String, ArrayList<Player>> members = ColorTeaming.getAllTeamMembers();
        Enumeration<String> keys = members.keys();
        while ( keys.hasMoreElements() ) {
            String key = keys.nextElement();

            if ( teamscores.containsKey(key) ) {
                SidebarTeamScore team = teamscores.get(key);
                if ( ColorTeaming.killDeathCounts.containsKey(key) ) {
                    int[] data = ColorTeaming.killDeathCounts.get(key);
                    objective.setScore(team, data[index]);
                } else {
                    objective.setScore(team, 0);
                }
            }
        }
    }

    private void refreshScoreByPoint() {

        Hashtable<String, ArrayList<Player>> members = ColorTeaming.getAllTeamMembers();
        Enumeration<String> keys = members.keys();
        while ( keys.hasMoreElements() ) {
            String key = keys.nextElement();

            if ( teamscores.containsKey(key) ) {
                SidebarTeamScore team = teamscores.get(key);
                if ( ColorTeaming.killDeathCounts.containsKey(key) ) {
                    int[] data = ColorTeaming.killDeathCounts.get(key);
                    int point = data[0] * ColorTeamingConfig.killPoint +
                            data[1] * ColorTeamingConfig.deathPoint +
                            data[2] * ColorTeamingConfig.tkPoint;
                    objective.setScore(team, point);
                } else {
                    objective.setScore(team, 0);
                }
            }
        }
    }

    private void refreshScoreByLeastPlayerCount() {

        Hashtable<String, ArrayList<Player>> members = ColorTeaming.getAllTeamMembers();
        Enumeration<String> keys = members.keys();
        while ( keys.hasMoreElements() ) {
            String key = keys.nextElement();

            if ( teamscores.containsKey(key) ) {
                SidebarTeamScore team = teamscores.get(key);
                int least = members.get(key).size();
                objective.setScore(team, least);
            }
        }
    }

    public void remove() {

        objective.setDisplaySlot(DISPLAY.NONE);

        ColorTeaming.getScoreboard().removeObjective(objective);
    }

    private String getSidebarTitle(TeamCriteria criteria) {

        switch (criteria) {
        case KILL_COUNT:
            return "スコア(キル数)";
        case DEATH_COUNT:
            return "スコア(デス数)";
        case POINT:
            return "スコア(ポイント)";
        case LEAST_PLAYER:
            return "残り人数";
        case NONE:
        default:
            return "";
        }
    }
}
