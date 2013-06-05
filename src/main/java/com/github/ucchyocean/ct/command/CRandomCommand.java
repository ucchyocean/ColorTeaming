/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.ColorTeamingAPI;
import com.github.ucchyocean.ct.ColorTeamingConfig;
import com.github.ucchyocean.ct.Utility;

/**
 * ColorRandom(CR)コマンドの実行クラス
 * @author ucchy
 */
public class CRandomCommand implements CommandExecutor {

    private static final String PREERR = ChatColor.RED.toString();

    private static final String[] GROUP_COLORS =
        {"red", "blue", "yellow", "green", "aqua", "gray", "dark_red", "dark_green", "dark_aqua"};

    private ColorTeaming plugin;

    public CRandomCommand(ColorTeaming plugin) {
        this.plugin = plugin;
    }

    /**
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        // rest指定の場合は、別メソッドで処理する
        if ( args.length >= 1 && args[0].equalsIgnoreCase("rest") ) {
            return onRestCommand(sender, args);
        }

        // 設定するグループ数を、1番目の引数から取得する
        int numberOfGroups = 2;
        if ( args.length >= 1 && args[0].matches("[2-9]") ) {
            numberOfGroups = Integer.parseInt(args[0]);
        }

        ColorTeamingConfig config = plugin.getCTConfig();
        ColorTeamingAPI api = plugin.getAPI();

        // ゲームモードがクリエイティブの人は除外する
        ArrayList<Player> tempPlayers =
                api.getAllPlayersOnWorld(config.getWorldNames());
        ArrayList<Player> players = new ArrayList<Player>();
        for ( Player p : tempPlayers ) {
            if ( p.getGameMode() != GameMode.CREATIVE ) {
                players.add(p);
            }
        }
        if ( players.size() == 0 ) {
            sender.sendMessage(
                    PREERR + "対象のワールドに、誰も居ないようです。");
            return true;
        }

        // 全てのグループをいったん削除する
        api.removeAllTeam();

        // シャッフル
        Collections.shuffle(players);

        // グループを設定していく
        for ( int i=0; i<players.size(); i++ ) {
            int group = i % numberOfGroups;
            String color = GROUP_COLORS[group];
            api.addPlayerTeam(players.get(i), color);
        }

        // 各グループに、通知メッセージを出す
        for ( int i=0; i<numberOfGroups; i++ ) {
            api.sendInfoToTeamChat(GROUP_COLORS[i],
                    "あなたは " +
                    Utility.replaceColors(GROUP_COLORS[i]) +
                    GROUP_COLORS[i] +
                    ChatColor.GREEN +
                    " グループになりました。");
        }

        // キルデス情報のクリア
        api.clearKillDeathPoints();

        // スコアボードの作成
        api.makeSidebar();
        api.makeTabkeyListScore();
        api.makeBelowNameScore();

        // メンバー情報の取得
        HashMap<String, ArrayList<Player>> members = api.getAllTeamMembers();

        // コマンド完了を、CCメッセージで通知する
        CCountCommand.sendCCMessage(sender, members, false);

        // メンバー情報をlastdataに保存する
        api.getCTSaveDataHandler().save("lastdata");

        return true;
    }

    /**
     * コマンド /crandom rest の実行処理
     * @param sender
     * @param args
     * @return
     */
    private boolean onRestCommand(CommandSender sender, String[] args) {

        ColorTeamingConfig config = plugin.getCTConfig();
        ColorTeamingAPI api = plugin.getAPI();

        // ゲームモードがクリエイティブの人や、既に色が設定されている人は除外する
        ArrayList<Player> tempPlayers =
                api.getAllPlayersOnWorld(config.getWorldNames());
        ArrayList<Player> players = new ArrayList<Player>();
        for ( Player p : tempPlayers ) {
            Team team = api.getPlayerTeam(p);
            if ( p.getGameMode() != GameMode.CREATIVE &&
                    (team == null || team.getName().equals("") ||
                            team.getName().equals("white")) ) {
                players.add(p);
            }
        }
        if ( players.size() == 0 ) {
            sender.sendMessage(
                    PREERR + "設定されたワールドに、対象プレイヤーがいないようです。");
            return true;
        }

        // シャッフル
        Collections.shuffle(players);

        // 人数の少ないグループに設定していく
        for ( int i=0; i<players.size(); i++ ) {
            String color = getLeastGroup();
            if ( color != null ) {
                api.addPlayerTeam(players.get(i), color);
                players.get(i).sendMessage(
                        ChatColor.GREEN + "あなたは " +
                        Utility.replaceColors(color) +
                        color +
                        ChatColor.GREEN +
                        " グループになりました。");
            } else {
                sender.sendMessage(
                        PREERR + "設定できるグループが無いようです。");
                return true;
            }
        }

        // スコアボードの作成
        api.makeSidebar();
        api.refreshTabkeyListScore();
        api.refreshBelowNameScore();

        // メンバー情報の取得
        HashMap<String, ArrayList<Player>> members =
                api.getAllTeamMembers();

        // コマンド完了を、CCメッセージで通知する
        CCountCommand.sendCCMessage(sender, members, false);

        // メンバー情報をlastdataに保存する
        api.getCTSaveDataHandler().save("lastdata");

        return true;
    }

    /**
     * メンバー人数が最小のグループを返す。
     * @return メンバー人数が最小のグループ
     */
    private String getLeastGroup() {

        HashMap<String, ArrayList<Player>> members =
                plugin.getAPI().getAllTeamMembers();
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

        return leastGroup;

    }
}
