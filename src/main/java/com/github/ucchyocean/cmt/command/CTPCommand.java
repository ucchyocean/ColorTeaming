/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.cmt.command;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.github.ucchyocean.cmt.ColorMeTeaming;
import com.github.ucchyocean.cmt.ColorMeTeamingConfig;

/**
 * @author ucchy
 * colortp(ctp)コマンドの実行クラス
 */
public class CTPCommand implements CommandExecutor {

    private static final String PREERR = ChatColor.RED.toString();
    private static final String PREINFO = ChatColor.GRAY.toString();

    private static final String PRE_LIST_MESSAGE =
            PREINFO + "=== TP Points Information ===";

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        // 引数が一つもない場合は、ここで終了
        if ( args.length == 0 ) {
            return false;
        }

        if ( args[0].equalsIgnoreCase("list") ) {
            // ctp list の実行

            sender.sendMessage(PRE_LIST_MESSAGE);
            ArrayList<String> list = ColorMeTeaming.tppointConfig.list();
            for ( String l : list ) {
                sender.sendMessage(PREINFO + l);
            }

            return true;
        }

        // ここ以降は引数が最低2つ必要なので、1つしかない場合はここで終了させておく
        if ( args.length == 1 ) {
            return false;
        }

        if ( args[0].equalsIgnoreCase("all") ) {

            Hashtable<String, ArrayList<Player>> members = ColorMeTeaming.getAllColorMembers();

            if ( args[1].equalsIgnoreCase("spawn") ) {
                // ctp all spawn の実行

                // テレポート実行
                Enumeration<String> keys = members.keys();
                while ( keys.hasMoreElements() ) {
                    String group = keys.nextElement();

                    Location location = ColorMeTeaming.respawnConfig.get(group);
                    if ( location == null ) {
                        sender.sendMessage(PREERR +
                                "グループ " + group + " にリスポーンポイントが指定されていません。");
                    } else {
                        for ( Player p : members.get(group) ) {
                            p.teleport(location, TeleportCause.COMMAND);
                        }
                        sender.sendMessage(PREINFO +
                                "グループ " + group + " のプレイヤーを全員テレポートしました。");
                    }
                }

            } else {
                // ctp all (point) の実行

                String point = args[1];
                Location location = ColorMeTeaming.tppointConfig.get(point);

                // point が登録済みのポイントかどうかを確認する
                if ( location == null ) {
                    sender.sendMessage(PREERR +
                            "ポイント " + point + " は登録されていません。");
                    return true;
                }

                // テレポート実行
                Enumeration<String> keys = members.keys();
                while ( keys.hasMoreElements() ) {
                    String group = keys.nextElement();
                    for ( Player p : members.get(group) ) {
                        p.teleport(location, TeleportCause.COMMAND);
                    }
                }
            }

            return true;

        } else if ( args[0].equalsIgnoreCase("set") ) {

            String point = args[1];

            // 引数が3個以下なら、ここで終了
            if ( args.length < 3 ) {
                return false;
            }

            Location location;

            if ( args[2].equalsIgnoreCase("here") ) {
                // ctp set (point) here

                if ( sender instanceof Player ) {
                    location = ((Player)sender).getLocation();
                } else {
                    sender.sendMessage(PREERR + "ctp set point here 指定は、" +
                            "コンソールやコマンドブロックからは実行できません。");
                    return true;
                }
            } else {
                // ctp set (point) [world] (x) (y) (z)

                location = checkAndGetLocation(sender, args, 2);
                if ( location == null ) {
                    return true;
                }
            }

            // ポイント登録の実行
            ColorMeTeaming.tppointConfig.set(point, location);

            return true;

        } else if ( args[0].equalsIgnoreCase("remove") ) {

            String point = args[1];

            // point が登録済みのポイントかどうかを確認する、未登録なら終了
            if ( ColorMeTeaming.tppointConfig.get(point) == null ) {
                sender.sendMessage(PREERR +
                        "ポイント " + point + " は登録されていません。");
                return true;
            }

            // ポイント削除の実行
            ColorMeTeaming.tppointConfig.set(point, null);

        } else {
            // ctp (group) ほにゃらら の実行

            String group = args[0];
            Hashtable<String, ArrayList<Player>> members = ColorMeTeaming.getAllColorMembers();

            // 有効なグループ名が指定されたか確認する
            if ( !members.containsKey(group) ) {
                sender.sendMessage(PREERR + "グループ " + group + " が存在しません。");
                return true;
            }

            Location location;

            if ( args[1].equalsIgnoreCase("here") ) {
                // ctp (group) here

                // コマンド実行者の場所を取得、コンソールなら終了
                if ( sender instanceof Player ) {
                    location = ((Player)sender).getLocation();
                } else if ( sender instanceof BlockCommandSender ) {
                    location = ((BlockCommandSender)sender).getBlock().getLocation().add(0, 1, 0);
                } else {
                    sender.sendMessage(PREERR +
                            "ctp の here 指定は、コンソールからは実行できません。");
                    return true;
                }

            } else if ( args[1].equalsIgnoreCase("spawn") ) {
                // ctp (group) spawn

                // グループのリスポーンポイントを取得、登録されていなければ終了
                location = ColorMeTeaming.respawnConfig.get(group);
                if ( location == null ) {
                    sender.sendMessage(PREERR +
                            "グループ " + group + " にリスポーンポイントが指定されていません。");
                    return true;
                }

            } else if ( args.length <= 3 ) {
                // ctp (group) (point)

                // 登録ポイントの取得、ポイントがなければ終了
                String point = args[1];
                location = ColorMeTeaming.tppointConfig.get(point);
                if ( location == null ) {
                    sender.sendMessage(PREERR +
                            "ポイント " + point + " は登録されていません。");
                    return true;
                }

            } else {
                // ctp (group) [world] (x) (y) (z)

                // 指定された座標からlocationを取得、取得できなければ終了
                location = checkAndGetLocation(sender, args, 1);
                if ( location == null ) {
                    return true;
                }
            }

            // テレポートの実行
            for ( Player p : members.get(group) ) {
                p.teleport(location, TeleportCause.COMMAND);
            }

            sender.sendMessage(PREINFO + "グループ " + group + " のプレイヤーを全員テレポートしました。");

            return true;
        }

