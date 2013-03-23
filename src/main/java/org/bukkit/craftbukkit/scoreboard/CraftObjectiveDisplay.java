package org.bukkit.craftbukkit.scoreboard;

import static org.bukkit.scoreboard.Objective.Display;

import org.apache.commons.lang.Validate;

public class CraftObjectiveDisplay {
    private static int[] displays = new int[Display.values().length];

    static {
        displays[Display.LIST.ordinal()] = 0;
        displays[Display.SIDEBAR.ordinal()] = 1;
        displays[Display.BELOW_NAME.ordinal()] = 2;
    }

    public static int getDisplay(final Display display) {
        Validate.notNull(display, "Display can not be null");
        return displays[display.ordinal()];
    }

    private CraftObjectiveDisplay() {
    }
}
