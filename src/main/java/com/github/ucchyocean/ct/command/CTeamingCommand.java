/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.command;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.Utility;
import com.github.ucchyocean.ct.scoreboard.PlayerCriteria;
import com.github.ucchyocean.ct.scoreboard.SidebarCriteria;

/**
 * @author ucchy
 * colorteaming(ct)コマンドの実行クラス
 */
public class CTeamingCommand implements CommandExecutor {

    private static final String PREERR = ChatColor.RED.toString();
    private static final String PREINFO = ChatColor.GRAY.toString();

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length < 1 ) {
            return false;
        }

        if ( args[0].equalsIgnoreCase("reload") ) {

            ColorTeaming.instance.reloadCTConfig();
            sender.sendMessage("config.ymlの再読み込みを行いました。");
            return true;

        } else if ( args[0].equalsIgnoreCase("removeall") ) {

            Hashtable<String, ArrayList<Player>> members =
                    ColorTeaming.instance.getAllTeamMembers();
            Enumeration<String> keys = members.keys();
            while ( keys.hasMoreElements() ) {
                String group = keys.nextElement();
                for ( Player p : members.get(group) ) {
                    ColorTeaming.instance.leavePlayerTeam(p);
                }
                ColorTeaming.instance.removeTeam(group);
            }

            // サイドバー削除、タブキーリスト更新
            ColorTeaming.instance.removeSidebar();
            ColorTeaming.instance.refreshTabkeyListScore();
            ColorTeaming.instance.refreshBelowNameScore();

            sender.sendMessage(PREINFO + "全てのグループが解散しました。");

            return true;

        } else if ( args.length >= 2 && args[0].equalsIgnoreCase("remove") ) {

            Hashtable<String, ArrayList<Player>> members =
                    ColorTeaming.instance.getAllTeamMembers();
            String group = args[1];
            if ( !members.containsKey(group) ) {
                sender.sendMessage(PREERR + "グループ " + group + " は存在しません。");
                return true;
            }

            for ( Player p : members.get(group) ) {
                ColorTeaming.instance.leavePlayerTeam(p);
                p.sendMessage(PREINFO + "グループ " + group + " が解散しました。");
            }
            ColorTeaming.instance.removeTeam(group);

            // サイドバー再作成、タブキーリスト更新
            ColorTeaming.instance.makeSidebar();
            ColorTeaming.instance.refreshTabkeyListScore();
            ColorTeaming.instance.refreshBelowNameScore();

            sender.sendMessage(PREINFO + "グループ " + group + " が解散しました。");

            return true;

        } else if ( args.length >= 2 && args[0].equalsIgnoreCase("trophy") ) {

            if ( !Utility.tryIntParse(args[1]) && !args[1].equalsIgnoreCase("off") ) {
                sender.sendMessage(PREERR + "キル数 " + args[1] + " は、数値として解釈できません。");
                return true;
            }

            int amount;
            if ( args[1].equalsIgnoreCase("off") ) {
                amount = 0;
            } else {
                amount = Integer.parseInt(args[1]);
            }

            if ( amount < 0 ) {
                sender.sendMessage(PREERR + "ct trophy コマンドには、マイナス値を指定できません。");
                return true;
            }

            ColorTeaming.instance.getCTConfig().setKillTrophy(amount);

            if ( amount == 0 ) {
                sender.sendMessage(PREINFO + "キル数達成時の通知機能をオフにしました。");
            } else {
                sender.sendMessage(PREINFO + "キル数達成時の通知機能を、" + amount + "キル数に設定します。");
            }
            return true;

        } else if ( args.length >= 2 && args[0].equalsIgnoreCase("reachTrophy") ) {

            if ( !Utility.tryIntParse(args[1]) && !args[1].equalsIgnoreCase("off") ) {
                sender.sendMessage(PREERR + "キル数 " + args[1] + " は、数値として解釈できません。");
                return true;
            }

            int amount;
            if ( args[1].equalsIgnoreCase("off") ) {
                amount = 0;
            } else {
                amount = Integer.parseInt(args[1]);
            }

            if ( amount < 0 ) {
                sender.sendMessage(PREERR + "ct reachTrophy コマンドには、マイナス値を指定できません。");
                return true;
            } else if ( ColorTeaming.instance.getCTConfig().getKillTrophy() < amount ) {
                sender.sendMessage(PREERR + "killTrophyの設定値より大きな値は指定できません。");
                return true;
            }

            ColorTeaming.instance.getCTConfig().setKillReachTrophy(amount);

            if ( amount == 0 ) {
                sender.sendMessage(PREINFO + "キル数リーチ時の通知機能をオフにしました。");
            } else {
                sender.sendMessage(PREINFO + "キル数リーチ時の通知機能を、" + amount + "キル数に設定します。");
            }
            return true;

        } else if ( args.length >= 2 && args[0].equalsIgnoreCase("allowJoinAny") ) {

            if ( args[1].equalsIgnoreCase("on") ) {
                ColorTeaming.instance.getCTConfig().setAllowPlayerJoinAny(true);
                sender.sendMessage(ChatColor.GRAY + "一般プレイヤーの /cjoin (group) の使用が可能になりました。");
                return true;
            } else if ( args[1].equalsIgnoreCase("off") ) {
                ColorTeaming.instance.getCTConfig().setAllowPlayerJoinAny(false);
                sender.sendMessage(ChatColor.GRAY + "一般プレイヤーの /cjoin (group) の使用が不可になりました。");
                return true;
            }

            return false;

        } else if ( args.length >= 2 && args[0].equalsIgnoreCase("allowJoinRandom") ) {

            if ( args[1].equalsIgnoreCase("on") ) {
                ColorTeaming.instance.getCTConfig().setAllowPlayerJoinRandom(true);
                sender.sendMessage(ChatColor.GRAY + "一般プレイヤーの /cjoin の使用が可能になりました。");
                return true;
            } else if ( args[1].equalsIgnoreCase("off") ) {
                ColorTeaming.instance.getCTConfig().setAllowPlayerJoinRandom(false);
                sender.sendMessage(ChatColor.GRAY + "一般プレイヤーの /cjoin の使用が不可になりました。");
                return true;
            }

            return false;

        } else if ( args.length >= 3 && args[0].equalsIgnoreCase("add") ) {

            String group = args[1];
            if ( !Utility.isValidColor(group) ) {
                sender.sendMessage(PREERR + "グループ " + group + " は設定できないグループ名です。");
                return true;
            }

            Player player = ColorTeaming.instance.getPlayerExact(args[2]);
            if ( player == null ) {
                sender.sendMessage(PREERR + "プレイヤー " + args[2] + " は存在しません。");
                return true;
            }

            boolean isNewGroup = ! ColorTeaming.instance.getAllTeamMembers().containsKey(group);
            ColorTeaming.instance.addPlayerTeam(player, group);

            // メンバー情報をlastdataに保存する
            ColorTeaming.sdhandler.save("lastdata");

            // サイドバーの更新 グループが増える場合は、再生成する
            if ( isNewGroup ) {
                ColorTeaming.instance.makeSidebar();
            }
            ColorTeaming.instance.refreshSidebarScore();
            ColorTeaming.instance.refreshTabkeyListScore();
            ColorTeaming.instance.refreshBelowNameScore();

            sender.sendMessage(PREINFO + "プレイヤー " + player.getName() + " をグループ " +
                    group + " に追加しました。");

            return true;

        } else if ( args.length >= 2 && args[0].equalsIgnoreCase("side") ) {

            if ( args[1].equalsIgnoreCase("kill") ) {
                ColorTeaming.instance.getCTConfig().setSideCriteria(SidebarCriteria.KILL_COUNT);
            } else if ( args[1].equalsIgnoreCase("death") ) {
                ColorTeaming.instance.getCTConfig().setSideCriteria(SidebarCriteria.DEATH_COUNT);
            } else if ( args[1].equalsIgnoreCase("point") ) {
                ColorTeaming.instance.getCTConfig().setSideCriteria(SidebarCriteria.POINT);
            } else if ( args[1].equalsIgnoreCase("rest") ) {
                ColorTeaming.instance.getCTConfig().setSideCriteria(SidebarCriteria.REST_PLAYER);
            } else if ( args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("none") ) {
                ColorTeaming.instance.getCTConfig().setSideCriteria(SidebarCriteria.NONE);
            } else {
                return false;
            }

            // サイドバーの更新
            ColorTeaming.instance.makeSidebar();

            String criteria = ColorTeaming.instance.getCTConfig().getSideCriteria().toString();
            sender.sendMessage(PREINFO + "サイドバーの表示を" + criteria + "にしました。");

            return true;

        } else if ( args.length >= 2 && args[0].equalsIgnoreCase("list") ) {

            if ( args[1].equalsIgnoreCase("kill") ) {
                ColorTeaming.instance.getCTConfig().setListCriteria(PlayerCriteria.KILL_COUNT);
            } else if ( args[1].equalsIgnoreCase("death") ) {
                ColorTeaming.instance.getCTConfig().setListCriteria(PlayerCriteria.DEATH_COUNT);
            } else if ( args[1].equalsIgnoreCase("point") ) {
                ColorTeaming.instance.getCTConfig().setListCriteria(PlayerCriteria.POINT);
            } else if ( args[1].equalsIgnoreCase("health") ) {
                ColorTeaming.instance.getCTConfig().setListCriteria(PlayerCriteria.HEALTH);
            } else if ( args[1].equalsIgnoreCase("custom") ) {
                ColorTeaming.instance.getCTConfig().setListCriteria(PlayerCriteria.CUSTOM);
            } else if ( args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("none") ) {
                ColorTeaming.instance.getCTConfig().setListCriteria(PlayerCriteria.NONE);
            } else {
                return false;
            }

            // スコアボードの更新
            ColorTeaming.instance.makeTabkeyListScore();

            String criteria = ColorTeaming.instance.getCTConfig().getListCriteria().toString();
            sender.sendMessage(PREINFO + "リストの表示を" + criteria + "にしました。");

            return true;

        } else if ( args.length >= 2 && args[0].equalsIgnoreCase("below") ) {

            if ( args[1].equalsIgnoreCase("kill") ) {
                ColorTeaming.instance.getCTConfig().setBelowCriteria(PlayerCriteria.KILL_COUNT);
            } else if ( args[1].equalsIgnoreCase("death") ) {
                ColorTeaming.instance.getCTConfig().setBelowCriteria(PlayerCriteria.DEATH_COUNT);
            } else if ( args[1].equalsIgnoreCase("point") ) {
                ColorTeaming.instance.getCTConfig().setBelowCriteria(PlayerCriteria.POINT);
            } else if ( args[1].equalsIgnoreCase("health") ) {
                ColorTeaming.instance.getCTConfig().setBelowCriteria(PlayerCriteria.HEALTH);
            } else if ( args[1].equalsIgnoreCase("custom") ) {
                ColorTeaming.instance.getCTConfig().setBelowCriteria(PlayerCriteria.CUSTOM);
            } else if ( args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("none") ) {
                ColorTeaming.instance.getCTConfig().setBelowCriteria(PlayerCriteria.NONE);
            } else {
                return false;
            }

            // スコアボードの更新
            ColorTeaming.instance.makeBelowNameScore();

            // 設定の保存
            String criteria = ColorTeaming.instance.getCTConfig().getBelowCriteria().toString();
            sender.sendMessage(PREINFO + "名前欄のスコア表示を" + criteria + "にしました。");

            return true;

        }

        return false;
    }

}
