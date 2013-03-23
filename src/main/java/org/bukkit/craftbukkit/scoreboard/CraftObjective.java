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

    public Display getDisplaySlot() {
        for(Display display : Display.values()) {
            if(this.scoreboard.getObjectiveForSlot(CraftObjectiveDisplay.getDisplay(display)) == this.getHandle()) return display;
        }

        return Objective.Display.NONE;
    }

    public void setDisplaySlot(Display display) {
        Validate.notNull(display, "Display slot can not be null");

        if(display == Display.NONE) {
            this.scoreboard.setDisplaySlot(CraftObjectiveDisplay.getDisplay(this.getDisplaySlot()), null);
            return;
        }
        this.scoreboard.setDisplaySlot(CraftObjectiveDisplay.getDisplay(display), this.getHandle());
    }

    public int getScore(OfflinePlayer player) {
        Validate.notNull(player, "Player can not be null");

        return this.scoreboard.getPlayerScoreForObjective(player.getName(), this.getHandle()).getScore();
    }

    public void setScore(OfflinePlayer player, int score) {
        Validate.notNull(player, "Player can not be null");

        this.scoreboard.getPlayerScoreForObjective(player.getName(), this.getHandle()).setScore(score);
    }

    public ScoreboardObjective getHandle() {
        return this.handle;
    }
}
