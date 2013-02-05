/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.cmt.command;

import java.util.Random;
import java.util.Vector;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.cmt.ColorMeTeaming;

/**
 * @author ucchy
 * colorteaming(ct)コマンドの実行クラス
 */
public class CTeamingCommand implements CommandExecutor {

    private static final String[] GROUP_COLORS =
        {"red", "blue", "yellow", "green", "aqua", "gray", "dark_red", "dark_green", "dark_aqua"};

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length < 1 ) {
            return false;
        }

        if ( args[0].equalsIgnoreCase("reload") ) {

            ColorMeTeaming.reloadConfigFile();
            sender.sendMessage("config.ymlの再読み込みを行いました。");
            return true;

        } else if ( args[0].equalsIgnoreCase("random") ) {

            // 設定するグループ数を、2番目の引数から取得する
            int numberOfGroups = 2;
            if ( args.length >= 2 && args[1].matches("[2-9]") ) {
                numberOfGroups = Integer.parseInt(args[1]);
            }

            // ゲームモードがクリエイティブの人は除外する
            Vector<Player> players = ColorMeTeaming.getAllPlayers();
            for ( Player p : players ) {
                if ( p.getGameMode() == GameMode.CREATIVE ) {
                    players.remove(p);
                }
            }

            // シャッフル
            Random rand = new Random();
            for ( int i=0; i<players.size(); i++ ) {
                int j = rand.nextInt(players.size());
                Player temp = players.elementAt(i);
                players.set(i, players.elementAt(j));
                players.set(j, temp);
            }

            // グループを設定していく
            for ( int i=0; i<players.size(); i++ ) {
                int group = i % numberOfGroups;
                String color = GROUP_COLORS[group];
                ColorMeTeaming.setPlayerColor(players.elementAt(i), color);
            }

            // 各グループに、通知メッセージを出す
            for ( int i=0; i<numberOfGroups; i++ ) {
                ColorMeTeaming.sendTeamChat(GROUP_COLORS[i],
                        "あなたは " + GROUP_COLORS[i] + " グループになりました。");
            }
        }

        return false;
    }

}
