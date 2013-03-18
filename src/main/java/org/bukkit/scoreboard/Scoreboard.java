package org.bukkit.scoreboard;

import java.util.Set;

import org.bukkit.OfflinePlayer;

public interface Scoreboard {
    /**
     * Create a team
     *
     * @return team
     */
    public Team createTeam(String name, String displayName);

    /**
     * Get a team by name
     *
     * @return team
     */
    public Team getTeam(String name);

    /**
     * Gets all teams
     *
     * @return teams
     */
    public Set<Team> getTeams();

    /**
     * Put a player on a team
     * Can pass null to remove player from current team
     */
    public void setTeam(OfflinePlayer player, Team team);

    public void removeTeam(Team team);


    // Added by ucchy.
    public Team getTeamByPlayer(OfflinePlayer player);

    /*
     * OBJECTIVES
     */

    /**
     * Create an objective
     *
     * @return objective
     */
    public Objective createObjective(String name, Objective.CRITERIA criteria, String displayName);

    /**
     * Get an objective by name
     *
     * @return objective
     */
    public Objective getObjective(String name);

    /**
     * Gets all objectives
     *
     * @return objectives
     */
    public Set<Objective> getObjectives();

    /**
     * Remove an objective
     */
    public void removeObjective(Objective objective);
}
