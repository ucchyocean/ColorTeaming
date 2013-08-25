/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.github.ucchyocean.ct.config.ColorTeamingConfig;
import com.github.ucchyocean.ct.config.RespawnConfiguration;
import com.github.ucchyocean.ct.config.TPPointConfiguration;
import com.github.ucchyocean.ct.config.TeamMemberSaveDataHandler;
import com.github.ucchyocean.ct.config.TeamNameConfig;
import com.github.ucchyocean.ct.event.ColorTeamingKillDeathClearedEvent;
import com.github.ucchyocean.ct.event.ColorTeamingPlayerAddEvent;
import com.github.ucchyocean.ct.event.ColorTeamingPlayerLeaveEvent;
import com.github.ucchyocean.ct.event.ColorTeamingPlayerLeaveEvent.Reason;
import com.github.ucchyocean.ct.event.ColorTeamingTeamChatEvent;
import com.github.ucchyocean.ct.event.ColorTeamingTeamCreateEvent;
import com.github.ucchyocean.ct.event.ColorTeamingTeamRemoveEvent;
import com.github.ucchyocean.ct.scoreboard.BelowNameScoreDisplay;
import com.github.ucchyocean.ct.scoreboard.PlayerCriteria;
import com.github.ucchyocean.ct.scoreboard.SidebarCriteria;
import com.github.ucchyocean.ct.scoreboard.SidebarScoreDisplay;
import com.github.ucchyocean.ct.scoreboard.TabListScoreDisplay;

/**
 * ColorTeamingAPIの実体クラス
 * @author ucchy
 */
public class ColorTeamingManager implements ColorTeamingAPI {

    private static final String TEAM_CHAT_FORMAT = "&a[%s&a]<%s&r&a> %s";
    private static final String TEAM_INFORMATION_FORMAT = "&a[%s&a] %s";

    private static final String[] GROUP_COLORS =
        {"red", "blue", "yellow", "green", "aqua", "gray", "dark_red", "dark_green", "dark_aqua"};

    private ColorTeaming plugin;
    private ColorTeamingConfig config;

    private Scoreboard sb;
    private TeamMemberSaveDataHandler sdhandler;

    private RespawnConfiguration respawnConfig;
    private TPPointConfiguration tppointConfig;
    private TeamNameConfig teamNameConfig;
    private SidebarScoreDisplay sidebarScore;
    private TabListScoreDisplay tablistScore;
    private BelowNameScoreDisplay belownameScore;

    private HashMap<String, ArrayList<String>> leaders;
    private HashMap<String, int[]> killDeathCounts;
    private HashMap<String, int[]> killDeathUserCounts;

    private String respawnMapName;

    /**
     * コンストラクタ
     * @param plugin
     * @param config
     */
    public ColorTeamingManager(ColorTeaming plugin, ColorTeamingConfig config) {

        this.plugin = plugin;
        this.config = config;

        // 変数の初期化
        killDeathCounts = new HashMap<String, int[]>();
        killDeathUserCounts = new HashMap<String, int[]>();
        leaders = new HashMap<String, ArrayList<String>>();
        respawnConfig = new RespawnConfiguration();
        tppointConfig = new TPPointConfiguration();
        teamNameConfig = new TeamNameConfig();
        sdhandler = new TeamMemberSaveDataHandler(plugin.getDataFolder());
    }

    /**
     * スコアボードを返す。
     * @return スコアボード
     */
    @Override
    public Scoreboard getScoreboard() {
        if ( sb == null ) {
            sb = plugin.getServer().getScoreboardManager().getMainScoreboard();
        }
        return sb;
    }

    /**
     * Player に設定されている、チームを取得する。
     * @param player プレイヤー
     * @return チーム
     */
    @Override
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
     * @param player プレイヤー
     * @return チーム名
     */
    @Override
    public String getPlayerTeamName(Player player) {

        Team team = getPlayerTeam(player);
        if ( team == null ) return "";
        else return team.getName();
    }

