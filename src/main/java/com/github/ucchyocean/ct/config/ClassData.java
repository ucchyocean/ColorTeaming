/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.config;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 * クラスデータ
 * @author ucchy
 */
public class ClassData {

    private static KitParser kparser;
    private static EffectParser eparser;

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
        if ( kparser == null ) {
            kparser = new KitParser();
        }
        return kparser.parseToItemStack(items);
    }

    public ArrayList<ItemStack> getArmor() {

        if ( armor == null ) {
            return new ArrayList<ItemStack>();
        }
        if ( kparser == null ) {
            kparser = new KitParser();
        }
        return kparser.parseToItemStack(armor);
    }
    
    public ArrayList<PotionEffect> getEffect() {
        
        if ( effect == null ) {
            return new ArrayList<PotionEffect>();
        }
        if ( eparser == null ) {
            eparser = new EffectParser();
        }
        return eparser.parseEffectData(effect);
    }
}
