/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.cmt;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Color;

/**
 * @author ucchy
 *
 */
public class Tester {

    private static final String REGEX_ITEM_PATTERN =
            "([0-9]+)(?:@([0-9]+))?(?::([0-9]+))?|" +
            "([0-9]+)((?:\\^[0-9]+-[0-9]+)*)(?:@([0-9]+))?(?:\\$([0-9A-Fa-f]{6}))?";
    private static final String REGEX_ENCHANT_PATTERN = "\\^([0-9]+)-([0-9]+)";

    /**
     * @param args
     */
    public static void main(String[] args) {

        String data = "298$667F33,299$667F33,300$667F33,301$667F33";

        Pattern pattern = Pattern.compile(REGEX_ITEM_PATTERN);
        Pattern patternEnchant = Pattern.compile(REGEX_ENCHANT_PATTERN);

        String[] array = data.split("[,]");
        for (int i = 0; i < array.length; i++) {

            Matcher matcher = pattern.matcher(array[i]);

            if ( matcher.matches() ) {

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

                    System.out.println("item" + item);
                    System.out.println("damage" + damage);
                    System.out.println("amount" + amount);

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

                    System.out.println("item" + item);
                    System.out.println("damage" + damage);
                    System.out.println("color" + color);

                } else {

                    System.out.println("指定された形式 " + matcher.group(0) + " が正しく解析できません。");
                }
            }
        }


    }

}
