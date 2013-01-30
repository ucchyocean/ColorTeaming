/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.cmt.command;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.ucchyocean.cmt.ColorMeTeaming;

/**
 * @author ucchy
 * colorclass(cclass)コマンドの実行クラス
 */
public class CClassCommand implements CommandExecutor {

    private static final String PREERR = ChatColor.RED.toString();
    private static final String PREINFO = ChatColor.GRAY.toString();

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length < 2 ) {
            return false;
        }

        String group = args[0];
        String clas = args[1];

        Hashtable<String, Vector<Player>> members = ColorMeTeaming.getAllColorMembers();

        // 有効なグループ名が指定されたか確認する
        if ( !members.containsKey(group) ) {
            sender.sendMessage(PREERR + "グループ " + group + " が存在しません。");
            return true;
        }

        // 有効なクラス名が指定されたか確認する
        if ( !ColorMeTeaming.classItems.containsKey(clas) ) {
            sender.sendMessage(PREERR + "クラス " + clas + " が存在しません。");
            return true;
        }

        // クラス設定を実行する
        String items = ColorMeTeaming.classItems.get(clas);
        String armor = ColorMeTeaming.classArmors.get(clas);
        int[][] itemData = parseClassItemData(items);

        for ( Player p : members.get(group) ) {

            // インベントリの消去
            p.getInventory().clear();
            p.getInventory().setHelmet(null);
            p.getInventory().setChestplate(null);
            p.getInventory().setLeggings(null);
            p.getInventory().setBoots(null);

            // アイテムの配布
            for (int i = 0; i < itemData.length; i++) {
                if (itemData[i][2] == 0) giveItems(p, itemData[i][0], itemData[i][1]);
                else giveItems(p, itemData[i][0], itemData[i][1], itemData[i][2]);
            }

            // 防具の配布
            if ( armor != null ) {
                String[] armorarr = armor.split("[,]");
                if (Integer.parseInt(armorarr[0]) != 0) {
                    p.getInventory().setHelmet(new ItemStack(Integer.parseInt(armorarr[0]), 1));
                }
                if (Integer.parseInt(armorarr[1]) != 0) {
                    p.getInventory().setChestplate(new ItemStack(Integer.parseInt(armorarr[1]), 1));
                }
                if (Integer.parseInt(armorarr[2]) != 0) {
                    p.getInventory().setLeggings(new ItemStack(Integer.parseInt(armorarr[2]), 1));
                }
                if (Integer.parseInt(armorarr[3]) != 0) {
                    p.getInventory().setBoots(new ItemStack(Integer.parseInt(armorarr[3]), 1));
                }
            }
        }

        sender.sendMessage(PREINFO +
                String.format("グループ %s に、%s クラスの装備とアイテムを配布しました。", group, clas));

        return true;
    }



    private int[][] parseClassItemData(String data) {

        ArrayList<int[]> buffer = new ArrayList<int[]>();
        String[] array = data.split("[,]");
        for (int i = 0; i < array.length; i++) {
            int amnt = 0;
            int dmg = 0;
            int item = 0;

            if (array[i].contains("@")) {
                String[] array2 = array[i].split("[@]");

                if (array2[1].contains(":")) {
                    String[] array3 = array2[1].split("[:]");

                    try {
                        item = Integer.parseInt(array2[0]);
                        dmg = Integer.parseInt(array3[0]);
                        amnt = Integer.parseInt(array3[1]);
                    } catch (NumberFormatException e) {}
                } else {
                    try {
                        item = Integer.parseInt(array2[0]);
                        dmg = Integer.parseInt(array2[1]);
                        amnt = 1;
                    } catch (NumberFormatException e) {}
                }
            } else if (array[i].contains(":")) {
                String[] array2 = array[i].split("[:]");

                try {
                    item = Integer.parseInt(array2[0]);
                    amnt = Integer.parseInt(array2[1]);
                } catch (NumberFormatException e) {}
            } else {
                try {
                    item = Integer.parseInt(array[i]);
                    amnt = 1;
                } catch (NumberFormatException e) {}
            }

            if (amnt > 0 || item > 0) {
                buffer.add(new int[]{item, amnt, dmg});
            }
        }

        int[][] result = new int[buffer.size()][];
        buffer.toArray(result);

        return result;
    }

    private boolean giveItems(Player p, int item, int amnt) {
        Material m = Material.getMaterial(item);
        if (m == null) {
            p.sendMessage(PREERR + "指定されたItemID " + item + " が見つかりません。");
            return false;
        }

        while (amnt > 64) {
            p.getInventory().addItem(new ItemStack(item, 64));
            amnt -= 64;
        }

        p.getInventory().addItem(new ItemStack(item, amnt));

        return true;
    }

    private boolean giveItems(Player p, int item, int amnt, int dmg) {
        Material m = Material.getMaterial(item);
        if (m == null) {
            p.sendMessage(PREERR + "指定されたItemID " + item + " が見つかりません。");
            return false;
        }

        while (amnt > 64) {
            p.getInventory().addItem(new ItemStack(item, 64, (byte)dmg));
            amnt -= 64;
        }

        p.getInventory().addItem(new ItemStack(item, amnt, (byte)dmg));

        return true;
    }
}
