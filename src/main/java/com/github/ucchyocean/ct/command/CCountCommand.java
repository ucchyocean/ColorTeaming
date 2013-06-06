/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.command;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.ct.ColorTeaming;

/**
 * colorcount(cc)コマンド、colorcountsay(ccsay)コマンドの実行クラス
 * @author ucchy
 */
public class CCountCommand implements CommandExecutor {

    private static final String PRE_LINE_MESSAGE =
            "=== Team Member Information ===";

    private ColorTeaming plugin;

    public CCountCommand(ColorTeaming plugin) {
        this.plugin = plugin;
    }

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        // 引数の処理
        boolean isBroadcast = false;
        boolean isAll = false;

        if ( command.getName().equals("colorcountsay") ) {
            isBroadcast = true;
            if ( args.length >= 1 && args[0].equalsIgnoreCase("all") ) {
                isAll = true;
            }
        } else if ( args.length >= 1 && args[0].equalsIgnoreCase("say") ) {
            isBroadcast = true;
            if ( args.length >= 2 && args[1].equalsIgnoreCase("all") ) {
                isAll = true;
            }
        } else if ( args.length >= 1 && args[0].equalsIgnoreCase("all") ) {
            isAll = true;
        }

        // メンバー情報の取得
        HashMap<String, ArrayList<Player>> members;
        if ( !isAll ) {
            members = plugin.getAPI().getAllTeamMembers();
        } else {
            members = new HashMap<String, ArrayList<Player>>();
            ArrayList<Player> players = plugin.getAPI().getAllPlayers();
            for ( Player p : players ) {
                String color = plugin.getAPI().getPlayerTeamName(p);
                if ( members.containsKey(color) ) {
                    members.get(color).add(p);
                } else {
                    ArrayList<Player> data = new ArrayList<Player>();
                    data.add(p);
                    members.put(color, data);
                }
            }
        }

        // ccコマンドの実行
        sendCCMessage(sender, members, isBroadcast);

        return true;
    }

    /**
     * ccコマンドを実行して、実行結果を表示する。
     * @param sender コマンド実行者
     * @param members カウント対象のメンバー情報
     * @param isBroadcast ブロードキャストかどうか
     */
    protected static void sendCCMessage(CommandSender sender,
            HashMap<String, ArrayList<Player>> members, boolean isBroadcast) {

        // 最初の行を送信
        if ( !isBroadcast ) {
            sender.sendMessage(ChatColor.GRAY + PRE_LINE_MESSAGE);
        } else {
            Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + PRE_LINE_MESSAGE);
        }

        // メンバー情報を送信
        for ( String key : members.keySet() ) {

            ArrayList<Player> member = members.get(key);

            StringBuilder value = new StringBuilder();
            for ( Player p : member ) {
                if ( value.length() != 0 ) {
                    value.append(",");
                }
                value.append(p.getName());
            }

            if ( !isBroadcast ) {
                String pre = ChatColor.GRAY.toString();
                sender.sendMessage(String.format("%s* %s - %d",
                        pre, key, member.size()));
                sender.sendMessage(pre + value);
            } else {
                String pre = ChatColor.RED.toString();
                Bukkit.broadcastMessage( String.format("%s* %s - %d",
                        pre, key, member.size()));
                Bukkit.broadcastMessage(pre + value);
            }
        }
    }
}
