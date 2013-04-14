/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.ct;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.github.ucchyocean.ct.scoreboard.SidebarCriteria;
import com.github.ucchyocean.ct.scoreboard.TabListCriteria;

/**
 * @author ucchy
 * ColorMeTeaming の設定ハンドルクラス
 */
public class ColorTeamingConfig {

    public static List<String> worldNames;
    public static boolean isTeamChatMode;
    public static boolean isOPDisplayMode;
    public static boolean isFriendlyFireDisabler;
    public static Map<String, String> classItems;
    public static Map<String, String> classArmors;
    public static boolean colorRemoveOnDeath;
    public static boolean colorRemoveOnQuit;
    public static int noDamageSeconds;
    public static boolean allowPlayerJoinAny;
    public static boolean allowPlayerJoinRandom;
    public static int killPoint;
    public static int deathPoint;
    public static int tkPoint;
    public static SidebarCriteria sideCriteria;
    public static TabListCriteria listCriteria;
    public static int killTrophy;
    public static int killReachTrophy;

    /**
     * config.ymlの読み出し処理。
     * @throws IOException
     * @return 成功したかどうか
     */
    public static void reloadConfig() {

        File configFile = new File(ColorTeaming.instance.getDataFolder(), "config.yml");
        if ( !configFile.exists() ) {
            Utility.copyFileFromJar(ColorTeaming.getPluginJarFile(), configFile, "config_ja.yml", false);
        }

        ColorTeaming.instance.reloadConfig();
        FileConfiguration config = ColorTeaming.instance.getConfig();

        worldNames = config.getStringList("worlds");
        if ( worldNames == null ) {
            worldNames = new ArrayList<String>();
        }

        isTeamChatMode = config.getBoolean("teamChatMode", false);
        isOPDisplayMode = config.getBoolean("opDisplayMode", false);
        isFriendlyFireDisabler = config.getBoolean("firelyFireDisabler", true);

        classItems = new HashMap<String, String>();
        classArmors = new HashMap<String, String>();
        ConfigurationSection section = config.getConfigurationSection("classes");
        if ( section != null ) {
            Iterator<String> i = section.getValues(false).keySet().iterator();
            while (i.hasNext()) {
                String clas = i.next();
                classItems.put(clas, config.getString("classes." + clas + ".items", "") );
                if ( config.contains("classes." + clas + ".armor") ) {
                    classArmors.put(clas, config.getString("classes." + clas + ".armor") );
                }
            }
        }

        colorRemoveOnDeath = config.getBoolean("colorRemoveOnDeath", false);
        colorRemoveOnQuit = config.getBoolean("colorRemoveOnQuit", false);
        noDamageSeconds = config.getInt("noDamageSeconds", 5);

        allowPlayerJoinAny = config.getBoolean("allowPlayerJoinAny", false);
        allowPlayerJoinRandom = config.getBoolean("allowPlayerJoinRandom", true);

        killPoint = config.getInt("points.killPoint", 1);
        deathPoint = config.getInt("points.deathPoint", -1);
        tkPoint = config.getInt("points.tkPoint", -3);

        String criteriaTemp = config.getString("sideCriteria", "rest");
        sideCriteria = SidebarCriteria.fromString(criteriaTemp);
        criteriaTemp = config.getString("listCriteria", "point");
        listCriteria = TabListCriteria.fromString(criteriaTemp);

        killTrophy = config.getInt("killTrophy", 0);
        if ( killTrophy > 0 ) {
            killReachTrophy = config.getInt("killReachTrophy", 0);
            if ( killReachTrophy > killTrophy ) {
                killReachTrophy = 0;
            }
        } else {
            killReachTrophy = 0;
        }
    }

    /**
     * config.yml に、設定値を保存する
     * @param key 設定値のキー
     * @param value 設定値の値
     */
    public static void setConfigValue(String key, Object value) {

        FileConfiguration config = ColorTeaming.instance.getConfig();
        config.set(key, value);
        ColorTeaming.instance.saveConfig();
    }
}
