package org.bukkit.craftbukkit.scoreboard;

import static org.bukkit.scoreboard.Objective.Criteria;

import org.apache.commons.lang.Validate;

public class CraftObjectiveCriteria {
    private static String[] criterias = new String[Criteria.values().length];

    static {
        criterias[Criteria.PLAYER_KILL_COUNT.ordinal()] = "playerKillCount";
        criterias[Criteria.TOTAL_KILL_COUNT.ordinal()] = "totalKillCount";
        criterias[Criteria.DUMMY.ordinal()] = "dummy";
        criterias[Criteria.HEALTH.ordinal()] = "health";
        criterias[Criteria.DEATH_COUNT.ordinal()] = "deathCount";
    }

    public static String getCriteria(final Criteria criteria) {
        Validate.notNull(criteria, "Criteria can not be null");
        return criterias[criteria.ordinal()];
    }

    private CraftObjectiveCriteria() {
    }
}
