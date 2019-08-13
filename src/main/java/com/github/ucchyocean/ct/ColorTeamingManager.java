/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.github.ucchyocean.ct.bridge.VaultChatBridge;
import com.github.ucchyocean.ct.config.ClassData;
import com.github.ucchyocean.ct.config.ColorTeamingConfig;
import com.github.ucchyocean.ct.config.ColorTeamingMessages;
import com.github.ucchyocean.ct.config.NametagVisibilityEnum;
import com.github.ucchyocean.ct.config.RespawnConfiguration;
import com.github.ucchyocean.ct.config.TPPointConfiguration;
import com.github.ucchyocean.ct.config.TeamNameConfig;
import com.github.ucchyocean.ct.config.TeamNameSetting;
import com.github.ucchyocean.ct.config.TeamOptionStatusEnum;
import com.github.ucchyocean.ct.event.ColorTeamingKillDeathClearedEvent;
import com.github.ucchyocean.ct.event.ColorTeamingPlayerAddEvent;
import com.github.ucchyocean.ct.event.ColorTeamingPlayerLeaveEvent;
import com.github.ucchyocean.ct.event.ColorTeamingPlayerLeaveEvent.Reason;
import com.github.ucchyocean.ct.event.ColorTeamingTeamChatEvent;
import com.github.ucchyocean.ct.event.ColorTeamingTeamCreateEvent;
import com.github.ucchyocean.ct.event.ColorTeamingTeamRemoveEvent;
import com.github.ucchyocean.ct.event.ColorTeamingTeamscoreChangeEvent;
import com.github.ucchyocean.ct.item.CustomItem;

/**
 * ColorTeamingAPIの実体クラス
 * @author ucchy
 */
public class ColorTeamingManager implements ColorTeamingAPI {

    private ColorTeaming plugin;
    private ColorTeamingConfig config;
    private VaultChatBridge vaultchat;

    private Scoreboard scoreboard;
    private ObjectiveManager objectives;

    private RespawnConfiguration respawnConfig;
    private TPPointConfiguration tppointConfig;
    private TeamNameConfig teamNameConfig;

    private HashMap<String, ArrayList<String>> leaders;

    private String respawnMapName;

    private HashMap<String, ClassData> classDatas;

    private File debugLogFile;

    /**
     * コンストラクタ
     * @param plugin
     * @param config
     * @param vaultchat
     */
    public ColorTeamingManager(ColorTeaming plugin,
            ColorTeamingConfig config, VaultChatBridge vaultchat) {

        this.plugin = plugin;
        this.config = config;
        this.vaultchat = vaultchat;
        this.scoreboard = plugin.getServer().getScoreboardManager().getMainScoreboard();

        // 変数の初期化
        leaders = new HashMap<String, ArrayList<String>>();
        respawnConfig = new RespawnConfiguration();
        tppointConfig = new TPPointConfiguration();
        teamNameConfig = new TeamNameConfig();
        classDatas = ClassData.loadAllClasses(new File(plugin.getDataFolder(), "classes"));
        objectives = new ObjectiveManager(scoreboard, this);
    }

    /**
     * 指定されたチームIDが存在するかどうかを返す。
     * @param id チームID
     * @return 存在するかどうか
     */
    @Override
    public boolean isExistTeam(String id) {

        writeDebugLog("isExistTeam start. " + id);
        long start = System.currentTimeMillis();

        Set<Team> teams = scoreboard.getTeams();
        for ( Team team : teams ) {
            if ( team.getName().equals(id) ) {
                writeDebugLog("isExistTeam end. : " + (System.currentTimeMillis() - start));
                return true;
            }
        }
        writeDebugLog("isExistTeam end. " + (System.currentTimeMillis() - start));
        return false;
    }

    /**
     * チーム名をチームIDから取得する。
     * @param id チームID
     * @return チーム名
     */
    @Override
    public TeamNameSetting getTeamNameFromID(String id) {

        return teamNameConfig.getTeamNameFromID(id);
    }

    /**
     * Player に設定されている、チームを取得する。
     * @param player プレイヤー
     * @return チーム
     */
    @Override
    public Team getPlayerTeam(Player player) {

        writeDebugLog("getPlayerTeam start. " + player);
        long start = System.currentTimeMillis();

        if ( Utility.isCB186orLater() ) {
            Team team = scoreboard.getEntryTeam(player.getName());
            writeDebugLog("getPlayerTeam - CB186 API getEntryTeam was selected.");
            writeDebugLog("getPlayerTeam end. : " + (System.currentTimeMillis() - start));
            return team;
        }

        @SuppressWarnings("deprecation")
        Team team = scoreboard.getPlayerTeam(player);
        writeDebugLog("getPlayerTeam end. : " + (System.currentTimeMillis() - start));
        return team;
    }

    /**
     * Player に設定されている、チームのチーム名を取得する。
     * @param player プレイヤー
     * @return チーム名
     */
    @Override
    public TeamNameSetting getPlayerTeamName(Player player) {

        Team team = getPlayerTeam(player);
        if ( team == null ) return null;
        else return teamNameConfig.getTeamNameFromID(team.getName());
    }

