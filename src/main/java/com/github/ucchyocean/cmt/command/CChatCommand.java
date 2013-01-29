/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.cmt.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.github.ucchyocean.cmt.ColorMeTeaming;

/**
 * @author ucchy
 * colorchat(cchat)コマンドの実行クラス
 */
public class CChatCommand implements CommandExecutor {

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
            return true;
        } else if ( args[0].equalsIgnoreCase("off") ) {
            ColorMeTeaming.isTeamChatMode = false;
            sender.sendMessage(ChatColor.RED + "チームチャットを一時的に無効にしました。");
            return true;
        } else if ( args[0].equalsIgnoreCase("opcopyon") ) {
            ColorMeTeaming.isOPDisplayMode = true;
            sender.sendMessage(ChatColor.RED + "チームチャットをOPにも表示します。");
            return true;
        } else if ( args[0].equalsIgnoreCase("opcopyoff") ) {
            ColorMeTeaming.isOPDisplayMode = false;
            sender.sendMessage(ChatColor.RED + "チームチャットのOPへの表示をオフにします。");
            return true;
        }

        return false;
    }

}
