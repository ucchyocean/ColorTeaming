/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.github.ucchyocean.ct.ColorTeaming;

/**
 * colorsave(csave)コマンドの実行クラス
 * @author ucchy
 */
public class CSaveCommand implements CommandExecutor {

    private static final String PREERR = ChatColor.RED.toString();
    private static final String PREINFO = ChatColor.GRAY.toString();

    private ColorTeaming plugin;

    public CSaveCommand(ColorTeaming plugin) {
        this.plugin = plugin;
    }

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        String profileName = "lastdata";
        if ( args.length >= 1 ) {
            profileName = args[0];
        }

        boolean result = plugin.getAPI().getCTSaveDataHandler().save(profileName);

        if ( result ) {
            sender.sendMessage(PREINFO + "現在のメンバー状況を、" + profileName + " に保存しました。");
        } else {
            sender.sendMessage(PREERR + "現在のメンバー状況保存に失敗しました。");
        }

        return true;
    }

}
