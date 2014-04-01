/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.event.ColorTeamingPlayerLeaveEvent.Reason;

/**
 * colorleave(cleave)コマンドの実行クラス
 * @author ucchy
 */
public class CLeaveCommand implements TabExecutor {

    private static final String PREERR = ChatColor.RED.toString();

    private ColorTeaming plugin;

    public CLeaveCommand(ColorTeaming plugin) {
        this.plugin = plugin;
    }

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( !(sender instanceof Player) ) {
            sender.sendMessage(
                    PREERR + "このコマンドはゲーム内からのみ実行可能です。");
            return true;
        }

        Player player = (Player)sender;

        if ( !plugin.getCTConfig().isAllowPlayerLeave() ) {
            player.sendMessage(
                    PREERR + "cleaveコマンドによる離脱は、許可されておりません。");
            return true;
        }

        if ( plugin.getAPI().getPlayerTeamName(player) == null ) {
            player.sendMessage(
                    PREERR + "あなたはチームに所属していません。");
            return true;
        }

        plugin.getAPI().leavePlayerTeam(player, Reason.SELF);
        player.sendMessage(ChatColor.GREEN + "チームから離脱しました。");

        // スコアボード更新
        plugin.getAPI().refreshRestTeamMemberScore();

        return true;
    }

    /**
     * @see org.bukkit.command.TabCompleter#onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public List<String> onTabComplete(
            CommandSender sender, Command command, String label, String[] args) {

        return null;
    }
}