    /**
     * Player にチームを設定する。
     * @param player プレイヤー
     * @param teamName チーム名
     * @return チーム、イベントキャンセルされた場合はnullになることに注意
     */
    @SuppressWarnings("deprecation")
    @Override
    public Team addPlayerTeam(Player player, TeamNameSetting teamName) {

        writeDebugLog("addPlayerTeam start. " + player + ", " + teamName);
        long start = System.currentTimeMillis();

        String id = teamName.getID();
        String name = teamName.getName();
        ChatColor color = teamName.getColor();

        Team team = scoreboard.getTeam(id);
        if ( team == null ) {

            // イベントコール
            ColorTeamingTeamCreateEvent event =
                    new ColorTeamingTeamCreateEvent(teamName);
            Utility.callEventSync(event);
            if ( event.isCancelled() ) {
                return null;
            }

            team = scoreboard.registerNewTeam(id);
            team.setDisplayName(color + name + ChatColor.RESET);
            team.setPrefix(color.toString());
            team.setSuffix(ChatColor.RESET.toString());
            team.setCanSeeFriendlyInvisibles(config.isCanSeeFriendlyInvisibles());
            team.setAllowFriendlyFire(config.isFriendlyFire());

            if ( Utility.isCB19orLater() ) {
                // CB1.9 以上の固有設定
                team.setOption(Team.Option.NAME_TAG_VISIBILITY,
                        config.getNametagVisibility().getBukkitOptionStatus());
                team.setOption(Team.Option.COLLISION_RULE,
                        config.getCollisionRule().getBukkit());
                team.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY,
                        config.getDeathMessageVisibility().getBukkit());

            } else if ( Utility.isCB18orLater() ) {
                // CB1.8 の固有設定
                team.setNameTagVisibility(config.getNametagVisibility().getBukkit());

            }
        }

        // イベントコール
        ColorTeamingPlayerAddEvent event =
                new ColorTeamingPlayerAddEvent(player, team);
        Utility.callEventSync(event);
        if ( event.isCancelled() ) {
            return null;
        }

        // チームに所属させる
        addPlayerInternal(team, player);
        player.setDisplayName(color + player.getName() + ChatColor.RESET);

        // パーミッションを設定する
        plugin.addMemberPermission(player, id);

        // 該当プレイヤーに通知
        String msg = ColorTeamingMessages.getJoinTeamMessage(teamName.toString());
        if ( msg != null ) {
            player.sendMessage(msg);
        }