    /**
     * Player にチームを設定する。
     * @param player プレイヤー
     * @param color チーム名
     * @return チーム、イベントキャンセルされた場合はnullになることに注意
     */
    @Override
    public Team addPlayerTeam(Player player, String color) {

        Scoreboard scoreboard = getScoreboard();

        Team team = scoreboard.getTeam(color);
        if ( team == null ) {

            // イベントコール
            ColorTeamingTeamCreateEvent event =
                    new ColorTeamingTeamCreateEvent(color);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if ( event.isCancelled() ) {
                return null;
            }

            team = scoreboard.registerNewTeam(color);
            team.setDisplayName(Utility.replaceColors(color) + color + ChatColor.RESET);
            team.setPrefix(Utility.replaceColors(color).toString());
            team.setSuffix(ChatColor.RESET.toString());
            team.setCanSeeFriendlyInvisibles(config.isCanSeeFriendlyInvisibles());
            team.setAllowFriendlyFire(
                    config.isCanSeeFriendlyInvisibles() ||
                    !config.isFriendlyFireDisabler());
        }

        // イベントコール
        ColorTeamingPlayerAddEvent event =
                new ColorTeamingPlayerAddEvent(player, team);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if ( event.isCancelled() ) {
            return null;
        }

        team.addPlayer(player);
        player.setDisplayName(
                Utility.replaceColors(color) + player.getName() + ChatColor.RESET);

        return team;
    }

    /**
     * Player に設定されているチームを削除する。
     * @param player プレイヤー
     * @param reason 離脱理由
     */
    @Override
    public void leavePlayerTeam(Player player, Reason reason) {

        Team team = getPlayerTeam(player);
        if ( team != null ) {

            // イベントコール
            ColorTeamingPlayerLeaveEvent event =
                    new ColorTeamingPlayerLeaveEvent(player, team, reason);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if ( event.isCancelled() ) {
                return;
            }

            team.removePlayer(player);

//            if ( team.getPlayers().size() <= 0 ) {
//                removeTeam(team.getName());
//            }
        }

        player.setDisplayName(player.getName());
    }

    /**
     * フレンドリーファイアの設定。<br>
     * NOTE: 本メソッドは、透明可視化が設定されている場合は、
     * 強制的にfalseになることに注意
     * @param ff trueならフレンドリーファイア有効、falseなら無効
     */
    @Override
    public void setFriendlyFire(boolean ff) {

        Scoreboard scoreboard = getScoreboard();

        Set<Team> teams = scoreboard.getTeams();
        for ( Team t : teams ) {
            boolean invisible = t.canSeeFriendlyInvisibles();
            t.setAllowFriendlyFire(invisible || ff);
        }

        config.setFriendlyFireDisabler(ff);
        config.saveConfig();
    }

    /**
     * 仲間の可視化の設定。<br>
     * @param fi trueならフレンドリーファイア有効、falseなら無効
     */
    @Override
    public void setSeeFriendlyInvisibles(boolean fi) {

        Scoreboard scoreboard = getScoreboard();

        Set<Team> teams = scoreboard.getTeams();
        for ( Team t : teams ) {
            t.setCanSeeFriendlyInvisibles(fi);
        }

        config.setCanSeeFriendlyInvisibles(fi);
        config.saveConfig();
    }

    /**
     * 指定したチーム名のチームを削除する
     * @param name
     */
    @Override
    public void removeTeam(String name) {

        // イベントコール
        ColorTeamingTeamRemoveEvent event =
                new ColorTeamingTeamRemoveEvent(name);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if ( event.isCancelled() ) {
            return;
        }

        Scoreboard scoreboard = getScoreboard();
        Team team = scoreboard.getTeam(name);
        if ( team != null ) {
            for ( OfflinePlayer player : team.getPlayers() ) {
                if ( player.getPlayer() != null && player.isOnline() ) {
                    leavePlayerTeam(player.getPlayer(), Reason.TEAM_REMOVED);
                }
            }
            team.unregister();
        }
    }

