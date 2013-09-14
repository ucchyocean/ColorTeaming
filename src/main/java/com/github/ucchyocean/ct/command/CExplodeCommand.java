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

/**
 * colorexplode(ce)コマンドの実行クラス
 * @author ucchy
 */
public class CExplodeCommand implements CommandExecutor {

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
        } else if ( Bukkit.getPlayerExact(target) != null ) {
            // target はプレイヤーである場合
            playersToExplode.add(Bukkit.getPlayerExact(target));
        } else {
            sender.sendMessage(PREERR + target +
                    " というチームまたはプレイヤーは存在しません。");
            return true;
        }

        for ( Player p : playersToExplode ) {
            p.getWorld().createExplosion(p.getLocation(), 0); // 爆発エフェクト発生
            p.setHealth(0F); // HPを0に設定
            p.sendMessage("どーーん！");
        }

        sender.sendMessage(PREINFO + "ターゲットは爆死しました。");

        return true;
    }

}
