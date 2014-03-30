/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.command;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.ColorTeamingAPI;
import com.github.ucchyocean.ct.Utility;
import com.github.ucchyocean.ct.config.TeamNameSetting;

/**
 * colorpoint(cpoint)コマンドの実行クラス
 * @author ucchy
 */
public class CPointCommand implements CommandExecutor {

    private static final String PRE_LINE_MESSAGE =
            "=== Team Point Information ===";

    private ColorTeaming plugin;

    public CPointCommand(ColorTeaming plugin) {
        this.plugin = plugin;
    }

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

            HashMap<String, int[]> killDeathCounts =
                    plugin.getAPI().getKillDeathCounts();
            HashMap<String, int[]> killDeathPersonalCounts =
                    plugin.getAPI().getKillDeathPersonalCounts();
            HashMap<String, Integer> teamPoints =
                    plugin.getAPI().getAllTeamPoints();
            HashMap<String, Integer> personalPoints =
                    plugin.getAPI().getAllPlayerPoints();

            // 全チームの得点を集計して、得点順に並べる
            ArrayList<TeamNameSetting> teams = new ArrayList<TeamNameSetting>();
            ArrayList<Integer> points = new ArrayList<Integer>();

            for ( String team : killDeathCounts.keySet() ) {

                int point = 0;
                if ( teamPoints.containsKey(team) ) {
                    point = teamPoints.get(team);
                }

                int index = 0;
                while ( teams.size() > index && points.get(index) > point ) {
                    index++;
                }

                TeamNameSetting tns = plugin.getAPI().getTeamNameFromID(team);
                teams.add(index, tns);
                points.add(index, point);
            }

            // 全チームの得点を表示する
            if ( !isBroadcast ) {
                sender.sendMessage(ChatColor.GRAY + PRE_LINE_MESSAGE);
            } else {
                Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + PRE_LINE_MESSAGE);
            }

            for ( int rank=1; rank<=teams.size(); rank++ ) {

                String color;
                if ( !isBroadcast ) {
                    color = ChatColor.GRAY.toString();
                } else {
                    color = ChatColor.RED.toString();
                }

                TeamNameSetting tns = teams.get(rank-1);
                int point = points.get(rank-1);
                int[] counts = killDeathCounts.get(tns.getID());
                String message = String.format(
                        "%s%d. %s %s%dpoints (%dkill, %ddeath)",
                        color, rank, tns.toString(), color, point,
                        counts[0], counts[1]);

                if ( !isBroadcast ) {
                    sender.sendMessage(message);
                } else {
                    Bukkit.broadcastMessage(message);
                }
            }

            // ユーザー得点の集計

            // まだ1つも得点が記録されていないなら、ここでコマンドは終わる。
            if ( killDeathPersonalCounts.size() <= 0 ) {
                return true;
            }

            // 1位と、1位と同じ得点の人を、MVPにする
            int maxPersonalPoints = -99999;
            for ( String name : personalPoints.keySet() ) {
                if ( maxPersonalPoints < personalPoints.get(name) ) {
                    maxPersonalPoints = personalPoints.get(name);
                }
            }

            ArrayList<String> mvp = new ArrayList<String>();
            for ( String name : personalPoints.keySet() ) {
                if ( maxPersonalPoints == personalPoints.get(name) ) {
                    mvp.add(name);
                }
            }

            // MVPの得点を表示する
            for ( String mvpName : mvp ) {

                int point = personalPoints.get(mvpName);
                int[] counts = killDeathPersonalCounts.get(mvpName);
                String message = String.format(
                        "[MVP] %s %dpoints (%dkill, %ddeath)",
                        mvpName, point, counts[0], counts[1]);
                if ( !isBroadcast ) {
                    sender.sendMessage(ChatColor.GRAY + message);
                } else {
                    Bukkit.broadcastMessage(ChatColor.RED + message);
                }
            }

            // 個人の得点を個人のコンソールに表示する
            if ( isBroadcast ) {
                for ( String playerName : killDeathPersonalCounts.keySet() ) {

                    int point = personalPoints.get(playerName);
                    int[] counts = killDeathPersonalCounts.get(playerName);
                    String message = String.format(
                            "[Your Score] %s %dpoints (%dkill, %ddeath)",
                            playerName, point, counts[0], counts[1]);

                    Player player = Utility.getPlayerExact(playerName);
                    if ( player != null ) {
                        player.sendMessage(ChatColor.GRAY + message);
                    }
                }
            }

            return true;

        } else if ( args.length >= 1 && args[0].equalsIgnoreCase("clear") ) {

            ColorTeamingAPI api = plugin.getAPI();
            api.clearKillDeathPoints();
            sender.sendMessage(ChatColor.GRAY + "KillDeath数をリセットしました。");
            return true;

        } else if ( args.length >= 3 && args[0].equalsIgnoreCase("set") ) {

            if ( !args[2].matches("-?[0-9]{1,9}") ) {
                sender.sendMessage(ChatColor.RED + "pointには数字を指定してください。");
                sender.sendMessage(ChatColor.RED + "/" + label + " set (team) (point)");
                return true;
            }

            String id = args[1];
            int point = Integer.parseInt(args[2]);

            if ( plugin.getAPI().isExistTeam(id) ) {
                // 指定された対象がチームの場合

                plugin.getAPI().setTeamPoint(id, point);

                sender.sendMessage(ChatColor.RED +
                        "チーム" + id + "のポイントを、" + point + "に設定しました。");
                return true;

            } else if ( Utility.getPlayerExact(id) != null ) {
                // 指定された対象がプレイヤーの場合

                Player player = Utility.getPlayerExact(id);
                plugin.getAPI().setPlayerPoint(player, point);

                sender.sendMessage(ChatColor.RED +
                        "プレイヤー" + id + "のポイントを、" + point + "に設定しました。");
                return true;

            } else {
                // 指定対象が見つからない

                sender.sendMessage(ChatColor.RED + "指定された" + id + "が存在しません。");
                return true;
            }


        } else if ( args.length >= 3 && args[0].equalsIgnoreCase("add") ) {

            if ( !args[2].matches("-?[0-9]{1,9}") ) {
                sender.sendMessage(ChatColor.RED + "pointには数字を指定してください。");
                sender.sendMessage(ChatColor.RED + "/" + label + " set (team) (point)");
                return true;
            }

            String id = args[1];
            int amount = Integer.parseInt(args[2]);

            if ( plugin.getAPI().isExistTeam(id) ) {
                // 指定された対象がチームの場合

                int point = plugin.getAPI().addTeamPoint(id, amount);

                sender.sendMessage(ChatColor.RED +
                        "チーム" + id + "のポイントを、" + point + "に設定しました。");
                return true;

            } else if ( Utility.getPlayerExact(id) != null ) {
                // 指定された対象がプレイヤーの場合

                Player player = Utility.getPlayerExact(id);
                int point = plugin.getAPI().addPlayerPoint(player, amount);

                sender.sendMessage(ChatColor.RED +
                        "プレイヤー" + id + "のポイントを、" + point + "に設定しました。");
                return true;

            } else {
                // 指定対象が見つからない

                sender.sendMessage(ChatColor.RED + "指定された" + id + "が存在しません。");
                return true;
            }

        }

        return false;
    }

}
