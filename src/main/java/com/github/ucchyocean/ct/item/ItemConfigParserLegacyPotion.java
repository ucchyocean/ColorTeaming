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
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

/**
 * アイテム設定のパーサー(従来形式のPotion用)
 * @author ucchy
 */
@SuppressWarnings("deprecation")
public class ItemConfigParserLegacyPotion {

    /**
     * 指定されたアイテムがポーションだったときに、メタ情報をセクションに保存する。
     * @param item
     * @param section
     */
    protected static void addPotionInfoToSection(ItemStack item, ConfigurationSection section) {

        if ( item.getType() != Material.POTION || isWaterBottle(item) ) {
            return;
        }

        cleanupInvalidExtendedPotionFlag(item);

        Potion potion = Potion.fromItemStack(item);
        section.set("potion_type", potion.getType().toString());
        section.set("potion_level", potion.getLevel());
        if ( potion.isSplash() ) {
            section.set("splash", true);
        }
        if ( potion.hasExtendedDuration() ) {
            section.set("extend", true);
        }

        PotionMeta meta = (PotionMeta)item.getItemMeta();
        if ( meta.hasCustomEffects() ) {
            // カスタムポーションの設定

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
     * ポーションのメタデータを含める必要がある場合に、メタ情報を復帰して含めておく。
     * @param item
     * @param section
     * @throws ItemConfigParseException
     */
    protected static void addPotionInfoToItem(ItemStack item, ConfigurationSection section)
            throws ItemConfigParseException {

        if ( item.getType() != Material.POTION || !section.contains("potion_type") ) {
            return;
        }

        String name = section.getString("potion_type");
        PotionType type = getPotionTypeByName(name);
        if ( type == null ) {
            throw new ItemConfigParseException(
                    "The potion type '" + name + "' is invalid.");
        }

        int amp = section.getInt("potion_level", 1);
        if ( amp < 1 ) {
            amp = 1;
        } else if ( amp > type.getMaxLevel() ) {
            amp = type.getMaxLevel();
        }

        Potion potion = new Potion(type, amp);
        potion.setSplash(section.getBoolean("splash", false));
        if ( !type.isInstant() ) {
            potion.setHasExtendedDuration(section.getBoolean("extend", false));
        }
        potion.apply(item);

        // カスタムポーションの詳細設定
        if ( section.contains("custom_effects") ) {

            PotionMeta meta = (PotionMeta)item.getItemMeta();
            PotionEffectType mainType = null;

            for ( String key :
                    section.getConfigurationSection("custom_effects").getKeys(false) ) {

                ConfigurationSection custom_sec =
                        section.getConfigurationSection("custom_effects." + key);
                String cname = custom_sec.getString("type");
                if ( cname == null ) {
//                        throw new ItemConfigParseException(
//                                "Potion type tag was not found.");
                    continue;
                }
                PotionEffectType ctype = PotionEffectType.getByName(cname.toUpperCase());
                if ( ctype == null ) {
//                        throw new ItemConfigParseException(
//                                "Potion type '" + cname + "' is invalid.");
                    continue;
                }
                if ( mainType == null ) {
                    mainType = ctype;
                }
                int amplifier = custom_sec.getInt("amplifier", 1);
                int duration = custom_sec.getInt("duration", 100);
                boolean ambient = custom_sec.getBoolean("ambient", true);
                PotionEffect effect = new PotionEffect(ctype, duration, amplifier, ambient);
                meta.addCustomEffect(effect, ambient);
            }

            if ( mainType != null ) {
                meta.setMainEffect(mainType);
            }

            item.setItemMeta(meta);
        }
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

    /**
     * 指定されたポーションが、水ボトル（データ値が0）かどうかを確認します。
     * @param item ポーション
     * @return 水ボトルかどうか
     */
    private static boolean isWaterBottle(ItemStack item) {
        return item.getDurability() == 0;
    }

    /**
     * ポーションのデータ値を調べ、タイプにそぐわないextendフラグが立っている場合、
     * 強制的にフラグを降ろします。
     * @param item ポーション
     */
    private static void cleanupInvalidExtendedPotionFlag(ItemStack item) {

        short data = item.getDurability();
        int typeFlag = data & 0xF;

        if ( typeFlag == 5 || typeFlag == 12 ) {
            // INSTANT_DAMAGE か INSTANT_HEAL なら、extendフラグを確認し、
            // フラグが立っているなら降ろす。

            if ( (data & 0x40) > 0 ) {
                data -= 0x40;
                item.setDurability(data);
            }
        }
    }
}
