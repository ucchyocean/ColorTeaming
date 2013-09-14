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
 * colorfriendlyfire(cff)コマンドの実行クラス
 * @author ucchy
 */
public class CFriendlyFireCommand implements CommandExecutor {

    private ColorTeaming plugin;

    public CFriendlyFireCommand(ColorTeaming plugin) {
        this.plugin = plugin;
    }

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length <= 0 ) {
            return false;
        }

        if ( args[0].equalsIgnoreCase("on") ) {
            plugin.getAPI().setFriendlyFire(true);
            sender.sendMessage(ChatColor.GRAY + "仲間同士の攻撃が有効になりました。");
            return true;
        } else if ( args[0].equalsIgnoreCase("off") ) {
            plugin.getAPI().setFriendlyFire(false);
            sender.sendMessage(ChatColor.GRAY + "仲間同士の攻撃が無効になりました。");
            return true;
        }

        if ( args.length >= 2 && args[0].equalsIgnoreCase("invisible") ) {
            if ( args[1].equalsIgnoreCase("on") ) {
                plugin.getAPI().setSeeFriendlyInvisibles(true);
                sender.sendMessage(ChatColor.GRAY + "仲間同士の透明化が見えるようになりました。");
                return true;
            } else if ( args[1].equalsIgnoreCase("off") ) {
                plugin.getAPI().setSeeFriendlyInvisibles(false);
                sender.sendMessage(ChatColor.GRAY + "仲間同士の透明化が見えないようになりました。");
                return true;
            }
        }

        return false;
    }

}
