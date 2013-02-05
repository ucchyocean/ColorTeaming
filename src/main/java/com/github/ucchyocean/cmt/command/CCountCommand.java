/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.cmt.command;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.cmt.ColorMeTeaming;

/**
 * @author ucchy
 * colorcount(cc)コマンド、colorcountsay(ccsay)コマンドの実行クラス
 */
public class CCountCommand implements CommandExecutor {

    private static final String PRE_LINE_MESSAGE =
            "=== Team Member Information ===";

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        boolean isBroadcast = false;
        if ( command.getName().equals("colorcountsay") ||
                (args.length >= 1 && args[0].equalsIgnoreCase("say") ))
            isBroadcast = true;

        // メンバー情報の取得
        Hashtable<String, Vector<Player>> members =
                ColorMeTeaming.getAllColorMembers();

        // 最初の行を送信
        if ( !isBroadcast ) {
            sender.sendMessage(ChatColor.GRAY + PRE_LINE_MESSAGE);
        } else {
            ColorMeTeaming.sendBroadcast(ChatColor.LIGHT_PURPLE + PRE_LINE_MESSAGE);
        }

        // メンバー情報を送信
        Enumeration<String> keys = members.keys();
        while ( keys.hasMoreElements() ) {

            String key = (String)keys.nextElement();
            Vector<Player> member = members.get(key);

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
                ColorMeTeaming.sendBroadcast( String.format("%s* %s - %d",
                        pre, key, member.size()));
                ColorMeTeaming.sendBroadcast(pre + value);
            }
        }

        return true;
    }
}
