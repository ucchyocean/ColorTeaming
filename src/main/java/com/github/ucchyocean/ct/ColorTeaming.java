/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.ct;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.github.ucchyocean.ct.command.CChatCommand;
import com.github.ucchyocean.ct.command.CChatGlobalCommand;
import com.github.ucchyocean.ct.command.CClassCommand;
import com.github.ucchyocean.ct.command.CCountCommand;
import com.github.ucchyocean.ct.command.CExplodeCommand;
import com.github.ucchyocean.ct.command.CFriendlyFireCommand;
import com.github.ucchyocean.ct.command.CGiveCommand;
import com.github.ucchyocean.ct.command.CJoinCommand;
import com.github.ucchyocean.ct.command.CKillCommand;
import com.github.ucchyocean.ct.command.CLeaderCommand;
import com.github.ucchyocean.ct.command.CRandomCommand;
import com.github.ucchyocean.ct.command.CRemoveCommand;
import com.github.ucchyocean.ct.command.CRestoreCommand;
import com.github.ucchyocean.ct.command.CSaveCommand;
import com.github.ucchyocean.ct.command.CSpawnCommand;
import com.github.ucchyocean.ct.command.CTPCommand;
import com.github.ucchyocean.ct.command.CTeamingCommand;
import com.github.ucchyocean.ct.listener.EntityDamageListener;
import com.github.ucchyocean.ct.listener.PlayerChatListener;
import com.github.ucchyocean.ct.listener.PlayerDeathListener;
import com.github.ucchyocean.ct.listener.PlayerJoinQuitListener;
import com.github.ucchyocean.ct.listener.PlayerRespawnListener;
import com.github.ucchyocean.ct.scoreboard.BelowNameScoreDisplay;
import com.github.ucchyocean.ct.scoreboard.CTScoreInterface;
import com.github.ucchyocean.ct.scoreboard.PlayerCriteria;
import com.github.ucchyocean.ct.scoreboard.SidebarCriteria;
import com.github.ucchyocean.ct.scoreboard.SidebarScoreDisplay;
import com.github.ucchyocean.ct.scoreboard.TabListScoreDisplay;

/**
 * 簡易PVPチーミングプラグイン
 * @author ucchy
 */
public class ColorTeaming extends JavaPlugin {

    private static final String TEAM_CHAT_FORMAT = "&a[%s&a]<%s&r&a> %s";
    private static final String TEAM_INFORMATION_FORMAT = "&a[%s&a] %s";

    private static Scoreboard sb;
    public static ColorTeaming instance;
    public static TeamMemberSaveDataHandler sdhandler;

    public static Logger logger;
    protected static ColorTeamingConfig ctconfig;
    public static RespawnConfiguration respawnConfig;
    public static TPPointConfiguration tppointConfig;
    public static SidebarScoreDisplay sidebarScore;
    public static TabListScoreDisplay tablistScore;
    public static BelowNameScoreDisplay belownameScore;

    public static Hashtable<String, ArrayList<String>> leaders;
    public static Hashtable<String, int[]> killDeathCounts;
    public static Hashtable<String, int[]> killDeathUserCounts;

    public static String respawnMapName;

