/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.Utility;
import com.github.ucchyocean.ct.config.TeamNameSetting;

/**
 * colorcount(cc)コマンド、colorcountsay(ccsay)コマンドの実行クラス
 * @author ucchy
 */
public class CCountCommand implements TabExecutor {

    private static final String PRE_LINE_MESSAGE =
            "=== Team Member Information ===";

    private ColorTeaming plugin;

    public CCountCommand(ColorTeaming plugin) {
        this.plugin = plugin;
    }

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        // 引数の処理
        boolean isBroadcast = false;
        boolean isAll = false;

        if ( args.length >= 1 && args[0].equalsIgnoreCase("say") ) {
            isBroadcast = true;
            if ( args.length >= 2 && args[1].equalsIgnoreCase("all") ) {
                isAll = true;
            }
        } else if ( args.length >= 1 && args[0].equalsIgnoreCase("all") ) {
            isAll = true;
        }

        // メンバー情報の取得
        HashMap<String, ArrayList<Player>> members;
        if ( !isAll ) {
            members = plugin.getAPI().getAllTeamMembers();
        } else {
            TeamNameSetting emptyTeam = new TeamNameSetting("", "未所属", ChatColor.WHITE);
            members = new HashMap<String, ArrayList<Player>>();
            for ( Player p : Utility.getOnlinePlayers() ) {
                TeamNameSetting teamName = plugin.getAPI().getPlayerTeamName(p);
                if ( teamName == null ) {
                    teamName = emptyTeam;
                }
                if ( members.containsKey(teamName.getID()) ) {
                    members.get(teamName.getID()).add(p);
                } else {
                    ArrayList<Player> data = new ArrayList<Player>();
                    data.add(p);
                    members.put(teamName.getID(), data);
                }
            }
        }

        // ccコマンドの実行
        sendCCMessage(sender, members, isBroadcast);

        return true;
    }

    /**
     * ccコマンドを実行して、実行結果を表示する。
     * @param sender コマンド実行者
     * @param members カウント対象のメンバー情報
     * @param isBroadcast ブロードキャストかどうか
     */
    protected static void sendCCMessage(CommandSender sender,
            HashMap<String, ArrayList<Player>> members, boolean isBroadcast) {

        // 最初の行を送信
        if ( !isBroadcast ) {
            sender.sendMessage(ChatColor.GRAY + PRE_LINE_MESSAGE);
        } else {
            Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + PRE_LINE_MESSAGE);
        }

        // メンバー情報を送信
        for ( String key : members.keySet() ) {

            ArrayList<Player> member = members.get(key);

            StringBuilder value = new StringBuilder();
            for ( Player p : member ) {
                if ( value.length() != 0 ) {
                    value.append(",");
                }
                value.append(p.getName());
            }

            TeamNameSetting teamName = ColorTeaming.instance.getAPI().getTeamNameFromID(key);
            if ( key.equals("") ) {
                teamName = new TeamNameSetting("", "未所属", ChatColor.WHITE);
            }

            if ( !isBroadcast ) {
                String color = ChatColor.GRAY.toString();
                String team = teamName.toString();
                sender.sendMessage(String.format("%s* %s %s- %d",
                        color, team, color, member.size()));
                sender.sendMessage(color + value);
            } else {
                String color = ChatColor.RED.toString();
                String team = teamName.toString();
                Bukkit.broadcastMessage( String.format("%s* %s %s- %d",
                        color, team, color, member.size()));
                Bukkit.broadcastMessage(color + value);
            }
        }
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
            for ( String c : new String[]{"say", "all"} ) {
                if ( c.startsWith(prefix) ) {
                    commands.add(c);
                }
            }
            return commands;

        } else if ( args.length == 2 && args[0].equalsIgnoreCase("say") ) {

            String prefix = args[1].toLowerCase();
            ArrayList<String> commands = new ArrayList<String>();
            for ( String c : new String[]{"all"} ) {
                if ( c.startsWith(prefix) ) {
                    commands.add(c);
                }
            }
            return commands;

        }

        return null;
    }
}