        return false;
    }

    /**
     * Locationを作成できるかどうかチェックして、Locationを作成し、返すメソッド。
     * @param sender コマンド実行者
     * @param args コマンドの引数
     * @param fromIndex コマンドの引数の、どのインデクスからチェックを開始するか
     * @return 作成したLocation。ただし、作成に失敗した場合はnullになる。
     */
    private Location checkAndGetLocation(CommandSender sender, String[] args, int fromIndex) {

        String world = ColorMeTeamingConfig.defaultWorldName;
        String x_str, y_str, z_str;

        if ( args.length >= fromIndex + 4 ) {
            world = args[fromIndex];
            x_str = args[fromIndex + 1];
            y_str = args[fromIndex + 2];
            z_str = args[fromIndex + 3];
        } else if ( args.length >= fromIndex + 3 ) {
            x_str = args[fromIndex];
            y_str = args[fromIndex + 1];
            z_str = args[fromIndex + 2];
            if ( sender instanceof BlockCommandSender ) {
                BlockCommandSender block = (BlockCommandSender)sender;
                world = block.getBlock().getWorld().getName();
            } else if ( sender instanceof Player ) {
                Player player = (Player)sender;
                world = player.getWorld().getName();
            }
        } else {
            sender.sendMessage(PREERR + "引数の指定が足りません。");
            return null;
        }

        // 有効な座標が指定されたか確認する
        if ( !checkXYZ(sender, x_str) ||
                !checkXYZ(sender, y_str) ||
                !checkXYZ(sender, z_str) ) {
            return null;
        }

        // 有効なワールド名が指定されたか確認する
        if ( ColorMeTeaming.getWorld(world) == null ) {
            sender.sendMessage(PREERR + "ワールド " + world + " が存在しません。");
            return null;
        }

        // Locationを作成して返す。
        double x = Integer.parseInt(x_str) + 0.5;
        double y = Integer.parseInt(y_str);
        double z = Integer.parseInt(z_str) + 0.5;

        return new Location(ColorMeTeaming.getWorld(world), x, y, z);
    }

    /**
     * value が、座標の数値として適切な内容かどうかを確認する。
     * @param sender エラー時のメッセージ送り先
     * @param value 検査対象の文字列
     * @return 座標の数値として適切かどうか
     */
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
