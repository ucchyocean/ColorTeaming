package org.bukkit.craftbukkit.scoreboard;

import net.minecraft.server.v1_5_R2.Scoreboard;
import net.minecraft.server.v1_5_R2.ScoreboardObjective;

import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Objective;

public class CraftObjective implements Objective {
    private final Scoreboard scoreboard;
    private final ScoreboardObjective handle;

    public CraftObjective(Scoreboard scoreboard, ScoreboardObjective handle) {
        this.scoreboard = scoreboard;
        this.handle = handle;
    }

    public String getName() {
        return this.getHandle().getName();
    }

    public String getDisplayName() {
        return this.getHandle().getDisplayName();
    }

    public void setDisplayName(String displayName) {
        Validate.notNull(displayName, "Display name can not be null");

        this.getHandle().setDisplayName(displayName);
    }

    public DISPLAY getDisplaySlot() {
        if(this.scoreboard.getObjectiveForSlot(DISPLAY.LIST.toInt()) == this.getHandle()) return DISPLAY.LIST;
        if(this.scoreboard.getObjectiveForSlot(DISPLAY.SIDEBAR.toInt()) == this.getHandle()) return DISPLAY.SIDEBAR;
        if(this.scoreboard.getObjectiveForSlot(DISPLAY.BELOW_NAME.toInt()) == this.getHandle()) return DISPLAY.BELOW_NAME;

        return Objective.DISPLAY.NONE;
    }

    public void setDisplaySlot(DISPLAY display) {
        if ( display != null ) {
            this.scoreboard.setDisplaySlot(display.toInt(), this.getHandle());
        }
    }

    public int getScore(OfflinePlayer player) {
        Validate.notNull(player, "Player can not be null");

        return this.scoreboard.getPlayerScoreForObjective(player.getName(), this.getHandle()).getScore();
    }

    public void setScore(OfflinePlayer player, int score) {
        Validate.notNull(player, "Player can not be null");

        this.scoreboard.getPlayerScoreForObjective(player.getName(), this.getHandle()).setScore(score);
    }

    public void removeScore(OfflinePlayer player) {
        Validate.notNull(player, "Player can not be null");

        this.scoreboard.resetPlayerScores(player.getName());
    }

    public ScoreboardObjective getHandle() {
        return this.handle;
    }
}
