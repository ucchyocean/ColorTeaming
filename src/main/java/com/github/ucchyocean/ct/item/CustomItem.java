/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.item;

import org.bukkit.inventory.ItemStack;

/**
 * カスタムアイテム定義用インターフェイス
 * @author ucchy
 */
public class CustomItem {

    String name;
    String displayName;
    ItemStack item;
    
    public CustomItem(ItemStack item, String name, String displayName) {
        this.item = item;
        this.name = name;
        this.displayName = displayName;
    }
    
    /**
     * カスタムアイテム名を返す
     * @return カスタムアイテム名
     */
    public String getName() {
        return name;
    }
    
    /**
     * カスタムアイテムの表示名を返す
     * @return 表示名
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * カスタムアイテムのアイテムスタックを返す
     * @return アイテムスタック
     */
    public ItemStack getItemStack() {
        return item;
    }
}
