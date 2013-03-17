/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.cmt.command;

import java.util.ArrayList;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.cmt.ColorMeTeaming;

/**
 * @author ucchy
 * colorexplode(ce)コマンドの実行クラス
 */
public class CExplodeCommand implements CommandExecutor {

    private static final String PREERR = ChatColor.RED.toString();
    private static final String PRENOTICE = ChatColor.LIGHT_PURPLE.toString();

    /**
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length <= 0 ) {
            return false;
        }

        String target = args[0]; // 制裁を加えるグループかユーザー

        Hashtable<String, ArrayList<Player>> members =
                ColorMeTeaming.getAllColorMembers();
        ArrayList<Player> playersToExplode = new ArrayList<Player>();

        if ( members.containsKey(target) ) {
            // target はグループである場合
            playersToExplode = members.get(target);
        } else if ( ColorMeTeaming.getPlayerExact(target) != null ) {
            // target はプレイヤーである場合
            playersToExplode.add(ColorMeTeaming.getPlayerExact(target));
        } else {
            sender.sendMessage(PREERR + target +
                    " というグループまたはプレイヤーは存在しません。");
            return true;
        }

        for ( Player p : playersToExplode ) {
            p.getWorld().createExplosion(p.getLocation(), 0); // 爆発エフェクト発生
            p.setHealth(0); // HPを0に設定
            p.sendMessage("どーーん！");
        }

        ColorMeTeaming.sendBroadcast(PRENOTICE + "ターゲットは爆死しました。");

        return true;
    }

}
