/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2015
 */
package com.github.ucchyocean.ct.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.Utility;
import com.github.ucchyocean.ct.config.NametagVisibilityEnum;

/**
 *
 * @author ucchy
 */
public class CNametagCommand implements TabExecutor {

    private static final String PREERR = ChatColor.RED.toString();
    private static final String PREINFO = ChatColor.GRAY.toString();

    private ColorTeaming plugin;

    public CNametagCommand(ColorTeaming plugin) {
        this.plugin = plugin;
    }

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length <= 0 ) {
            return false;
        }

        if ( !Utility.isCB18orLater() ) {
            sender.sendMessage(PREERR + "このコマンドは、Bukkit 1.7.x 以前のバージョンでは使えません。");
            return true;
        }

        if ( args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("always") ) {
            plugin.getAPI().setNametagVisibility(NametagVisibilityEnum.ALWAYS);
            sender.sendMessage(PREINFO + "ネームタグを表示に設定しました。");
            return true;
        } else if ( args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("never") ) {
            plugin.getAPI().setNametagVisibility(NametagVisibilityEnum.NEVER);
            sender.sendMessage(PREINFO + "ネームタグを非表示に設定しました。");
            return true;
        } else if ( args[0].equalsIgnoreCase("team") || args[0].equalsIgnoreCase("hide_for_other_teams") ) {
            plugin.getAPI().setNametagVisibility(NametagVisibilityEnum.HIDE_FOR_OTHER_TEAMS);
            sender.sendMessage(PREINFO + "ネームタグを他チームから非表示に設定しました。");
            return true;
        } else if ( args[0].equalsIgnoreCase("other") || args[0].equalsIgnoreCase("hide_for_own_team") ) {
            plugin.getAPI().setNametagVisibility(NametagVisibilityEnum.HIDE_FOR_OWN_TEAM);
            sender.sendMessage(PREINFO + "ネームタグを自チームから非表示に設定しました。");
            return true;
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
            for ( String c : new String[]{"on", "off", "team", "other", "always", "never",
                    "hide_for_other_teams", "hide_for_own_team"} ) {
                if ( c.startsWith(prefix) ) {
                    commands.add(c);
                }
            }
            return commands;
        }

        return null;
    }
}
