/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.cmt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author ucchy
 * ColorMeTeaming の設定ハンドルクラス
 */
public class ColorMeTeamingConfig {

    public static String defaultWorldName;

    public static List<String> ignoreGroups;
    public static boolean isTeamChatMode;
    public static boolean isOPDisplayMode;
    public static boolean isFriendlyFireDisabler;
    public static Map<String, String> classItems;
    public static Map<String, String> classArmors;
    public static boolean autoColorRemove;
    public static boolean coloringDeathMessage;
    public static boolean protectRespawnPointWithWorldGuard;
    public static int protectRespawnPointRange;

    public static int killPoint;
    public static int deathPoint;
    public static int tkPoint;

    /**
     * config.ymlの読み出し処理。
     * @throws IOException
     * @return 成功したかどうか
     */
    public static void reloadConfig() {

        File configFile = new File(ColorMeTeaming.instance.getDataFolder(), "config.yml");
        if ( !configFile.exists() ) {
            Utility.copyFileFromJar(ColorMeTeaming.getPluginJarFile(), configFile, "config_ja.yml", false);
        }

        ColorMeTeaming.instance.reloadConfig();
        FileConfiguration config = ColorMeTeaming.instance.getConfig();

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

        autoColorRemove = config.getBoolean("autoColorRemove", true);

        coloringDeathMessage = config.getBoolean("coloringDeathMessage", true);

        protectRespawnPointWithWorldGuard =
                config.getBoolean("protectRespawnPointWithWorldGuard", false);
        protectRespawnPointRange = config.getInt("protectRespawnPointRange", 3);

        // WorldGuardプラグイン連携が true になったら、WorldGaurdをロードする
        if ( protectRespawnPointWithWorldGuard && ColorMeTeaming.wghandler == null ) {
            ColorMeTeaming.instance.loadWorldGuard();
        }

        defaultWorldName = config.getString("world", "world");
    }

    /**
     * config.yml に、設定値を保存する
     * @param key 設定値のキー
     * @param value 設定値の値
     */
    public static void setConfigValue(String key, Object value) {

        FileConfiguration config = ColorMeTeaming.instance.getConfig();
        config.set(key, value);
        ColorMeTeaming.instance.saveConfig();
    }
}