    /**
     * 全てのチームを削除する
     */
    @Override
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
    @Override
    public HashMap<String, ArrayList<Player>> getAllTeamMembers() {

        HashMap<String, ArrayList<Player>> result = new HashMap<String, ArrayList<Player>>();
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
    @Override
    public ArrayList<Player> getAllPlayers() {
        Player[] temp = plugin.getServer().getOnlinePlayers();
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
    @Override
    public ArrayList<Player> getAllPlayersOnWorld(List<String> worldNames) {

        if ( worldNames == null ) {
            return null;
        }

        Player[] temp = plugin.getServer().getOnlinePlayers();
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
    @Override
    public ArrayList<String> getAllTeamNames() {

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
    @Override
    public void sendTeamChat(Player player, String message) {

        Team team = getPlayerTeam(player);
        if ( team == null ) {
            return;
        }
        String color = team.getName();

        // 設定に応じて、Japanize化する
        if ( config.isShowJapanizeTeamChat() ) {
            // 2byteコードを含む場合や、半角カタカナしか含まない場合は、
            // 処理しないようにする。
            if ( message.getBytes().length == message.length() &&
                    !message.matches("[ \\uFF61-\\uFF9F]+") ) {
                String kana = KanaConverter.conv(message);
                message = message + "(" + kana + ")";
            }
        }

        // イベントコール
        ColorTeamingTeamChatEvent event =
                new ColorTeamingTeamChatEvent(player, message);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if ( event.isCancelled() ) {
            return;
        }
        message = event.getMessage();

        // メッセージを生成
        String partyMessage = String.format(
                Utility.replaceColorCode(TEAM_CHAT_FORMAT),
                Utility.replaceColors(color) + color,
                player.getDisplayName(),
                message
                );

        // チームメンバに送信する
        ArrayList<Player> playersToSend = getAllTeamMembers().get(color);
        if ( config.isOPDisplayMode() ) {
            Player[] players = plugin.getServer().getOnlinePlayers();
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
        if ( config.isTeamChatLogMode() ) {
            plugin.getLogger().info(partyMessage);
        }
    }

    /**
     * 情報をチームチャットに送信する。
     * @param color 送信先のチーム
     * @param message 送信するメッセージ
     */
    @Override
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
     * サイドバーを新しく作る。
     * もともとサイドバーがあった場合は、削除して再作成される。
     */
    @Override
    public void makeSidebarScore() {

        removeSidebarScore();
        if ( config.getSideCriteria() != SidebarCriteria.NONE ) {
            sidebarScore = new SidebarScoreDisplay(ColorTeaming.instance);
        }
    }

    /**
     * サイドバーを消去する。
     */
    @Override
    public void removeSidebarScore() {

        getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        if ( sidebarScore != null ) {
            sidebarScore.remove();
            sidebarScore = null;
        }
    }

    /**
     * サイドバーのスコアを更新する。
     */
    @Override
    public void refreshSidebarScore() {

        if ( sidebarScore != null ) {
            sidebarScore.refreshScore();
        }
    }

    /**
     * タブキーリストのスコアを新しく作る。
     * もともとスコアがあった場合は、削除して再作成される。
     */
    @Override
    public void makeTabkeyListScore() {

        removeTabkeyListScore();
        if ( config.getListCriteria() != PlayerCriteria.NONE ) {
            tablistScore = new TabListScoreDisplay(ColorTeaming.instance);
        }
    }

    /**
     * タブキーリストのスコアを消去する。
     */
    @Override
    public void removeTabkeyListScore() {

        getScoreboard().clearSlot(DisplaySlot.PLAYER_LIST);
        if ( tablistScore != null ) {
            tablistScore.remove();
            tablistScore = null;
        }
    }

    /**
     * タブキーリストのスコアを更新する。
     */
    @Override
    public void refreshTabkeyListScore() {

        if ( tablistScore != null ) {
            tablistScore.refreshScore();
        }
    }

    /**
     * 名前下のスコアを新しく作る。
     * もともとスコアがあった場合は、削除して再作成される。
     */
    @Override
    public void makeBelowNameScore() {

        removeBelowNameScore();
        if ( config.getBelowCriteria() != PlayerCriteria.NONE ) {
            belownameScore = new BelowNameScoreDisplay(ColorTeaming.instance);
        }
    }

    /**
     * 名前下のスコアを消去する。
     */
    @Override
    public void removeBelowNameScore() {

        getScoreboard().clearSlot(DisplaySlot.BELOW_NAME);
        if ( belownameScore != null ) {
            belownameScore.remove();
            belownameScore = null;
        }
    }

    /**
     * 名前下のスコアを更新する。
     */
    @Override
    public void refreshBelowNameScore() {

        if ( belownameScore != null ) {
            belownameScore.refreshScore();
        }
    }

    /**
     * TeamMemberSaveDataHandler を取得する
     * @return TeamMemberSaveDataHandler
     */
    @Override
    public TeamMemberSaveDataHandler getCTSaveDataHandler() {
        return sdhandler;
    }

    /**
     * キルデス数を全てクリアする
     */
    @Override
    public void clearKillDeathPoints() {

        // イベントコール
        ColorTeamingKillDeathClearedEvent event =
                new ColorTeamingKillDeathClearedEvent();
        Bukkit.getServer().getPluginManager().callEvent(event);

        // クリア
        killDeathCounts.clear();
        killDeathUserCounts.clear();
    }

    /**
     * チーム単位のキルデス数を取得する
     * @return キルデス数
     */
    @Override
    public HashMap<String, int[]> getKillDeathCounts() {
        return killDeathCounts;
    }

    /**
     * ユーザー単位のキルデス数を取得する
     * @return キルデス数
     */
    @Override
    public HashMap<String, int[]> getKillDeathUserCounts() {
        return killDeathUserCounts;
    }

    /**
     * ユーザー単位のキルデス数を設定する
     * @param playerName プレイヤー名
     * @param kill キル数
     * @param death デス数
     * @param tk TK数
     */
    public void setKillDeathUserCounts(String playerName, int kill, int death, int tk) {
        int[] data = new int[]{kill, death, tk};
        killDeathUserCounts.put(playerName, data);
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
        
        // 全てのグループをいったん削除する
        removeAllTeam();

        // グループを設定していく
        for ( int i=0; i<players.size(); i++ ) {
            int group = i % teamNum;
            String color = GROUP_COLORS[group];
            addPlayerTeam(players.get(i), color);
        }

        // 各グループに、通知メッセージを出す
        for ( int i=0; i<teamNum; i++ ) {
            sendInfoToTeamChat(GROUP_COLORS[i],
                    "あなたは " +
                    Utility.replaceColors(GROUP_COLORS[i]) +
                    GROUP_COLORS[i] +
                    ChatColor.GREEN +
                    " グループになりました。");
        }

        // キルデス情報のクリア
        clearKillDeathPoints();

        // スコアボードの作成
        makeSidebarScore();
        makeTabkeyListScore();
        makeBelowNameScore();

        // メンバー情報をlastdataに保存する
        getCTSaveDataHandler().save("lastdata");
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
        
        // 人数の少ないグループに設定していく
        for ( int i=0; i<players.size(); i++ ) {

            // 人数の少ないグループの取得
            HashMap<String, ArrayList<Player>> members = getAllTeamMembers();
            int least = 999;
            String leastGroup = null;

            ArrayList<String> groups = new ArrayList<String>(members.keySet());
            // ランダム要素を入れるため、グループ名をシャッフルする
            Collections.shuffle(groups);

            for ( String group : groups ) {
                if ( least > members.get(group).size() ) {
                    least = members.get(group).size();
                    leastGroup = group;
                }
            }
            
            // グループへプレイヤーを追加
            if ( leastGroup != null ) {
                addPlayerTeam(players.get(i), leastGroup);
                players.get(i).sendMessage(
                        ChatColor.GREEN + "あなたは " +
                        Utility.replaceColors(leastGroup) +
                        leastGroup +
                        ChatColor.GREEN +
                        " グループになりました。");
            } else {
//                sender.sendMessage(
//                        PREERR + "設定できるグループが無いようです。");
                return false;
            }
        }

        // スコアボードの作成
        makeSidebarScore();
        refreshTabkeyListScore();
        refreshBelowNameScore();

        // メンバー情報をlastdataに保存する
        getCTSaveDataHandler().save("lastdata");
        
        return true;
    }
    
    /**
     * ColorTeamingの設定ファイルを全て再読み込みする
     */
    @Override
    public void realod() {
        
        ColorTeaming.instance.config = ColorTeamingConfig.loadConfig();
        respawnConfig = new RespawnConfiguration();
        tppointConfig = new TPPointConfiguration();
        teamNameConfig = new TeamNameConfig();
    }
}
