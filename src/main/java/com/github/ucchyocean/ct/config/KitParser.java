/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * kitの文字列からアイテムスタックを作ったり、
 * インベントリからkitの文字列に変換したりするユーティリティクラス
 * @author ucchy
 */
public class KitParser {

    private static final String REGEX_ITEM_PATTERN =
            "([0-9]+)(?:@([0-9]+))?(?::([0-9]+))?|" +
            "([0-9]+)((?:\\^[0-9]+-[0-9]+)*)(?:@([0-9]+))?(?:\\$([0-9A-Fa-f]{6}))?";
    private static final String REGEX_ENCHANT_PATTERN =
            "\\^([0-9]+)-([0-9]+)";

    private Pattern pattern;
    private Pattern patternEnchant;
    private Logger logger;

    /**
     * コンストラクタ
     */
    public KitParser() {
        logger = Bukkit.getLogger();
        pattern = Pattern.compile(REGEX_ITEM_PATTERN);
        patternEnchant = Pattern.compile(REGEX_ENCHANT_PATTERN);
    }

    /**
     * kitのアイテムデータ文字列を解析し、ItemStack配列に変換する。
     * @param data 解析元の文字列　例）"44:64,44@2:64,281:10"
     * @return ItemStackの配列
     */
    public ArrayList<ItemStack> parseToItemStack(String data) {

        ArrayList<ItemStack> items = new ArrayList<ItemStack>();

        String[] array = data.split("[,]");
        for (int i = 0; i < array.length; i++) {
            items.add(parseItemInfoToItemStack(array[i]));
        }

        return items;
    }

    /**
     * アイテム文字列表現をItemStackに変換して返す。
     * @param info アイテム文字列表現
     * @return アイテム
     */
    public ItemStack parseItemInfoToItemStack(String info) {

        if ( info == null || info.equals("") ) {
            return null;
        }

        Matcher matcher = pattern.matcher(info);

        if ( !matcher.matches() ) {
            logger.severe("指定された形式 " + info + " が正しく解析できません。");
            return null;
        }

        if ( matcher.group(1) != null ) {
            // group1 が null でないなら、id@damage:amount 形式の指定である。

            int item = 0, amount = 1;
            short damage = 0;
            item = Integer.parseInt(matcher.group(1));
            if ( matcher.group(2) != null ) {
                damage = Short.parseShort(matcher.group(2));
            }
            if ( matcher.group(3) != null ) {
                amount = Integer.parseInt(matcher.group(3));
            }

            if ( item == 0 ) {
                return new ItemStack(Material.AIR, 0);
            }

            // Materialの取得をして、正しいIDが指定されたかどうかを確認する
            Material m = Material.getMaterial(item);
            if (m == null) {
                logger.severe("指定されたItemID " + item + " が見つかりません。");
                return null;
            }

            // 65個を超える量の場合は、64個ごとにItemStackにする。
//            while (amount > 64) {
//                items.add(getItemStack(item, 64, damage));
//                amount -= 64;
//            }

            return getItemStack(item, amount, damage);

        } else if ( matcher.group(4) != null ) {
            // group4、group5 が null でないなら、ID^Ench-Level... 形式の指定である。

            int item = 0;
            short damage = 0;
            HashMap<Integer, Integer> enchants = new HashMap<Integer, Integer>();
            Color color = null;

            item = Integer.parseInt(matcher.group(4));
            if ( matcher.group(6) != null ) {
                damage = Short.parseShort(matcher.group(6));
            }

            // item id が0なら、nullを設定して次へ進む
            if ( item == 0 ) {
                return new ItemStack(Material.AIR, 0);
            }

            // Materialの取得をして、正しいIDが指定されたかどうかを確認する
            Material m = Material.getMaterial(item);
            if (m == null) {
                logger.severe("指定されたItemID " + item + " が見つかりません。");
                return null;
            }

            // 指定エンチャントの解析
            Matcher matcherEnchant = patternEnchant.matcher(matcher.group(5));
            while ( matcherEnchant.find() ) {
                int enchantID = Integer.parseInt(matcherEnchant.group(1));
                int enchantLevel = Integer.parseInt(matcherEnchant.group(2));
                enchants.put(enchantID, enchantLevel);
            }

            // 指定カラーの解析
            if ( matcher.group(7) != null ) {
                String colorID = matcher.group(7);
                int red = Integer.decode( "0x" + colorID.substring(0, 2) );
                int green = Integer.decode( "0x" + colorID.substring(2, 4) );
                int blue = Integer.decode( "0x" + colorID.substring(4, 6) );
                color = Color.fromRGB(red, green, blue);
            }

            return getEnchantedItem(item, enchants, damage, color);

        } else {
            logger.severe("内部エラー : 正規表現が正しくマッチしていません。");
            return null;
        }
    }

    /**
     * インベントリの内容を、kit形式の文字列に変換する。
     * @param inventory インベントリ
     * @return kit形式文字列
     */
    public String convertInvToItemString(PlayerInventory inventory) {

        StringBuffer buffer = new StringBuffer();

        for ( ItemStack item : inventory.getContents() ) {
            if ( item != null && item.getType() != Material.AIR ) {
                if ( buffer.length() > 0 ) {
                    buffer.append(",");
                }
                buffer.append(getItemInfo(item));
            }
        }

        return buffer.toString();
    }

