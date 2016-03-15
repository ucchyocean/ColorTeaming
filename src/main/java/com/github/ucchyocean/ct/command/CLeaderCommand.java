/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.Utility;
import com.github.ucchyocean.ct.config.ColorTeamingConfig;
import com.github.ucchyocean.ct.config.ColorTeamingMessages;
import com.github.ucchyocean.ct.config.TeamNameSetting;

/**
 * colorleader(cl)コマンドの実行クラス
 * @author ucchy
 */
public class CLeaderCommand implements TabExecutor {

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

        String team = args[0];
        HashMap<String, ArrayList<String>> leaders = plugin.getAPI().getLeaders();
        ColorTeamingConfig config = plugin.getCTConfig();

        if ( team.equalsIgnoreCase("clear") ) {
            // clear 指定の場合。

            plugin.getAPI().clearLeaders();

            String msg = ColorTeamingMessages.getLeaderClearMessage();
            if ( msg != null ) {
                Bukkit.broadcastMessage(msg);
            }

            return true;

        } else if ( team.equalsIgnoreCase("view") || team.equalsIgnoreCase("say") ) {
            // view または say 指定の場合。

            boolean isBroadcast = false;
            if ( team.equalsIgnoreCase("say") ) {
                isBroadcast = true;
            }

            // 大将が設定されていない場合
            if ( leaders.size() <= 0 ) {
                sender.sendMessage(PREERR + "大将はまだ設定されていません。");
                return true;
            }

            for ( String key : leaders.keySet() ) {

                String pre = ColorTeamingMessages.getLeaderInformationSummayMessage(key);
                if ( pre == null ) {
                    break;
                }

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
                    Bukkit.broadcastMessage(PRENOTICE + pre);
                    Bukkit.broadcastMessage(PRENOTICE + temp.toString());
                } else {
                    sender.sendMessage(PREINFO + pre);
                    sender.sendMessage(PREINFO + temp.toString());
                }
            }

            return true;

        } else if ( team.equalsIgnoreCase("all") ) {
            // all 指定の場合。

            HashMap<String, ArrayList<Player>> members =
                    plugin.getAPI().getAllTeamMembers();

            int numberOfLeaders = 1;
            if ( args.length >= 2 && args[1].matches("[1-9]") ) {
                numberOfLeaders = Integer.parseInt(args[1]);
            }

            // 全ての色チームに対して処理をする
            for ( String key : members.keySet() ) {

                TeamNameSetting teamName = plugin.getAPI().getTeamNameFromID(key);

                // 人数が少なすぎるチームは無視
                if ( numberOfLeaders > members.get(key).size() ) {
                    sender.sendMessage(PREERR + teamName.getName() + " チームは人数が少なすぎて、大将を設定できません！");
                    continue;
                }

                // ランダムにリーダーを選出する
                int[] leaderIndexes = getPickupNumbers(members.get(key).size(), numberOfLeaders);
                leaders.put(key, new ArrayList<String>());
                for ( int i : leaderIndexes ) {
                    leaders.get(key).add(members.get(key).get(i).getName());
                }

                // リーダーになった人を、チームに通知する
                StringBuilder l = new StringBuilder();
                for ( String name : leaders.get(key) ) {
                    if ( l.length() != 0 ) {
                        l.append(", ");
                    }
                    l.append(name);
                }

                String message = ColorTeamingMessages.getLeaderInformationTeamChatMessage(teamName.toString(), l.toString());
                if ( message != null ) {
                    plugin.getAPI().sendTeamChat(null, key, message);
                }

                sender.sendMessage(String.format(PREINFO + "%s チームの大将を、%d 人設定しました。",
                        teamName.getName(), numberOfLeaders));
            }

            return true;

        } else {
            // group 指定処理の場合

            if ( !plugin.getAPI().isExistTeam(team) ) {
                sender.sendMessage(PREERR + team + " チームは存在しないようです。");
                return true;
            }

            if ( args.length <= 1 ) {
                sender.sendMessage(PREERR + "team を指定する場合は、user も指定してください。");
                return false;
            }

            TeamNameSetting teamName = plugin.getAPI().getTeamNameFromID(team);

            HashMap<String, ArrayList<Player>> members =
                    plugin.getAPI().getAllTeamMembers();

            String user = args[1];
            Player player = Utility.getPlayerExact(user);

            if ( user.equalsIgnoreCase("random") ) {

                // ランダムにリーダーを選出する
                leaders.put(team, new ArrayList<String>());
                Random random = new Random();
                int value = random.nextInt(members.get(team).size());
                String newLeader = members.get(team).get(value).getName();
                leaders.get(team).add(newLeader);

                String message = ColorTeamingMessages.getLeaderInformationTeamChatMessage(teamName.toString(), newLeader);
                if ( message != null ) {
                    plugin.getAPI().sendTeamChat(null, team, message);
                }

                sender.sendMessage(String.format(PREINFO + "%s チームの大将を、1 人設定しました。",
                        teamName.toString()));

                return true;

            } else if ( player != null && !members.get(team).contains(player) ) {

                sender.sendMessage(PREERR + user + " は、" + teamName.toString() + " チームにいないようです。");
                return true;

            } else {

                // リーダーを設定
                leaders.put(team, new ArrayList<String>());
                leaders.get(team).add(user);

                String message = ColorTeamingMessages.getLeaderInformationTeamChatMessage(teamName.toString(), user);
                if ( message != null ) {
                    plugin.getAPI().sendTeamChat(null, team, message);
                }

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

    /**
     * @see org.bukkit.command.TabCompleter#onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public List<String> onTabComplete(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length == 1 ) {
            String prefix = args[0].toLowerCase();
            ArrayList<String> commands = new ArrayList<String>();
            for ( String c : new String[]{"clear", "say", "all"} ) {
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
