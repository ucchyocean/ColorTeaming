/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.scoreboard;

import org.bukkit.scoreboard.DisplaySlot;

import com.github.ucchyocean.ct.ColorTeaming;

/**
 * @author ucchy
 * TABキーリストのスコア表示を管理するクラス
 */
public class TabListScoreDisplay extends ScoreDisplayBase {

    private static final String NAME = "listscore";

    /**
     * @see com.github.ucchyocean.ct.scoreboard.ScoreDisplayBase#getConfigData()
     */
    @Override
    public PlayerCriteria getConfigData() {
        return ColorTeaming.getCTConfig().getListCriteria();
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
        return DisplaySlot.PLAYER_LIST;
    }

    /**
     * @see com.github.ucchyocean.ct.scoreboard.ScoreDisplayBase#refreshDisplayName()
     */
    @Override
    public void refreshDisplayName() {
        // do nothing.
    }
}