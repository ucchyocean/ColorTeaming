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

            ColorTeaming.reloadCTConfig();
            sender.sendMessage("config.ymlの再読み込みを行いました。");
            return true;

        } else if ( args[0].equalsIgnoreCase("removeall") ) {

            Hashtable<String, ArrayList<Player>> members = ColorTeaming.getAllTeamMembers();
            Enumeration<String> keys = members.keys();
            while ( keys.hasMoreElements() ) {
                String group = keys.nextElement();
                for ( Player p : members.get(group) ) {
                    ColorTeaming.leavePlayerTeam(p);
                }
                ColorTeaming.removeTeam(group);
            }

            // サイドバー削除、タブキーリスト更新
            ColorTeaming.removeSidebar();
            ColorTeaming.refreshTabkeyListScore();
            ColorTeaming.refreshBelowNameScore();

            sender.sendMessage(PREINFO + "全てのグループが解散しました。");

            return true;

        } else if ( args.length >= 2 && args[0].equalsIgnoreCase("remove") ) {

            Hashtable<String, ArrayList<Player>> members = ColorTeaming.getAllTeamMembers();
            String group = args[1];
            if ( !members.containsKey(group) ) {
                sender.sendMessage(PREERR + "グループ " + group + " は存在しません。");
                return true;
            }

            for ( Player p : members.get(group) ) {
                ColorTeaming.leavePlayerTeam(p);
                p.sendMessage(PREINFO + "グループ " + group + " が解散しました。");
            }
            ColorTeaming.removeTeam(group);

            // サイドバー再作成、タブキーリスト更新
            ColorTeaming.makeSidebar();
            ColorTeaming.refreshTabkeyListScore();
            ColorTeaming.refreshBelowNameScore();

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

            ColorTeaming.getCTConfig().setKillTrophy(amount);

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
            } else if ( ColorTeaming.getCTConfig().getKillTrophy() < amount ) {
                sender.sendMessage(PREERR + "killTrophyの設定値より大きな値は指定できません。");
                return true;
            }

            ColorTeaming.getCTConfig().setKillReachTrophy(amount);

            if ( amount == 0 ) {
                sender.sendMessage(PREINFO + "キル数リーチ時の通知機能をオフにしました。");
            } else {
                sender.sendMessage(PREINFO + "キル数リーチ時の通知機能を、" + amount + "キル数に設定します。");
            }
            return true;

        } else if ( args.length >= 2 && args[0].equalsIgnoreCase("allowJoinAny") ) {

            if ( args[1].equalsIgnoreCase("on") ) {
                ColorTeaming.getCTConfig().setAllowPlayerJoinAny(true);
                sender.sendMessage(ChatColor.GRAY + "一般プレイヤーの /cjoin (group) の使用が可能になりました。");
                return true;
            } else if ( args[1].equalsIgnoreCase("off") ) {
                ColorTeaming.getCTConfig().setAllowPlayerJoinAny(false);
                sender.sendMessage(ChatColor.GRAY + "一般プレイヤーの /cjoin (group) の使用が不可になりました。");
                return true;
            }

            return false;

        } else if ( args.length >= 2 && args[0].equalsIgnoreCase("allowJoinRandom") ) {

            if ( args[1].equalsIgnoreCase("on") ) {
                ColorTeaming.getCTConfig().setAllowPlayerJoinRandom(true);
                sender.sendMessage(ChatColor.GRAY + "一般プレイヤーの /cjoin の使用が可能になりました。");
                return true;
            } else if ( args[1].equalsIgnoreCase("off") ) {
                ColorTeaming.getCTConfig().setAllowPlayerJoinRandom(false);
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

            Player player = ColorTeaming.getPlayerExact(args[2]);
            if ( player == null ) {
                sender.sendMessage(PREERR + "プレイヤー " + args[2] + " は存在しません。");
                return true;
            }

            boolean isNewGroup = ! ColorTeaming.getAllTeamMembers().containsKey(group);
            ColorTeaming.addPlayerTeam(player, group);

            // メンバー情報をlastdataに保存する
            ColorTeaming.sdhandler.save("lastdata");

            // サイドバーの更新 グループが増える場合は、再生成する
            if ( isNewGroup ) {
                ColorTeaming.makeSidebar();
            }
            ColorTeaming.refreshSidebarScore();
            ColorTeaming.refreshTabkeyListScore();
            ColorTeaming.refreshBelowNameScore();

            sender.sendMessage(PREINFO + "プレイヤー " + player.getName() + " をグループ " +
                    group + " に追加しました。");

            return true;

        } else if ( args.length >= 2 && args[0].equalsIgnoreCase("side") ) {

            if ( args[1].equalsIgnoreCase("kill") ) {
                ColorTeaming.getCTConfig().setSideCriteria(SidebarCriteria.KILL_COUNT);
            } else if ( args[1].equalsIgnoreCase("death") ) {
                ColorTeaming.getCTConfig().setSideCriteria(SidebarCriteria.DEATH_COUNT);
            } else if ( args[1].equalsIgnoreCase("point") ) {
                ColorTeaming.getCTConfig().setSideCriteria(SidebarCriteria.POINT);
            } else if ( args[1].equalsIgnoreCase("rest") ) {
                ColorTeaming.getCTConfig().setSideCriteria(SidebarCriteria.REST_PLAYER);
            } else if ( args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("none") ) {
                ColorTeaming.getCTConfig().setSideCriteria(SidebarCriteria.NONE);
            } else {
                return false;
            }

            // サイドバーの更新
            ColorTeaming.makeSidebar();

            String criteria = ColorTeaming.getCTConfig().getSideCriteria().toString();
            sender.sendMessage(PREINFO + "サイドバーの表示を" + criteria + "にしました。");

            return true;

        } else if ( args.length >= 2 && args[0].equalsIgnoreCase("list") ) {

            if ( args[1].equalsIgnoreCase("kill") ) {
                ColorTeaming.getCTConfig().setListCriteria(PlayerCriteria.KILL_COUNT);
            } else if ( args[1].equalsIgnoreCase("death") ) {
                ColorTeaming.getCTConfig().setListCriteria(PlayerCriteria.DEATH_COUNT);
            } else if ( args[1].equalsIgnoreCase("point") ) {
                ColorTeaming.getCTConfig().setListCriteria(PlayerCriteria.POINT);
            } else if ( args[1].equalsIgnoreCase("health") ) {
                ColorTeaming.getCTConfig().setListCriteria(PlayerCriteria.HEALTH);
            } else if ( args[1].equalsIgnoreCase("custom") ) {
                ColorTeaming.getCTConfig().setListCriteria(PlayerCriteria.CUSTOM);
            } else if ( args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("none") ) {
                ColorTeaming.getCTConfig().setListCriteria(PlayerCriteria.NONE);
            } else {
                return false;
            }

            // スコアボードの更新
            ColorTeaming.makeTabkeyListScore();

            String criteria = ColorTeaming.getCTConfig().getListCriteria().toString();
            sender.sendMessage(PREINFO + "リストの表示を" + criteria + "にしました。");

            return true;

        } else if ( args.length >= 2 && args[0].equalsIgnoreCase("below") ) {

            if ( args[1].equalsIgnoreCase("kill") ) {
                ColorTeaming.getCTConfig().setBelowCriteria(PlayerCriteria.KILL_COUNT);
            } else if ( args[1].equalsIgnoreCase("death") ) {
                ColorTeaming.getCTConfig().setBelowCriteria(PlayerCriteria.DEATH_COUNT);
            } else if ( args[1].equalsIgnoreCase("point") ) {
                ColorTeaming.getCTConfig().setBelowCriteria(PlayerCriteria.POINT);
            } else if ( args[1].equalsIgnoreCase("health") ) {
                ColorTeaming.getCTConfig().setBelowCriteria(PlayerCriteria.HEALTH);
            } else if ( args[1].equalsIgnoreCase("custom") ) {
                ColorTeaming.getCTConfig().setBelowCriteria(PlayerCriteria.CUSTOM);
            } else if ( args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("none") ) {
                ColorTeaming.getCTConfig().setBelowCriteria(PlayerCriteria.NONE);
            } else {
                return false;
            }

            // スコアボードの更新
            ColorTeaming.makeBelowNameScore();

            // 設定の保存
            String criteria = ColorTeaming.getCTConfig().getBelowCriteria().toString();
            sender.sendMessage(PREINFO + "名前欄のスコア表示を" + criteria + "にしました。");

            return true;

        }

        return false;
    }

}
