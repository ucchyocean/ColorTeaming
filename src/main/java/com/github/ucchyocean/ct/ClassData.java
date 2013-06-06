/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

/**
 * クラスデータ
 * @author ucchy
 */
public class ClassData {

    private static KitHandler handler;

    /** アイテムデータ */
    protected String items;
    /** 防具データ */
    protected String armor;
    /** エフェクトデータ */
    protected String effect;

    /**
     * コンストラクタ
     * @param items アイテムデータ
     * @param armors 防具データ
     * @param effects エフェクトデータ
     */
    public ClassData(String items, String armor, String effect) {
        this.items = items;
        this.armor = armor;
        this.effect = effect;
    }

    public ArrayList<ItemStack> getItems() {

        if ( items == null ) {
            return new ArrayList<ItemStack>();
        }
        if ( handler == null ) {
            handler = new KitHandler();
        }
        return handler.convertToItemStack(items);
    }

    public ArrayList<ItemStack> getArmor() {

        if ( armor == null ) {
            return null;
        }
        if ( handler == null ) {
            handler = new KitHandler();
        }
        return handler.convertToItemStack(armor);
    }
}
