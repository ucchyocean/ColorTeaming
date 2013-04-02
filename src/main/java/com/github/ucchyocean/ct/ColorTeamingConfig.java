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

import com.github.ucchyocean.ct.scoreboard.TabListCriteria;
import com.github.ucchyocean.ct.scoreboard.TeamCriteria;

/**
 * @author ucchy
 * ColorMeTeaming の設定ハンドルクラス
 */
public class ColorTeamingConfig {

    public static String defaultWorldName;

    public static List<String> ignoreGroups;
    public static boolean isTeamChatMode;
    public static boolean isOPDisplayMode;
    public static boolean isFriendlyFireDisabler;
    public static Map<String, String> classItems;
    public static Map<String, String> classArmors;
    public static boolean colorRemoveOnDeath;
    public static boolean colorRemoveOnQuit;
    public static boolean coloringDeathMessage;
    public static TeamCriteria teamCriteria;
    public static TabListCriteria listCriteria;
    public static boolean protectRespawnPointWithWorldGuard;
    public static int protectRespawnPointRange;

    public static int killPoint;
    public static int deathPoint;
    public static int tkPoint;

    public static int killTrophy;

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

        isTeamChatMode = config.getBoolean("teamChatMode", false);
        isOPDisplayMode = config.getBoolean("opDisplayMode", false);

        isFriendlyFireDisabler = config.getBoolean("firelyFireDisabler", true);

        ignoreGroups = config.getStringList("ignoreGroups");
        if ( ignoreGroups == null ) {
            ignoreGroups = new ArrayList<String>();
        }

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

        killPoint = config.getInt("points.killPoint", 1);
        deathPoint = config.getInt("points.deathPoint", -1);
        tkPoint = config.getInt("points.tkPoint", -3);

        colorRemoveOnDeath = config.getBoolean("autoColorRemove", true);
        colorRemoveOnDeath = config.getBoolean("colorRemoveOnDeath", colorRemoveOnDeath);
        colorRemoveOnQuit = config.getBoolean("colorRemoveOnQuit", colorRemoveOnDeath);

        coloringDeathMessage = config.getBoolean("coloringDeathMessage", true);

        String criteriaTemp = config.getString("teamCriteria", "rest");
        teamCriteria = TeamCriteria.fromString(criteriaTemp);
        criteriaTemp = config.getString("listCriteria", "kill");
        listCriteria = TabListCriteria.fromString(criteriaTemp);

        protectRespawnPointWithWorldGuard =
                config.getBoolean("protectRespawnPointWithWorldGuard", false);
        protectRespawnPointRange = config.getInt("protectRespawnPointRange", 3);

        killTrophy = config.getInt("killTrophy", 0);

        defaultWorldName = config.getString("world", "world");
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
