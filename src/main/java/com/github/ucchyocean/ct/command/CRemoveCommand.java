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
import com.github.ucchyocean.ct.config.ColorTeamingConfig;

/**
 * ColorRemove(CR)コマンドの実行クラス
 * @author ucchy
 */
public class CRemoveCommand implements TabExecutor {

    private ColorTeaming plugin;

    public CRemoveCommand(ColorTeaming plugin) {
        this.plugin = plugin;
    }

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
            setColorRemoveOnChangeWorld(sender, true);
            return true;
        } else if ( args[0].equalsIgnoreCase("off") ) {
            setColorRemoveOnDeath(sender, false);
            setColorRemoveOnQuit(sender, false);
            setColorRemoveOnChangeWorld(sender, false);
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
        } else if ( args[0].equalsIgnoreCase("changeworld") && args.length >= 2 ) {
            if ( args[1].equalsIgnoreCase("off") ) {
                setColorRemoveOnChangeWorld(sender, false);
            } else {
                setColorRemoveOnChangeWorld(sender, true);
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
        ColorTeamingConfig config = plugin.getCTConfig();
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
        ColorTeamingConfig config = plugin.getCTConfig();
        config.setColorRemoveOnQuit(enable);
        config.saveConfig();
        String msg = enable ? "有効" : "無効";
        sender.sendMessage(ChatColor.GRAY +
                "ログアウト時のチーム離脱が" + msg + "になりました。");
    }

    /**
     * ワールド変更時のチーム離脱の有効/無効を切り替える
     * @param sender メッセージ送信先
     * @param enable 有効/無効
     */
    private void setColorRemoveOnChangeWorld(CommandSender sender, boolean enable) {
        ColorTeamingConfig config = plugin.getCTConfig();
        config.setColorRemoveOnChangeWorld(enable);
        config.saveConfig();
        String msg = enable ? "有効" : "無効";
        sender.sendMessage(ChatColor.GRAY +
                "ワールド変更時のチーム離脱が" + msg + "になりました。");
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
            for ( String c : new String[]{"on", "off", "death", "quit", "changeworld"} ) {
                if ( c.startsWith(prefix) ) {
                    commands.add(c);
                }
            }
            return commands;

        } else if ( args.length == 2 &&
                        (args[0].equalsIgnoreCase("death") ||
                        args[0].equalsIgnoreCase("quit") ||
                        args[0].equalsIgnoreCase("changeworld") ) ) {

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
