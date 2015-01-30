/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.ColorTeamingAPI;
import com.github.ucchyocean.ct.config.RespawnConfiguration;
import com.github.ucchyocean.ct.config.TeamNameSetting;

/**
 * colorspawn(cs)コマンドの実行クラス
 * @author ucchy
 */
public class CSpawnCommand implements TabExecutor {

    private static final String PRE_LINE_MESSAGE =
            "=== Team Spawn Point Information ===";

    private static final String PREERR = ChatColor.RED.toString();
    private static final String PREINFO = ChatColor.GRAY.toString();
    private static final String PRENOTIFY = ChatColor.YELLOW.toString();

    private ColorTeaming plugin;

    public CSpawnCommand(ColorTeaming plugin) {
        this.plugin = plugin;
    }

    /**
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        RespawnConfiguration respawnConfig = plugin.getAPI().getRespawnConfig();

        if ( args.length == 1 ) {
            if ( args[0].equalsIgnoreCase("list") ) {
                // cspawn list の実行

                sender.sendMessage(PREINFO + PRE_LINE_MESSAGE);
                ArrayList<String> list = respawnConfig.list();
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

                plugin.getAPI().setRespawnMapName("");
                sender.sendMessage(PREINFO + "リスポーン設定を、デフォルトに切り替えました。");

                // 切り替えたマップのリスポーン地点一覧を表示する
                ArrayList<String> list = respawnConfig.list("");
                for ( String l : list ) {
                    sender.sendMessage(PREINFO + l);
                }

                return true;

            } else {
                // cspawn (group) の実行

                String target = args[0];
                ColorTeamingAPI api = plugin.getAPI();

                // 有効なチーム名が指定されたか確認する
                if ( !api.getTeamNameConfig().containsID(target) ) {
                    sender.sendMessage(PREERR + "チーム " + target + " は設定できないチーム名です。");
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
                respawnConfig.set(target, location);

                String message = String.format(
                        "チーム %s のリスポーンポイントを (%d, %d, %d) に設定しました。",
                        target, location.getBlockX(), location.getBlockY(), location.getBlockZ());
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

                ArrayList<String> keys = respawnConfig.keys();
                for ( String k : keys ) {
                    respawnConfig.set(k, null);
                }

                sender.sendMessage(PREINFO + "全てのチームリスポーン設定を削除しました。");

                return true;

            } else if ( args.length == 2 ) {
                // cspawn remove (group) の実行

                String group = args[1];

                if ( respawnConfig.get(group) == null ) {
                    sender.sendMessage(PREERR + "チーム " + group + " のリスポーン設定がありません。");
                    return true;
                }

                respawnConfig.set(group, null);

                sender.sendMessage(PREINFO + "チーム " + group + " のリスポーン設定を削除しました。");

                return true;

            } else {
                // cspawn remove (group) (map) の実行

                String group = args[1];
                String map = args[2];

                if ( respawnConfig.get(group, map) == null ) {
                    sender.sendMessage(PREERR + "チーム " + group + "、マップ " + map + " のリスポーン設定がありません。");
                    return true;
                }

                respawnConfig.set(group, map, null);

                sender.sendMessage(PREINFO + "チーム " + group + "、マップ " + map + " のリスポーン設定を削除しました。");

                return true;
            }

        } else if ( args[0].equalsIgnoreCase("switch") ) {

            if ( args[1].equalsIgnoreCase("random") ) {
                // cspawn switch random の実行

                // マップの取得
                ArrayList<String> maps = plugin.getAPI().getRespawnConfig().getAllMapNames();

                if ( maps.size() == 0 ) {
                    sender.sendMessage(PREERR + "マップ名が1つも登録されていません。");
                    return true;
                }

                // ランダムに1つを取り出す
                Random rand = new Random(System.currentTimeMillis());
                int index = rand.nextInt(maps.size());
                String map = maps.get(index);

                plugin.getAPI().setRespawnMapName(map);
                sender.sendMessage(PREINFO + "リスポーン設定を、マップ " + map + " 用に切り替えました。");

                // 切り替えたマップのリスポーン地点一覧を表示する
                ArrayList<String> list = respawnConfig.list(map);
                for ( String l : list ) {
                    sender.sendMessage(PREINFO + l);
                }

                Bukkit.broadcastMessage(PRENOTIFY + "今回のマップは" + PREERR + map + PRENOTIFY + "です！");

                return true;

            } else {
                // cspawn switch (map) の実行

                String map = args[1];

                if ( !isValidMapName(map) ) {
                    sender.sendMessage(PREERR + "マップ名 " + map + " は指定不可能な文字を含んでいます。");
                    return true;
                }

                ArrayList<String> maps = plugin.getAPI().getRespawnConfig().getAllMapNames();
                if ( !maps.contains(map) ) {
                    sender.sendMessage(PREERR + "指定されたマップ名 " + map + " は登録されていません。");
                    return true;
                }

                plugin.getAPI().setRespawnMapName(map);
                sender.sendMessage(PREINFO + "リスポーン設定を、マップ " + map + " 用に切り替えました。");

                // 切り替えたマップのリスポーン地点一覧を表示する
                ArrayList<String> list = respawnConfig.list(map);
                for ( String l : list ) {
                    sender.sendMessage(PREINFO + l);
                }

                return true;
            }
        }

        // 以下、リスポーン地点の設定系処理

        String group = args[0];
        String map = args[1];
        Location location;

        if ( args.length <= 3 ) {
            // cspawn (group) (map) の実行

            if ( !isValidMapName(map) ) {
                sender.sendMessage(PREERR + "マップ名 " + map + " は指定不可能な文字を含んでいます。");
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
            World world = Bukkit.getWorld("world");
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
                sender.sendMessage(PREERR + "マップ名 " + map + " は指定不可能な文字を含んでいます。");
                return true;
            }

            // 有効な座標が指定されたか確認する
            if ( !checkXYZ(sender, args[2]) ||
                    !checkXYZ(sender, args[3]) ||
                    !checkXYZ(sender, args[4]) ) {
                return true;
            }

            // 実行者がプレイヤーかコマンドブロックなら、worldを取得して設定する
            World world = Bukkit.getWorld("world");
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

        // 有効なチーム名が指定されたか確認する
        if ( !plugin.getAPI().getTeamNameConfig().containsID(group) ) {
            sender.sendMessage(PREERR + "チーム " + group + " は設定できないチーム名です。");
            return true;
        }

        // spawnpoint設定を行う
        respawnConfig.set(group, map, location);

        if ( map == null || map.equals("") ) {
            String message = String.format(
                    "チーム %s のリスポーンポイントを (%d, %d, %d) に設定しました。",
                    group, location.getBlockX(), location.getBlockY(), location.getBlockZ() );
            sender.sendMessage(PREINFO + message);
        } else {
            String message = String.format(
                    "チーム %s、マップ %s のリスポーンポイントを (%d, %d, %d) に設定しました。",
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
        return name.matches("[a-zA-Z0-9_]{1,10}");
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
            for ( String c : new String[]{"list", "world", "remove", "switch"} ) {
                if ( c.startsWith(prefix) ) {
                    commands.add(c);
                }
            }
            for ( TeamNameSetting tns :
                    plugin.getAPI().getTeamNameConfig().getTeamNames() ) {
                String name = tns.getID();
                if ( name.startsWith(prefix) ) {
                    commands.add(name);
                }
            }
            return commands;

        } else if ( args.length == 2 && args[0].equalsIgnoreCase("remove") ) {

            String prefix = args[0].toLowerCase();
            ArrayList<String> commands = new ArrayList<String>();
            for ( String c : new String[]{"all"} ) {
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

        } else if ( args.length == 2 && args[0].equalsIgnoreCase("switch") ) {

            String prefix = args[0].toLowerCase();
            ArrayList<String> commands = new ArrayList<String>();
            for ( String c : new String[]{"random"} ) {
                if ( c.startsWith(prefix) ) {
                    commands.add(c);
                }
            }
            for ( String name :
                    plugin.getAPI().getRespawnConfig().getAllMapNames() ) {
                if ( name.toLowerCase().startsWith(prefix) ) {
                    commands.add(name);
                }
            }
            return commands;

        }

        return null;
    }
}
