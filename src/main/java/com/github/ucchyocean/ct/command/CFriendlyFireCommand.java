/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.ColorTeamingConfig;

/**
 * @author ucchy
 * colorfriendlyfire(cff)コマンドの実行クラス
 */
public class CFriendlyFireCommand implements CommandExecutor {

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length <= 0 ) {
            return false;
        }

        if ( args[0].equalsIgnoreCase("on") ) {
            ColorTeamingConfig.isFriendlyFireDisabler = true;
            ColorTeaming.setFriendlyFilre(false);
            sender.sendMessage(ChatColor.GRAY + "仲間同士の攻撃が無効になりました。");
            ColorTeamingConfig.setConfigValue("friendlyFireDisabler", true);
            return true;
        } else if ( args[0].equalsIgnoreCase("off") ) {
            ColorTeamingConfig.isFriendlyFireDisabler = false;
            ColorTeaming.setFriendlyFilre(true);
            sender.sendMessage(ChatColor.GRAY + "仲間同士の攻撃が有効になりました。");
            ColorTeamingConfig.setConfigValue("friendlyFireDisabler", false);
            return true;
        }

        if ( args.length >= 2 && args[0].equalsIgnoreCase("invisible") ) {
            if ( args[1].equalsIgnoreCase("on") ) {
                ColorTeamingConfig.canSeeFriendlyInvisibles = true;
                ColorTeaming.setSeeFriendlyInvisibles(true);
                sender.sendMessage(ChatColor.GRAY + "仲間同士の透明化が見えるようになりました。");
                ColorTeamingConfig.setConfigValue("seeFriendlyInvisible", true);
                return true;
            } else if ( args[1].equalsIgnoreCase("off") ) {
                ColorTeamingConfig.canSeeFriendlyInvisibles = false;
                ColorTeaming.setSeeFriendlyInvisibles(false);
                sender.sendMessage(ChatColor.GRAY + "仲間同士の透明化が見えないようになりました。");
                ColorTeamingConfig.setConfigValue("seeFriendlyInvisible", false);
                return true;
            }
        }

        return false;
    }

}
