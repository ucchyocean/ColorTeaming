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

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.ColorTeamingAPI;
import com.github.ucchyocean.ct.Utility;
import com.github.ucchyocean.ct.config.TeamNameSetting;

/**
 * colorexplode(ce)コマンドの実行クラス
 * @author ucchy
 */
public class CExplodeCommand implements TabExecutor {

    private static final String PREERR = ChatColor.RED.toString();
    private static final String PREINFO = ChatColor.GRAY.toString();

    private ColorTeaming plugin;

    public CExplodeCommand(ColorTeaming plugin) {
        this.plugin = plugin;
    }

    /**
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length <= 0 ) {
            return false;
        }

        String target = args[0]; // 制裁を加えるチームかユーザー

        ColorTeamingAPI api = plugin.getAPI();
        HashMap<String, ArrayList<Player>> members =
                api.getAllTeamMembers();
        ArrayList<Player> playersToExplode = new ArrayList<Player>();

        if ( target.equalsIgnoreCase("all") ) {
            // target はallである場合
            for ( String key : members.keySet() ) {
                playersToExplode.addAll(members.get(key));
            }
        } else if ( api.isExistTeam(target) ) {
            // target はチームである場合
            playersToExplode = members.get(target);
        } else if ( Utility.getPlayerExact(target) != null ) {
            // target はプレイヤーである場合
            playersToExplode.add(Utility.getPlayerExact(target));
        } else {
            sender.sendMessage(PREERR + target +
                    " というチームまたはプレイヤーは存在しません。");
            return true;
        }

        for ( Player p : playersToExplode ) {
            p.getWorld().createExplosion(p.getLocation(), 0); // 爆発エフェクト発生
            p.damage(p.getMaxHealth());
            p.sendMessage("どーーん！");
        }

        sender.sendMessage(PREINFO + "ターゲットは爆死しました。");

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
