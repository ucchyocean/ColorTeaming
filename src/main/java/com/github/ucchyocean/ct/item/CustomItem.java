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
public interface CustomItem {

    /**
     * カスタムアイテム名を返す
     * @return カスタムアイテム名
     */
    public String getName();
    
    /**
     * カスタムアイテムの表示名を返す
     * @return 表示名
     */
    public String getDisplayName();

    /**
     * カスタムアイテムのアイテムスタックを返す
     * @return アイテムスタック
     */
    public ItemStack getItemStack();
}
