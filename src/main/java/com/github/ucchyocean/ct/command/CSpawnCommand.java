/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.command;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.ColorTeamingConfig;
import com.github.ucchyocean.ct.Utility;

/**
 * @author ucchy
 * colorspawn(cs)コマンドの実行クラス
 */
public class CSpawnCommand implements CommandExecutor {

    private static final String PRE_LINE_MESSAGE =
            "=== Team Spawn Point Information ===";

    private static final String PREERR = ChatColor.RED.toString();
    private static final String PREINFO = ChatColor.GRAY.toString();

    /**
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length >= 1 && args[0].equalsIgnoreCase("list") ) {
            // cspawn list の実行

            sender.sendMessage(PREINFO + PRE_LINE_MESSAGE);
            ArrayList<String> list = ColorTeaming.respawnConfig.list();
            for ( String l : list ) {
                sender.sendMessage(PREINFO + l);
            }

            return true;
        }

        // 以下、引数2つ以上が必要になるので、1つしか指定されていなければここで終わる.
        if ( args.length < 2 ) {
            return false;
        }

        if ( args[0].equalsIgnoreCase("remove") ) {

            if ( args[1].equalsIgnoreCase("all") ) {
                // cspawn remove all の実行

                ArrayList<String> keys = ColorTeaming.respawnConfig.keys();
                for ( String k : keys ) {
                    ColorTeaming.respawnConfig.set(k, null);
                }

                sender.sendMessage(PREINFO + "全てのグループリスポーン設定を削除しました。");

                return true;

            } else {
                // cspawn remove (group) の実行

                String group = args[1];

                if ( ColorTeaming.respawnConfig.get(group) == null ) {
                    sender.sendMessage(PREERR + "グループ " + group + " のリスポーン設定がありません。");
                    return true;
                }

                ColorTeaming.respawnConfig.set(group, null);

                sender.sendMessage(PREINFO + "グループ " + group + " のリスポーン設定を削除しました。");

                return true;
            }
        }

        String group;
        String world = ColorTeamingConfig.defaultWorldName;
        int x_actual, y_actual, z_actual;

        group = args[0];

        if ( args[1].equalsIgnoreCase("here") ) {
            // cspawn (group) here の実行

            if ( sender instanceof Player ) {
                x_actual = ((Player)sender).getLocation().getBlockX();
                y_actual = ((Player)sender).getLocation().getBlockY();
                z_actual = ((Player)sender).getLocation().getBlockZ();
                world = ((Player)sender).getWorld().getName();
            } else if ( sender instanceof BlockCommandSender ) {
                x_actual = ((BlockCommandSender)sender).getBlock().getX();
                y_actual = ((BlockCommandSender)sender).getBlock().getY()+1;
                z_actual = ((BlockCommandSender)sender).getBlock().getZ();
                world = ((BlockCommandSender)sender).getBlock().getWorld().getName();
            } else {
                sender.sendMessage(PREERR + "cspawn の here 指定は、コンソールからは実行できません。");
                return true;
            }

        } else if ( args.length == 4 ) {
            // cspawn (group) (x) (y) (z) の実行

            // 有効な座標が指定されたか確認する
            if ( !checkXYZ(sender, args[1]) ||
                    !checkXYZ(sender, args[2]) ||
                    !checkXYZ(sender, args[3]) ) {
                return true;
            }

            // 実行者がプレイヤーかコマンドブロックなら、worldを取得して設定する
            if ( sender instanceof BlockCommandSender ) {
                BlockCommandSender block = (BlockCommandSender)sender;
                world = block.getBlock().getWorld().getName();
            } else if ( sender instanceof Player ) {
                Player player = (Player)sender;
                world = player.getWorld().getName();
            }

            x_actual = Integer.parseInt(args[1]);
            y_actual = Integer.parseInt(args[2]);
            z_actual = Integer.parseInt(args[3]);

        } else if ( args.length >= 5 ) {
            // cspawn (group) (world) (x) (y) (z) の実行

            // 有効な座標が指定されたか確認する
            if ( !checkXYZ(sender, args[2]) ||
                    !checkXYZ(sender, args[3]) ||
                    !checkXYZ(sender, args[4]) ) {
                return true;
            }

            world = args[1];
            x_actual = Integer.parseInt(args[2]);
            y_actual = Integer.parseInt(args[3]);
            z_actual = Integer.parseInt(args[4]);

        } else {
            // 引数指定不足
            return false;
        }

        double x = (double)x_actual + 0.5;
        double y = (double)y_actual;
        double z = (double)z_actual + 0.5;

        // 有効なグループ名が指定されたか確認する
        if ( !Utility.isValidColor(group) ) {
            sender.sendMessage(PREERR + "グループ " + group + " はColorMeに設定できないグループ名です。");
            return true;
        }
        if ( ColorTeamingConfig.ignoreGroups.contains(group) ) {
            sender.sendMessage(PREERR + "グループ " + group + " は" +
                    "config.ymlでignoreGroupsに指定されているグループなので、使用できません。");
            return true;
        }

        // 有効なワールド名が指定されたか確認する
        if ( ColorTeaming.getWorld(world) == null ) {
            sender.sendMessage(PREERR + "ワールド " + world + " が存在しません。");
            return true;
        }

        // spawnpoint設定を行う
        Location location = new Location(ColorTeaming.getWorld(world), x, y, z);
        ColorTeaming.respawnConfig.set(group, location);

        String message = String.format(
                "グループ %s のリスポーンポイントを (%d, %d, %d) に設定しました。",
                group, x_actual, y_actual, z_actual);
        sender.sendMessage(PREINFO + message);

        return true;
    }

    private boolean checkXYZ (CommandSender sender, String value) {

        // 数値かどうかをチェックする
        if ( !value.matches("-?[0-9]+") ) {
            sender.sendMessage(PREERR + value + " は数値ではありません。");
            return false;
        }

        int v = Integer.parseInt(value);

        // 大きすぎる値でないかどうかチェックする
        if ( v < -30000000 || 30000000 < v ) {
            sender.sendMessage(PREERR + value + " は遠すぎて指定できません。");
            return false;
        }

        return true;
    }
}
