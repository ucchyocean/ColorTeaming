/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.scoreboard;

import org.bukkit.scoreboard.DisplaySlot;

import com.github.ucchyocean.ct.ColorTeamingConfig;

/**
 * @author ucchy
 * 名前下のスコア表示を管理するクラス
 */
public class BelowNameScoreDisplay extends ScoreDisplayBase {

    private static final String NAME = "belowscore";

    /**
     * @see com.github.ucchyocean.ct.scoreboard.ScoreDisplayBase#getConfigData()
     */
    @Override
    public PlayerCriteria getConfigData() {
        return ColorTeamingConfig.belowCriteria;
    }

    /**
     * @see com.github.ucchyocean.ct.scoreboard.ScoreDisplayBase#getObjectiveName()
     */
    @Override
    public String getObjectiveName() {
        return NAME;
    }

    /**
     * @see com.github.ucchyocean.ct.scoreboard.ScoreDisplayBase#getDisplaySlot()
     */
    @Override
    public DisplaySlot getDisplaySlot() {
        return DisplaySlot.BELOW_NAME;
    }

    /**
     * @see com.github.ucchyocean.ct.scoreboard.ScoreDisplayBase#refreshDisplayName()
     */
    @Override
    public void refreshDisplayName() {
        String title = getConfigData().getBelowNameTitle();
        objective.setDisplayName(title);
    }
}
