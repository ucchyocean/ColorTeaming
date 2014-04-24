/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2014
 */
package com.github.ucchyocean.ct.config;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

/**
 * アイテム設定のパーサー
 * @author ucchy
 */
public class ItemConfigParser {

    /** カスタムアイテム用のダミーアイテム */
    protected static final Material DUMMY_ITEM = Material.BED_BLOCK;

    private Logger logger;

    public ItemConfigParser(Logger logger) {
        this.logger = logger;
    }

    /**
     * コンフィグセクションから、アイテム設定を読みだして、ItemStackを生成して返します。
     * @param section コンフィグセクション
     * @param info セクションの情報、パースに失敗した時の警告に使用されます。
     * @return ItemStack
     */
    public ItemStack getItemFromSection(ConfigurationSection section, String info) {

        if ( section == null ) {
            return null;
        }

        ItemStack item = null;

        if ( section.contains("material") ) {
            // 通常のアイテム設定

            // materialは大文字に変換して読み込ませる
            String name = section.getString("material");
            Material material = Material.getMaterial(name.toUpperCase());
            if ( material == null ) {
                logger.warning("materialの指定 " + name + " が正しくありません。");
                logger.warning("└ " + info);
                return null;
            }
            if ( material == Material.AIR ) {
                return new ItemStack(Material.AIR);
            }

            // データ値は、ここで設定する（たぶん、将来サポートされなくなるので注意）
            short data = (short)section.getInt("data", 0);
            item = new ItemStack(material, 1, data);

        } else if ( section.contains("custom_item") ) {
            // カスタムアイテムの設定

            String name = section.getString("custom_item");
            item = new ItemStack(DUMMY_ITEM);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(name);
            item.setItemMeta(meta);

            return item;

        } else {
            logger.warning("アイテム設定に必須項目（material または custom_item）がありません。");
            logger.warning("└ " + info);
            return null;
        }

        // アイテムの個数
        item.setAmount(section.getInt("amount", 1));

        // アイテムの表示名
        if ( section.contains("display_name") ) {
            String dname = section.getString("display_name");
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(dname);
            item.setItemMeta(meta);
        }

        // アイテムの説明
        if ( section.contains("lore") ) {
            List<String> lore = section.getStringList("lore");
            ItemMeta meta = item.getItemMeta();
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        // エンチャント
        if ( section.contains("enchants") ) {
            ConfigurationSection enchants_sec = section.getConfigurationSection("enchants");
            for ( String type_str : enchants_sec.getKeys(false) ) {
                Enchantment enchant = Enchantment.getByName(type_str);

                if ( enchant == null ) {
                    logger.warning("指定されたエンチャント形式 " + type_str + " が正しくありません。");
                    logger.warning("└ " + info);
                    continue;
                }

                int level = enchants_sec.getInt(type_str, 1);
                if ( level < enchant.getStartLevel() ) {
                    level = enchant.getStartLevel();
                } else if ( level > 1000 ) {
                    level = 1000;
                }
                item.addUnsafeEnchantment(enchant, level);
            }
        }

        // 消耗度
        if ( section.contains("remain") ) {

            short remain = (short)section.getInt("remain");
            short durability = (short)(item.getType().getMaxDurability() - remain + 1);
            if ( durability < 0 ) {
                durability = 0;
            }
            item.setDurability(durability);
        }

        // 革防具の染色設定
        if ( item.getType() == Material.LEATHER_BOOTS ||
                item.getType() == Material.LEATHER_LEGGINGS ||
                item.getType() == Material.LEATHER_CHESTPLATE ||
                item.getType() == Material.LEATHER_HELMET ) {

            LeatherArmorMeta lam = (LeatherArmorMeta)item.getItemMeta();
            int red = section.getInt("red", 160);
            int blue = section.getInt("blue", 101);
            int green = section.getInt("green", 64);
            Color color = Color.fromBGR(blue, green, red);
            lam.setColor(color);
            item.setItemMeta(lam);
        }

        // スカルの詳細設定
        if ( item.getType() == Material.SKULL_ITEM &&
                section.contains("owner") ) {

            SkullMeta sm = (SkullMeta)item.getItemMeta();
            if ( sm.setOwner(section.getString("owner")) ) {
                item.setItemMeta(sm);
            }
        }

        // 本の詳細設定
        if ( item.getType() == Material.WRITTEN_BOOK ||
                item.getType() == Material.BOOK_AND_QUILL ) {

            BookMeta bm = (BookMeta)item.getItemMeta();
            boolean needToSet = false;
            if ( section.contains("author") ) {
                bm.setAuthor(section.getString("author"));
                needToSet = true;
            }
            if ( section.contains("title") ) {
                bm.setTitle(section.getString("title"));
                needToSet = true;
            }
            if ( section.contains("pages") ) {
                bm.setPages(section.getStringList("pages"));
                needToSet = true;
            }
            if ( needToSet ) {
                item.setItemMeta(bm);
            }
        }

        // ポーションの詳細設定
        if ( item.getType() == Material.POTION && section.contains("potion_type") ) {

            String name = section.getString("potion_type");
            PotionType type = getPotionTypeByName(name);
            if ( type == null ) {
                logger.warning("指定されたポーション形式 " + name + " が正しくありません。");
                logger.warning("└ " + info);
            } else {
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
                            logger.warning("指定されたポーション形式 type の指定がありません。");
                            logger.warning("└ " + info);
                            continue;
                        }
                        PotionEffectType ctype = PotionEffectType.getByName(cname.toUpperCase());
                        if ( ctype == null ) {
                            logger.warning("指定されたポーション形式 " + cname + " が正しくありません。");
                            logger.warning("└ " + info);
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

                    if ( mainType == null ) {
                        meta.setMainEffect(mainType);
                    }

                    item.setItemMeta(meta);
                }
            }
        }

        // 花火の詳細設定
        if ( item.getType() == Material.FIREWORK ) {

            FireworkMeta meta = (FireworkMeta)item.getItemMeta();
            meta.setPower(section.getInt("power", 1));

            for ( String key :
                    section.getConfigurationSection("effects").getKeys(false) ) {

                ConfigurationSection effect_sec =
                        section.getConfigurationSection("effects." + key);
                String tname = effect_sec.getString("type");
                if ( tname == null ) {
                    logger.warning("指定されたエフェクト形式 type の指定がありません。");
                    logger.warning("└ " + info);
                    continue;
                }
                FireworkEffect.Type type = getFireworkEffectTypeByName(tname);
                if ( type == null ) {
                    logger.warning("指定されたエフェクト形式 " + tname + " が正しくありません。");
                    logger.warning("└ " + info);
                    continue;
                }

                Builder effect = FireworkEffect.builder();
                effect.with(type);
                effect.flicker(effect_sec.getBoolean("flicker", false));
                effect.trail(effect_sec.getBoolean("trail", false));

                for ( String ckey :
                        effect_sec.getConfigurationSection("colors").getKeys(false) ) {

                    ConfigurationSection color_sec =
                            effect_sec.getConfigurationSection("colors." + ckey);
                    int red = color_sec.getInt("red", 255);
                    int blue = color_sec.getInt("blue", 255);
                    int green = color_sec.getInt("green", 255);
                    effect.withColor(Color.fromBGR(blue, green, red));
                }

                for ( String fkey :
                        effect_sec.getConfigurationSection("fades").getKeys(false) ) {

                    ConfigurationSection fade_sec =
                            effect_sec.getConfigurationSection("fades." + fkey);
                    int red = fade_sec.getInt("red", 255);
                    int blue = fade_sec.getInt("blue", 255);
                    int green = fade_sec.getInt("green", 255);
                    effect.withFade(Color.fromBGR(blue, green, red));
                }

                meta.addEffect(effect.build());
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * アイテムの情報を文字列表現で返す
     * @param item アイテム
     * @param indent 表示文字列のインデント
     * @return 文字列表現
     */
    public static ArrayList<String> getItemInfo(ItemStack item, String indent) {

        if ( item == null ) {
            return null;
        }

        ArrayList<String> message = new ArrayList<String>();

        message.add(indent + "material: " + item.getType());
        if ( item.getAmount() > 1 ) {
            message.add(indent + "amount: " + item.getAmount());
        }

        short data = item.getDurability();
        if ( item.getDurability() > 0 && item.getType().getMaxDurability() > 1 ) {
            int remain = item.getType().getMaxDurability() - item.getDurability() + 1;
            message.add(indent + "remain: " + remain);
        } else if ( data > 0 && item.getType() != Material.POTION ) {
            message.add(indent + "data: " + data);
        }

        if ( item.hasItemMeta() ) {
            ItemMeta meta = item.getItemMeta();
            if ( meta.hasDisplayName() ) {
                message.add(indent + "display_name: " + meta.getDisplayName());
            }
            if ( meta.hasLore() ) {
                message.add(indent + "lore: ");
                for ( String l : meta.getLore() ) {
                    message.add(indent + "- '" + l + "'");
                }
            }
        }

        if ( item.getEnchantments().size() > 0 ) {
            message.add(indent + "enchants: ");
            for ( Enchantment ench : item.getEnchantments().keySet() ) {
                message.add(indent + "  " + ench.getName() + ": " +
                        item.getEnchantmentLevel(ench));
            }
        }

        if ( item.getType() == Material.LEATHER_BOOTS ||
                item.getType() == Material.LEATHER_LEGGINGS ||
                item.getType() == Material.LEATHER_CHESTPLATE ||
                item.getType() == Material.LEATHER_HELMET ) {

            LeatherArmorMeta lam = (LeatherArmorMeta)item.getItemMeta();
            message.add(indent + "red: " + lam.getColor().getRed());
            message.add(indent + "blue: " + lam.getColor().getBlue());
            message.add(indent + "green: " + lam.getColor().getGreen());
        }

        if ( item.getType() == Material.SKULL_ITEM ) {

            SkullMeta sm = (SkullMeta)item.getItemMeta();
            if ( sm.hasOwner() ) {
                message.add(indent + "owner: " + sm.getOwner() );
            }
        }

        if ( item.getType() == Material.WRITTEN_BOOK ||
                item.getType() == Material.BOOK_AND_QUILL ) {

            BookMeta bm = (BookMeta)item.getItemMeta();
            if ( bm.hasAuthor() ) {
                message.add(indent + "author: " + bm.getAuthor() );
            }
            if ( bm.hasTitle() ) {
                message.add(indent + "title: " + bm.getTitle() );
            }
            if ( bm.hasPages() ) {
                message.add(indent + "pages:");
                for ( String page : bm.getPages() ) {
                    message.add(indent + "- " + page);
                }
            }
        }

        if ( item.getType() == Material.POTION && !isWaterBottle(item) ) {

            cleanupInvalidExtendedPotionFlag(item);

            Potion potion = Potion.fromItemStack(item);
            message.add(indent + "potion_type: " + potion.getType());
            message.add(indent + "potion_level: " + potion.getLevel());
            if ( potion.isSplash() ) {
                message.add(indent + "splash: true");
            }
            if ( potion.hasExtendedDuration() ) {
                message.add(indent + "extend: true");
            }

            PotionMeta meta = (PotionMeta)item.getItemMeta();
            if ( meta.hasCustomEffects() ) {
                // カスタムポーションの設定

                message.add(indent + "custom_effects:");
                List<PotionEffect> customs = meta.getCustomEffects();
                for ( int i=0; i<customs.size(); i++ ) {
                    PotionEffect custom = customs.get(i);
                    message.add(indent + "  effect" + (i+1) + ":");
                    message.add(indent + "    type: " + custom.getType().getName());
                    message.add(indent + "    amplifier: " + custom.getAmplifier());
                    message.add(indent + "    duration: " + custom.getDuration());
                    message.add(indent + "    ambient: " + custom.isAmbient());
                }
            }
        }

        if ( item.getType() == Material.FIREWORK ) {

            FireworkMeta meta = (FireworkMeta)item.getItemMeta();
            message.add(indent + "power: " + meta.getPower());

            if ( meta.hasEffects() ) {
                message.add(indent + "effects:");
                List<FireworkEffect> effects = meta.getEffects();

                for ( int i=0; i<effects.size(); i++ ) {
                    FireworkEffect effect = effects.get(i);
                    message.add(indent + "  effect" + (i+1) + ":");
                    message.add(indent + "    type: " + effect.getType().name());
                    message.add(indent + "    flicker: " + effect.hasFlicker());
                    message.add(indent + "    trail: " + effect.hasTrail());

                    List<Color> colors = effect.getColors();
                    if ( colors.size() > 0 ) {
                        String indent1 = indent + "    ";
                        message.add(indent1 + "colors:");
                        for ( int j=0; j<colors.size(); j++ ) {
                            Color color = colors.get(j);
                            message.add(indent1 + "  color" + (j+1) + ":");
                            message.add(indent1 + "    red: " + color.getRed());
                            message.add(indent1 + "    blue: " + color.getBlue());
                            message.add(indent1 + "    green: " + color.getGreen());
                        }
                    }

                    List<Color> fades = effect.getFadeColors();
                    if ( fades.size() > 0 ) {
                        String indent1 = indent + "    ";
                        message.add(indent1 + "fades:");
                        for ( int j=0; j<fades.size(); j++ ) {
                            Color fade = fades.get(j);
                            message.add(indent1 + "  fade" + (j+1) + ":");
                            message.add(indent1 + "    red: " + fade.getRed());
                            message.add(indent1 + "    blue: " + fade.getBlue());
                            message.add(indent1 + "    green: " + fade.getGreen());
                        }
                    }
                }
            }
        }

        return message;
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
     * 指定された文字列に一致するFireworkEffectTypeを返します。
     * @param name
     * @return
     */
    private static FireworkEffect.Type getFireworkEffectTypeByName(String name) {

        for ( FireworkEffect.Type type : FireworkEffect.Type.values() ) {
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
