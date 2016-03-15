/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2016
 */
package com.github.ucchyocean.ct.item;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

/**
 * アイテム設定のパーサー for Bukkit v1.9
 * @author ucchy
 */
public class ItemConfigParserV19 {

    /**
     * 指定されたアイテムがポーション関連アイテムだったときに、メタ情報をセクションに保存する。
     * @param item
     * @param section
     */
    protected static void addPotionInfoToSection(ItemStack item, ConfigurationSection section) {

        if ( !isPotionRelatedItem(item) ) return;

        PotionMeta meta = (PotionMeta)item.getItemMeta();

        // ベースポーションの設定
        PotionData base = meta.getBasePotionData();
        section.set("potion_type", base.getType().toString());
        if ( base.isUpgraded() ) {
            section.set("upgrade", true);
        }
        if ( base.isExtended() ) {
            section.set("extend", true);
        }

        // カスタムポーションの設定
        if ( meta.hasCustomEffects() ) {
            ConfigurationSection customSection =
                    section.createSection("custom_effects");
            List<PotionEffect> customs = meta.getCustomEffects();
            for ( int i=0; i<customs.size(); i++ ) {
                ConfigurationSection sub = customSection.createSection("effect" + (i+1));
                PotionEffect custom = customs.get(i);
                sub.set("type", custom.getType().getName());
                sub.set("amplifier", custom.getAmplifier());
                sub.set("duration", custom.getDuration());
                sub.set("ambient", custom.isAmbient());
            }
        }
    }


    /**
     * ポーション関連アイテムのメタデータを含める必要がある場合に、メタ情報を復帰して含めておく。
     * @param item
     * @param section
     * @throws ItemConfigParseException
     */
    protected static void addPotionInfoToItem(ItemStack item, ConfigurationSection section)
            throws ItemConfigParseException {

        if ( !isPotionRelatedItem(item) ) return;

        PotionMeta meta = (PotionMeta)item.getItemMeta();

        // ベースポーションの設定

        if ( item.getType() == Material.POTION && section.getBoolean("splash", false) ) {
            // アイテムをスプラッシュポーションに作り直す
            item.setType(Material.SPLASH_POTION);
            meta = (PotionMeta)item.getItemMeta();
        }

        String name = section.getString("potion_type");
        PotionType type = getPotionTypeByName(name);
        if ( type == null ) {
            throw new ItemConfigParseException(
                    "The potion type '" + name + "' is invalid.");
        }

        boolean upgraded = section.getBoolean("upgrade", false);
        boolean extended = section.getBoolean("extend", false);

        int amp = section.getInt("potion_level", 1);
        if ( amp == 2 ) upgraded = true;

        PotionData data = new PotionData(type, extended, upgraded);
        meta.setBasePotionData(data);

        // カスタムポーションの設定
        if ( section.contains("custom_effects") ) {

            for ( String key :
                    section.getConfigurationSection("custom_effects").getKeys(false) ) {

                ConfigurationSection custom_sec =
                        section.getConfigurationSection("custom_effects." + key);
                String cname = custom_sec.getString("type");
                if ( cname == null ) {
                    continue;
                }
                PotionEffectType ctype = PotionEffectType.getByName(cname.toUpperCase());
                if ( ctype == null ) {
                    continue;
                }
                int amplifier = custom_sec.getInt("amplifier", 1);
                int duration = custom_sec.getInt("duration", 100);
                boolean ambient = custom_sec.getBoolean("ambient", true);
                PotionEffect effect = new PotionEffect(ctype, duration, amplifier, ambient);
                meta.addCustomEffect(effect, ambient);
            }
        }

        item.setItemMeta(meta);
    }

    /**
     * ポーション関連アイテムかどうかを判定して返します。
     * @param item アイテム
     * @return ポーション関連アイテムかどうか
     */
    private static boolean isPotionRelatedItem(ItemStack item) {
        Material type = item.getType();
        return type == Material.POTION || type == Material.SPLASH_POTION
                || type == Material.LINGERING_POTION || type == Material.TIPPED_ARROW;
    }

    /**
     * 指定された文字列に一致するPotionTypeを返します。
     * @param name
     * @return
     */
    private static PotionType getPotionTypeByName(String name) {

        for ( PotionType type : PotionType.values() ) {
            if ( type.name().equalsIgnoreCase(name) ) {
                return type;
            }
        }
        return null;
    }
}
