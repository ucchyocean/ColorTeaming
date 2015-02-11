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
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.ColorTeamingAPI;
import com.github.ucchyocean.ct.Utility;
import com.github.ucchyocean.ct.config.TeamNameSetting;

/**
 * colorgive(cgive)コマンドの実行クラス
 * @author ucchy
 */
public class CGiveCommand implements TabExecutor {

    private static final String PREERR = ChatColor.RED.toString();
    private static final String PREINFO = ChatColor.GRAY.toString();

    private ColorTeaming plugin;

    public CGiveCommand(ColorTeaming plugin) {
        this.plugin = plugin;
    }

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length <= 1 ) {
            return false;
        }

        String target = args[0]; // アイテムを配布するチームかユーザー
        String targetDesc = "";

        ColorTeamingAPI api = plugin.getAPI();
        HashMap<String, ArrayList<Player>> members = api.getAllTeamMembers();
        ArrayList<Player> playersForGive = new ArrayList<Player>();

        if ( target.equalsIgnoreCase("all") ) {
            // 全員を対象とする
            playersForGive = Utility.getOnlinePlayers();
            targetDesc = "全員";
        } else if ( api.isExistTeam(target) ) {
            // target はチームである場合
            TeamNameSetting tns = api.getTeamNameFromID(target);
            playersForGive = members.get(target);
            targetDesc = "チーム" + tns.getName();
        } else if ( Utility.getPlayerExact(target) != null ) {
            // target はプレイヤーである場合
            playersForGive.add(Utility.getPlayerExact(target));
            targetDesc = "プレイヤー" + target;
        } else {
            sender.sendMessage(PREERR + target +
                    " というチームまたはプレイヤーは存在しません。");
            return true;
        }

        ItemStack item;
        if ( args[1].equalsIgnoreCase("hand") ) {
            if ( !(sender instanceof Player) ) {
                sender.sendMessage(PREERR + "このコマンドは、ゲーム内からしか実行できません。");
                return true;
            }
            Player player = (Player)sender;
            item = player.getItemInHand().clone();
        } else {
            item = parseItemInfoToItemStack(args[1]);
            if ( item == null ) {
                sender.sendMessage(PREERR + "指定した形式" + args[1] + "が正しくありません。");
                return true;
            }
        }

        for ( Player p : playersForGive ) {
            p.getInventory().addItem(item);
        }

        sender.sendMessage(PREINFO + targetDesc + "に、" +
                item.getType().toString() + "を" + item.getAmount() + "個、配布しました。");

        return true;
    }

    private static ItemStack parseItemInfoToItemStack(String info) {

        String name;
        int amount = 1;
        if ( info.contains(":") ) {
            String[] datas = info.split(":");
            name = datas[0];
            if ( datas[1].matches("^[0-9]{1,9}$") ) {
                amount = Integer.parseInt(datas[1]);
            }
        } else {
            name = info;
        }

        Material material = Material.getMaterial(name);
        if ( material == null ) {
            return null;
        }

        return new ItemStack(material, amount);
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
            for ( Player player : Utility.getOnlinePlayers() ) {
                String name = player.getName();
                if ( name.toLowerCase().startsWith(prefix) ) {
                    commands.add(name);
                }
            }
            return commands;
        }

        return null;
    }
}
