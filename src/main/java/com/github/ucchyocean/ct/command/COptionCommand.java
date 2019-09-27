/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2016
 */
package com.github.ucchyocean.ct.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.config.TeamOptionStatusEnum;

/**
 * coloroption(coption)コマンドの実装クラス
 * @author ucchy
 */
public class COptionCommand implements TabExecutor {

    private static final String PREINFO = ChatColor.GRAY.toString();

    private ColorTeaming plugin;

    public COptionCommand(ColorTeaming plugin) {
        this.plugin = plugin;
    }

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length <= 1 ) {
            return false;
        }

        TeamOptionStatusEnum status = getSwitchFromString(args[1]);
        if ( status == null ) {
            return false;
        }

        if ( args[0].equalsIgnoreCase("nametag") ) {
            plugin.getAPI().setNametagVisibility(status.getNametagVisibility());
            sender.sendMessage(String.format(PREINFO + "ネームタグを%sに設定しました。",
                    getStatusName(status)));
            return true;
        } else if ( args[0].equalsIgnoreCase("collision") ) {
            plugin.getAPI().setCollisionRule(status);
            sender.sendMessage(String.format(PREINFO + "当たり判定を%sに設定しました。",
                    getStatusNameForCollision(status)));
            return true;
        } else if ( args[0].equalsIgnoreCase("deathmessage") ) {
            plugin.getAPI().setDeathMessageVisibility(status);
            sender.sendMessage(String.format(PREINFO + "死亡メッセージを%sに設定しました。",
                    getStatusName(status)));
            return true;
        }

        return false;
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
            for ( String c : new String[]{"nametag", "collision", "deathmessage"} ) {
                if ( c.startsWith(prefix) ) {
                    commands.add(c);
                }
            }
            return commands;

        } else if ( args.length == 2 ) {

            String prefix = args[1].toLowerCase();
            ArrayList<String> commands = new ArrayList<String>();
            for ( String c : new String[]{"on", "off", "team", "other", "always", "never",
                    "for_other_teams", "for_own_team"} ) {
                if ( c.startsWith(prefix) ) {
                    commands.add(c);
                }
            }
            return commands;
        }

        return null;
    }

    private static TeamOptionStatusEnum getSwitchFromString(String str) {

        if ( str.equalsIgnoreCase("on") || str.equalsIgnoreCase("always") ) {
            return TeamOptionStatusEnum.ALWAYS;
        } else if ( str.equalsIgnoreCase("off") || str.equalsIgnoreCase("never") ) {
            return TeamOptionStatusEnum.NEVER;
        } else if ( str.equalsIgnoreCase("team") || str.equalsIgnoreCase("for_other_teams") ) {
            return TeamOptionStatusEnum.FOR_OTHER_TEAMS;
        } else if ( str.equalsIgnoreCase("other") || str.equalsIgnoreCase("for_own_team") ) {
            return TeamOptionStatusEnum.FOR_OWN_TEAM;
        }
        return null;
    }

    private static String getStatusName(TeamOptionStatusEnum status) {

        if ( status == TeamOptionStatusEnum.ALWAYS ) {
            return "表示";
        } else if ( status == TeamOptionStatusEnum.NEVER ) {
            return "非表示";
        } else if ( status == TeamOptionStatusEnum.FOR_OTHER_TEAMS ) {
            return "自チームは表示";
        } else if ( status == TeamOptionStatusEnum.FOR_OWN_TEAM ) {
            return "他チームは表示";
        }
        return "";
    }

    private static String getStatusNameForCollision(TeamOptionStatusEnum status) {

        if ( status == TeamOptionStatusEnum.ALWAYS ) {
            return "有効";
        } else if ( status == TeamOptionStatusEnum.NEVER ) {
            return "無効";
        } else if ( status == TeamOptionStatusEnum.FOR_OTHER_TEAMS ) {
            return "他チームメンバーは無効";
        } else if ( status == TeamOptionStatusEnum.FOR_OWN_TEAM ) {
            return "自チームメンバーは無効";
        }
        return "";
    }
}
