/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.cmt.command;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.cmt.ColorMeTeaming;
import com.github.ucchyocean.cmt.ColorMeTeamingConfig;

/**
 * @author ucchy
 * colorkill(ckill)コマンドの実行クラス
 */
public class CKillCommand implements CommandExecutor {

    private static final String PRE_LINE_MESSAGE =
            "=== Kill Death Counts Information ===";

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length == 0 || (args.length >= 1 && args[0].equalsIgnoreCase("say")) ) {

            boolean isBroadcast = false;
            if ( args.length >= 1 && args[0].equalsIgnoreCase("say") ) {
                isBroadcast = true;
            }

            // グループは存在するが、得点データがない場合、このタイミングで作成しておく
            Hashtable<String, ArrayList<Player>> members = new Hashtable<String, ArrayList<Player>>();
            Enumeration<String> keys_all = members.keys();
            while ( keys_all.hasMoreElements() ) {
                String key = keys_all.nextElement();
                if ( !ColorMeTeaming.killDeathCounts.containsKey(key) ) {
                    ColorMeTeaming.killDeathCounts.put(key, new int[3]);
                }
            }

            // 全グループの得点を集計して、得点順に並べる
            ArrayList<String> groups = new ArrayList<String>();
            ArrayList<Integer> points = new ArrayList<Integer>();

            Enumeration<String> keys = ColorMeTeaming.killDeathCounts.keys();
            while ( keys.hasMoreElements() ) {
                String group = keys.nextElement();
                int[] counts = ColorMeTeaming.killDeathCounts.get(group);
                int point = counts[0] * ColorMeTeamingConfig.killPoint +
                        counts[1] * ColorMeTeamingConfig.deathPoint +
                        counts[2] * ColorMeTeamingConfig.tkPoint;

                int index = 0;
                while ( groups.size() > index && points.get(index) > point ) {
                    index++;
                }
                groups.add(index, group);
                points.add(index, point);
            }

            // 全グループの得点を表示する
            if ( !isBroadcast ) {
                sender.sendMessage(ChatColor.GRAY + PRE_LINE_MESSAGE);
            } else {
                ColorMeTeaming.sendBroadcast(ChatColor.LIGHT_PURPLE + PRE_LINE_MESSAGE);
            }

            for ( int rank=1; rank<=groups.size(); rank++ ) {
                String group = groups.get(rank-1);
                int point = points.get(rank-1);
                int[] counts = ColorMeTeaming.killDeathCounts.get(group);
                String message = String.format(
                        "%d. %s %dpoints (%dkill, %ddeath, %dtk)",
                        rank, group, point, counts[0], counts[1], counts[2]);

                if ( !isBroadcast ) {
                    sender.sendMessage(ChatColor.GRAY + message);
                } else {
                    ColorMeTeaming.sendBroadcast(ChatColor.RED + message);
                }
            }

            // ユーザー得点の集計

            // まだ1つも得点が記録されていないなら、ここでコマンドは終わる。
            if ( ColorMeTeaming.killDeathUserCounts.size() <= 0 ) {
                return true;
            }

            ArrayList<String> users = new ArrayList<String>();
            ArrayList<Integer> userPoints = new ArrayList<Integer>();

            Enumeration<String> playersEnum = ColorMeTeaming.killDeathUserCounts.keys();
            while ( playersEnum.hasMoreElements() ) {
                String playerName = playersEnum.nextElement();
                int[] counts = ColorMeTeaming.killDeathUserCounts.get(playerName);
                int point = counts[0] * ColorMeTeamingConfig.killPoint +
                        counts[1] * ColorMeTeamingConfig.deathPoint +
                        counts[2] * ColorMeTeamingConfig.tkPoint;

                int index = 0;
                while ( users.size() > index && userPoints.get(index) > point ) {
                    index++;
                }
                users.add(index, playerName);
                userPoints.add(index, point);
            }

            // 1位と、1位と同じ得点の人を、MVPにする
            ArrayList<String> mvp = new ArrayList<String>();
            mvp.add(users.get(0));
            int index = 1;
            while ( userPoints.size() > index && userPoints.get(0) == userPoints.get(index) ) {
                mvp.add(users.get(index));
                index++;
            }

            // MVPの得点を表示する
            for ( int i=0; i<mvp.size(); i++ ) {
                String mvpName = users.get(i);
                int point = userPoints.get(i);
                int[] counts = ColorMeTeaming.killDeathUserCounts.get(users.get(i));
                String message = String.format(
                        "[MVP] %s %dpoints (%dkill, %ddeath, %dtk)",
                        mvpName, point, counts[0], counts[1], counts[2]);
                if ( !isBroadcast ) {
                    sender.sendMessage(ChatColor.GRAY + message);
                } else {
                    ColorMeTeaming.sendBroadcast(ChatColor.RED + message);
                }
            }

            // 個人の得点を個人のコンソールに表示する
            if ( isBroadcast ) {
                for ( int i=0; i<users.size(); i++ ) {
                    String playerName = users.get(i);
                    int point = userPoints.get(i);
                    int[] counts = ColorMeTeaming.killDeathUserCounts.get(users.get(i));
                    String message = String.format(
                            "[Your Score] %s %dpoints (%dkill, %ddeath, %dtk)",
                            playerName, point, counts[0], counts[1], counts[2]);

                    ColorMeTeaming.getPlayerExact(playerName).sendMessage(ChatColor.GRAY + message);
                }
            }

            return true;

        } else if ( args.length >= 1 && args[0].equalsIgnoreCase("clear") ) {

            ColorMeTeaming.killDeathCounts.clear();
            ColorMeTeaming.killDeathUserCounts.clear();
            sender.sendMessage(ChatColor.GRAY + "KillDeath数をリセットしました。");
            return true;
        }

        return false;
    }

}
