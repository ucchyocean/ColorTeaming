/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.cmt.command;

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
 * colorchat(cchat)コマンドの実行クラス
 */
public class CChatCommand implements CommandExecutor {

    private static final String PREERR = ChatColor.RED.toString();

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length <= 0 ) {
            return false;
        }

        if ( args[0].equalsIgnoreCase("on") ) {
            ColorMeTeaming.isTeamChatMode = true;
            sender.sendMessage(ChatColor.RED + "チームチャットモードになりました。");
            ColorMeTeaming.setConfigValue("teamChatMode", true);
            return true;
        } else if ( args[0].equalsIgnoreCase("off") ) {
            ColorMeTeaming.isTeamChatMode = false;
            sender.sendMessage(ChatColor.RED + "チームチャットを一時的に無効にしました。");
            ColorMeTeaming.setConfigValue("teamChatMode", false);
            return true;
        } else if ( args[0].equalsIgnoreCase("opcopyon") ) {
            ColorMeTeaming.isOPDisplayMode = true;
            sender.sendMessage(ChatColor.RED + "チームチャットをOPにも表示します。");
            ColorMeTeaming.setConfigValue("opDisplayMode", true);
            return true;
        } else if ( args[0].equalsIgnoreCase("opcopyoff") ) {
            ColorMeTeaming.isOPDisplayMode = false;
            sender.sendMessage(ChatColor.RED + "チームチャットのOPへの表示をオフにします。");
            ColorMeTeaming.setConfigValue("opDisplayMode", false);
            return true;
        } else if ( args.length >= 2 ){
            // グループにメッセージ送信
            String group = args[0];

            Hashtable<String, Vector<Player>> members = ColorMeTeaming.getAllColorMembers();

            // 有効なグループ名が指定されたか確認する
            if ( !members.containsKey(group) ) {
                sender.sendMessage(PREERR + "グループ " + group + " が存在しません。");
                return true;
            }

            // メッセージの整形
            StringBuilder message = new StringBuilder();
            for ( int i=1; i<args.length; i++ ) {
                message.append(" " + args[i]);
            }

            // 送信
            ColorMeTeaming.sendTeamChat(group, message.toString());
        }

        return false;
    }

}
