/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.scoreboard;

import org.bukkit.scoreboard.Objective;

/**
 * 外部連携用スコアインターフェイス
 * @author ucchy
 */
public interface CustomScoreInterface {

    /**
     * 表示が開始されたときに、ColorTeamingから呼び出しされます。
     * @param objective 更新対象のobjective
     */
    public void displayStart(Objective objective);

    /**
     * 表示が終了されたときに、ColorTeamingから呼び出されます。
     * objective を保持したままの場合は、使用しないようにしてください。
     */
    public void displayEnd();

    /**
     * 表示が開始されていて、ColorTeamingの更新タイミング
     * （チーム参加・離脱時、プレイヤー死亡時、ログイン・ログアウト時）
     * に、ColorTeamingから呼び出しされます。
     * @param objective
     */
    public void refreshScore(Objective objective);

    /**
     * サイドバー表示時に使われるタイトルを返すようにしてください。
     * @return サイドバーのタイトル
     */
    public String getTitle();

    /**
     * BelowName表示時に使われる単位を返すようにしてください。
     * @return BelowNameの単位
     */
    public String getUnit();
}
