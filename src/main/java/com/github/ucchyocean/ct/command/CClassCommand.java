/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.ColorTeamingAPI;
import com.github.ucchyocean.ct.config.ClassData;
import com.github.ucchyocean.ct.config.KitParser;
import com.github.ucchyocean.ct.config.TeamNameSetting;

/**
 * colorclass(cclass)コマンドの実行クラス
 * @author ucchy
 */
public class CClassCommand implements CommandExecutor {

    private static final String PREERR = ChatColor.RED.toString();
    private static final String PREINFO = ChatColor.GRAY.toString();

    private KitParser handler;
    private ColorTeaming plugin;

    public CClassCommand(ColorTeaming plugin) {
        this.plugin = plugin;
        handler = new KitParser();
    }

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length >= 1 && args[0].equalsIgnoreCase("check") ) {
            // cclass check コマンドの処理
            if ( !(sender instanceof Player) ) {
                sender.sendMessage(PREERR + "cclass check コマンドは、ゲーム内でのみ実行できます。");
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

        String target = args[0];
        String clas = args[1];

        ColorTeamingAPI api = plugin.getAPI();
        HashMap<TeamNameSetting, ArrayList<Player>> members = api.getAllTeamMembers();

        // 有効なチーム名かユーザー名か'all'が指定されたかを確認する
        boolean isAll = false;
        boolean isTeam = false;
        if ( target.equalsIgnoreCase("all") ) {
            // 全プレイヤー指定
            isAll = true;
        } else if ( api.isExistTeam(target) ) {
            // チーム指定
            isTeam = true;
        } else if ( api.getAllPlayers().contains(Bukkit.getPlayerExact(target)) ) {
            // ユーザー指定
        } else {
            sender.sendMessage(PREERR + "チームまたはプレイヤー " + target + " が存在しません。");
            return true;
        }

        // 有効なクラス名が指定されたか確認する
        if ( !plugin.getCTConfig().getClasses().containsKey(clas) ) {
            sender.sendMessage(PREERR + "クラス " + clas + " が存在しません。");
            return true;
        }

        // クラス設定を実行する
        ClassData cdata = plugin.getCTConfig().getClasses().get(clas);
        ArrayList<ItemStack> itemData = cdata.getItems();
        ArrayList<ItemStack> armorData = cdata.getArmor();
        ArrayList<PotionEffect> effectData = cdata.getEffect();

        ArrayList<Player> playersToSet = new ArrayList<Player>();
        if ( isAll ) {
            for ( TeamNameSetting key : members.keySet() ) {
                playersToSet.addAll(members.get(key));
            }
        } else if ( isTeam ) {
            playersToSet = members.get(target);
        } else {
            playersToSet.add(Bukkit.getPlayerExact(target));
        }

        boolean isHealOnSetClass = plugin.getCTConfig().isHealOnSetClass();
        
        for ( Player p : playersToSet ) {

            // 全回復の実行
            if ( isHealOnSetClass ) {
                heal(p);
            }
            
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
            if ( armorData != null ) {

                if (armorData.size() >= 1 && armorData.get(0) != null ) {
                    p.getInventory().setHelmet(armorData.get(0));
                }
                if (armorData.size() >= 2 && armorData.get(1) != null ) {
                    p.getInventory().setChestplate(armorData.get(1));
                }
                if (armorData.size() >= 3 && armorData.get(2) != null ) {
                    p.getInventory().setLeggings(armorData.get(2));
                }
                if (armorData.size() >= 4 && armorData.get(3) != null ) {
                    p.getInventory().setBoots(armorData.get(3));
                }
            }
            
            // ポーション効果の設定
            if ( effectData != null ) {
                p.addPotionEffects(effectData);
            }

            updateInventory(p);
        }

        String targetName;
        if ( isAll ) {
            targetName = "全てのプレイヤー";
        } else if ( isTeam ) {
            TeamNameSetting tns = api.getTeamNameFromID(target);
            targetName = "チーム" + tns.toString();
        } else {
            targetName = "プレイヤー" + target;
        }

        sender.sendMessage(PREINFO +
                String.format("%s に、%s クラスの装備とアイテムを配布しました。", 
                        targetName, clas));

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
    
    /**
     * プレイヤーの全回復、および、全エフェクトの除去を行う
     * @param player 対象プレイヤー
     */
    private void heal(Player player) {
        
        player.setHealth(player.getMaxHealth());
        player.setFireTicks(0);
        player.setFallDistance(0);
        player.setFoodLevel(20);
        Collection<PotionEffect> effects = player.getActivePotionEffects();
        for ( PotionEffect e : effects ) {
            player.removePotionEffect(e.getType());
        }
    }
}
