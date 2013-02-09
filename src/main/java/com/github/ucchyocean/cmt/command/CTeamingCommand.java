/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.cmt.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.github.ucchyocean.cmt.ColorMeTeamingConfig;

/**
 * @author ucchy
 * colorteaming(ct)コマンドの実行クラス
 */
public class CTeamingCommand implements CommandExecutor {

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length < 1 ) {
            return false;
        }

        if ( args[0].equalsIgnoreCase("reload") ) {

            ColorMeTeamingConfig.reloadConfig();
            sender.sendMessage("config.ymlの再読み込みを行いました。");
            return true;
        }

        return false;
    }

}
