/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.ColorTeamingAPI;
import com.github.ucchyocean.ct.config.ColorTeamingConfig;
import com.github.ucchyocean.ct.config.TeamNameConfig;
import com.github.ucchyocean.ct.config.TeamNameSetting;

/**
 * colorjoin(cjoin)コマンドの実行クラス
 * @author ucchy
 */
public class CJoinCommand implements TabExecutor {

    private static final String PREERR = ChatColor.RED.toString();

    private ColorTeaming plugin;

    public CJoinCommand(ColorTeaming plugin) {
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
        ColorTeamingConfig config = plugin.getCTConfig();

        if ( args.length == 0 || args[0].equalsIgnoreCase("random") ) {

            if ( !plugin.getCTConfig().isAllowPlayerJoinRandom() ) {

                String msg = config.getErrorJoinRandomNotAllowMessage();
                if ( msg != null ) {
                    player.sendMessage(msg);
                }

                return true;
            }

            if ( plugin.getAPI().getPlayerTeamName(player) != null ) {

                String msg = config.getErrorAlreadyJoinMessage();
                if ( msg != null ) {
                    player.sendMessage(msg);
                }

                return true;
            }

            ArrayList<Player> players = new ArrayList<Player>();
            players.add(player);
            boolean result = plugin.getAPI().addPlayerToColorTeamsWithOrderSelection(players);

            if ( !result ) {

                String msg = config.getErrorNoTeamMessage();
                if ( msg != null ) {
                    player.sendMessage(msg);
                }

                return true;
            }

            return true;

        } else {

            if ( !plugin.getCTConfig().isAllowPlayerJoinAny() ) {

                String msg = config.getErrorJoinAnyNotAllowMessage();
                if ( msg != null ) {
                    player.sendMessage(msg);
                }

                return true;
            }

            if ( plugin.getAPI().getPlayerTeamName(player) != null ) {

                String msg = config.getErrorAlreadyJoinMessage();
                if ( msg != null ) {
                    player.sendMessage(msg);
                }

                return true;
            }

            String target = args[0];
            ArrayList<TeamNameSetting> teams = plugin.getAPI().getTeamNameConfig().getTeamNames();
            if ( !TeamNameConfig.containsID(teams, target) ) {

                String msg = config.getErrorInvalidTeamNameMessage(target);
                if ( msg != null ) {
                    player.sendMessage(msg);
                }

                return true;
            }

            ColorTeamingAPI api = plugin.getAPI();
            boolean needToDisplayScore = (api.getAllTeamNames().size() == 0);
            TeamNameSetting tns = api.getTeamNameConfig().getTeamNameFromID(target);
            api.addPlayerTeam(player, tns);

            // スコアボード更新
            api.refreshRestTeamMemberScore();
            if ( needToDisplayScore ) {
                api.displayScoreboard();
            }

            return true;

        }
    }

    /**
     * @see org.bukkit.command.TabCompleter#onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public List<String> onTabComplete(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length == 1 && plugin.getCTConfig().isAllowPlayerJoinAny() ) {

            String prefix = args[0].toLowerCase();
            ArrayList<String> commands = new ArrayList<String>();
            for ( TeamNameSetting tns : plugin.getAPI().getTeamNameConfig().getTeamNames() ) {
                String name = tns.getID();
                if ( name.toLowerCase().startsWith(prefix) ) {
                    commands.add(name);
                }
            }
            return commands;
        }

        return null;
    }
}
