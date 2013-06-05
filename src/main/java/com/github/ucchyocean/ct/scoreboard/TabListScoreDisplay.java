/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.scoreboard;

import org.bukkit.scoreboard.DisplaySlot;

import com.github.ucchyocean.ct.ColorTeaming;

/**
 * TABキーリストのスコア表示を管理するクラス
 * @author ucchy
 */
public class TabListScoreDisplay extends ScoreDisplayBase {

    private static final String NAME = "listscore";

    private ColorTeaming plugin;

    public TabListScoreDisplay(ColorTeaming plugin) {
        this.plugin = plugin;
    }

    /**
     * @see com.github.ucchyocean.ct.scoreboard.ScoreDisplayBase#getConfigData()
     */
    @Override
    public PlayerCriteria getConfigData() {
        return plugin.getCTConfig().getListCriteria();
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