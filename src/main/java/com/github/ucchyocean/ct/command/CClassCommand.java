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
import org.bukkit.inventory.ItemStack;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.KitHandler;

/**
 * @author ucchy
 * colorclass(cclass)コマンドの実行クラス
 */
public class CClassCommand implements CommandExecutor {

    private static final String PREERR = ChatColor.RED.toString();
    private static final String PREINFO = ChatColor.GRAY.toString();

    private KitHandler handler;

    public CClassCommand() {
        handler = new KitHandler();
    }

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length >= 1 && args[0].equalsIgnoreCase("hand") ) {
            // cclass hand コマンドの処理
            if ( !(sender instanceof Player) ) {
                sender.sendMessage(PREERR + "cclass hand コマンドは、ゲーム内でのみ実行できます。");
                return true;
            }
            Player player = (Player)sender;
            String message = handler.getItemInfo(player.getItemInHand());
            sender.sendMessage("アイテム情報 " + message);
            return true;
        }

        // ここ以下は引数が2つ以上必要である。
        if ( args.length < 2 ) {
            return false;
        }

        String group = args[0];
        String clas = args[1];

        Hashtable<String, ArrayList<Player>> members = ColorTeaming.instance.getAllTeamMembers();

        // 有効なグループ名かユーザー名か'all'が指定されたかを確認する
        boolean isAll = false;
        boolean isGroup = false;
        if ( group.equalsIgnoreCase("all") ) {
            // 全プレイヤー指定
            isAll = true;
        } else if ( members.containsKey(group)  ) {
            // グループ指定
            isGroup = true;
        } else if ( ColorTeaming.instance.getAllPlayers().contains(ColorTeaming.instance.getPlayerExact(group)) ) {
            // ユーザー指定
        } else {
            sender.sendMessage(PREERR + "グループまたはプレイヤー " + group + " が存在しません。");
            return true;
        }

        // 有効なクラス名が指定されたか確認する
        if ( !ColorTeaming.instance.getCTConfig().getClassItems().containsKey(clas) ) {
            sender.sendMessage(PREERR + "クラス " + clas + " が存在しません。");
            return true;
        }

        // クラス設定を実行する
        String items = ColorTeaming.instance.getCTConfig().getClassItems().get(clas);
        String armor = ColorTeaming.instance.getCTConfig().getClassArmors().get(clas);

        ArrayList<ItemStack> itemData = handler.convertToItemStack(items);
        ArrayList<ItemStack> armorData = null;
        if ( armor != null ) {
            armorData = handler.convertToItemStack(armor);
        }

        ArrayList<Player> playersToSet = new ArrayList<Player>();
        if ( isAll ) {
            Enumeration<String> groups = members.keys();
            while ( groups.hasMoreElements() ) {
                playersToSet.addAll(members.get(groups.nextElement()));
            }
        } else if ( isGroup ) {
            playersToSet = members.get(group);
        } else {
            playersToSet.add(ColorTeaming.instance.getPlayerExact(group));
        }

        for ( Player p : playersToSet ) {

            // インベントリの消去
            p.getInventory().clear();
            p.getInventory().setHelmet(null);
            p.getInventory().setChestplate(null);
            p.getInventory().setLeggings(null);
            p.getInventory().setBoots(null);

            // アイテムの配布
            for ( ItemStack item : itemData ) {
                if ( item != null ) {
                    p.getInventory().addItem(item);
                }
            }

            // 防具の配布
            if ( armor != null ) {

                if (armorData.get(0) != null ) {
                    p.getInventory().setHelmet(armorData.get(0));
                }
                if (armorData.get(1) != null ) {
                    p.getInventory().setChestplate(armorData.get(1));
                }
                if (armorData.get(2) != null ) {
                    p.getInventory().setLeggings(armorData.get(2));
                }
                if (armorData.get(3) != null ) {
                    p.getInventory().setBoots(armorData.get(3));
                }
            }

            updateInventory(p);
        }

        String target;
        if ( isAll ) {
            target = "全てのプレイヤー";
        } else {
            String type = "グループ";
            if ( !isGroup ) {
                type = "プレイヤー";
            }
            target = type + group;
        }

        sender.sendMessage(PREINFO +
                String.format("%s に、%s クラスの装備とアイテムを配布しました。", target, clas));

        return true;
    }

    /**
     * プレイヤーのインベントリをアップデートする
     * @param player プレイヤー
     */
    @SuppressWarnings("deprecation")
    private void updateInventory(Player player) {
        player.updateInventory();
    }
}
