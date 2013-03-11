/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.cmt.command;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.cmt.ColorMeTeaming;
import com.github.ucchyocean.cmt.ColorMeTeamingConfig;

/**
 * @author ucchy
 * colorleader(cl)コマンドの実行クラス
 */
public class CLeaderCommand implements CommandExecutor {

    private static final String PREERR = ChatColor.RED.toString();
    private static final String PREINFO = ChatColor.GRAY.toString();
    private static final String PRENOTICE = ChatColor.LIGHT_PURPLE.toString();

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length <= 0 ) {
            return false;
        }

        String group = args[0];

        if ( group.equalsIgnoreCase("clear") ) {
            // clear 指定の場合。

            Enumeration<String> keys = ColorMeTeaming.leaders.keys();
            while ( keys.hasMoreElements() ) {
                String key = keys.nextElement();
                ColorMeTeaming.leaders.remove(key);
            }

            ColorMeTeaming.sendBroadcast(PRENOTICE + "大将設定がクリアされました。");
            return true;

        } else if ( group.equalsIgnoreCase("view") || group.equalsIgnoreCase("say") ) {
            // view または say 指定の場合。

            boolean isBroadcast = false;
            if ( group.equalsIgnoreCase("say") ) {
                isBroadcast = true;
            }

            // 大将が設定されていない場合
            if ( ColorMeTeaming.leaders.size() <= 0 ) {
                sender.sendMessage(PREERR + "大将はまだ設定されていません。");
                return true;
            }

            Enumeration<String> keys = ColorMeTeaming.leaders.keys();
            while ( keys.hasMoreElements() ) {
                String key = keys.nextElement();
                StringBuilder temp = new StringBuilder();
                for ( String name : ColorMeTeaming.leaders.get(key) ) {
                    if ( temp.length() == 0 ) {
                        temp.append("  ");
                    } else {
                        temp.append(", ");
                    }
                    temp.append(name);
                }

                if ( isBroadcast ) {
                    ColorMeTeaming.sendBroadcast(PRENOTICE + key + " グループの大将：");
                    ColorMeTeaming.sendBroadcast(PRENOTICE + temp.toString());
                } else {
                    sender.sendMessage(PREINFO + key + " グループの大将：");
                    sender.sendMessage(PREINFO + temp.toString());
                }
            }

            return true;

        } else if ( group.equalsIgnoreCase("all") ) {
            // all 指定の場合。

            Hashtable<String, ArrayList<Player>> members = ColorMeTeaming.getAllColorMembers();

            int numberOfLeaders = 1;
            if ( args.length >= 2 && args[1].matches("[1-9]") ) {
                numberOfLeaders = Integer.parseInt(args[1]);
            }

            // 全ての色グループに対して処理をする
            Enumeration<String> keys = members.keys();

            while ( keys.hasMoreElements() ) {

                String key = keys.nextElement();

                // ignore group の場合は、無視
                if ( ColorMeTeamingConfig.ignoreGroups.contains(key) ) {
                    continue;
                }

                // 人数が少なすぎるグループは無視
                if ( numberOfLeaders > members.get(key).size() ) {
                    sender.sendMessage(PREERR + key + " グループは人数が少なすぎて、大将を設定できません！");
                    continue;
                }

                // ランダムにリーダーを選出する
                int[] leaderIndexes = getPickupNumbers(members.get(key).size(), numberOfLeaders);
                ColorMeTeaming.leaders.put(key, new ArrayList<String>());
                for ( int i : leaderIndexes ) {
                    ColorMeTeaming.leaders.get(key).add(members.get(key).get(i).getName());
                }

                // リーダーになった人を、チームに通知する
                StringBuilder l = new StringBuilder();
                for ( String name : ColorMeTeaming.leaders.get(key) ) {
                    if ( l.length() != 0 ) {
                        l.append(", ");
                    }
                    l.append(name);
                }
                String message = String.format("%s チームの大将に、%s が選ばれました。", key, l);
                ColorMeTeaming.sendTeamChat(key, message);
                sender.sendMessage(String.format(PREINFO + "%s チームの大将を、%d 人設定しました。", key, numberOfLeaders));
            }

            return true;

        } else {
            // group 指定処理の場合

            Hashtable<String, ArrayList<Player>> members = ColorMeTeaming.getAllColorMembers();

            if ( !members.containsKey(group) ) {
                sender.sendMessage(PREERR + group + " グループは存在しないようです。");
                return true;
            }

            if ( args.length <= 1 ) {
                sender.sendMessage(PREERR + "group を指定する場合は、user も指定してください。");
                return false;
            }

            String user = args[1];
            Player player = ColorMeTeaming.getPlayerExact(user);

            if ( user.equalsIgnoreCase("random") ) {

                // ランダムにリーダーを選出する
                ColorMeTeaming.leaders.put(group, new ArrayList<String>());
                Random random = new Random();
                int value = random.nextInt(members.get(group).size());
                String newLeader = members.get(group).get(value).getName();
                ColorMeTeaming.leaders.get(group).add(newLeader);

                String message = String.format("%s チームの大将に、%s が選ばれました。", group, newLeader);
                ColorMeTeaming.sendTeamChat(group, message);
                sender.sendMessage(String.format(PREINFO + "%s チームの大将を、1 人設定しました。", group));

                return true;

            } else if ( player != null && !members.get(group).contains(player) ) {

                sender.sendMessage(PREERR + user + " は、" + group + " グループにいないようです。");
                return true;

            } else {

                // リーダーを設定
                ColorMeTeaming.leaders.put(group, new ArrayList<String>());
                ColorMeTeaming.leaders.get(group).add(user);

                String message = String.format("%s チームの大将に、%s が選ばれました。", group, user);
                ColorMeTeaming.sendTeamChat(group, message);
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
