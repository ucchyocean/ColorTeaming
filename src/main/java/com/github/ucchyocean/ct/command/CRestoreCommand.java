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
import com.github.ucchyocean.ct.config.TeamMemberSaveDataHandler;

/**
 * colorrestore(crestore)コマンドの実行クラス
 * @author ucchy
 */
public class CRestoreCommand implements CommandExecutor {

    private ColorTeaming plugin;

    public CRestoreCommand(ColorTeaming plugin) {
        this.plugin = plugin;
    }

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

        TeamMemberSaveDataHandler sdhandler = plugin.getAPI().getCTSaveDataHandler();

        if ( !sdhandler.isExist(profileName) ) {
            sender.sendMessage(PREERR + profileName + " というデータが存在しません。");
            return true;
        }

        boolean result = sdhandler.load(profileName);

        if ( result ) {
            sender.sendMessage(PREINFO + "メンバー状況を、" + profileName + " から復帰しました。");

            // スコアボードの作成
            plugin.getAPI().makeSidebarScore();
            plugin.getAPI().makeTabkeyListScore();
            plugin.getAPI().makeBelowNameScore();

        } else {
            sender.sendMessage(PREERR + "メンバー状況の復帰に失敗しました。");
        }

        return true;
    }
}
