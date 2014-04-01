/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.github.ucchyocean.ct.ColorTeaming;

/**
 * colorfriendlyfire(cff)コマンドの実行クラス
 * @author ucchy
 */
public class CFriendlyFireCommand implements TabExecutor {

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

    /**
     * @see org.bukkit.command.TabCompleter#onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public List<String> onTabComplete(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length == 1 ) {

            String prefix = args[0].toLowerCase();
            ArrayList<String> commands = new ArrayList<String>();
            for ( String c : new String[]{"on", "off", "invisible"} ) {
                if ( c.startsWith(prefix) ) {
                    commands.add(c);
                }
            }
            return commands;

        } else if ( args.length == 2 && args[0].equalsIgnoreCase("invisible") ) {

            String prefix = args[1].toLowerCase();
            ArrayList<String> commands = new ArrayList<String>();
            for ( String c : new String[]{"on", "off"} ) {
                if ( c.startsWith(prefix) ) {
                    commands.add(c);
                }
            }
            return commands;

        }

        return null;
    }
}
