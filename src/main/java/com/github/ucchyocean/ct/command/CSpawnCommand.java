/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.command;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.ct.ColorTeaming;
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

        if ( args.length == 1 ) {
            if ( args[0].equalsIgnoreCase("list") ) {
                // cspawn list の実行

                sender.sendMessage(PREINFO + PRE_LINE_MESSAGE);
                ArrayList<String> list = ColorTeaming.respawnConfig.list();
                for ( String l : list ) {
                    sender.sendMessage(PREINFO + l);
                }

                return true;

            } else if ( args[0].equalsIgnoreCase("world") ) {
                // cspawn world の実行

                if ( !(sender instanceof Player) ) {
                    sender.sendMessage(PREERR + "cspawn の world 指定は、コンソールからは実行できません。");
                    return true;
                }

                Location location = ((Player)sender).getLocation();
                int x = location.getBlockX();
                int y = location.getBlockY();
                int z = location.getBlockZ();
                location.getWorld().setSpawnLocation(x, y, z);

                String message = String.format(
                        "ワールドの初期リスポーンポイントを (%d, %d, %d) に設定しました。",
                        x, y, z);
                sender.sendMessage(PREINFO + message);
                return true;

            } else if ( args[0].equalsIgnoreCase("switch") ) {
                // cspawn switch の実行

                ColorTeaming.respawnMapName = "";
                sender.sendMessage(PREINFO + "リスポーン設定を、デフォルトに切り替えました。");

                // 切り替えたマップのリスポーン地点一覧を表示する
                ArrayList<String> list = ColorTeaming.respawnConfig.list("");
                for ( String l : list ) {
                    sender.sendMessage(PREINFO + l);
                }

                return true;

            } else {
                // cspawn (group) の実行

                String group = args[0];

                // 有効なグループ名が指定されたか確認する
                if ( !Utility.isValidColor(group) ) {
                    sender.sendMessage(PREERR + "グループ " + group + " は設定できないグループ名です。");
                    return true;
                }

                Location location;
                if ( sender instanceof Player ) {
                    location = ((Player)sender).getLocation();
                } else if ( sender instanceof BlockCommandSender ) {
                    location = ((BlockCommandSender)sender).getBlock().getLocation();
                } else {
                    sender.sendMessage(PREERR + "cspawn の here 指定は、コンソールからは実行できません。");
                    return true;
                }

                // spawnpoint設定を行う
                ColorTeaming.respawnConfig.set(group, location);

                String message = String.format(
                        "グループ %s のリスポーンポイントを (%d, %d, %d) に設定しました。",
                        group, location.getBlockX(), location.getBlockY(), location.getBlockZ());
                sender.sendMessage(PREINFO + message);

                return true;
            }
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

            } else if ( args.length == 2 ) {
                // cspawn remove (group) の実行

                String group = args[1];

                if ( ColorTeaming.respawnConfig.get(group) == null ) {
                    sender.sendMessage(PREERR + "グループ " + group + " のリスポーン設定がありません。");
                    return true;
                }

                ColorTeaming.respawnConfig.set(group, null);

                sender.sendMessage(PREINFO + "グループ " + group + " のリスポーン設定を削除しました。");

                return true;

            } else {
                // cspawn remove (group) (map) の実行

                String group = args[1];
                String map = args[2];

                if ( ColorTeaming.respawnConfig.get(group, map) == null ) {
                    sender.sendMessage(PREERR + "グループ " + group + "、マップ " + map + " のリスポーン設定がありません。");
                    return true;
                }

                ColorTeaming.respawnConfig.set(group, map, null);

                sender.sendMessage(PREINFO + "グループ " + group + "、マップ " + map + " のリスポーン設定を削除しました。");

                return true;
            }

        } else if ( args[0].equalsIgnoreCase("switch") ) {
            // cspawn switch (map) の実行

            String map = args[1];

            if ( !isValidMapName(map) ) {
                sender.sendMessage(PREINFO + "マップ名 " + map + " は指定不可能な文字を含んでいます。");
                return true;
            }

            ColorTeaming.respawnMapName = map;
            sender.sendMessage(PREINFO + "リスポーン設定を、マップ " + map + " 用に切り替えました。");

            // 切り替えたマップのリスポーン地点一覧を表示する
            ArrayList<String> list = ColorTeaming.respawnConfig.list(map);
            for ( String l : list ) {
                sender.sendMessage(PREINFO + l);
            }

            return true;
        }


        String group;
        String map;
        Location location;

        group = args[0];
        map = args[1];

        if ( args.length <= 3 ) {
            // cspawn (group) (map) の実行

            if ( !isValidMapName(map) ) {
                sender.sendMessage(PREINFO + "マップ名 " + map + " は指定不可能な文字を含んでいます。");
                return true;
            }

            if ( sender instanceof Player ) {
                location = ((Player)sender).getLocation();
            } else if ( sender instanceof BlockCommandSender ) {
                location = ((BlockCommandSender)sender).getBlock().getLocation();
            } else {
                sender.sendMessage(PREERR + "cspawn の here 指定は、コンソールからは実行できません。");
                return true;
            }

        } else if ( args.length == 4 ) {
            // cspawn (group) (x) (y) (z) の実行

            map = "";

            // 有効な座標が指定されたか確認する
            if ( !checkXYZ(sender, args[1]) ||
                    !checkXYZ(sender, args[2]) ||
                    !checkXYZ(sender, args[3]) ) {
                return true;
            }

            // 実行者がプレイヤーかコマンドブロックなら、worldを取得して設定する
            World world = ColorTeaming.getWorld("world");
            if ( sender instanceof BlockCommandSender ) {
                BlockCommandSender block = (BlockCommandSender)sender;
                world = block.getBlock().getWorld();
            } else if ( sender instanceof Player ) {
                Player player = (Player)sender;
                world = player.getWorld();
            }

            location = new Location(world, Integer.parseInt(args[1]),
                    Integer.parseInt(args[2]), Integer.parseInt(args[3]));

        } else {
            // cspawn (group) (point) (x) (y) (z) の実行

            if ( !isValidMapName(map) ) {
                sender.sendMessage(PREINFO + "マップ名 " + map + " は指定不可能な文字を含んでいます。");
                return true;
            }

            // 有効な座標が指定されたか確認する
            if ( !checkXYZ(sender, args[2]) ||
                    !checkXYZ(sender, args[3]) ||
                    !checkXYZ(sender, args[4]) ) {
                return true;
            }

            // 実行者がプレイヤーかコマンドブロックなら、worldを取得して設定する
            World world = ColorTeaming.getWorld("world");
            if ( sender instanceof BlockCommandSender ) {
                BlockCommandSender block = (BlockCommandSender)sender;
                world = block.getBlock().getWorld();
            } else if ( sender instanceof Player ) {
                Player player = (Player)sender;
                world = player.getWorld();
            }

            location = new Location(world, Integer.parseInt(args[2]),
                    Integer.parseInt(args[3]), Integer.parseInt(args[4]));
        }

        // 有効なグループ名が指定されたか確認する
        if ( !Utility.isValidColor(group) ) {
            sender.sendMessage(PREERR + "グループ " + group + " は設定できないグループ名です。");
            return true;
        }

        // spawnpoint設定を行う
        ColorTeaming.respawnConfig.set(group, map, location);

        if ( map == null || map.equals("") ) {
            String message = String.format(
                    "グループ %s のリスポーンポイントを (%d, %d, %d) に設定しました。",
                    group, location.getBlockX(), location.getBlockY(), location.getBlockZ() );
            sender.sendMessage(PREINFO + message);
        } else {
            String message = String.format(
                    "グループ %s、マップ %s のリスポーンポイントを (%d, %d, %d) に設定しました。",
                    group, map, location.getBlockX(), location.getBlockY(), location.getBlockZ() );
            sender.sendMessage(PREINFO + message);
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

    private boolean isValidMapName(String name) {
        return name.matches("[a-zA-Z]{1,10}");
    }
}