        writeDebugLog("addPlayerTeam end. : " + (System.currentTimeMillis() - start));
        return team;
    }

    /**
     * Player に設定されているチームを削除する。
     * @param player プレイヤー
     * @param reason 離脱理由
     */
    @Override
    public void leavePlayerTeam(Player player, Reason reason) {

        writeDebugLog("leavePlayerTeam start. " + player + ", " + reason);
        long start = System.currentTimeMillis();

        Team team = getPlayerTeam(player);
        if ( team != null ) {

            // イベントコール
            ColorTeamingPlayerLeaveEvent event =
                    new ColorTeamingPlayerLeaveEvent(player, team, reason);
            Utility.callEventSync(event);
            if ( event.isCancelled() ) {
                return;
            }

            // チーム所属に所属していたプレイヤーは、ここで体力最大値を20に戻す
            if ( config.isResetMaxHealthOnDeath() ) {
                player.setMaxHealth(20);
            }

            // チーム脱退
            removePlayerInternal(team, player);

            // パーミッションを削除する
            plugin.removeAllMemberPermission(player);

            // チーム削除により呼び出されたのでなければ、メンバー0人でチーム削除する
            if ( reason != Reason.TEAM_REMOVED && getPlayersInternal(team).size() == 0 ) {
                removeTeam(team.getName());
            }
        }

        player.setDisplayName(player.getName());

        writeDebugLog("leavePlayerTeam end. : " + (System.currentTimeMillis() - start));
    }

    /**
     * フレンドリーファイアの設定。
     * @param ff trueならフレンドリーファイア有効、falseなら無効
     */
    @Override
    public void setFriendlyFire(boolean ff) {

        Set<Team> teams = scoreboard.getTeams();
        for ( Team t : teams ) {
            t.setAllowFriendlyFire(ff);
        }

        config.setFriendlyFire(ff);
        config.saveConfig();
    }

    /**
     * 仲間の可視化の設定。
     * @param fi trueなら仲間の可視化有効、falseなら無効
     */
    @Override
    public void setSeeFriendlyInvisibles(boolean fi) {

        Set<Team> teams = scoreboard.getTeams();
        for ( Team t : teams ) {
            t.setCanSeeFriendlyInvisibles(fi);
        }

        config.setCanSeeFriendlyInvisibles(fi);
        config.saveConfig();
    }

    /**
     * 指定したチームIDのチームを削除する
     * @param id チームID
     * @return 削除したかどうか（イベントでキャンセルされた場合はfalseになる）
     */
    @Override
    public boolean removeTeam(String id) {

        writeDebugLog("removeTeam start. " + id);
        long start = System.currentTimeMillis();

        TeamNameSetting teamName = teamNameConfig.getTeamNameFromID(id);

        // イベントコール
        ColorTeamingTeamRemoveEvent event =
                new ColorTeamingTeamRemoveEvent(teamName);
        Utility.callEventSync(event);
        if ( event.isCancelled() ) {
            return false;
        }

        Team team = scoreboard.getTeam(id);
        if ( team != null ) {
            for ( Player player : getPlayersInternal(team) ) {
                leavePlayerTeam(player.getPlayer(), Reason.TEAM_REMOVED);
            }
            team.unregister();
        }
        writeDebugLog("removeTeam end. : " + (System.currentTimeMillis() - start));
        return true;
    }

    /**
     * 全てのチームを削除する
     */
    @Override
    public void removeAllTeam() {

        writeDebugLog("removeAllTeam start.");
        long start = System.currentTimeMillis();

        for ( TeamNameSetting tns : getAllTeamNames() ) {
            Team team = scoreboard.getTeam(tns.getID());
            if ( team != null ) {
                removeTeam(team.getName());
            }
        }

        // チームリーダー設定を削除する
        clearLeaders();

        writeDebugLog("removeAllTeam end. : " + (System.currentTimeMillis() - start));
    }

    /**
     * ユーザーをチームごとのメンバーに整理して返すメソッド
     * @return チームIDをKey メンバーをValueとした HashMap
     */
    @Override
    public HashMap<String, ArrayList<Player>> getAllTeamMembers() {

        writeDebugLog("getAllTeamMembers start.");
        long start = System.currentTimeMillis();

        ArrayList<TeamNameSetting> teamNames = getAllTeamNames();
        HashMap<String, ArrayList<Player>> result =
                new HashMap<String, ArrayList<Player>>();

        for ( TeamNameSetting tns : teamNames ) {
            result.put(tns.getID(), getTeamMembers(tns.getID()));
        }

        writeDebugLog("getAllTeamMembers end. : " + (System.currentTimeMillis() - start));
        return result;
    }

    /**
     * チームメンバーを取得する
     * @param id チームID
     * @return チームメンバー。チームが存在しない場合はnullが返されることに注意
     */
    @Override
    public ArrayList<Player> getTeamMembers(String id) {

        writeDebugLog("getTeamMembers start. " + id);
        long start = System.currentTimeMillis();

        Team team = scoreboard.getTeam(id);

        if ( team == null ) {
            writeDebugLog("getTeamMembers end. : " + (System.currentTimeMillis() - start));
            return null;
        }

        ArrayList<Player> players = new ArrayList<Player>();
        for ( Player player : getPlayersInternal(team) ) {
            players.add(player.getPlayer());
        }

        writeDebugLog("getTeamMembers end. : " + (System.currentTimeMillis() - start));
        return players;
    }

    /**
     * 指定したワールドにいる全てのプレイヤーを取得する。
     * ただし、指定したワールドが存在しない場合は、空のリストが返される。
     * @param worldNames 対象にするワールド名
     * @return 全てのプレイヤー
     */
    @Override
    public ArrayList<Player> getAllPlayersOnWorld(List<String> worldNames) {

        writeDebugLog("getAllPlayersOnWorld start. ");
        long start = System.currentTimeMillis();

        if ( worldNames == null ) {
            writeDebugLog("getAllPlayersOnWorld end. : " + (System.currentTimeMillis() - start));
            return null;
        }

        ArrayList<Player> result = new ArrayList<Player>();
        for ( Player p : Utility.getOnlinePlayers() ) {
            if ( worldNames.contains(p.getWorld().getName()) ) {
                result.add(p);
            }
        }

        writeDebugLog("getAllPlayersOnWorld end. : " + (System.currentTimeMillis() - start));
        return result;
    }

    /**
     * 全てのチーム名を取得する
     * @return 全てのチーム名
     */
    @Override
    public ArrayList<TeamNameSetting> getAllTeamNames() {

        writeDebugLog("getAllTeamNames start. ");
        long start = System.currentTimeMillis();

        ArrayList<TeamNameSetting> result = new ArrayList<TeamNameSetting>();

        for ( TeamNameSetting tns : teamNameConfig.getTeamNames() ) {
            Team team = scoreboard.getTeam(tns.getID());
            if ( team != null ) {
                result.add(tns);
            }
        }

        writeDebugLog("getAllTeamNames end. : " + (System.currentTimeMillis() - start));
        return result;
    }

    /**
     * メッセージをチームチャットに送信する。
     * @param player 送信元プレイヤー
     * @param message 送信するメッセージ
     */
    @Override
    public void sendTeamChat(Player player, String message) {

        writeDebugLog("sendTeamChat start. " + player + ", " + message);
        long start = System.currentTimeMillis();

        // チームを取得する
        Team team = getPlayerTeam(player);
        if ( team == null ) {
            return;
        }

        sendTeamChat(player, team.getName(), message);

        writeDebugLog("sendTeamChat end. : " + (System.currentTimeMillis() - start));
    }

    /**
     * 情報をチームチャットに送信する。
     * @param sender 送信者
     * @param team 送信先のチームID
     * @param message 送信するメッセージ
     */
    @Override
    public void sendTeamChat(CommandSender sender, String team, String message) {

        writeDebugLog("sendTeamChat start. " + sender + ", " + team + ", " + message);
        long start = System.currentTimeMillis();

        // チームを取得する
        Team t = scoreboard.getTeam(team);
        if ( t == null ) {
            return;
        }

        // 設定に応じて、Japanize化する
        if ( sender instanceof Player && config.isShowJapanizeTeamChat() ) {
            // 2byteコードを含む場合や、半角カタカナしか含まない場合は、
            // 処理しないようにする。
            if ( message.getBytes().length == message.length() &&
                    !message.matches("[ \\uFF61-\\uFF9F]+") ) {
                String kana = KanaConverter.conv(message);
                message = message + " (" + kana + ")";
            }
        }

        // イベントコール
        ColorTeamingTeamChatEvent event =
                new ColorTeamingTeamChatEvent(sender, message, t);
        Utility.callEventSync(event);
        if ( event.isCancelled() ) {
            return;
        }
        message = event.getMessage();

        // キーワード生成
        String teamName = t.getDisplayName();
        String playerName = "";
        String prefix = "";
        String suffix = "";
        if ( sender instanceof Player ) {
            Player player = (Player)sender;
            playerName = player.getDisplayName();
            if ( vaultchat != null ) {
                prefix = vaultchat.getPlayerPrefix(player);
                suffix = vaultchat.getPlayerSuffix(player);
            }
        } else {
            if ( sender != null ) {
                playerName = sender.getName();
            }
        }

        // メッセージを生成
        String partyMessage = config.getTeamChatFormat();
        partyMessage = partyMessage.replace("%team", teamName);
        partyMessage = partyMessage.replace("%name", playerName);
        partyMessage = partyMessage.replace("%prefix", prefix);
        partyMessage = partyMessage.replace("%suffix", suffix);
        partyMessage = partyMessage.replace("%message", message);
        partyMessage = Utility.replaceColorCode(partyMessage);

        // チームメンバに送信する
        ArrayList<Player> playersToSend = getTeamMembers(t.getName());
        if ( config.isOPDisplayMode() ) {
            for ( OfflinePlayer p : plugin.getServer().getOperators() ) {
                if ( p.isOnline() && !playersToSend.contains(p.getPlayer()) ) {
                    playersToSend.add(p.getPlayer());
                }
            }
        }
        for ( Player p : playersToSend ) {
            p.sendMessage(partyMessage);
        }

        // ログ記録する
        if ( config.isTeamChatLogMode() ) {
            plugin.getLogger().info(partyMessage);
        }

        writeDebugLog("sendTeamChat end. : " + (System.currentTimeMillis() - start));
    }

    /**
     * スコアボードの表示を行う
     */
    @Override
    public void displayScoreboard() {

        writeDebugLog("displayScoreboard start.");
        long start = System.currentTimeMillis();

        // サイドバー
        switch (config.getSideCriteria()) {
        case POINT:
            objectives.getTeamPointObjective().setDisplaySlot(DisplaySlot.SIDEBAR);
            break;
        case KILL_COUNT:
            objectives.getTeamKillObjective().setDisplaySlot(DisplaySlot.SIDEBAR);
            break;
        case DEATH_COUNT:
            objectives.getTeamDeathObjective().setDisplaySlot(DisplaySlot.SIDEBAR);
            break;
        case REST_PLAYER:
            objectives.getTeamRestObjective().setDisplaySlot(DisplaySlot.SIDEBAR);
            break;
        case NONE:
            break;
        default:
            break;
        }

        // リスト
        switch (config.getListCriteria()) {
        case POINT:
            objectives.getPersonalPointObjective().setDisplaySlot(DisplaySlot.PLAYER_LIST);
            break;
        case KILL_COUNT:
            objectives.getPersonalKillObjective().setDisplaySlot(DisplaySlot.PLAYER_LIST);
            break;
        case DEATH_COUNT:
            objectives.getPersonalDeathObjective().setDisplaySlot(DisplaySlot.PLAYER_LIST);
            break;
        case HEALTH:
            objectives.getPersonalHealthObjective().setDisplaySlot(DisplaySlot.PLAYER_LIST);
            break;
        case NONE:
            break;
        default:
            break;
        }

        // 頭の上
        switch (config.getBelowCriteria()) {
        case POINT:
            objectives.getPersonalPointObjective().setDisplaySlot(DisplaySlot.BELOW_NAME);
            break;
        case KILL_COUNT:
            objectives.getPersonalKillObjective().setDisplaySlot(DisplaySlot.BELOW_NAME);
            break;
        case DEATH_COUNT:
            objectives.getPersonalDeathObjective().setDisplaySlot(DisplaySlot.BELOW_NAME);
            break;
        case HEALTH:
            objectives.getPersonalHealthObjective().setDisplaySlot(DisplaySlot.BELOW_NAME);
            break;
        case NONE:
            break;
        default:
            break;
        }

        writeDebugLog("displayScoreboard end. : " + (System.currentTimeMillis() - start));
    }

    /**
     * 残りチームメンバーのスコアボードを更新する。
     */
    @Override
    public void refreshRestTeamMemberScore() {

        writeDebugLog("refreshRestTeamMemberScore start.");
        long start = System.currentTimeMillis();

        Objective obj = objectives.getTeamRestObjective();

        // まず、全ての項目をいったん0にする
        for ( TeamNameSetting tns : getTeamNameConfig().getTeamNames() ) {
            Score score = getScore(obj, tns);
            if ( score.getScore() > 0 ) {
                score.setScore(0);
            }
        }

        // チーム人数を取得して設定する
        HashMap<String, ArrayList<Player>> members = getAllTeamMembers();
        for ( String id : members.keySet() ) {
            TeamNameSetting tns = getTeamNameFromID(id);
            getScore(obj, tns).setScore(members.get(id).size());
        }

        writeDebugLog("refreshRestTeamMemberScore end. : " + (System.currentTimeMillis() - start));
    }

    /**
     * キルデス数やポイントを全てクリアする
     */
    @Override
    public void clearKillDeathPoints() {

        writeDebugLog("clearKillDeathPoints start.");
        long start = System.currentTimeMillis();

        // イベントコール
        ColorTeamingKillDeathClearedEvent event =
                new ColorTeamingKillDeathClearedEvent();
        Utility.callEventSync(event);
        if ( event.isCancelled() ) {
            return;
        }

        // 各オブジェクティブを初期化
        objectives.resetAll();

        writeDebugLog("clearKillDeathPoints end. : " + (System.currentTimeMillis() - start));
    }

    /**
     * チームのポイント数を全取得する
     * @return チームのポイント数
     */
    @Override
    public HashMap<String, Integer> getAllTeamPoints() {

        writeDebugLog("getAllTeamPoints start.");
        long start = System.currentTimeMillis();

        HashMap<String, Integer> points = new HashMap<String, Integer>();
        Objective obj = objectives.getTeamPointObjective();

        for ( TeamNameSetting tns : getAllTeamNames() ) {
            int p = getScore(obj, tns).getScore();
            points.put(tns.getID(), p);
        }

        writeDebugLog("getAllTeamPoints end. : " + (System.currentTimeMillis() - start));
        return points;
    }

    /**
     * プレイヤーのポイント数を全取得する
     * @return プレイヤーのポイント数
     */
    @Override
    public HashMap<String, Integer> getAllPlayerPoints() {

        writeDebugLog("getAllPlayerPoints start.");
        long start = System.currentTimeMillis();

        HashMap<String, Integer> points = new HashMap<String, Integer>();
        Objective obj = objectives.getPersonalPointObjective();
        HashMap<String, ArrayList<Player>> members = getAllTeamMembers();

        for ( String team : members.keySet() ) {
            for ( Player player : members.get(team) ) {
                int point = getScore(obj, player).getScore();
                points.put(player.getName(), point);
            }
        }

        writeDebugLog("getAllPlayerPoints end. : " + (System.currentTimeMillis() - start));
        return points;
    }

    /**
     * チームポイントを設定する。
     * @param team チーム名
     * @param point ポイント数
     */
    @Override
    public void setTeamPoint(String team, int point) {

        writeDebugLog("setTeamPoint start. " + team + ", " + point);
        long start = System.currentTimeMillis();

        Objective obj = objectives.getTeamPointObjective();
        TeamNameSetting tns = teamNameConfig.getTeamNameFromID(team);

        Score score = getScore(obj, tns);
        int pointBefore = score.getScore();

        if ( point == 0 ) {
            // NOTE: ポイント0を設定する場合は、一旦1を設定して項目を表示させる。
            score.setScore(1);
        }
        score.setScore(point);

        // イベント呼び出し
        ColorTeamingTeamscoreChangeEvent event =
                new ColorTeamingTeamscoreChangeEvent(tns, pointBefore, point);
        Utility.callEventSync(event);

        writeDebugLog("setTeamPoint end. : " + (System.currentTimeMillis() - start));
    }

    /**
     * チームポイントを増減する。
     * @param team チーム名
     * @param amount ポイント増減量（マイナスでポイント減少）
     * @return 増減後のポイント
     */
    @Override
    public int addTeamPoint(String team, int amount) {

        writeDebugLog("addTeamPoint start. " + team + ", " + amount);
        long start = System.currentTimeMillis();

        Objective obj = objectives.getTeamPointObjective();
        TeamNameSetting tns = teamNameConfig.getTeamNameFromID(team);

        Score score = getScore(obj, tns);
        int point = score.getScore();
        score.setScore(point + amount);

        // イベント呼び出し
        ColorTeamingTeamscoreChangeEvent event =
                new ColorTeamingTeamscoreChangeEvent(tns, point, point + amount);
        Utility.callEventSync(event);

        writeDebugLog("addTeamPoint end. : " + (System.currentTimeMillis() - start));
        return point + amount;
    }

    /**
     * チーム単位のキルデス数を取得する
     * @return キルデス数
     */
    @Override
    public HashMap<String, int[]> getKillDeathCounts() {

        writeDebugLog("getKillDeathCounts start.");
        long start = System.currentTimeMillis();

        Objective kills = objectives.getTeamKillObjective();
        Objective deaths = objectives.getTeamDeathObjective();

        HashMap<String, int[]> result = new HashMap<String, int[]>();
        for ( TeamNameSetting tns : getAllTeamNames() ) {
            int[] data = new int[2];
            data[0] = getScore(kills, tns).getScore();
            data[1] = getScore(deaths, tns).getScore();
            result.put(tns.getID(), data);
        }

        writeDebugLog("getKillDeathCounts end. : " + (System.currentTimeMillis() - start));
        return result;
    }

    /**
     * ユーザー単位のキルデス数を取得する
     * @return キルデス数
     */
    @Override
    public HashMap<String, int[]> getKillDeathPersonalCounts() {

        writeDebugLog("getKillDeathPersonalCounts start.");
        long start = System.currentTimeMillis();

        Objective kills = objectives.getPersonalKillObjective();
        Objective deaths = objectives.getPersonalDeathObjective();

        HashMap<String, int[]> result = new HashMap<String, int[]>();
        HashMap<String, ArrayList<Player>> members = getAllTeamMembers();
        for ( String teamID : members.keySet() ) {
            for ( Player player : members.get(teamID) ) {
                int[] data = new int[2];
                data[0] = getScore(kills, player).getScore();
                data[1] = getScore(deaths, player).getScore();
                result.put(player.getName(), data);
            }
        }

        writeDebugLog("getKillDeathPersonalCounts end. : " + (System.currentTimeMillis() - start));
        return result;
    }

    /**
     * チームのキル数カウントを、+1する。
     * @param team チームID
     */
    @Override
    public void increaseTeamKillCount(String team) {

        writeDebugLog("increaseTeamKillCount start.");
        long start = System.currentTimeMillis();

        TeamNameSetting tns = getTeamNameFromID(team);
        Objective obj = objectives.getTeamKillObjective();
        Score score = getScore(obj, tns);
        score.setScore(score.getScore() + 1);

        writeDebugLog("increaseTeamKillCount end. : " + (System.currentTimeMillis() - start));
    }

    /**
     * チームのデス数カウントを、+1する。
     * @param team チームID
     */
    @Override
    public void increaseTeamDeathCount(String team) {

        writeDebugLog("increaseTeamDeathCount start.");
        long start = System.currentTimeMillis();

        TeamNameSetting tns = getTeamNameFromID(team);
        Objective obj = objectives.getTeamDeathObjective();
        Score score = getScore(obj, tns);
        score.setScore(score.getScore() + 1);

        writeDebugLog("increaseTeamDeathCount end. : " + (System.currentTimeMillis() - start));
    }

    /**
     * 指定したプレイヤーのポイントを追加する。
     * @param player プレイヤー
     * @param amount 追加ポイント（マイナス指定でポイントを減らす。）
     * @return 設定後のポイント
     */
    @Override
    public int addPlayerPoint(Player player, int amount) {

        writeDebugLog("addPlayerPoint start.");
        long start = System.currentTimeMillis();

        Score score = getScore(objectives.getPersonalPointObjective(), player);
        int point = score.getScore() + amount;
        score.setScore(point);

        writeDebugLog("addPlayerPoint end. : " + (System.currentTimeMillis() - start));
        return point;
    }

    /**
     * 指定したプレイヤーのポイントを設定する。
     * @param player プレイヤー
     * @param amount ポイント
     */
    @Override
    public void setPlayerPoint(Player player, int amount) {
        getScore(objectives.getPersonalPointObjective(), player).setScore(amount);
    }

    /**
     * ユーザー単位のキルデス数を設定する
     * @param playerName プレイヤー名
     * @param kill キル数
     * @param death デス数
     */
    @Override
    public void setKillDeathUserCounts(String playerName, int kill, int death) {

        writeDebugLog("setKillDeathUserCounts start.");
        long start = System.currentTimeMillis();

        Player player = Utility.getPlayerExact(playerName);
        if ( player == null ) {
            return;
        }

        Objective kills = objectives.getPersonalKillObjective();
        Objective deaths = objectives.getPersonalDeathObjective();

        getScore(kills, player).setScore(kill);
        getScore(deaths, player).setScore(death);

        writeDebugLog("setKillDeathUserCounts end. : " + (System.currentTimeMillis() - start));
    }

    /**
     * リーダー設定を全てクリアする
     */
    @Override
    public void clearLeaders() {
        leaders.clear();
    }

    /**
     * リーダー設定を取得する
     * @return リーダー設定
     */
    @Override
    public HashMap<String, ArrayList<String>> getLeaders() {
        return leaders;
    }

    /**
     * リスポーン設定を取得する
     * @return リスポーン設定
     */
    @Override
    public RespawnConfiguration getRespawnConfig() {
        return respawnConfig;
    }

    /**
     * TP地点設定を取得する
     * @return TP地点設定
     */
    @Override
    public TPPointConfiguration getTppointConfig() {
        return tppointConfig;
    }

    /**
     * チーム名設定を取得する
     * @return チーム名設定
     */
    @Override
    public TeamNameConfig getTeamNameConfig() {
        return teamNameConfig;
    }

    /**
     * リスポーンマップ名を取得する
     * @return リスポーンマップ名
     */
    @Override
    public String getRespawnMapName() {
        return respawnMapName;
    }

    /**
     * リスポーンマップ名を設定する
     * @param respawnMapName リスポーンマップ名
     */
    @Override
    public void setRespawnMapName(String respawnMapName) {
        this.respawnMapName = respawnMapName;
    }

    /**
     * カスタムアイテムを登録する
     * @param item カスタムアイテム
     * @deprecated ColorTeaming v2.4.0 以降では非サポートとなったため、実行しても何も起こりません。
     */
    @Override
    @Deprecated
    public void registerCustomItem(CustomItem item) {
        // do nothing.
    }

    /**
     * カスタムアイテムを登録する
     * @param item 登録するアイテム
     * @param name アイテム名
     * @param displayName 表示アイテム名
     * @deprecated ColorTeaming v2.4.0 以降では非サポートとなったため、実行しても何も起こりません。
     */
    @Override
    @Deprecated
    public void registerCustomItem(ItemStack item, String name, String displayName) {
        // do nothing.
    }

    /**
     * 登録されているカスタムアイテムを取得する
     * @param name カスタムアイテム名
     * @return カスタムアイテム、登録されていないアイテム名を指定した場合はnullが返される。
     * @deprecated ColorTeaming v2.4.0 以降では非サポートとなったため、必ずnullが返されます。
     */
    @Override
    @Deprecated
    public CustomItem getCustomItem(String name) {
        return null;
    }

    /**
     * 登録されているカスタムアイテムの名前を取得する
     * @return カスタムアイテムの名前
     * @deprecated ColorTeaming v2.4.0 以降では非サポートとなったため、必ず空配列が返されます。
     */
    @Override
    @Deprecated
    public Set<String> getCustomItemNames() {
        return new HashSet<String>();
    }

    /**
     * 指定されたプレイヤーに指定されたクラスを設定する
     * @param players プレイヤー
     * @param classname クラス名
     * @return クラス設定を実行したかどうか。<br/>
     * 例えば、指定されたクラス名が存在しない場合は、falseになる。
     */
    @Override
    public boolean setClassToPlayer(ArrayList<Player> players, String classname) {

        writeDebugLog("setClassToPlayer start. " + classname);
        long start = System.currentTimeMillis();

        // クラス設定が存在しない場合は falseを返す
        if ( !classDatas.containsKey(classname) ) {
            return false;
        }

        // 設定対象が居ない場合は falseを返す
        if ( players == null || players.size() <= 0 ) {
            return false;
        }

        // 設定の実行
        ClassData cd = classDatas.get(classname);
        for ( Player player : players ) {
            cd.setClassToPlayer(player);
        }

        writeDebugLog("setKillDeathUserCounts end. : " + (System.currentTimeMillis() - start));

        return true;
    }

    /**
     * 指定されたクラス名が存在するかどうかを確認する
     * @param classname クラス名
     * @return 存在するかどうか
     */
    @Override
    public boolean isExistClass(String classname) {
        return classDatas.containsKey(classname);
    }

    /**
     * 登録されている全てのクラスデータをまとめて取得する。
     * @return 全てのクラスデータ
     */
    @Override
    public HashMap<String, ClassData> getClasses() {
        return classDatas;
    }

    /**
     * クラスデータを設定する。同名のクラスが存在する場合は上書きに、無い場合は新規追加になる。
     * @param classdata クラスデータ
     */
    @Override
    public void setClassData(ClassData classdata) {

        String name = classdata.getTitle();
        classDatas.put(name, classdata);
    }

    /**
     * ランダムな順序で、プレイヤーをチームわけします。<br/>
     * 既にチームわけが存在する場合は、全部クリアしてから分けられます。
     * @param players チームわけを行うプレイヤー
     * @param teamNum チーム数（2から9までの数を指定可能です）
     */
    @Override
    public void makeColorTeamsWithRandomSelection(
            ArrayList<Player> players, int teamNum) {
        Collections.shuffle(players);
        makeColorTeamsWithOrderSelection(players, teamNum);
    }

    /**
     * 指定されたプレイヤー順序で、プレイヤーをチームわけします。<br/>
     * 既にチームわけが存在する場合は、全部クリアしてから分けられます。
     * @param players チームわけを行うプレイヤー
     * @param teamNum チーム数（2から9までの数を指定可能です）
     */
    @Override
    public void makeColorTeamsWithOrderSelection(ArrayList<Player> players, int teamNum) {

        writeDebugLog("makeColorTeamsWithOrderSelection start. " + teamNum);
        long start = System.currentTimeMillis();

        // 全てのチームをいったん削除する
        removeAllTeam();

        // チーム名設定を取得する
        ArrayList<TeamNameSetting> tns = teamNameConfig.getTeamNames();

        // チームを設定していく
        for ( int i=0; i<players.size(); i++ ) {
            int group = i % teamNum;
            TeamNameSetting teamName = tns.get(group);
            addPlayerTeam(players.get(i), teamName);
        }

        // キルデス情報のクリア
        clearKillDeathPoints();

        // チーム人数の更新、スコアボードの表示
        refreshRestTeamMemberScore();
        displayScoreboard();

        writeDebugLog("makeColorTeamsWithOrderSelection end. : "
                + (System.currentTimeMillis() - start));
    }

    /**
     * 既存のチームわけをそのままに、指定されたプレイヤーを既存のチームへ加えていきます。<br/>
     * プレイヤーはランダムな順序で追加が行われます。<br/>
     * 加えられる先のチームは、人数の少ないチームが選択されます。
     * 同数の場合はその中からランダムに選択されます。
     * @param players チームに加えるプレイヤー
     * @return 最後まで処理が行われたかどうか
     */
    @Override
    public boolean addPlayerToColorTeamsWithRandomSelection(ArrayList<Player> players) {
        Collections.shuffle(players);
        return addPlayerToColorTeamsWithOrderSelection(players);
    }

    /**
     * 既存のチームわけをそのままに、指定されたプレイヤーを既存のチームへ加えていきます。<br/>
     * プレイヤーは指定の順序で追加が行われます。<br/>
     * 加えられる先のチームは、人数の少ないチームが選択されます。
     * 同数の場合はその中からランダムに選択されます。
     * @param players チームに加えるプレイヤー
     * @return 最後まで処理が行われたかどうか
     */
    @Override
    public boolean addPlayerToColorTeamsWithOrderSelection(ArrayList<Player> players) {

        writeDebugLog("addPlayerToColorTeamsWithOrderSelection start. ");
        long start = System.currentTimeMillis();

        // 人数の少ないチームに設定していく
        for ( int i=0; i<players.size(); i++ ) {

            // 人数の少ないチームの取得
            HashMap<String, ArrayList<Player>> members = getAllTeamMembers();
            int least = 999;
            TeamNameSetting leastTeam = null;
            ArrayList<TeamNameSetting> teams = getAllTeamNames();

            // 例外チームが含まれている場合は、あらかじめ除外する（see issue #99）
            if ( teams.contains(teamNameConfig.getIgnoreTeam()) ) {
                teams.remove(teamNameConfig.getIgnoreTeam());
            }

            // ランダム要素を入れるため、チーム名をシャッフルする
            Collections.shuffle(teams);

            for ( TeamNameSetting t : teams ) {
                if ( least > members.get(t.getID()).size() ) {
                    least = members.get(t.getID()).size();
                    leastTeam = t;
                }
            }

            // チームへプレイヤーを追加
            if ( leastTeam != null ) {
                addPlayerTeam(players.get(i), leastTeam);
            } else {
                // 参加できるチームが無い
                return false;
            }
        }

        // チーム人数の更新、スコアボードの表示
        refreshRestTeamMemberScore();
        displayScoreboard();

        writeDebugLog("addPlayerToColorTeamsWithOrderSelection end. : "
                + (System.currentTimeMillis() - start));
        return true;
    }


    /**
     * ネームタグの表示/非表示を、コンフィグから取得して設定します。このAPIは、CB1.7.x以前では動作しません。
     * @see com.github.ucchyocean.ct.ColorTeamingAPI#setNametagVisibility()
     */
    @SuppressWarnings("deprecation")
    @Override
    public void setNametagVisibility() {

        // CB 1.8 以降でなければ何もしない
        if ( !Utility.isCB18orLater() ) {
            return;
        }

        NametagVisibilityEnum visibility = config.getNametagVisibility();

        for ( TeamNameSetting tns : getAllTeamNames() ) {
            Team team = scoreboard.getTeam(tns.getID());
            if ( team != null ) {
                if ( Utility.isCB19orLater() ) {
                    // CB1.9以降
                    team.setOption(Team.Option.NAME_TAG_VISIBILITY, visibility.getBukkitOptionStatus());
                } else {
                    // CB1.8
                    team.setNameTagVisibility(visibility.getBukkit());
                }
            }
        }
    }

    /**
     * ネームタグの表示/非表示を設定します。このAPIは、CB1.7.x以前では動作しません。
     * @param visibility 表示設定
     * @see com.github.ucchyocean.ct.ColorTeamingAPI#setNametagVisibility(com.github.ucchyocean.ct.config.NametagVisibilityEnum)
     */
    @Override
    public void setNametagVisibility(NametagVisibilityEnum visibility) {

        // CB 1.8 以降でなければ何もしない
        if ( !Utility.isCB18orLater() ) {
            return;
        }

        config.setNametagVisibility(visibility);
        config.saveConfig();
        setNametagVisibility();
    }

    /**
     * プレイヤー間の当たり判定を、コンフィグから取得して設定します。このAPIは、CB1.8.x以前では動作しません。
     * @see com.github.ucchyocean.ct.ColorTeamingAPI#setCollisionRule()
     */
    @Override
    public void setCollisionRule() {

        // CB 1.9 以降でなければ何もしない
        if ( !Utility.isCB19orLater() ) {
            return;
        }

        TeamOptionStatusEnum rule = config.getCollisionRule();

        for ( TeamNameSetting tns : getAllTeamNames() ) {
            Team team = scoreboard.getTeam(tns.getID());
            if ( team != null ) {
                team.setOption(Team.Option.COLLISION_RULE, rule.getBukkit());
            }
        }
    }

    /**
     * プレイヤー間の当たり判定を設定します。このAPIは、CB1.8.x以前では動作しません。
     * @param rule 当たり判定設定
     * @see com.github.ucchyocean.ct.ColorTeamingAPI#setCollisionRule(com.github.ucchyocean.ct.config.TeamOptionStatusEnum)
     */
    @Override
    public void setCollisionRule(TeamOptionStatusEnum rule) {

        // CB 1.9 以降でなければ何もしない
        if ( !Utility.isCB19orLater() ) {
            return;
        }

        config.setCollisionRule(rule);
        config.saveConfig();
        setCollisionRule();
    }

    /**
     * 死亡ログの表示設定を、コンフィグから取得して設定します。このAPIは、CB1.8.x以前では動作しません。
     * @see com.github.ucchyocean.ct.ColorTeamingAPI#setDeathMessageVisibility()
     */
    @Override
    public void setDeathMessageVisibility() {

        // CB 1.9 以降でなければ何もしない
        if ( !Utility.isCB19orLater() ) {
            return;
        }

        TeamOptionStatusEnum visibility = config.getDeathMessageVisibility();

        for ( TeamNameSetting tns : getAllTeamNames() ) {
            Team team = scoreboard.getTeam(tns.getID());
            if ( team != null ) {
                team.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, visibility.getBukkit());
            }
        }
    }

    /**
     * 死亡ログの表示を設定します。このAPIは、CB1.8.x以前では動作しません。
     * @param visibility 表示設定
     * @see com.github.ucchyocean.ct.ColorTeamingAPI#setDeathMessageVisibility(com.github.ucchyocean.ct.config.TeamOptionStatusEnum)
     */
    @Override
    public void setDeathMessageVisibility(TeamOptionStatusEnum visibility) {

        // CB 1.9 以降でなければ何もしない
        if ( !Utility.isCB19orLater() ) {
            return;
        }

        config.setDeathMessageVisibility(visibility);
        config.saveConfig();
        setDeathMessageVisibility();
    }

    /**
     * ColorTeamingの設定ファイルを全て再読み込みする
     * @see com.github.ucchyocean.ct.ColorTeamingAPI#realod()
     */
    @Override
    public void realod() {

        writeDebugLog("realod start. ");
        long start = System.currentTimeMillis();

        ColorTeaming.instance.config = ColorTeamingConfig.loadConfig();
        ColorTeamingMessages.reload();
        respawnConfig = new RespawnConfiguration();
        tppointConfig = new TPPointConfiguration();
        teamNameConfig = new TeamNameConfig();
        classDatas = ClassData.loadAllClasses(new File(plugin.getDataFolder(), "classes"));

        writeDebugLog("realod end. : " + (System.currentTimeMillis() - start));
    }

    /**
     * デバッグが有効ならログを記録する。デバッグが無効なら何もしない。
     * @see com.github.ucchyocean.ct.ColorTeamingAPI#writeDebugLog(java.lang.String)
     */
    @Override
    public void writeDebugLog(final String log) {

        if ( !config.isDebug() ) return;

        if ( debugLogFile == null ) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String filename = "debug_" + sdf.format(new Date()) + ".log";
            debugLogFile = new File(plugin.getDataFolder(), filename);
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter(debugLogFile, true);
            String str = new Date() + ", " + log;
            writer.write(str + "\r\n");
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if ( writer != null ) {
                try {
                    writer.close();
                } catch (Exception e) {
                    // do nothing.
                }
            }
        }
    }

    /**
     * スコアオブジェクトを取得します（CB178前後の仕様差異を埋めるための実装です）
     * @param objective オブジェクティブ
     * @param player プレイヤー
     * @return スコアオブジェクト
     */
    private static Score getScore(Objective objective, Player player) {

        if ( Utility.isCB178orLater() ) {
            return objective.getScore(player.getName());
        } else {
            @SuppressWarnings("deprecation")
            Score score = objective.getScore(player);
            return score;
        }
    }

    /**
     * スコアオブジェクトを取得します（CB178前後の仕様差異を埋めるための実装です）
     * @param objective オブジェクティブ
     * @param tns チーム
     * @return スコアオブジェクト
     */
    private static Score getScore(Objective objective, TeamNameSetting tns) {

        if ( Utility.isCB178orLater() ) {
            return objective.getScore(tns.toString());
        } else {
            @SuppressWarnings("deprecation")
            Score score = objective.getScore(tns.getScoreItem());
            return score;
        }
    }

    /**
     * チームにプレイヤーを追加します（CB186前後の仕様差異を埋めるための実装です）
     * @param team チーム
     * @param player プレイヤー
     */
    @SuppressWarnings("deprecation")
    private static void addPlayerInternal(Team team, Player player) {

        if ( Utility.isCB186orLater() ) {
            team.addEntry(player.getName());
        } else {
            team.addPlayer(player);
        }
    }

    /**
     * チームからプレイヤーを削除します（CB186前後の仕様差異を埋めるための実装です）
     * @param team チーム
     * @param player プレイヤー
     */
    @SuppressWarnings("deprecation")
    private static void removePlayerInternal(Team team, Player player) {

        if ( Utility.isCB186orLater() ) {
            team.removeEntry(player.getName());
        } else {
            team.removePlayer(player);
        }
    }

    /**
     * チームからプレイヤーの一覧を取得します（CB186前後の仕様差異を埋めるための実装です）
     * @param team チーム
     * @return 所属プレイヤー
     */
    @SuppressWarnings("deprecation")
    private static Set<Player> getPlayersInternal(Team team) {

        HashSet<Player> players = new HashSet<Player>();
        if ( Utility.isCB186orLater() ) {
            for ( String name : team.getEntries() ) {
                Player player = Utility.getPlayerExact(name);
                if ( player != null ) {
                    players.add(player);
                }
            }
        } else {
            for ( OfflinePlayer player : team.getPlayers() ) {
                if ( player.isOnline() ) {
                    players.add(player.getPlayer());
                }
            }
        }
        return players;
    }
}
