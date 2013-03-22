package org.bukkit.scoreboard;

import java.util.ArrayList;

import org.bukkit.ChatColor;

public interface Team {

    public String getName();

    public String getDisplayName();

    public void setDisplayName(String name);

    // Added by ucchy.
    public ArrayList<String> getMemberNames();

    /*
     * OPTIONS
     */
    public ChatColor getColor();

    public void setColor(ChatColor color);

    public boolean getFriendlyFire();

    public void setFriendlyFire(boolean friendlyfire);

    public boolean getSeeFriendlyInvisibles();

    public void setSeeFriendlyInvisibles(boolean seeFriendlyInvisibles);
}
