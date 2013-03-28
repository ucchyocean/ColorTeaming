package org.bukkit.craftbukkit.scoreboard;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.server.v1_5_R2.ScoreboardTeam;

import org.apache.commons.lang.Validate;
import org.bukkit.scoreboard.Team;

public class CraftTeam implements Team {
    private final ScoreboardTeam handle;

    public CraftTeam(ScoreboardTeam handle) {
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

    public String getPrefix() {
        return this.getHandle().getPrefix();
    }

    public void setPrefix(String prefix) {
        Validate.notNull(prefix, "Prefix can not be null");

        this.getHandle().setPrefix(prefix);
    }

    public String getSuffix() {
        return this.getHandle().getSuffix();
    }

    public void setSuffix(String suffix) {
        Validate.notNull(suffix, "Suffix can not be null");

        this.getHandle().setSuffix(suffix);
    }

    public boolean allowFriendlyFire() {
        return this.getHandle().allowFriendlyFire();
    }

    public void setAllowFriendlyFire(boolean friendlyFire) {
        this.getHandle().setAllowFriendlyFire(friendlyFire);
    }

    public boolean canSeeFriendlyInvisibles() {
        return this.getHandle().canSeeFriendlyInvisibles();
    }

    public void setSeeFriendlyInvisibles(boolean seeFriendlyInvisibles) {
        this.getHandle().setCanSeeFriendlyInvisibles(seeFriendlyInvisibles);
    }

    public ScoreboardTeam getHandle() {
        return this.handle;
    }

    public ArrayList<String> getPlayerNames() {
        Collection<?> names = this.getHandle().getPlayerNameSet();
        ArrayList<String> result = new ArrayList<String>();
        for ( Object name : names ) {
            result.add(name.toString());
        }
        return result;
    }
}
