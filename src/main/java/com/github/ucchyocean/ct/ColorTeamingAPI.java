/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.github.ucchyocean.ct.scoreboard.CustomScoreCriteria;

/**
 * ColorTeaming APIクラス
 * @author ucchy
 */
public interface ColorTeamingAPI {

    /**
     * スコアボードを返す。
     * @return スコアボード
     */
    public Scoreboard getScoreboard();

    /**
     * Player に設定されている、チームを取得する。
     * @param player プレイヤー
     * @return チーム
     */
    public Team getPlayerTeam(Player player);

    /**
     * Player に設定されている、チームのチーム名を取得する。
     * @param player プレイヤー
     * @return チーム名
     */
    public String getPlayerTeamName(Player player);

    /**
     * Player にチームを設定する。
     * @param player プレイヤー
     * @param color チーム名
     * @return チーム、イベントキャンセルされた場合はnullになることに注意
     */
    public Team addPlayerTeam(Player player, String color);

    /**
     * Player に設定されているチームを削除する。
     * @param player プレイヤー
     */
    public void leavePlayerTeam(Player player);

    /**
     * フレンドリーファイアの設定。<br>
     * NOTE: 本メソッドは、透明可視化が設定されている場合は、
     * 強制的にfalseになることに注意
     * @param ff trueならフレンドリーファイア有効、falseなら無効
     */
    public void setFriendlyFire(boolean ff);

    /**
     * 仲間の可視化の設定。<br>
     * @param fi trueならフレンドリーファイア有効、falseなら無効
     */
    public void setSeeFriendlyInvisibles(boolean fi);

    /**
     * 指定したチーム名のチームを削除する
     * @param name
     */
    public void removeTeam(String name);

    /**
     * 全てのチームを削除する
     */
    public void removeAllTeam();

    /**
     * ユーザーをチームごとのメンバーに整理して返すメソッド
     * @return 色をKey メンバーをValueとした Hashtable
     */
    public HashMap<String, ArrayList<Player>> getAllTeamMembers();

    /**
     * 全てのプレイヤーを取得する
     * @return 全てのプレイヤー
     */
    public ArrayList<Player> getAllPlayers();

    /**
     * 指定したワールドにいる全てのプレイヤーを取得する。
     * ただし、指定したワールドが存在しない場合は、空のリストが返される。
     * @param worldNames 対象にするワールド名
     * @return 全てのプレイヤー
     */
    public ArrayList<Player> getAllPlayersOnWorld(List<String> worldNames);

    /**
     * 全てのチーム名（＝全ての色）を取得する
     * @return 全てのチーム名
     */
    public ArrayList<String> getAllTeamNames();

    /**
     * メッセージをチームチャットに送信する。
     * @param player 送信元プレイヤー
     * @param message 送信するメッセージ
     */
    public void sendTeamChat(Player player, String message);

    /**
     * 情報をチームチャットに送信する。
     * @param color 送信先のチーム
     * @param message 送信するメッセージ
     */
    public void sendInfoToTeamChat(String color, String message);

    /**
     * サイドバーを新しく作る。
     * もともとサイドバーがあった場合は、削除して再作成される。
     */
    public void makeSidebarScore();

    /**
     * サイドバーを消去する。
     */
    public void removeSidebarScore();

    /**
     * サイドバーのスコアを更新する。
     */
    public void refreshSidebarScore();

    /**
     * タブキーリストのスコアを新しく作る。
     * もともとスコアがあった場合は、削除して再作成される。
     */
    public void makeTabkeyListScore();

    /**
     * タブキーリストのスコアを消去する。
     */
    public void removeTabkeyListScore();

    /**
     * タブキーリストのスコアを更新する。
     */
    public void refreshTabkeyListScore();

    /**
     * 名前下のスコアを新しく作る。
     * もともとスコアがあった場合は、削除して再作成される。
     */
    public void makeBelowNameScore();

    /**
     * 名前下のスコアを消去する。
     */
    public void removeBelowNameScore();

    /**
     * 名前下のスコアを更新する。
     */
    public void refreshBelowNameScore();

    /**
     * TeamMemberSaveDataHandler を取得する
     * @return TeamMemberSaveDataHandler
     */
    public TeamMemberSaveDataHandler getCTSaveDataHandler();

    /**
     * キルデス数を全てクリアする
     */
    public void clearKillDeathPoints();

    /**
     * カスタムスコアを取得する
     * @return カスタムスコア
     */
    public CustomScoreCriteria getCustomScoreCriteria();

    /**
     * カスタムスコアを設定する
     * @param score カスタムスコア
     */
    public void setCustomScoreCriteria(CustomScoreCriteria score);

    /**
     * チーム単位のキルデス数を取得する
     * @return キルデス数
     */
    public HashMap<String, int[]> getKillDeathCounts();

    /**
     * ユーザー単位のキルデス数を取得する
     * @return キルデス数
     */
    public HashMap<String, int[]> getKillDeathUserCounts();

    /**
     * リーダー設定を全てクリアする
     */
    public void clearLeaders();

    /**
     * リーダー設定を取得する
     * @return リーダー設定
     */
    public HashMap<String, ArrayList<String>> getLeaders();

    /**
     * リスポーン設定を取得する
     * @return リスポーン設定
     */
    public RespawnConfiguration getRespawnConfig();

    /**
     * TP地点設定を設定する
     * @return TP地点設定
     */
    public TPPointConfiguration getTppointConfig();

    /**
     * リスポーンマップ名を取得する
     * @return リスポーンマップ名
     */
    public String getRespawnMapName();

    /**
     * リスポーンマップ名を設定する
     * @param respawnMapName リスポーンマップ名
     */
    public void setRespawnMapName(String respawnMapName);

}
