/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.cmt.command;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.cmt.ColorMeTeaming;
import com.github.ucchyocean.cmt.ColorMeTeamingConfig;
import com.github.ucchyocean.cmt.Utility;

/**
 * @author ucchy
 * colorspawn(cs)コマンドの実行クラス
 */
public class CSpawnCommand implements CommandExecutor {

    private static final String PREERR = ChatColor.RED.toString();
    private static final String PREINFO = ChatColor.GRAY.toString();

    /**
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length < 2 ) {
            return false;
        }

        String group;
        String world = ColorMeTeamingConfig.defaultWorldName;
        int x_actual, y_actual, z_actual;

        group = args[0];

        if ( args[1].equalsIgnoreCase("here") ) {
            // here 指定

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
        if ( ColorMeTeamingConfig.ignoreGroups.contains(group) ) {
            sender.sendMessage(PREERR + "グループ " + group + " は" +
                    "config.ymlでignoreGroupsに指定されているグループなので、使用できません。");
            return true;
        }

        // 有効なワールド名が指定されたか確認する
        if ( ColorMeTeaming.getWorld(world) == null ) {
            sender.sendMessage(PREERR + "ワールド " + world + " が存在しません。");
            return true;
        }

        // spawnpoint設定を行う
        Location location = new Location(ColorMeTeaming.getWorld(world), x, y, z);
        ColorMeTeaming.respawnConfig.set(group, location);

        String message = String.format(
                "グループ %s のリスポーンポイントを (%d,%d,%d) に設定しました。",
                group, x_actual, y_actual, z_actual);
        sender.sendMessage(PREINFO + message);

        // WorldGuard連携の場合は、保護領域を作成する
        if ( ColorMeTeamingConfig.protectRespawnPointWithWorldGuard ) {
            if ( !location.getWorld().getName().equals(ColorMeTeamingConfig.defaultWorldName) ) {
                sender.sendMessage(PREERR + "config.yml の設定[" +
                        ColorMeTeamingConfig.defaultWorldName +
                        "]と、指定したポイントのワールド[" +
                        location.getWorld().getName() +
                        "が異なるため、保護領域の作成に失敗しました。");
            } else {
                int range = ColorMeTeamingConfig.protectRespawnPointRange;
                ColorMeTeaming.wghandler.makeTeamRegion(group, location, range);
            }
        }

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
