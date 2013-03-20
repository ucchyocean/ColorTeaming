/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.command;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.ColorTeamingConfig;
import com.github.ucchyocean.ct.Utility;

/**
 * @author ucchy
 * ColorRandom(CR)コマンドの実行クラス
 */
public class CRandomCommand implements CommandExecutor {

    private static final String PREERR = ChatColor.RED.toString();

    private static final String[] GROUP_COLORS =
        {"red", "blue", "yellow", "green", "aqua", "gray", "dark_red", "dark_green", "dark_aqua"};

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

        // ゲームモードがクリエイティブの人は除外する
        ArrayList<Player> tempPlayers =
                ColorTeaming.getAllPlayersOnWorld(ColorTeamingConfig.defaultWorldName);
        ArrayList<Player> players = new ArrayList<Player>();
        for ( Player p : tempPlayers ) {
            if ( p.getGameMode() != GameMode.CREATIVE ) {
                players.add(p);
            }
        }
        if ( players.size() == 0 ) {
            sender.sendMessage(
                    PREERR + "ワールド " +
                    ColorTeamingConfig.defaultWorldName +
                    " に、誰も居ないようです。");
            return true;
        }

        // 全てのグループをいったん削除する
        ColorTeaming.removeAllTeam();

        // シャッフル
        Random rand = new Random();
        for ( int i=0; i<players.size(); i++ ) {
            int j = rand.nextInt(players.size());
            Player temp = players.get(i);
            players.set(i, players.get(j));
            players.set(j, temp);
        }

        // グループを設定していく
        for ( int i=0; i<players.size(); i++ ) {
            int group = i % numberOfGroups;
            String color = GROUP_COLORS[group];
            ColorTeaming.addPlayerTeam(players.get(i), color);
        }

        // 各グループに、通知メッセージを出す
        for ( int i=0; i<numberOfGroups; i++ ) {
            ColorTeaming.sendTeamChat(GROUP_COLORS[i],
                    "あなたは " +
                    Utility.replaceColors(GROUP_COLORS[i]) +
                    GROUP_COLORS[i] +
                    ChatColor.GREEN +
                    " グループになりました。");
        }

        // 保護領域の更新
        if ( ColorTeamingConfig.protectRespawnPointWithWorldGuard ) {
            ColorTeaming.wghandler.refreshGroupMembers();
        }

        // スコアボードの作成
        ColorTeaming.makeSidebar();
        ColorTeaming.makeTabkeyListScore();

        // メンバー情報の取得
        Hashtable<String, ArrayList<Player>> members =
                ColorTeaming.getAllTeamMembers();

        // コマンド完了を、CCメッセージで通知する
        CCountCommand.sendCCMessage(sender, members, false);

        // メンバー情報をlastdataに保存する
        ColorTeaming.sdhandler.save("lastdata");

        return true;
    }

    /**
     * コマンド /crandom rest の実行処理
     * @param sender
     * @param args
     * @return
     */
    private boolean onRestCommand(CommandSender sender, String[] args) {

        // ゲームモードがクリエイティブの人や、既に色が設定されている人は除外する
        ArrayList<Player> tempPlayers =
                ColorTeaming.getAllPlayersOnWorld(ColorTeamingConfig.defaultWorldName);
        ArrayList<Player> players = new ArrayList<Player>();
        for ( Player p : tempPlayers ) {
            if ( p.getGameMode() != GameMode.CREATIVE &&
                    (ColorTeaming.getPlayerTeam(p).equals("") ||
                     ColorTeaming.getPlayerTeam(p).equals("white")) ) {
                players.add(p);
            }
        }
        if ( players.size() == 0 ) {
            sender.sendMessage(
                    PREERR + "ワールド " +
                    ColorTeamingConfig.defaultWorldName +
                    " に、対象プレイヤーがいないようです。");
            return true;
        }

        // シャッフル
        Random rand = new Random();
        for ( int i=0; i<players.size(); i++ ) {
            int j = rand.nextInt(players.size());
            Player temp = players.get(i);
            players.set(i, players.get(j));
            players.set(j, temp);
        }

        // 人数の少ないグループに設定していく
        for ( int i=0; i<players.size(); i++ ) {
            String color = getLeastGroup();
            if ( color != null ) {
                ColorTeaming.addPlayerTeam(players.get(i), color);
                players.get(i).sendMessage(
                        ChatColor.GREEN + "あなたは " +
                        Utility.replaceColors(GROUP_COLORS[i]) +
                        GROUP_COLORS[i] +
                        ChatColor.GREEN +
                        " グループになりました。");
            }
        }

        // 保護領域の更新
        if ( ColorTeamingConfig.protectRespawnPointWithWorldGuard ) {
            ColorTeaming.wghandler.refreshGroupMembers();
        }

        // スコアボードの作成
        // TODO: 要確認。TABキーリストはリフレッシュでいい…はず。
        ColorTeaming.makeSidebar();
        ColorTeaming.refreshTabkeyListScore();

        // メンバー情報の取得
        Hashtable<String, ArrayList<Player>> members =
                ColorTeaming.getAllTeamMembers();

        // コマンド完了を、CCメッセージで通知する
        CCountCommand.sendCCMessage(sender, members, false);

        // メンバー情報をlastdataに保存する
        ColorTeaming.sdhandler.save("lastdata");

        return true;
    }

    /**
     * メンバー人数が最小のグループを返す。
     * @return メンバー人数が最小のグループ
     */
    private String getLeastGroup() {

        Hashtable<String, ArrayList<Player>> members =
                ColorTeaming.getAllTeamMembers();
        int least = 999;
        String leastGroup = null;

        Enumeration<String> keys = members.keys();
        while ( keys.hasMoreElements() ) {
            String key = keys.nextElement();
            if ( least > members.get(key).size() ) {
                least = members.get(key).size();
                leastGroup = key;
            }
        }

        return leastGroup;
    }
}
