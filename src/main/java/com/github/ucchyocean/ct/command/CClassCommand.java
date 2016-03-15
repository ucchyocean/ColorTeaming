/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.ColorTeamingAPI;
import com.github.ucchyocean.ct.Utility;
import com.github.ucchyocean.ct.config.ClassData;
import com.github.ucchyocean.ct.config.TeamNameSetting;
import com.github.ucchyocean.ct.item.ItemConfigParser;

/**
 * colorclass(cclass)コマンドの実行クラス
 * @author ucchy
 */
public class CClassCommand implements TabExecutor {

    private static final String PREERR = ChatColor.RED.toString();
    private static final String PREINFO = ChatColor.GRAY.toString();

    private ColorTeaming plugin;

    public CClassCommand(ColorTeaming plugin) {
        this.plugin = plugin;
    }

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length >= 1 && args[0].equalsIgnoreCase("check") ) {
            // cclass check コマンドの処理
            if ( !(sender instanceof Player) ) {
                sender.sendMessage(PREERR + label + " check コマンドは、ゲーム内でのみ実行できます。");
                return true;
            }
            Player player = (Player)sender;
            ItemStack item = Utility.getItemInHand(player);
            if ( item != null ) {
                sender.sendMessage("===== アイテム情報 =====");
                sender.sendMessage(ItemConfigParser.getItemInfo(item));
            }
            return true;

        }

        // ここ以下は引数が2つ以上必要である。
        if ( args.length < 2 ) {
            return false;
        }

        if ( args[0].equalsIgnoreCase("export") ) {
            // cclass export コマンドの処理

            if ( !(sender instanceof Player) ) {
                sender.sendMessage(PREERR + label + " export コマンドは、ゲーム内でのみ実行できます。");
                return true;
            }
            Player player = (Player)sender;
            String name = args[1];
            if ( !name.matches("^[a-zA-Z0-9\\-_]{1,20}$") ) {
                sender.sendMessage(PREERR + "指定されたクラス名 " + name + " は使用できません。");
                return true;
            }

            boolean isOverwrite =
                    ( args.length >= 3 && args[2].equalsIgnoreCase("overwrite") );

            if ( ClassData.exportClassFromPlayer(player, name, isOverwrite) ) {
                sender.sendMessage(PREINFO + "クラス設定 " + name + " をエクスポートしました。");
            }
            return true;

        }

        String target = args[0];
        String clas = args[1];

        ColorTeamingAPI api = plugin.getAPI();
        HashMap<String, ArrayList<Player>> members = api.getAllTeamMembers();

        // 有効なチーム名かユーザー名か'all'が指定されたかを確認する
        boolean isAll = false;
        boolean isTeam = false;
        if ( target.equalsIgnoreCase("all") ) {
            // 全プレイヤー指定
            isAll = true;
        } else if ( api.isExistTeam(target) ) {
            // チーム指定
            isTeam = true;
        } else if ( Utility.getPlayerExact(target) != null ) {
            // ユーザー指定
        } else {
            sender.sendMessage(PREERR + "チームまたはプレイヤー " + target + " が存在しません。");
            return true;
        }

        // 有効なクラス名が指定されたか確認する
        if ( !plugin.getAPI().isExistClass(clas) ) {
            sender.sendMessage(PREERR + "クラス " + clas + " が存在しません。");
            return true;
        }

        // クラス設定対象を取得する
        ArrayList<Player> playersToSet = new ArrayList<Player>();
        if ( isAll ) {
            for ( String key : members.keySet() ) {
                playersToSet.addAll(members.get(key));
            }
        } else if ( isTeam ) {
            playersToSet = members.get(target);
        } else {
            playersToSet.add(Utility.getPlayerExact(target));
        }

        if ( playersToSet.size() <= 0 ) {
            sender.sendMessage(PREERR + "設定先 " + target + " の対象プレイヤーが誰もいません。");
            return true;
        }

        // クラス設定を実行する
        api.setClassToPlayer(playersToSet, clas);

        String targetName;
        if ( isAll ) {
            targetName = "全てのプレイヤー";
        } else if ( isTeam ) {
            TeamNameSetting tns = api.getTeamNameFromID(target);
            targetName = "チーム" + tns.toString();
        } else {
            targetName = "プレイヤー" + target;
        }

        sender.sendMessage(PREINFO +
                String.format("%s に、%s クラスの装備とアイテムを配布しました。",
                        targetName, clas));

        return true;
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
            for ( String c : new String[]{"check", "export", "all"} ) {
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
            for ( Player player : Utility.getOnlinePlayers() ) {
                String name = player.getName();
                if ( name.toLowerCase().startsWith(prefix) ) {
                    commands.add(name);
                }
            }
            return commands;
        }

        if ( args.length == 2 ) {

            if ( args[0].equalsIgnoreCase("check") ||
                    args[0].equalsIgnoreCase("export") ) {
                return null;
            }

            String prefix = args[1].toLowerCase();
            ArrayList<String> commands = new ArrayList<String>();
            for ( String name : plugin.getAPI().getClasses().keySet() ) {
                if ( name.toLowerCase().startsWith(prefix) ) {
                    commands.add(name);
                }
            }
            return commands;
        }

        return null;
    }
}
