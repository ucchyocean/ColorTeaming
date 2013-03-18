package org.bukkit.scoreboard;

import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;

public interface Objective {
    public enum CRITERIA {
        PLAYER_KILL_COUNT("playerKillCount"),
        TOTAL_KILL_COUNT("totalKillCount"),
        DUMMY("dummy"),
        HEALTH("health"),
        DEATH_COUNT("deathCount");

        private final String criteria;

        CRITERIA(String criteria) {
            this.criteria = criteria;
        }

        @Override
        public String toString() {
            return this.criteria;
        }

        public static CRITERIA fromString(String criteria) {
            Validate.notNull(criteria);

            for(CRITERIA value : CRITERIA.values()) {
                if(criteria.equalsIgnoreCase(value.criteria)) {
                    return value;
                }
            }

            throw new IllegalArgumentException("No valid criteria found for " + criteria);
        }
    }

    static enum DISPLAY {
        NONE(null, null),
        LIST("list", 0),
        SIDEBAR("sidebar", 1),
        BELOW_NAME("belowName", 2);

        private final String display;
        private final Integer position;

        DISPLAY(String display, Integer position) {
            this.display = display;
            this.position = position;
        }

        @Override
        public String toString() {
            return this.display;
        }

        public int toInt() {
            return this.position;
        }

        public static DISPLAY fromString(String display) {
            Validate.notNull(display);

            for(DISPLAY value : DISPLAY.values()) {
                if(display.equalsIgnoreCase(value.display)) {
                    return value;
                }
            }

            throw new IllegalArgumentException("No valid display found for " + display);
        }

        public static DISPLAY fromInt(int position) {
            Validate.notNull(position);

            for(DISPLAY value : DISPLAY.values()) {
                if(position == value.position.intValue()) {
                    return value;
                }
            }

            throw new IllegalArgumentException("No valid display found for " + position);
        }
    }

    public String getName();

    public String getDisplayName();

    public void setDisplayName(String displayName);

    public DISPLAY getDisplaySlot();

    public void setDisplaySlot(DISPLAY display);

    public int getScore(OfflinePlayer player);

    /*
     * Set a player's score
     * Passing null will remove the player
     */
    public void setScore(OfflinePlayer player, int score);
}
