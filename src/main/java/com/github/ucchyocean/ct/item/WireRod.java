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
import org.bukkit.entity.Fish;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.github.ucchyocean.ct.ColorTeaming;

/**
 * ワイヤロッドプラグイン
 * @author ucchy
 */
public class WireRod implements Listener, CustomItem {

    private static final String NAME = "wirerod";
    private static final String DISPLAY_NAME =
            ChatColor.BLUE.toString() + ChatColor.BOLD.toString() + NAME;
    private static final int LEVEL = 6;
    private static final int COST = 5;
    private static final int REVIVE_SECONDS = 5;
    private static final int REVIVE_AMOUNT = 30;
    private static final double HOOK_LAUNCH_SPEED = 3.0;

    private ItemStack item;

    /**
     * コンストラクタ
     */
    public WireRod() {

        item = new ItemStack(Material.FISHING_ROD, 1);
        ItemMeta wirerodMeta = item.getItemMeta();
        wirerodMeta.setDisplayName(DISPLAY_NAME);
        item.setItemMeta(wirerodMeta);
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
     * Wirerodの針を投げたり、針がかかったときに呼び出されるメソッド
     * @param event
     */
    @EventHandler
    public void onHook(PlayerFishEvent event) {

        final Player player = event.getPlayer();
        final Fish hook = event.getHook();

        // ワイヤロッドを手に持っているときに発生したイベントで無い場合は無視する
        if ( player.getItemInHand() == null ||
                player.getItemInHand().getType() == Material.AIR ||
                !player.getItemInHand().getItemMeta().hasDisplayName() ||
                !player.getItemInHand().getItemMeta().getDisplayName().equals(DISPLAY_NAME) ) {
            return;
        }

        if ( event.getState() == State.FISHING ) {
            // 針を投げるときの処理

            // 針の打ち出し速度を加速する
            hook.setVelocity(hook.getVelocity().multiply(HOOK_LAUNCH_SPEED));
            return;

        } else if ( event.getState() == State.CAUGHT_ENTITY ||
                event.getState() == State.IN_GROUND ) {
            // 針をひっぱるときの処理

            // ひっかかっているのは自分なら、2ダメージ(1ハート)を与える
            if ( event.getCaught() != null &&
                    event.getCaught().equals(player) ) {
                player.damage(2, player);
                return;
            }

            Location eLoc = player.getEyeLocation();
            Location baseLoc = player.getLocation();

            // 経験値が不足している場合は、燃料切れとして終了する
            if ( !hasExperience(player, COST) ) {
                player.sendMessage(ChatColor.RED + "no fuel!!");
                player.playEffect(eLoc, Effect.SMOKE, 4);
                player.playEffect(eLoc, Effect.SMOKE, 4);
                player.playSound(eLoc, Sound.IRONGOLEM_THROW,
                        (float)1.0, (float)1.5);
                return;
            }

            // ロッドと、そのレベルを取得
            ItemStack rod = player.getItemInHand();

            // 経験値を消費する、耐久値を0に戻す
            takeExperience(player, COST);
            rod.setDurability((short)0);

            // もし今回の操作で燃料切れになった場合は、指定秒後に復活させる
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
            Location hookLoc = hook.getLocation();
            Vector vector = new Vector(
                    hookLoc.getX()-baseLoc.getX(),
                    hookLoc.getY()-baseLoc.getY(),
                    hookLoc.getZ()-baseLoc.getZ());
            player.setVelocity(vector.normalize().multiply(LEVEL/2));
            player.setFallDistance(-1000F);
            player.playEffect(eLoc, Effect.POTION_BREAK, 22);
            player.playEffect(eLoc, Effect.POTION_BREAK, 22);
            return;
        }
    }

    /**
     * Wirerodの針が、地面やブロック、MOBに刺さったときに呼び出されるメソッド
     * @param event
     */
    @EventHandler
    public void onHit(ProjectileHitEvent event) {

        final Projectile projectile = event.getEntity();
        LivingEntity shooter = projectile.getShooter();

        if ( shooter == null || !(shooter instanceof Player) ) {
            return;
        }

        Player player = (Player)shooter;

        if ( player.getItemInHand() == null ||
                player.getItemInHand().getType() == Material.AIR ||
                player.getItemInHand().getItemMeta().getDisplayName() == null ||
                !player.getItemInHand().getItemMeta().getDisplayName().equals(DISPLAY_NAME) ) {
            return;
        }

        // 音を出す
        player.playSound(player.getEyeLocation(), Sound.ARROW_HIT, 1, (float)0.5);
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
