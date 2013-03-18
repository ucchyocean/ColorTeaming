/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.github.ucchyocean.ct.ColorTeaming;

/**
 * @author ucchy
 * colorsave(csave)コマンドの実行クラス
 */
public class CSaveCommand implements CommandExecutor {

    private static final String PREERR = ChatColor.RED.toString();
    private static final String PREINFO = ChatColor.GRAY.toString();

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        String profileName = "lastdata";
        if ( args.length >= 1 ) {
            profileName = args[0];
        }

        boolean result = ColorTeaming.sdhandler.save(profileName);

        if ( result ) {
            sender.sendMessage(PREINFO + "現在のメンバー状況を、" + profileName + " に保存しました。");
        } else {
            sender.sendMessage(PREERR + "現在のメンバー状況保存に失敗しました。");
        }

        return true;
    }

}
