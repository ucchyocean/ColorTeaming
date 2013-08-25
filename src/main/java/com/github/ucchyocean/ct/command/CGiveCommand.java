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
import org.bukkit.inventory.ItemStack;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.ColorTeamingAPI;
import com.github.ucchyocean.ct.config.KitParser;
import com.github.ucchyocean.ct.config.TeamNameSetting;

/**
 * colorgive(cgive)コマンドの実行クラス
 * @author ucchy
 */
public class CGiveCommand implements CommandExecutor {

    private static final String PREERR = ChatColor.RED.toString();
    private static final String PREINFO = ChatColor.GRAY.toString();

    private KitParser handler;
    private ColorTeaming plugin;

    public CGiveCommand(ColorTeaming plugin) {
        this.plugin = plugin;
        handler = new KitParser();
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
        HashMap<TeamNameSetting, ArrayList<Player>> members =
                api.getAllTeamMembers();
        ArrayList<Player> playersForGive = new ArrayList<Player>();

        if ( target.equalsIgnoreCase("all") ) {
            // 全員を対象とする
            playersForGive = plugin.getAPI().getAllPlayers();
            targetDesc = "全員";
        } else if ( api.isExistTeam(target) ) {
            // target はチームである場合
            TeamNameSetting tns = api.getTeamNameFromID(target);
            playersForGive = members.get(tns);
            targetDesc = "チーム" + tns.getName();
        } else if ( Bukkit.getPlayerExact(target) != null ) {
            // target はプレイヤーである場合
            playersForGive.add(Bukkit.getPlayerExact(target));
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
            item = handler.parseItemInfoToItemStack(args[1]);
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
}
