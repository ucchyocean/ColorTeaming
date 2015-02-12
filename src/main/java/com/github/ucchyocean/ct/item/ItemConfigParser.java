/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2015
 */
package com.github.ucchyocean.ct.item;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
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

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.bridge.AttributeInfo;
import com.github.ucchyocean.ct.bridge.AttributesAPIBridge;

/**
 * アイテム設定のパーサー
 * @author ucchy
 */
public class ItemConfigParser {

    /**
     * コンフィグセクションから、アイテム設定を読みだして、ItemStackを生成して返します。
     * @param section コンフィグセクション
     * @return ItemStack
     */
    public static ItemStack getItemFromSection(ConfigurationSection section)
            throws ItemConfigParseException {

        if ( section == null ) {
            return null;
        }

        ItemStack item = null;

        if ( !section.contains("material") ) {
            if ( section.contains("custom_item") ) {
                return null; // custom_itemは過去の遺物。何も返さずに終了する。
            }
            throw new ItemConfigParseException("Material tag was not found.");
        }

        // 通常のアイテム設定

        // materialは大文字に変換して読み込ませる
        String mname = section.getString("material");
        Material material = Material.getMaterial(mname.toUpperCase());
        if ( material == null ) {
            throw new ItemConfigParseException(
                    "Material name '" + mname + "' is invalid.");
        }
        if ( material == Material.AIR ) {
            return new ItemStack(Material.AIR);
        }

        // データ値はここで設定する
        short data = (short)section.getInt("data", 0);
        item = new ItemStack(material, 1, data);

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
                    throw new ItemConfigParseException(
                            "Enchant type '" + type_str + "' is invalid.");
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

                if ( mainType == null ) {
                    meta.setMainEffect(mainType);
                }

                item.setItemMeta(meta);
            }
        }

        // 花火の詳細設定
        if ( item.getType() == Material.FIREWORK ) {

            FireworkMeta meta = (FireworkMeta)item.getItemMeta();
            meta.setPower(section.getInt("power", 1));

            if ( section.contains("effects") ) {

                for ( String key :
                        section.getConfigurationSection("effects").getKeys(false) ) {

                    ConfigurationSection effect_sec =
                            section.getConfigurationSection("effects." + key);
                    String tname = effect_sec.getString("type");
                    if ( tname == null ) {
//                        throw new ItemConfigParseException(
//                                "Effect type tag was not found.");
                        continue;
                    }
                    FireworkEffect.Type type = getFireworkEffectTypeByName(tname);
                    if ( type == null ) {
//                        throw new ItemConfigParseException(
//                                "Effect type '" + tname + "' is invalid.");
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
            }

            item.setItemMeta(meta);
        }

        // Attributeの詳細設定、AttributesAPIがロードされている場合にのみ実施する
        if ( ColorTeaming.instance.getAttributesAPI() != null &&
                section.contains("attributes") ) {

            AttributesAPIBridge bridge = ColorTeaming.instance.getAttributesAPI();

            for ( String key :
                    section.getConfigurationSection("attributes").getKeys(false) ) {

                ConfigurationSection attr_sec =
                        section.getConfigurationSection("attributes." + key);
                AttributeInfo attr = AttributeInfo.readFromConfigSection(attr_sec);

                if ( attr == null ) {
//                    logger.warning("指定された属性の、typeまたはoperatorが正しくありません。");
//                    logger.warning("└ " + info);
                    continue;
                }

                item = bridge.applyAttribute(item, attr);
            }
        }

        // バナーの詳細設定
        if ( isCB18orLater() ) {
            item = ItemConfigParserV18.addBannerInfoToItem(section, item);
        }

        return item;
    }

    /**
     * 指定されたコンフィグセクションに、指定されたItemStackの情報を保存します。
     * @param section コンフィグセクション
     * @param item アイテム
     */
    public static void setItemToSection(ConfigurationSection section, ItemStack item) {

        if ( section == null || item == null ) {
            return;
        }

        section.set("material", item.getType().toString());
        if ( item.getAmount() > 1 ) {
            section.set("amount", item.getAmount());
        }

        short data = item.getDurability();
        if ( item.getDurability() > 0 && item.getType().getMaxDurability() > 1 ) {
            int remain = item.getType().getMaxDurability() - item.getDurability() + 1;
            section.set("remain", remain);
        } else if ( data > 0 && item.getType() != Material.POTION ) {
            section.set("data", data);
        }

        if ( item.hasItemMeta() ) {
            ItemMeta meta = item.getItemMeta();
            if ( meta.hasDisplayName() ) {
                section.set("display_name", meta.getDisplayName());
            }
            if ( meta.hasLore() ) {
                section.set("lore", meta.getLore());
            }
        }

        if ( item.getEnchantments().size() > 0 ) {
            ConfigurationSection sub = section.createSection("enchants");
            for ( Enchantment ench : item.getEnchantments().keySet() ) {
                sub.set(ench.getName(), item.getEnchantmentLevel(ench));
            }
        }

        if ( item.getType() == Material.LEATHER_BOOTS ||
                item.getType() == Material.LEATHER_LEGGINGS ||
                item.getType() == Material.LEATHER_CHESTPLATE ||
                item.getType() == Material.LEATHER_HELMET ) {

            LeatherArmorMeta lam = (LeatherArmorMeta)item.getItemMeta();
            section.set("red", lam.getColor().getRed());
            section.set("blue", lam.getColor().getBlue());
            section.set("green", lam.getColor().getGreen());
        }

        if ( item.getType() == Material.SKULL_ITEM ) {

            SkullMeta sm = (SkullMeta)item.getItemMeta();
            if ( sm.hasOwner() ) {
                section.set("owner", sm.getOwner());
            }
        }

        if ( item.getType() == Material.WRITTEN_BOOK ||
                item.getType() == Material.BOOK_AND_QUILL ) {

            BookMeta bm = (BookMeta)item.getItemMeta();
            if ( bm.hasAuthor() ) {
                section.set("author", bm.getAuthor());
            }
            if ( bm.hasTitle() ) {
                section.set("title", bm.getTitle());
            }
            if ( bm.hasPages() ) {
                section.set("pages", bm.getPages());
            }
        }

        if ( item.getType() == Material.POTION && !isWaterBottle(item) ) {

            cleanupInvalidExtendedPotionFlag(item);

            Potion potion = Potion.fromItemStack(item);
            section.set("potion_type", potion.getType());
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

        if ( item.getType() == Material.FIREWORK ) {

            FireworkMeta meta = (FireworkMeta)item.getItemMeta();
            section.set("power", meta.getPower());

            if ( meta.hasEffects() ) {
                ConfigurationSection effectSection = section.createSection("effects");
                List<FireworkEffect> effects = meta.getEffects();

                for ( int i=0; i<effects.size(); i++ ) {
                    ConfigurationSection sub = effectSection.createSection("effect" + (i+1));
                    FireworkEffect effect = effects.get(i);
                    sub.set("type", effect.getType().name());
                    sub.set("flicker", effect.hasFlicker());
                    sub.set("trail", effect.hasTrail());

                    List<Color> colors = effect.getColors();
                    if ( colors.size() > 0 ) {
                        ConfigurationSection colorSection = sub.createSection("colors");
                        for ( int j=0; j<colors.size(); j++ ) {
                            ConfigurationSection csub = colorSection.createSection("color" + (j+1));
                            Color color = colors.get(j);
                            csub.set("red", color.getRed());
                            csub.set("blue", color.getBlue());
                            csub.set("green", color.getGreen());
                        }
                    }

                    List<Color> fades = effect.getFadeColors();
                    if ( fades.size() > 0 ) {
                        ConfigurationSection fadeSection = sub.createSection("fades");
                        for ( int j=0; j<fades.size(); j++ ) {
                            ConfigurationSection fsub = fadeSection.createSection("fade" + (j+1));
                            Color fade = fades.get(j);
                            fsub.set("red", fade.getRed());
                            fsub.set("blue", fade.getBlue());
                            fsub.set("green", fade.getGreen());
                        }
                    }
                }
            }
        }

        // Attributeの解析、AttributesAPIがロードされている場合にのみ実施する
        if ( ColorTeaming.instance.getAttributesAPI() != null ) {

            AttributesAPIBridge bridge = ColorTeaming.instance.getAttributesAPI();

            if ( bridge.getAttributeNum(item) > 0 ) {

                ConfigurationSection sub = section.createSection("attributes");
                int index = 0;

                for ( AttributeInfo info : bridge.readAttr(item) ) {
                    index++;
                    info.saveToConfigSection(sub.createSection("attribute" + index));
                }
            }
        }

        // バナーの詳細設定
        if ( isCB18orLater() ) {
            ItemConfigParserV18.addBannerInfoToSection(section, item);
        }
    }

    /**
     * アイテムの情報を文字列表現で返す
     * @param item アイテム
     * @return 文字列表現
     */
    public static String getItemInfo(ItemStack item) {
        YamlConfiguration config = new YamlConfiguration();
        setItemToSection(config, item);
        return decodeUnicode(config.saveToString());
    }

    /**
     * Unicodeを含む文字列を復号化し、エスケープされた改行を除去する。
     * @param source
     * @return
     */
    private static String decodeUnicode(String source) {

        // Unicode復号化
        Pattern pattern = Pattern.compile("\\\\u([0-9a-f]{4})");
        Matcher matcher = pattern.matcher(source);
        String result = source;
        while ( matcher.find() ) {
            int code = Integer.parseInt(matcher.group(1), 16);
            String replacement = new String(new int[]{code}, 0, 1);
            result = matcher.replaceFirst(replacement);
            matcher = pattern.matcher(result);
        }

        // エスケープされた改行文字の除去
        result = result.replaceAll("\\\\\\n *", "");

        return result;
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


    /**
     * 現在動作中のCraftBukkitが、v1.8 以上かどうかを確認する
     * @return v1.8以上ならtrue、そうでないならfalse
     */
    private static boolean isCB18orLater() {

        int[] borderNumbers = {1, 8};

        String version = Bukkit.getBukkitVersion();
        int hyphen = version.indexOf("-");
        if ( hyphen > 0 ) {
            version = version.substring(0, hyphen);
        }

        String[] versionArray = version.split("\\.");
        int[] versionNumbers = new int[versionArray.length];
        for ( int i=0; i<versionArray.length; i++ ) {
            if ( !versionArray[i].matches("[0-9]+") ) {
                return false;
            }
            versionNumbers[i] = Integer.parseInt(versionArray[i]);
        }

        int index = 0;
        while ( (versionNumbers.length > index) && (borderNumbers.length > index) ) {
            if ( versionNumbers[index] > borderNumbers[index] ) {
                return true;
            } else if ( versionNumbers[index] < borderNumbers[index] ) {
                return false;
            }
            index++;
        }
        if ( borderNumbers.length == index ) {
            return true;
        } else {
            return false;
        }
    }
}
