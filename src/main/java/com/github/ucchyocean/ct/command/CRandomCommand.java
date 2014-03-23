/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.command;

import java.util.ArrayList;
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
import com.github.ucchyocean.ct.config.ColorTeamingConfig;

/**
 * ColorRandom(rc)コマンドの実行クラス
 * @author ucchy
 */
public class CRandomCommand implements CommandExecutor {

    private static final String PREERR = ChatColor.RED.toString();

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

        // 設定するチーム数を、1番目の引数から取得する
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
                    PREERR + "設定されたワールドに、対象プレイヤーがいないようです。");
            return true;
        }

        // チームわけの実行
        api.makeColorTeamsWithRandomSelection(players, numberOfGroups);

        // メンバー情報の取得
        HashMap<String, ArrayList<Player>> members =
                api.getAllTeamMembers();

        // コマンド完了を、CCメッセージで通知する
        CCountCommand.sendCCMessage(sender, members, false);

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
                    (team == null || team.getName().equals("") )) {
                players.add(p);
            }
        }
        if ( players.size() == 0 ) {
            sender.sendMessage(
                    PREERR + "設定されたワールドに、対象プレイヤーがいないようです。");
            return true;
        }

        // チームがあるかどうかを確認する
        if ( api.getAllTeamNames().size() == 0 ) {
            sender.sendMessage(
                    PREERR + "設定できるチームが無いようです。");
            return true;
        }

        // チームへのプレイヤー追加実行
        api.addPlayerToColorTeamsWithRandomSelection(players);

        // メンバー情報の取得
        HashMap<String, ArrayList<Player>> members =
                api.getAllTeamMembers();

        // コマンド完了を、CCメッセージで通知する
        CCountCommand.sendCCMessage(sender, members, false);

        return true;
    }
}
