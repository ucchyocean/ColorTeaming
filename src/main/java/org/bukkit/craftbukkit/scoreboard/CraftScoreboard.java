package org.bukkit.craftbukkit.scoreboard;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.server.v1_5_R2.IScoreboardCriteria;
import net.minecraft.server.v1_5_R2.ScoreboardObjective;
import net.minecraft.server.v1_5_R2.ScoreboardTeam;

import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Objective.CRITERIA;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class CraftScoreboard implements Scoreboard {

    private final net.minecraft.server.v1_5_R2.Scoreboard handle;

    public CraftScoreboard(net.minecraft.server.v1_5_R2.Scoreboard scoreboard) {
        this.handle = scoreboard;
    }

    public Team createTeam(String name, String displayName) {
        Validate.notNull(name, "Name can not be null");

        Validate.isTrue(name.length() < 16, "Team names can only be a maximum of 16 characters long");
        Validate.isTrue(displayName == null || displayName.length() < 32, "Team display names can only be a maximum of 32 characters long");
        Validate.isTrue(this.getHandle().getTeam(name) == null, "Team " + name + " already exists");

        ScoreboardTeam team = this.getHandle().createTeam(name);
        if(displayName != null) {
            team.setDisplayName(displayName);
        }

        return new CraftTeam(team);
    }

    public Team getTeam(String name) {
        ScoreboardTeam team = this.getHandle().getTeam(name);

        if(team != null) {
            return new CraftTeam(team);
        }

        return null;
    }

    public Set<Team> getTeams() {
        Set<Team> result = new HashSet<Team>();

        for (Object team : this.getHandle().getTeams()) {
            result.add(new CraftTeam((ScoreboardTeam) team));
        }

        return result;
    }

    public void setTeam(OfflinePlayer player, Team team) {
        Validate.notNull(player, "Player can not be null");

        if(team == null) {
            this.getHandle().removePlayerFromTeam(player.getName());
        } else {
            this.getHandle().addPlayerToTeam(player.getName(), ((CraftTeam) team).getHandle());
        }
    }

    public void removeTeam(Team team) {
        Validate.notNull(team, "Team can not be null");

        this.getHandle().removeTeam(((CraftTeam) team).getHandle());
    }

    public Objective createObjective(String name, CRITERIA criteria, String displayName) {
        Validate.notNull(name, "Name can not be null");
        Validate.notNull(criteria, "Criteria can not be null");

        Validate.isTrue(name.length() < 16, "Objective names can only be a maximum of 16 characters long");
        Validate.isTrue(displayName == null || displayName.length() < 32, "Objective display names can only be a maximum of 32 characters long");

        Validate.isTrue(this.getHandle().getObjective(name) == null, "Objective already exists");

        IScoreboardCriteria isc = criteria.toIScoreboardCriteria();
        ScoreboardObjective objective = this.getHandle().registerObjective(name, isc);

        if(displayName != null) {
            objective.setDisplayName(displayName);
        }

        return new CraftObjective(getHandle(), objective);
    }

    public Objective getObjective(String name) {
        ScoreboardObjective objective = this.getHandle().getObjective(name);

        if(objective == null) {
            return null;
        }

        return new CraftObjective(getHandle(), objective);
    }

    public Set<Objective> getObjectives() {
        Set<Objective> result = new HashSet<Objective>();

        for (Object objective : this.getHandle().getObjectives()) {
            result.add(new CraftObjective(getHandle(), (ScoreboardObjective)objective));
        }

        return result;
    }

    public void removeObjective(Objective objective) {
        Validate.notNull(objective, "Objective can not be null");

        this.getHandle().registerObjective(objective.getName(), null);
    }

    public net.minecraft.server.v1_5_R2.Scoreboard getHandle() {
        return this.handle;
    }

    public Team getTeamByPlayer(OfflinePlayer player) {
        ScoreboardTeam team = this.getHandle().getPlayerTeam(player.getName());
        if ( team != null )
            return new CraftTeam(team);
        else
            return null;
    }
}
