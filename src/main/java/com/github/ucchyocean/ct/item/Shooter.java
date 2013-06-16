/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.item;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.ucchyocean.ct.ColorTeaming;

/**
 * シューター
 * @author ucchy
 */
public class Shooter implements Listener, CustomItem {

    private static final String NAME = "shooter";
    private static final String DISPLAY_NAME =
            ChatColor.BLUE.toString() + ChatColor.BOLD.toString() + NAME;
    private static final int LEVEL = 6;
    private static final int COST = 10;
    private static final int REVIVE_SECONDS = 5;
    private static final int REVIVE_AMOUNT = 30;
    private static final int RANGE = 50;

    private ItemStack item;

    /**
     * コンストラクタ
     */
    public Shooter() {

        item = new ItemStack(Material.TRIPWIRE_HOOK, 1);
        ItemMeta shooterMeta = item.getItemMeta();
        shooterMeta.setDisplayName(getDisplayName());
        item.setItemMeta(shooterMeta);
    }

    /**
     * @see com.github.ucchyocean.ct.item.CustomItem#getDisplayName()
     */
    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    /**
     * @see com.github.ucchyocean.ct.item.CustomItem#getItemStack()
     */
    @Override
    public ItemStack getItemStack() {
        return item;
    }

    /**
     * クリックされたときのイベント処理
     * @param event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        // シューターを手に持っているときに発生したイベントで無い場合は、無視する
        if ( player.getItemInHand() == null ||
                player.getItemInHand().getType() == Material.AIR ||
                !player.getItemInHand().getItemMeta().hasDisplayName() ||
                !player.getItemInHand().getItemMeta().getDisplayName().equals(DISPLAY_NAME) ) {
            return;
        }

        // 左クリック出なければ無視する。
        if ( event.getAction() == Action.PHYSICAL ) {
            return;
        } else if ( event.getAction() == Action.RIGHT_CLICK_AIR ||
                event.getAction() == Action.RIGHT_CLICK_BLOCK ) {
            event.setCancelled(true);
            return;
        }

        Location eLoc = player.getEyeLocation();

        // クリックしたところが空か、遠すぎる場合
        if ( player.getTargetBlock(null, RANGE).getType() == Material.AIR ) {
            player.sendMessage(ChatColor.RED + "out of range!!");
            player.playEffect(eLoc, Effect.SMOKE, 4);
            player.playEffect(eLoc, Effect.SMOKE, 4);
            player.playSound(eLoc, Sound.IRONGOLEM_THROW, (float)1.0, (float)1.5);
            event.setCancelled(true);
            return;
        }

        // 燃料が無い場合
        if ( !hasExperience(player, COST) ) {
            player.sendMessage(ChatColor.RED + "no fuel!!");
            player.playEffect(eLoc, Effect.SMOKE, 4);
            player.playEffect(eLoc, Effect.SMOKE, 4);
            player.playSound(eLoc, Sound.IRONGOLEM_THROW, (float)1.0, (float)1.5);
            event.setCancelled(true);
            return;
        }

        // 燃料消費
        takeExperience(player, COST);

        // 今回の操作で燃料がなくなった場合、数秒後に復活させる
        if ( !hasExperience(player, COST) ) {
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    takeExperience(player, -REVIVE_AMOUNT);
                }
            };
            runnable.runTaskLater(ColorTeaming.instance, REVIVE_SECONDS * 20);
            player.sendMessage(ChatColor.GOLD +
                    "your fuel will revive after " + REVIVE_SECONDS + " seconds.");
        }

        // 飛翔
        player.setVelocity(player.getLocation().getDirection().multiply(LEVEL));
        player.setFallDistance(-1000F);
        player.playEffect(eLoc, Effect.POTION_BREAK, 21);
        player.playEffect(eLoc, Effect.POTION_BREAK, 21);

        event.setCancelled(true);
    }

    /**
     * プレイヤーから、指定した経験値量を減らす。
     * @param player プレイヤー
     * @param amount 減らす量
     */
    public static void takeExperience(final Player player, int amount) {
        player.giveExp(-amount);
        updateExp(player);
    }

    /**
     * プレイヤーが指定した量の経験値を持っているかどうか判定する。
     * @param player プレイヤー
     * @param amount 判定する量
     * @return もっているかどうか
     */
    public static boolean hasExperience(final Player player, int amount) {
        return (player.getTotalExperience() >= amount);
    }

    /**
     * プレイヤーの経験値量を、指定値に設定する。
     * @param player プレイヤー
     * @param amount 経験値の量
     */
    public static void setExperience(final Player player, int amount) {
        player.setTotalExperience(amount);
        updateExp(player);
    }

    /**
     * 経験値表示を更新する
     * @param player 更新対象のプレイヤー
     */
    private static void updateExp(final Player player) {

        int total = player.getTotalExperience();
        player.setLevel(0);
        player.setExp(0);
        while ( total > player.getExpToLevel() ) {
            total -= player.getExpToLevel();
            player.setLevel(player.getLevel()+1);
        }
        float xp = (float)total / (float)player.getExpToLevel();
        player.setExp(xp);
    }
}
