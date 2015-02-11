/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2014
 */
package com.github.ucchyocean.ct;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.github.ucchyocean.ct.config.TeamNameSetting;

/**
 * オブジェクティブを管理するクラス
 * @author ucchy
 */
public class ObjectiveManager {

    private static final String OBJECTIVE_TEAM_POINT = "CTTeamPoint";
    private static final String OBJECTIVE_TEAM_KILL = "CTTeamKill";
    private static final String OBJECTIVE_TEAM_DEATH = "CTTeamDeath";
    private static final String OBJECTIVE_TEAM_REST = "CTTeamRest";
    private static final String OBJECTIVE_PERSONAL_POINT = "CTPlayerPoint";
    private static final String OBJECTIVE_PERSONAL_KILL = "CTPlayerKill";
    private static final String OBJECTIVE_PERSONAL_DEATH = "CTPlayerDeath";
    private static final String OBJECTIVE_PERSONAL_HEALTH = "CTPlayerHealth";

    private static final String HEART_BLACK = "\u2764";

    private Scoreboard scoreboard;
    private ColorTeamingManager parent;

    /**
     * コンストラクタ
     * @param scoreboard
     */
    protected ObjectiveManager(Scoreboard scoreboard, ColorTeamingManager parent) {
        this.scoreboard = scoreboard;
        this.parent = parent;
        makeAll();
    }

    protected Objective getTeamPointObjective() {
        return getObjective(OBJECTIVE_TEAM_POINT, "", "チームポイント");
    }

    protected Objective getTeamKillObjective() {
        return getObjective(OBJECTIVE_TEAM_KILL, "", "スコア(キル数)");
    }

    protected Objective getTeamDeathObjective() {
        return getObjective(OBJECTIVE_TEAM_DEATH, "", "スコア(デス数)");
    }

    protected Objective getTeamRestObjective() {
        return getObjective(OBJECTIVE_TEAM_REST, "", "チーム人数");
    }

    protected Objective getPersonalPointObjective() {
        return getObjective(OBJECTIVE_PERSONAL_POINT, "", "points");
    }

    protected Objective getPersonalKillObjective() {
        return getObjective(OBJECTIVE_PERSONAL_KILL, Criterias.PLAYER_KILLS, "kills");
    }

    protected Objective getPersonalDeathObjective() {
        return getObjective(OBJECTIVE_PERSONAL_DEATH, Criterias.DEATHS, "deaths");
    }

    protected Objective getPersonalHealthObjective() {
        return getObjective(OBJECTIVE_PERSONAL_HEALTH, Criterias.HEALTH, HEART_BLACK);
    }

    protected void unregisterAll() {

        unregisterObjective(OBJECTIVE_TEAM_POINT);
        unregisterObjective(OBJECTIVE_TEAM_KILL);
        unregisterObjective(OBJECTIVE_TEAM_DEATH);
        unregisterObjective(OBJECTIVE_TEAM_REST);
        unregisterObjective(OBJECTIVE_PERSONAL_POINT);
        unregisterObjective(OBJECTIVE_PERSONAL_KILL);
        unregisterObjective(OBJECTIVE_PERSONAL_DEATH);
        unregisterObjective(OBJECTIVE_PERSONAL_HEALTH);
    }

    private void makeAll() {

        getTeamPointObjective();
        getTeamKillObjective();
        getTeamDeathObjective();
        getTeamRestObjective();
        getPersonalPointObjective();
        getPersonalKillObjective();
        getPersonalDeathObjective();
        getPersonalHealthObjective();
    }

    protected void resetAll() {

        unregisterAll();
        makeAll();
    }

    private Objective getObjective(String name, String criteria, String displayName) {

        Objective objective = scoreboard.getObjective(name);
        if ( objective == null ) {
            objective = scoreboard.registerNewObjective(name, criteria);
            objective.setDisplayName(ChatColor.YELLOW + displayName + ChatColor.RESET);

            // 初期化が必要な項目は、ここで初期化を行う
            if ( name.equals(OBJECTIVE_TEAM_REST) ) {
                initTeamRest(objective);
            } else if ( name.equals(OBJECTIVE_TEAM_POINT) ||
                    name.equals(OBJECTIVE_TEAM_KILL) ||
                    name.equals(OBJECTIVE_TEAM_DEATH) ) {
                initZeroEachTeam(objective);
            } else if ( name.equals(OBJECTIVE_PERSONAL_HEALTH) ) {
                initPersonalHealth(objective);
            }
        }
        return objective;
    }

    private void unregisterObjective(String name) {

        Objective objective = scoreboard.getObjective(name);
        if ( objective != null ) {
            DisplaySlot slot = objective.getDisplaySlot();
            if ( slot != null ) {
                scoreboard.clearSlot(slot);
            }
            objective.unregister();
        }
    }

    private void initTeamRest(Objective objective) {

        HashMap<String, ArrayList<Player>> members = parent.getAllTeamMembers();
        for ( String id : members.keySet() ) {
            TeamNameSetting tns = parent.getTeamNameFromID(id);
            getScore(objective, tns).setScore(members.get(id).size());
        }
    }

    private void initZeroEachTeam(Objective objective) {

        for ( TeamNameSetting tns : parent.getAllTeamNames() ) {
            // NOTE: 初期状態では0が設定されたままの項目は非表示のままになるため、
            // いったん1を設定して項目を表示させる。
            Score score = getScore(objective, tns);
            score.setScore(1);
            score.setScore(0);
        }
    }

    private void initPersonalHealth(Objective objective) {

        for ( Player player : Utility.getOnlinePlayers() ) {
            getScore(objective, player).setScore((int)player.getHealth());
        }
    }

    private static Score getScore(Objective objective, Player player) {

        if ( Utility.isCB178orLater() ) {
            return objective.getScore(player.getName());
        } else {
            @SuppressWarnings("deprecation")
            Score score = objective.getScore(player);
            return score;
        }
    }

    private static Score getScore(Objective objective, TeamNameSetting tns) {

        if ( Utility.isCB178orLater() ) {
            return objective.getScore(tns.toString());
        } else {
            @SuppressWarnings("deprecation")
            Score score = objective.getScore(tns.getScoreItem());
            return score;
        }
    }
}
