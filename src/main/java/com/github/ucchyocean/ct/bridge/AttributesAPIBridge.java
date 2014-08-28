/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2014
 */
package com.github.ucchyocean.ct.bridge;

import java.util.ArrayList;
import java.util.List;

import nl.arfie.bukkit.attributes.Attribute;
import nl.arfie.bukkit.attributes.AttributeType;
import nl.arfie.bukkit.attributes.Attributes;
import nl.arfie.bukkit.attributes.AttributesAPI;
import nl.arfie.bukkit.attributes.Operation;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * AttributesAPI連携クラス
 * @author ucchy
 */
public class AttributesAPIBridge {

    /** オペレータ */
    public enum Operator {

        /** 加算 */
        ADD_NUMBER,

        /** 乗算 */
        MULTIPLY_PERCENTAGE,

        /** パーセンテージ */
        ADD_PERCENTAGE;

        /**
         * AttributesAPIのOperationへ変換する
         * @return AttributesAPIのOperation
         */
        public Operation toAPI() {
            for ( Operation o : Operation.values() ) {
                if ( o.name().equals(this.name()) ) {
                    return o;
                }
            }
            return null;
        }

        /**
         * 文字列から、Operatorへ変換する
         * @param oper 文字列
         * @return Operator
         */
        public static Operator fromString(String oper) {
            for ( Operator o : values() ) {
                if ( o.name().equals(oper) ) {
                    return o;
                }
            }
            return null;
        }
    };

    /** 属性タイプ */
    public enum Type {

        /** 最大体力 */
        MAX_HEALTH,

        /** 索敵範囲 */
        FOLLOW_RANGE,

        /** 移動速度 */
        MOVEMENT_SPEED,

        /** ノックバック耐性 */
        KNOCKBACK_RESISTANCE,

        /** 攻撃力増加 */
        ATTACK_DAMAGE,

        /** 馬エンティティのジャンプ力増加、ColorTeamingでは使用しない */
        JUMP_STRENGTH,

        /** ゾンビエンティティの増援呼び寄せ力、ColorTeamingでは使用しない */
        SPAWN_REINFORCEMENTS;

        /**
         * AttributesAPIのAttributeTypeへ変換する
         * @return AttributesAPIのAttributeType
         */
        public AttributeType toAPI() {
            for ( AttributeType t : AttributeType.values() ) {
                if ( t.name().equals(name()) ) {
                    return t;
                }
            }
            return null;
        }

        /**
         * 文字列から、Typeへ変換する
         * @param type 文字列
         * @return Type
         */
        public static Type fromString(String type) {
            for ( Type t : values() ) {
                if ( t.name().equals(type) ) {
                    return t;
                }
            }
            return null;
        }
    }


    /**
     * AttributesAPIをロードする
     * @param plugin AttributesAPIのインスタンス
     * @return ロードされたブリッジ
     */
    public static AttributesAPIBridge load(Plugin plugin) {

        if ( plugin instanceof AttributesAPI ) {
            return new AttributesAPIBridge();
        }
        return null;
    }

    /**
     * アイテムにAttributeを付与する
     * @param item アイテム
     * @param oper オペレータ
     * @param type 属性タイプ
     * @param amount 属性値
     * @return Attributeが付与されたアイテム
     */
    public ItemStack applyAttribute(ItemStack item, Operator oper, Type type, double amount) {
        Attribute attr = new Attribute(type.toAPI(), oper.toAPI(), amount);
        return Attributes.apply(item, attr, false);
    }

    /**
     * アイテムにAttributeを付与する
     * @param item アイテム
     * @param info 属性情報
     * @return Attributeが付与されたアイテム
     */
    public ItemStack applyAttribute(ItemStack item, AttributeInfo info) {
        return applyAttribute(item, info.getOperator(), info.getType(), info.getAmount());
    }

    /**
     * アイテムにAttributeをまとめて付与する
     * @param item アイテム
     * @param infos 属性情報リスト
     * @return Attributeが付与されたアイテム
     */
    public ItemStack applyAttributes(ItemStack item, List<AttributeInfo> infos) {
        for ( AttributeInfo info : infos ) {
            item = applyAttribute(item, info);
        }
        return item;
    }

    /**
     * アイテムから属性情報を読み取って返す
     * @param item アイテム
     * @return 属性情報
     */
    public List<AttributeInfo> readAttr(ItemStack item) {

        ArrayList<AttributeInfo> info = new ArrayList<AttributeInfo>();

        for ( Attribute attr : Attributes.fromStack(item) ) {

            Type type = Type.fromString(attr.getType().name());
            Operator oper = Operator.fromString(attr.getOperation().name());
            info.add(new AttributeInfo(type, oper, attr.getAmount()));
        }

        return info;
    }

    /**
     * 付与されている属性の個数を返す
     * @param item アイテム
     * @return 属性の個数
     */
    public int getAttributeNum(ItemStack item) {
        return Attributes.fromStack(item).size();
    }
}
