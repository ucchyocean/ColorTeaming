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

    public TabListScoreDisplay(ColorTeaming plugin) {
        super(plugin);
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

    /**
     * @see com.github.ucchyocean.ct.scoreboard.ScoreDisplayBase#getCustomScore()
     */
    @Override
    public CustomScoreInterface getCustomScore() {
        String slot = ColorTeaming.instance.getCTConfig().getListCustomSlot();
        CustomScoreInterface custom =
                ColorTeaming.instance.getAPI().getCustomScoreCriteria(slot);
        return custom;
    }
}