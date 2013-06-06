/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.scoreboard;

import org.bukkit.scoreboard.DisplaySlot;

import com.github.ucchyocean.ct.ColorTeaming;

/**
 * 名前下のスコア表示を管理するクラス
 * @author ucchy
 */
public class BelowNameScoreDisplay extends ScoreDisplayBase {

    private static final String NAME = "belowscore";

    public BelowNameScoreDisplay(ColorTeaming plugin) {
        super(plugin);
    }

    /**
     * @see com.github.ucchyocean.ct.scoreboard.ScoreDisplayBase#getConfigData()
     */
    @Override
    public PlayerCriteria getConfigData() {
        return plugin.getCTConfig().getBelowCriteria();
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