    /**
     * インベントリの防具を、kit形式の文字列に変換する。
     * @param inventory インベントリ
     * @return kit形式文字列
     */
    public String convertArmorToItemString(PlayerInventory inventory) {

        StringBuffer buffer = new StringBuffer();

        buffer.append(getItemInfo(inventory.getHelmet()) + ",");
        buffer.append(getItemInfo(inventory.getChestplate()) + ",");
        buffer.append(getItemInfo(inventory.getLeggings()) + ",");
        buffer.append(getItemInfo(inventory.getBoots()));

        return buffer.toString();
    }

    /**
     * ItemStackインスタンスを返す
     * @param item 配布するアイテムのID
     * @param amount 配布するアイテムの数量
     * @param damage 配布するアイテムのダメージ値（指定しない場合は0にする）
     * @return ItemStackインスタンス
     */
    private ItemStack getItemStack(int item, int amount, short damage) {
        if ( damage > 0 )
            return new ItemStack(item, amount, damage);
        else
            return new ItemStack(item, amount);
    }

    /**
     * エンチャント付きのItemStackインスタンスを返す
     * @param item 配布するアイテムのID
     * @param enchants 付与するエンチャントIDと、そのレベルのセット
     * @return ItemStackインスタンス
     */
    private ItemStack getEnchantedItem(int item, HashMap<Integer, Integer> enchants, short damage, Color color) {

        ItemStack i = getItemStack(item, 1, damage);

        Set<Integer> keys = enchants.keySet();
        for ( int eid : keys ) {
            int level = enchants.get(eid);
            Enchantment ench = new EnchantmentWrapper(eid);
            if ( level < ench.getStartLevel() ) {
                level = ench.getStartLevel();
            } else if ( level > 1000 ) {
                level = 1000;
            }
            i.addUnsafeEnchantment(ench, level);
        }

        if ( color != null && 298 <= item && item <= 301 ) {
            LeatherArmorMeta lam = (LeatherArmorMeta)i.getItemMeta();
            lam.setColor(color);
            i.setItemMeta(lam);
        }

        return i;
    }

    /**
     * アイテムの情報を文字列にして返します。
     * @param item アイテム
     * @return アイテムの文字列表現
     */
    public String getItemInfo(ItemStack item) {

        if ( item == null ) {
            return "";
        }

        StringBuilder message = new StringBuilder();

        int itemID = item.getTypeId();
        int amount = item.getAmount();
        short durability = item.getDurability();
        String color = null;
        if ( 298 <= itemID && itemID <= 301 ) {
            LeatherArmorMeta lam = (LeatherArmorMeta)item.getItemMeta();
            Color colorTemp = lam.getColor();
            if ( colorTemp != null ) {
                color = convertColorToString(colorTemp);
            }
        }

        message.append(itemID);
        Map<Enchantment, Integer> enchants = item.getEnchantments();
        Set<Enchantment> keys = enchants.keySet();
        for ( Enchantment e : keys ) {
            message.append("^" + e.getId() + "-" + enchants.get(e));
        }
        if ( durability > 1 ) {
            message.append("@" + durability);
        }
        if ( color != null ) {
            message.append("$" + color);
        }
        if ( amount > 1 ) {
            message.append(":" + amount);
        }

        return message.toString();
    }

    /**
     * アイテムの情報を詳細な内容にして返します。
     * @param info アイテムの文字列表現
     * @return アイテムの詳細情報
     */
    public ArrayList<String> getDescFromItemInfo(String info) {
        return getDescFromItem( parseItemInfoToItemStack(info) );
    }

    /**
     * アイテムの情報を詳細な内容にして返します。
     * @param item アイテム
     * @return アイテムの詳細情報
     */
    public ArrayList<String> getDescFromItem(ItemStack item) {

        ArrayList<String> result = new ArrayList<String>();

        String material = item.getType().toString();
        int itemID = item.getTypeId();
        int amount = item.getAmount();
        short durability = item.getDurability();
        String color = null;
        if ( 298 <= itemID && itemID <= 301 ) {
            LeatherArmorMeta lam = (LeatherArmorMeta)item.getItemMeta();
            Color colorTemp = lam.getColor();
            if ( colorTemp != null ) {
                color = convertColorToString(colorTemp);
            }
        }

        StringBuffer message = new StringBuffer();
        message.append( String.format("&8|&f   %s  ", material) );
        if ( durability > 1 ) {
            message.append("data:" + durability + " ");
        }
        if ( color != null ) {
            message.append("color:" + color + " ");
        }
        if ( amount > 1 ) {
            message.append("amount:" + amount + " ");
        }
        result.add(message.toString());

        Map<Enchantment, Integer> enchants = item.getEnchantments();
        Set<Enchantment> enchantKeys = enchants.keySet();
        for ( Enchantment e : enchantKeys ) {
            result.add(String.format(
                    "&7|&f     %s - level %d",
                    e.getName(), enchants.get(e)));
        }

        return result;
    }

    /**
     * Colorを文字列表現に変換します。
     * @param color Color
     * @return 文字列表現
     */
    private String convertColorToString(Color color) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        return String.format("%02x%02x%02x", red, green, blue).toUpperCase();
    }
}
