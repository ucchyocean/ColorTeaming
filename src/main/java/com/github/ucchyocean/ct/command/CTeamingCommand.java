/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.command;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.ColorTeamingConfig;
import com.github.ucchyocean.ct.Utility;
import com.github.ucchyocean.ct.scoreboard.TabListCriteria;
import com.github.ucchyocean.ct.scoreboard.TeamCriteria;

/**
 * @author ucchy
 * colorteaming(ct)コマンドの実行クラス
 */
public class CTeamingCommand implements CommandExecutor {

    private static final String PREERR = ChatColor.RED.toString();
    private static final String PREINFO = ChatColor.GRAY.toString();

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length < 1 ) {
            return false;
        }

        if ( args[0].equalsIgnoreCase("reload") ) {

            ColorTeamingConfig.reloadConfig();
            sender.sendMessage("config.ymlの再読み込みを行いました。");
            return true;

        } else if ( args[0].equalsIgnoreCase("removeall") ) {

            Hashtable<String, ArrayList<Player>> members = ColorTeaming.getAllTeamMembers();
            Enumeration<String> keys = members.keys();
            while ( keys.hasMoreElements() ) {
                String group = keys.nextElement();
                for ( Player p : members.get(group) ) {
                    ColorTeaming.leavePlayerTeam(p);
                }
                ColorTeaming.removeTeam(group);
            }
            sender.sendMessage(PREINFO + "全てのグループが解散しました。");

            // 保護領域の更新
            if ( ColorTeamingConfig.protectRespawnPointWithWorldGuard ) {
                ColorTeaming.wghandler.refreshGroupMembers();
            }

            return true;

        } else if ( args.length >= 2 && args[0].equalsIgnoreCase("remove") ) {

            Hashtable<String, ArrayList<Player>> members = ColorTeaming.getAllTeamMembers();
            String group = args[1];
            if ( !members.containsKey(group) ) {
                sender.sendMessage(PREERR + "グループ " + group + " は存在しません。");
                return true;
            }

            for ( Player p : members.get(group) ) {
                ColorTeaming.leavePlayerTeam(p);
                p.sendMessage(PREINFO + "グループ " + group + " が解散しました。");
            }
            ColorTeaming.removeTeam(group);
            sender.sendMessage(PREINFO + "グループ " + group + " が解散しました。");

            // 保護領域の更新
            if ( ColorTeamingConfig.protectRespawnPointWithWorldGuard ) {
                ColorTeaming.wghandler.refreshGroupMembers();
            }

            return true;

        } else if ( args.length >= 3 && args[0].equalsIgnoreCase("add") ) {

            String group = args[1];
            if ( !Utility.isValidColor(group) ) {
                sender.sendMessage(PREERR + "グループ " + group + " は設定できないグループ名です。");
                return true;
            }

            Player player = ColorTeaming.getPlayerExact(args[2]);
            if ( player == null ) {
                sender.sendMessage(PREERR + "プレイヤー " + args[2] + " は存在しません。");
                return true;
            }

            boolean isNewGroup = ! ColorTeaming.getAllTeamMembers().containsKey(group);
            ColorTeaming.addPlayerTeam(player, group);

            // メンバー情報をlastdataに保存する
            ColorTeaming.sdhandler.save("lastdata");

            // 保護領域の更新
            if ( ColorTeamingConfig.protectRespawnPointWithWorldGuard ) {
                ColorTeaming.wghandler.refreshGroupMembers();
            }

            // サイドバーの更新 グループが増える場合は、再生成する
            if ( isNewGroup ) {
                ColorTeaming.makeSidebar();
            }
            ColorTeaming.refreshSidebarScore();
            ColorTeaming.refreshTabkeyListScore();

            return true;

        } else if ( args.length >= 2 && args[0].equalsIgnoreCase("side") ) {

            if ( args[1].equalsIgnoreCase("kill") ) {
                ColorTeamingConfig.teamCriteria = TeamCriteria.KILL_COUNT;
            } else if ( args[1].equalsIgnoreCase("death") ) {
                ColorTeamingConfig.teamCriteria = TeamCriteria.DEATH_COUNT;
            } else if ( args[1].equalsIgnoreCase("point") ) {
                ColorTeamingConfig.teamCriteria = TeamCriteria.POINT;
            } else if ( args[1].equalsIgnoreCase("rest") ) {
                ColorTeamingConfig.teamCriteria = TeamCriteria.REST_PLAYER;
            } else if ( args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("none") ) {
                ColorTeamingConfig.teamCriteria = TeamCriteria.NONE;
            } else {
                return false;
            }

            // サイドバーの更新
            ColorTeaming.makeSidebar();

            // 設定の保存
            ColorTeamingConfig.setConfigValue("teamCriteria", args[1].toLowerCase());

            return true;

        } else if ( args.length >= 2 && args[0].equalsIgnoreCase("list") ) {

            if ( args[1].equalsIgnoreCase("kill") ) {
                ColorTeamingConfig.listCriteria = TabListCriteria.KILL_COUNT;
            } else if ( args[1].equalsIgnoreCase("death") ) {
                ColorTeamingConfig.listCriteria = TabListCriteria.DEATH_COUNT;
            } else if ( args[1].equalsIgnoreCase("point") ) {
                ColorTeamingConfig.listCriteria = TabListCriteria.POINT;
            } else if ( args[1].equalsIgnoreCase("health") ) {
                ColorTeamingConfig.listCriteria = TabListCriteria.HEALTH;
            } else if ( args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("none") ) {
                ColorTeamingConfig.listCriteria = TabListCriteria.NONE;
            } else {
                return false;
            }

            // スコアボードの更新
            ColorTeaming.makeTabkeyListScore();

            // 設定の保存
            ColorTeamingConfig.setConfigValue("listCriteria", args[1].toLowerCase());

            return true;
        }

        return false;
    }

}
