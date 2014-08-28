/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2014
 */
package com.github.ucchyocean.ct.bridge;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import com.github.ucchyocean.ct.bridge.AttributesAPIBridge.Operator;
import com.github.ucchyocean.ct.bridge.AttributesAPIBridge.Type;

/**
 * 属性情報クラス、AttributesAPIBridgeクラスから使用される
 * @author ucchy
 */
public class AttributeInfo {

    /** 属性タイプ */
    private Type type;

    /** オペレータ */
    private Operator operator;

    /** 属性値 */
    private double amount;

    /**
     * コンストラクタ
     * @param type 属性タイプ
     * @param operator オペレータ
     * @param amount 属性値
     */
    public AttributeInfo(Type type, Operator operator, double amount) {
        this.type = type;
        this.operator = operator;
        this.amount = amount;
    }

    /**
     * 文字列表現にして返す
     * @param indent インデント
     * @return 文字列表現
     */
    public List<String> toStrings(String indent) {

        ArrayList<String> result = new ArrayList<String>();

        result.add(indent + "type: " + type.name());
        result.add(indent + "operator: " + operator.name());
        result.add(indent + "amount: " + amount);

        return result;
    }

    /**
     * ConfigurationSectionを読み取って、AttributeInfoに変換して返す
     * @param section コンフィグセクション
     * @return AttributeInfo
     */
    public static AttributeInfo readFromConfigSection(ConfigurationSection section) {

        String type_ = section.getString("type");
        Type type = Type.fromString(type_);
        if ( type == null ) {
            return null;
        }

        String oper_ = section.getString("operator");
        Operator operator = Operator.fromString(oper_);
        if ( operator == null ) {
            return null;
        }

        double amount = section.getDouble("amount");

        return new AttributeInfo(type, operator, amount);
    }

    /**
     * @return type
     */
    public Type getType() {
        return type;
    }

    /**
     * @return operator
     */
    public Operator getOperator() {
        return operator;
    }

    /**
     * @return amount
     */
    public double getAmount() {
        return amount;
    }
}
