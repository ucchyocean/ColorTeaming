/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * ポーションエフェクト情報の解析クラス
 * @author ucchy
 */
public class EffectParser {

    private static final int DEFAULT_DURATION = 1000000;
    private static final String REGEX_EFFECT_PATTERN = "([0-9]+)-([0-9]+)(?::([0-9]+))?";
    
    private Pattern pattern;
    private Logger logger;
    
    public EffectParser() {
        logger = Bukkit.getLogger();
        pattern = Pattern.compile(REGEX_EFFECT_PATTERN);
    }

    /**
     * Classのエフェクトデータ文字列を解析し、PotionEffect配列に変換する。
     * @param data 解析元の文字列　例）"1-2,2-2:60,3-1"
     * @return PotionEffectの配列
     */
    public ArrayList<PotionEffect> parseEffectData(String data) {

        ArrayList<PotionEffect> effects = new ArrayList<PotionEffect>();

        String[] array = data.split("[,]");
        for (int i = 0; i < array.length; i++) {

            Matcher matcher = pattern.matcher(array[i]);

            if ( matcher.matches() ) {

                int id = Integer.parseInt(matcher.group(1));
                int level = Integer.parseInt(matcher.group(2));
                int duration = DEFAULT_DURATION;
                if ( matcher.group(3) != null ) {
                    duration = Integer.parseInt(matcher.group(3));
                }

                @SuppressWarnings("deprecation")
                PotionEffectType type = PotionEffectType.getById(id);
                if ( type == null ) {
                    continue;
                }

                PotionEffect applyEffect = new PotionEffect(
                        type, duration, level);
                if ( applyEffect != null ) {
                    effects.add(applyEffect);
                }

            } else {
                logger.severe(
                        "指定された形式 " + array[i] + " が正しく解析できません。");
                return null;
            }
        }

        return effects;
    }
    
    
    /**
     * 指定されたEntityに設定されているエフェクトの情報を、文字列にして返します。
     * @param entity エンティティ
     * @return エフェクトの文字列
     */
    public String getEffectInfo(LivingEntity entity) {
        
        if ( entity == null ) {
            return "";
        }
        
        StringBuilder message = new StringBuilder();
        Collection<PotionEffect> effects = entity.getActivePotionEffects();
        
        for ( PotionEffect effect : effects ) {
            
            if ( message.length() > 0 ) {
                message.append(",");
            }
            
            @SuppressWarnings("deprecation")
            int id = effect.getType().getId();
            int level = effect.getAmplifier();
            int duration = effect.getDuration();
            
            message.append(String.format("%d-%d:%d", id, level, duration));
        }
        
        return message.toString();
    }
}
