/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.config.TeamNameSetting;

/**
 * colorleader(cl)コマンドの実行クラス
 * @author ucchy
 */
public class CLeaderCommand implements CommandExecutor {

    private static final String PREERR = ChatColor.RED.toString();
    private static final String PREINFO = ChatColor.GRAY.toString();
    private static final String PRENOTICE = ChatColor.LIGHT_PURPLE.toString();

    private ColorTeaming plugin;

    public CLeaderCommand(ColorTeaming plugin) {
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

        String group = args[0];
        HashMap<String, ArrayList<String>> leaders = plugin.getAPI().getLeaders();

        if ( group.equalsIgnoreCase("clear") ) {
            // clear 指定の場合。

            plugin.getAPI().clearLeaders();
            Bukkit.broadcastMessage(PRENOTICE + "大将設定がクリアされました。");
            return true;

        } else if ( group.equalsIgnoreCase("view") || group.equalsIgnoreCase("say") ) {
            // view または say 指定の場合。

            boolean isBroadcast = false;
            if ( group.equalsIgnoreCase("say") ) {
                isBroadcast = true;
            }

            // 大将が設定されていない場合
            if ( leaders.size() <= 0 ) {
                sender.sendMessage(PREERR + "大将はまだ設定されていません。");
                return true;
            }

            for ( String key : leaders.keySet() ) {
                StringBuilder temp = new StringBuilder();
                for ( String name : leaders.get(key) ) {
                    if ( temp.length() == 0 ) {
                        temp.append("  ");
                    } else {
                        temp.append(", ");
                    }
                    temp.append(name);
                }

                if ( isBroadcast ) {
                    Bukkit.broadcastMessage(PRENOTICE + key + " チームの大将：");
                    Bukkit.broadcastMessage(PRENOTICE + temp.toString());
                } else {
                    sender.sendMessage(PREINFO + key + " チームの大将：");
                    sender.sendMessage(PREINFO + temp.toString());
                }
            }

            return true;

        } else if ( group.equalsIgnoreCase("all") ) {
            // all 指定の場合。

            HashMap<TeamNameSetting, ArrayList<Player>> members =
                    plugin.getAPI().getAllTeamMembers();

            int numberOfLeaders = 1;
            if ( args.length >= 2 && args[1].matches("[1-9]") ) {
                numberOfLeaders = Integer.parseInt(args[1]);
            }

            // 全ての色チームに対して処理をする
            for ( TeamNameSetting key : members.keySet() ) {

                // 人数が少なすぎるチームは無視
                if ( numberOfLeaders > members.get(key.getID()).size() ) {
                    sender.sendMessage(PREERR + key.getName() + " チームは人数が少なすぎて、大将を設定できません！");
                    continue;
                }

                // ランダムにリーダーを選出する
                int[] leaderIndexes = getPickupNumbers(members.get(key.getID()).size(), numberOfLeaders);
                leaders.put(key.getID(), new ArrayList<String>());
                for ( int i : leaderIndexes ) {
                    leaders.get(key.getID()).add(members.get(key.getID()).get(i).getName());
                }

                // リーダーになった人を、チームに通知する
                StringBuilder l = new StringBuilder();
                for ( String name : leaders.get(key.getID()) ) {
                    if ( l.length() != 0 ) {
                        l.append(", ");
                    }
                    l.append(name);
                }
                String message = String.format("%s チームの大将に、%s が選ばれました。", key.getName(), l);
                plugin.getAPI().sendInfoToTeamChat(key.getID(), message);
                sender.sendMessage(String.format(PREINFO + "%s チームの大将を、%d 人設定しました。", key, numberOfLeaders));
            }

            return true;

        } else {
            // group 指定処理の場合

            if ( !plugin.getAPI().isExistTeam(group) ) {
                sender.sendMessage(PREERR + group + " チームは存在しないようです。");
                return true;
            }

            if ( args.length <= 1 ) {
                sender.sendMessage(PREERR + "team を指定する場合は、user も指定してください。");
                return false;
            }

            HashMap<TeamNameSetting, ArrayList<Player>> members =
                    plugin.getAPI().getAllTeamMembers();

            String user = args[1];
            Player player = Bukkit.getPlayerExact(user);

            if ( user.equalsIgnoreCase("random") ) {

                // ランダムにリーダーを選出する
                leaders.put(group, new ArrayList<String>());
                Random random = new Random();
                int value = random.nextInt(members.get(group).size());
                String newLeader = members.get(group).get(value).getName();
                leaders.get(group).add(newLeader);

                String message = String.format("%s チームの大将に、%s が選ばれました。", group, newLeader);
                plugin.getAPI().sendInfoToTeamChat(group, message);
                sender.sendMessage(String.format(PREINFO + "%s チームの大将を、1 人設定しました。", group));

                return true;

            } else if ( player != null && !members.get(group).contains(player) ) {

                sender.sendMessage(PREERR + user + " は、" + group + " チームにいないようです。");
                return true;

            } else {

                // リーダーを設定
                leaders.put(group, new ArrayList<String>());
                leaders.get(group).add(user);

                String message = String.format("%s チームの大将に、%s が選ばれました。", group, user);
                plugin.getAPI().sendInfoToTeamChat(group, message);
                sender.sendMessage(PRENOTICE + message);

                return true;
            }
        }
    }

    private static int[] getPickupNumbers(int max, int numberOfPickup) {

        int[] result = new int[numberOfPickup];
        int index = 0;
        Random random = new Random();

        while ( index < numberOfPickup ) {

            int value = random.nextInt(max);
            if ( isAlreadyExists(result, index, value) ) {
                continue;
            }

            result[index] = value;
            index++;
        }

        return result;
    }

    private static boolean isAlreadyExists(int[] array, int index, int number) {

        for ( int i=0; i<index; i++ ) {
            if ( array[i] == number ) {
                return true;
            }
        }
        return false;
    }
}
