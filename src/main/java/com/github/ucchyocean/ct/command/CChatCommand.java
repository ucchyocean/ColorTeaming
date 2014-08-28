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
import com.github.ucchyocean.ct.ColorTeamingAPI;
import com.github.ucchyocean.ct.config.ColorTeamingConfig;
import com.github.ucchyocean.ct.config.TeamNameSetting;

/**
 * colorchat(cchat)コマンドの実行クラス
 * @author ucchy
 */
public class CChatCommand implements TabExecutor {

    private static final String PREERR = ChatColor.RED.toString();

    private ColorTeaming plugin;

    public CChatCommand(ColorTeaming plugin) {
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

        ColorTeamingConfig config = plugin.getCTConfig();

        if ( args[0].equalsIgnoreCase("on") ) {
            config.setTeamChatMode(true);
            config.saveConfig();
            sender.sendMessage(ChatColor.RED + "チームチャットモードになりました。");
            return true;
        } else if ( args[0].equalsIgnoreCase("off") ) {
            config.setTeamChatMode(false);
            config.saveConfig();
            sender.sendMessage(ChatColor.RED + "チームチャットを無効にしました。");
            return true;
        } else if ( args[0].equalsIgnoreCase("opon") || args[0].equalsIgnoreCase("opcopyon") ) {
            config.setOPDisplayMode(true);
            config.saveConfig();
            sender.sendMessage(ChatColor.RED + "チームチャットをOPにも表示します。");
            return true;
        } else if ( args[0].equalsIgnoreCase("opoff") || args[0].equalsIgnoreCase("opcopyoff") ) {
            config.setOPDisplayMode(false);
            config.saveConfig();
            sender.sendMessage(ChatColor.RED + "チームチャットのOPへの表示をオフにします。");
            return true;
        } else if ( args[0].equalsIgnoreCase("logon") ) {
            config.setTeamChatLogMode(true);
            config.saveConfig();
            sender.sendMessage(ChatColor.RED + "チームチャットのログ記録を有効にします。");
            return true;
        } else if ( args[0].equalsIgnoreCase("logoff") ) {
            config.setTeamChatLogMode(false);
            config.saveConfig();
            sender.sendMessage(ChatColor.RED + "チームチャットのログ記録を無効にします。");
            return true;
        } else if ( args.length >= 2 ){

            // チームにメッセージ送信
            String team = args[0];
            ColorTeamingAPI api = plugin.getAPI();

            // 有効なチーム名が指定されたか確認する
            if ( !api.isExistTeam(team) ) {
                sender.sendMessage(PREERR + "チーム " + team + " が存在しません。");
                return true;
            }

            // メッセージの整形
            StringBuilder message = new StringBuilder();
            for ( int i=1; i<args.length; i++ ) {
                message.append(" " + args[i]);
            }

            // 送信
            api.sendTeamChat(sender, team, message.toString().trim());

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
            for ( String c : new String[]{"on", "off", "opon", "opoff", "logon", "logoff"} ) {
                if ( c.startsWith(prefix) ) {
                    commands.add(c);
                }
            }
            for ( TeamNameSetting tns : plugin.getAPI().getAllTeamNames() ) {
                String name = tns.getID();
                if ( name.toLowerCase().startsWith(prefix) ) {
                    commands.add(name);
                }
            }
            return commands;
        }

        return null;
    }
}
