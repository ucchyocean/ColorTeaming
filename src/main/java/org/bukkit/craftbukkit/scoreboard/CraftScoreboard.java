package org.bukkit.craftbukkit.scoreboard;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.server.v1_5_R2.IScoreboardCriteria;
import net.minecraft.server.v1_5_R2.ScoreboardObjective;
import net.minecraft.server.v1_5_R2.ScoreboardTeam;

import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Objective.Criteria;
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

        return convertToBukkit(team);
    }

    public Team getTeam(String name) {
        ScoreboardTeam team = this.getHandle().getTeam(name);

        if(team != null) {
            return convertToBukkit(team);
        }

        return null;
    }

    public Set<Team> getTeams() {
        Set<Team> result = new HashSet<Team>();

        for (Object team : this.getHandle().getTeams()) {
            result.add(convertToBukkit((ScoreboardTeam)team));
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

    public Objective createObjective(String name, Criteria criteria, String displayName) {
        Validate.notNull(name, "Name can not be null");
        Validate.notNull(criteria, "Criteria can not be null");

        Validate.isTrue(name.length() < 16, "Objective names can only be a maximum of 16 characters long");
        Validate.isTrue(displayName == null || displayName.length() < 32, "Objective display names can only be a maximum of 32 characters long");

        Validate.isTrue(this.getHandle().getObjective(name) == null, "Objective already exists");

        IScoreboardCriteria iobjective = (IScoreboardCriteria) IScoreboardCriteria.a.get(criteria.toString());

        ScoreboardObjective objective = this.getHandle().registerObjective(name, iobjective);

        if(displayName != null) {
            objective.setDisplayName(displayName);
        }

        return convertToBukkit(this.getHandle(), objective);
    }

    public Objective getObjective(String name) {
        ScoreboardObjective objective = this.getHandle().getObjective(name);

        if(objective == null) {
            return null;
        }

        return convertToBukkit(this.getHandle(), objective);
    }

    public Set<Objective> getObjectives() {
        Set<Objective> result = new HashSet<Objective>();

        for (Object objective : this.getHandle().getObjectives()) {
            result.add(convertToBukkit(this.getHandle(), (ScoreboardObjective)objective));
        }

        return result;
    }

    public void removeObjective(Objective objective) {
        Validate.notNull(objective, "Objective can not be null");

        this.getHandle().unregisterObjective(((CraftObjective) objective).getHandle());
    }

    public void resetScores(OfflinePlayer player) {
        Validate.notNull(player, "Player can not be null");

        this.getHandle().resetPlayerScores(player.getName());
    }

    public net.minecraft.server.v1_5_R2.Scoreboard getHandle() {
        return this.handle;
    }

    private Objective convertToBukkit(net.minecraft.server.v1_5_R2.Scoreboard scoreboard, ScoreboardObjective objective) {
        return new CraftObjective(scoreboard, objective);
    }

    private Team convertToBukkit(ScoreboardTeam team) {
        return new CraftTeam(team);
    }
}
