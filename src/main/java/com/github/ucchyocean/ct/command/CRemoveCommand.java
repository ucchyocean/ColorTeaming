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
 * ColorRemove(CR)コマンドの実行クラス
 */
public class CRemoveCommand implements CommandExecutor {

    /**
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length <= 0 ) {
            return false;
        }

        if ( args[0].equalsIgnoreCase("on") ) {
            setColorRemoveOnDeath(sender, true);
            setColorRemoveOnQuit(sender, true);
            return true;
        } else if ( args[0].equalsIgnoreCase("off") ) {
            setColorRemoveOnDeath(sender, false);
            setColorRemoveOnQuit(sender, false);
            return true;
        } else if ( args[0].equalsIgnoreCase("death") && args.length >= 2 ) {
            if ( args[1].equalsIgnoreCase("off") ) {
                setColorRemoveOnDeath(sender, false);
            } else {
                setColorRemoveOnDeath(sender, true);
            }
            return true;
        } else if ( args[0].equalsIgnoreCase("quit") && args.length >= 2 ) {
            if ( args[1].equalsIgnoreCase("off") ) {
                setColorRemoveOnQuit(sender, false);
            } else {
                setColorRemoveOnQuit(sender, true);
            }
            return true;
        }

        return false;
    }

    /**
     * 死亡時のチーム離脱の有効/無効を切り替える
     * @param sender メッセージ送信先
     * @param enable 有効/無効
     */
    private void setColorRemoveOnDeath(CommandSender sender, boolean enable) {
        ColorTeamingConfig config = ColorTeaming.instance.getCTConfig();
        config.setColorRemoveOnDeath(enable);
        config.saveConfig();
        String msg = enable ? "有効" : "無効";
        sender.sendMessage(ChatColor.GRAY +
                "死亡時のチーム離脱が" + msg + "になりました。");
    }

    /**
     * ログアウト時のチーム離脱の有効/無効を切り替える
     * @param sender メッセージ送信先
     * @param enable 有効/無効
     */
    private void setColorRemoveOnQuit(CommandSender sender, boolean enable) {
        ColorTeamingConfig config = ColorTeaming.instance.getCTConfig();
        config.setColorRemoveOnQuit(enable);
        config.saveConfig();
        String msg = enable ? "有効" : "無効";
        sender.sendMessage(ChatColor.GRAY +
                "ログアウト時のチーム離脱が" + msg + "になりました。");
    }
}