    public static CTScoreInterface customScore;

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    @Override
    public void onEnable() {

        instance = this;
        logger = getLogger();

        // 設定の読み込み処理
        reloadCTConfig();

        // コマンドをサーバーに登録
        CCountCommand ccCommand = new CCountCommand();
        getCommand("colorcount").setExecutor(ccCommand);
        getCommand("colorcountsay").setExecutor(ccCommand);

        getCommand("colorfriendlyfire").setExecutor(new CFriendlyFireCommand());

        getCommand("colorchat").setExecutor(new CChatCommand());

        getCommand("colorglobal").setExecutor(new CChatGlobalCommand());

        getCommand("colorleader").setExecutor(new CLeaderCommand());

        getCommand("colortp").setExecutor(new CTPCommand());

        getCommand("colorclass").setExecutor(new CClassCommand());

        getCommand("colorkill").setExecutor(new CKillCommand());

        getCommand("colorspawn").setExecutor(new CSpawnCommand());

        getCommand("colorrandom").setExecutor(new CRandomCommand());

        getCommand("colorremove").setExecutor(new CRemoveCommand());

        getCommand("colorexplode").setExecutor(new CExplodeCommand());

        getCommand("colorsave").setExecutor(new CSaveCommand());

        getCommand("colorrestore").setExecutor(new CRestoreCommand());

        getCommand("colorgive").setExecutor(new CGiveCommand());

        getCommand("colorjoin").setExecutor(new CJoinCommand());

        getCommand("colorteaming").setExecutor(new CTeamingCommand());

        // イベント購読をサーバーに登録
        getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);

        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);

        getServer().getPluginManager().registerEvents(new PlayerJoinQuitListener(), this);

        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(), this);

        getServer().getPluginManager().registerEvents(new EntityDamageListener(), this);

        // 変数の初期化
        killDeathCounts = new Hashtable<String, int[]>();
        killDeathUserCounts = new Hashtable<String, int[]>();
        leaders = new Hashtable<String, ArrayList<String>>();
        respawnConfig = new RespawnConfiguration();
        tppointConfig = new TPPointConfiguration();
        sdhandler = new TeamMemberSaveDataHandler(getDataFolder());
    }

    /**
     * スコアボードを返す。
     * @return スコアボード
     */
    public Scoreboard getScoreboard() {
        if ( sb == null ) {
            sb = instance.getServer().getScoreboardManager().getMainScoreboard();
        }
        return sb;
    }

    /**
     * Player に設定されている、チームを取得する。
     * @param player プレイヤー
     * @return チーム
     */
    public Team getPlayerTeam(Player player) {

        Scoreboard scoreboard = getScoreboard();
        Set<Team> teams = scoreboard.getTeams();
        for ( Team team : teams ) {
            for ( OfflinePlayer p : team.getPlayers() ) {
                if ( p.getName().equalsIgnoreCase(player.getName()) ) {
                    return team;
                }
            }
        }
        return null;
    }

    /**
     * Player に設定されている、チームのチーム名を取得する。
     * @param player
     * @return
     */
    public String getPlayerColor(Player player) {

        Team team = getPlayerTeam(player);
        if ( team == null ) return "";
        else return team.getName();
    }

    /**
     * Player にチームを設定する。
     * @param player プレイヤー
     * @param color チームの色
     */
    public Team addPlayerTeam(Player player, String color) {

        Scoreboard scoreboard = getScoreboard();

        Team team = scoreboard.getTeam(color);
        if ( team == null ) {
            team = scoreboard.registerNewTeam(color);
            team.setDisplayName(Utility.replaceColors(color) + color + ChatColor.RESET);
            team.setPrefix(Utility.replaceColors(color).toString());
            team.setSuffix(ChatColor.RESET.toString());
            team.setCanSeeFriendlyInvisibles(ctconfig.isCanSeeFriendlyInvisibles());
            team.setAllowFriendlyFire(
                    ctconfig.isCanSeeFriendlyInvisibles() ||
                    !ctconfig.isFriendlyFireDisabler());
            System.out.println("team : " + team.getDisplayName() + " - " + (
                    ctconfig.isCanSeeFriendlyInvisibles() ||
                    !ctconfig.isFriendlyFireDisabler()));
        }
        team.addPlayer(player);
        player.setDisplayName(
                Utility.replaceColors(color) + player.getName() + ChatColor.RESET);

        return team;
    }

    /**
     * Player に設定されているチームを削除する。
     * @param player プレイヤー
     */
    public void leavePlayerTeam(Player player) {

        Team team = getPlayerTeam(player);
        if ( team != null )
            team.removePlayer(player);

        player.setDisplayName(player.getName());
    }

    /**
     * フレンドリーファイアの設定。<br>
     * NOTE: 本メソッドは、透明可視化が設定されている場合は、
     * 強制的にfalseになることに注意
     * @param ff trueならフレンドリーファイア有効、falseなら無効
     */
    public void setFriendlyFilre(boolean ff) {

        Scoreboard scoreboard = getScoreboard();

        Set<Team> teams = scoreboard.getTeams();
        for ( Team t : teams ) {
            boolean invisible = t.canSeeFriendlyInvisibles();
            System.out.println("setff : " + (invisible || ff));
            t.setAllowFriendlyFire(invisible || ff);
        }
    }

    /**
     * 仲間の可視化の設定。<br>
     * @param fi trueならフレンドリーファイア有効、falseなら無効
     */
    public void setSeeFriendlyInvisibles(boolean fi) {

        Scoreboard scoreboard = getScoreboard();

        Set<Team> teams = scoreboard.getTeams();
        for ( Team t : teams ) {
            t.setCanSeeFriendlyInvisibles(fi);
        }
    }

    /**
     * 指定したチーム名のチームを削除する
     * @param name
     */
    public void removeTeam(String name) {

        Scoreboard scoreboard = getScoreboard();
        Team team = scoreboard.getTeam(name);
        if ( team != null ) {
            for ( OfflinePlayer player : team.getPlayers() ) {
                if ( player.getPlayer() != null && player.isOnline() ) {
                    leavePlayerTeam(player.getPlayer());
                }
            }
            team.unregister();
        }
    }

    /**
     * 全てのチームを削除する
     */
    public void removeAllTeam() {

        Scoreboard scoreboard = getScoreboard();

        Set<Team> teams = scoreboard.getTeams();
        for ( Team team : teams ) {
            removeTeam(team.getName());
        }
    }

    /**
     * ユーザーをチームごとのメンバーに整理して返すメソッド
     * @return 色をKey メンバーをValueとした Hashtable
     */
    public Hashtable<String, ArrayList<Player>> getAllTeamMembers() {

        Hashtable<String, ArrayList<Player>> result = new Hashtable<String, ArrayList<Player>>();
        Scoreboard scoreboard = getScoreboard();

        Set<Team> teams = scoreboard.getTeams();
        for ( Team team : teams ) {
            Set<OfflinePlayer> playersTemp = team.getPlayers();
            ArrayList<Player> players = new ArrayList<Player>();
            for ( OfflinePlayer player : playersTemp ) {
                if ( player != null && player.isOnline() ) {
                    players.add(player.getPlayer());
                }
            }
            result.put(team.getName(), players);
        }

        return result;
    }

    /**
     * 全てのプレイヤーを取得する
     * @return 全てのプレイヤー
     */
    public ArrayList<Player> getAllPlayers() {
        Player[] temp = instance.getServer().getOnlinePlayers();
        ArrayList<Player> result = new ArrayList<Player>();
        for ( Player p : temp ) {
            result.add(p);
        }
        return result;
    }

    /**
     * 指定したワールドにいる全てのプレイヤーを取得する。
     * ただし、指定したワールドが存在しない場合は、空のリストが返される。
     * @param worldNames 対象にするワールド名
     * @return 全てのプレイヤー
     */
    public ArrayList<Player> getAllPlayersOnWorld(List<String> worldNames) {

        if ( worldNames == null ) {
            return null;
        }

        Player[] temp = instance.getServer().getOnlinePlayers();
        ArrayList<Player> result = new ArrayList<Player>();
        for ( Player p : temp ) {
            if ( worldNames.contains(p.getWorld().getName()) ) {
                result.add(p);
            }
        }
        return result;
    }

    /**
     * 全てのチーム名（＝全ての色）を取得する
     * @return 全てのチーム名
     */
    public ArrayList<String> getAllColors() {

        ArrayList<String> result = new ArrayList<String>();
        Set<Team> teams = getScoreboard().getTeams();

        for ( Team team : teams ) {
            result.add(team.getName());
        }

        return result;
    }

    /**
     * メッセージをチームチャットに送信する。
     * @param player 送信元プレイヤー
     * @param message 送信するメッセージ
     */
    public void sendTeamChat(Player player, String message) {

        Team team = getPlayerTeam(player);
        if ( team == null ) {
            return;
        }
        String color = team.getName();

        // 設定に応じて、Japanize化する
        if ( ctconfig.isShowJapanizeTeamChat() ) {
            // 2byteコードを含まない場合にのみ、処理を行う
            if ( message.getBytes().length == message.length() ) {
                String kana = KanaConverter.conv(message);
                message = message + "(" + kana + ")";
            }
        }

        // メッセージを生成
        String partyMessage = String.format(
                Utility.replaceColorCode(TEAM_CHAT_FORMAT),
                Utility.replaceColors(color) + color,
                player.getDisplayName(),
                message
                );

        // チームメンバに送信する
        ArrayList<Player> playersToSend = getAllTeamMembers().get(color);
        if ( ctconfig.isOPDisplayMode() ) {
            Player[] players = instance.getServer().getOnlinePlayers();
            for ( Player p : players ) {
                if ( p.isOp() && !playersToSend.contains(p) ) {
                    playersToSend.add(p);
                }
            }
        }
        for ( Player p : playersToSend ) {
            p.sendMessage(partyMessage);
        }

        // ログ記録する
        if ( ctconfig.isTeamChatLogMode() ) {
            logger.info(partyMessage);
        }
    }

    /**
     * 情報をチームチャットに送信する。
     * @param color 送信先のチーム
     * @param message 送信するメッセージ
     */
    public void sendInfoToTeamChat(String color, String message) {

        // メッセージを生成
        String partyMessage = String.format(
                Utility.replaceColorCode(TEAM_INFORMATION_FORMAT),
                Utility.replaceColors(color) + color,
                message
                );

        // チームメンバに送信する
        ArrayList<Player> playersToSend = getAllTeamMembers().get(color);
        if ( playersToSend != null ) {
            for ( Player p : playersToSend ) {
                p.sendMessage(partyMessage);
            }
        }
    }

    /**
     * プレイヤー名からPlayerインスタンスを返す。
     * @param name プレイヤー名
     * @return
     */
    public Player getPlayerExact(String name) {
        return instance.getServer().getPlayerExact(name);
    }

    /**
     * ワールド名からWorldインスタンスを返す。
     * @param name ワールド名
     * @return
     */
    public World getWorld(String name) {
        return instance.getServer().getWorld(name);
    }

    /**
     * このプラグインのJarファイル自身を示すFileクラスを返す。
     * @return
     */
    protected File getPluginJarFile() {
        return instance.getFile();
    }

    /**
     * サイドバーを新しく作る。
     * もともとサイドバーがあった場合は、削除して再作成される。
     */
    public void makeSidebar() {

        removeSidebar();
        if ( ctconfig.getSideCriteria() != SidebarCriteria.NONE ) {
            sidebarScore = new SidebarScoreDisplay();
        }
    }

    /**
     * サイドバーを消去する。
     */
    public void removeSidebar() {

        getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        if ( sidebarScore != null ) {
            sidebarScore.remove();
        }
    }

    /**
     * サイドバーのスコアを更新する。
     */
    public void refreshSidebarScore() {

        if ( sidebarScore != null ) {
            sidebarScore.refreshScore();
        }
    }

    /**
     * タブキーリストのスコアを新しく作る。
     * もともとスコアがあった場合は、削除して再作成される。
     */
    public void makeTabkeyListScore() {

        removeTabkeyListScore();
        if ( ctconfig.getListCriteria() != PlayerCriteria.NONE ) {
            tablistScore = new TabListScoreDisplay();
        }
    }

    /**
     * タブキーリストのスコアを消去する。
     */
    public void removeTabkeyListScore() {

        getScoreboard().clearSlot(DisplaySlot.PLAYER_LIST);
        if ( tablistScore != null ) {
            tablistScore.remove();
        }
    }

    /**
     * タブキーリストのスコアを更新する。
     */
    public void refreshTabkeyListScore() {

        if ( tablistScore != null ) {
            tablistScore.refreshScore();
        }
    }

    /**
     * 名前下のスコアを新しく作る。
     * もともとスコアがあった場合は、削除して再作成される。
     */
    public void makeBelowNameScore() {

        removeBelowNameScore();
        if ( ctconfig.getBelowCriteria() != PlayerCriteria.NONE ) {
            belownameScore = new BelowNameScoreDisplay();
        }
    }

    /**
     * 名前下のスコアを消去する。
     */
    public void removeBelowNameScore() {

        getScoreboard().clearSlot(DisplaySlot.BELOW_NAME);
        if ( belownameScore != null ) {
            belownameScore.remove();
        }
    }

    /**
     * 名前下のスコアを更新する。
     */
    public void refreshBelowNameScore() {

        if ( belownameScore != null ) {
            belownameScore.refreshScore();
        }
    }

    /**
     * ColorTeamingConfig を取得する
     * @return ColorTeamingConfig
     */
    public ColorTeamingConfig getCTConfig() {
        return ctconfig;
    }

    /**
     * ColorTeamingConfig を更新する
     */
    public void reloadCTConfig() {
        ctconfig = ColorTeamingConfig.loadConfig();
    }

    /**
     * TeamMemberSaveDataHandler を取得する
     * @return TeamMemberSaveDataHandler
     */
    public TeamMemberSaveDataHandler getCTSaveDataHandler() {
        return sdhandler;
    }

    /**
     * キルデス数を全てクリアする
     */
    public void clearKillDeathPoints() {
        killDeathCounts.clear();
        killDeathUserCounts.clear();
    }

    /**
     * カスタムスコアを設定する
     * @param score カスタムスコア
     */
    public void setCustomScore(CTScoreInterface score) {
        customScore = score;
    }

    /**
     * メッセージをブロードキャストに送信する。
     * @param message 送信するメッセージ
     */
    public static void sendBroadcast(String message) {
        instance.getServer().broadcastMessage(message);
    }
}
