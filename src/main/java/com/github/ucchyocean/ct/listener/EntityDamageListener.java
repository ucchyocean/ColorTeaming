/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scoreboard.Team;

import com.github.ucchyocean.ct.ColorTeaming;

/**
 * 仲間同士の攻撃が発生したかどうかを確認するクラス
 * @author ucchy
 */
public class EntityDamageListener implements Listener {

    private ColorTeaming plugin;

    public EntityDamageListener(ColorTeaming plugin) {
        this.plugin = plugin;
    }

    /**
     * Entity が Entity に、ダメージを与えたときに発生するイベント。
     * @param event
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        // 0以下のダメージのイベントは無視する
        if ( event.getDamage() <= 0 ) {
            return;
        }

        // フレンドリーファイア有効なら、何もしない
        if ( plugin.getCTConfig().isFriendlyFire() ) {
            return;
        }

        // 加害者と被害者の取得
        Entity attacker = event.getDamager();
        Entity defender = event.getEntity();

        // 両方プレイヤーの場合（＝剣や素手などの直接攻撃）
        if ( attacker instanceof Player && defender instanceof Player ) {

            // チームを取得し、同じチームかどうか確認する
            Team attackerTeam = 
                    plugin.getAPI().getPlayerTeam((Player)attacker);
            Team defenderTeam = 
                    plugin.getAPI().getPlayerTeam((Player)defender);

            // どちらかがチーム無所属なら抜ける
            if ( attackerTeam == null || defenderTeam == null ) {
                return;
            }

            // 同じチームならば、
            if ( attackerTeam.equals(defenderTeam) ) {
                // 攻撃イベントをキャンセルしちゃう。
                event.setCancelled(true);
            }

        // 加害者が飛来物(Projectile)、被害者がプレイヤーの場合
        } else if ( attacker instanceof Projectile && defender instanceof Player ) {

            Projectile projectile = (Projectile)attacker;
            LivingEntity shooter = projectile.getShooter();

            // 飛来物を打ったのがプレイヤーなら、
            if ( shooter instanceof Player ) {

                // ColorMe のカラーを取得し、同じ色かどうか確認する
                Team attackerTeam = 
                        plugin.getAPI().getPlayerTeam((Player)shooter);
                Team defenderTeam = 
                        plugin.getAPI().getPlayerTeam((Player)defender);

                // どちらかがチーム無所属なら抜ける
                if ( attackerTeam == null || defenderTeam == null ) {
                    return;
                }

                // 同じ色ならば、
                if ( attackerTeam.equals(defenderTeam) ) {
                    // 攻撃イベントをキャンセルしちゃう。
                    event.setCancelled(true);
                }
            }
        }
    }
}