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

import com.github.ucchyocean.ct.scoreboard.PlayerCriteria;
import com.github.ucchyocean.ct.scoreboard.SidebarCriteria;

/**
 * @author ucchy
 * ColorMeTeaming の設定ハンドルクラス
 */
public class ColorTeamingConfig {

    /** 対象とするワールドの名前 */
    public static List<String> worldNames;

    /** チームチャットのオンオフ */
    public static boolean isTeamChatMode;

    /** チームチャットのOP傍聴オンオフ */
    public static boolean isOPDisplayMode;

    /** チームチャットのロギング オンオフ */
    public static boolean isTeamChatLogMode;

    /** FriendlyFire無効機能の オンオフ */
    public static boolean isFriendlyFireDisabler;

    /** 仲間の透明が見えるかどうか のオンオフ */
    public static boolean canSeeFriendlyInvisibles;

    /** クラスのアイテム設定 */
    public static Map<String, String> classItems;

    /** クラスのアーマー設定 */
    public static Map<String, String> classArmors;

    /** 死亡時のチーム離脱 オンオフ */
    public static boolean colorRemoveOnDeath;

    /** ログアウト時のチーム離脱 オンオフ */
    public static boolean colorRemoveOnQuit;

    /** リスポーン後の無敵時間(秒) */
    public static int noDamageSeconds;

    /** /cjoin (group) を一般ユーザーに使用させるかどうか */
    public static boolean allowPlayerJoinAny;

    /** /cjoin  を一般ユーザーに使用させるかどうか */
    public static boolean allowPlayerJoinRandom;

    /** キル時のポイント設定 */
    public static int killPoint;

    /** デス時のポイント設定 */
    public static int deathPoint;

    /** チームメンバーキル時のポイント設定 */
    public static int tkPoint;

    /** サイドバーのスコア設定 */
    public static SidebarCriteria sideCriteria;

    /** Tabキーリストのスコア設定 */
    public static PlayerCriteria listCriteria;

    /** 名前下のスコア設定 */
    public static PlayerCriteria belowCriteria;

    /** キル数目標の設定 */
    public static int killTrophy;

    /** キル数リーチの設定 */
    public static int killReachTrophy;

    /** ワールドのリスポーン地点への初期リスポーン設定 */
    public static boolean worldSpawn;

    /** グローバルチャットをローマ字かな変換するかどうか */
    public static boolean showJapanizeGlobalChat;

    /** チームチャットをローマ字かな変換するかどうか */
    public static boolean showJapanizeTeamChat;

    /**
     * config.ymlの読み出し処理。
     * @throws IOException
     * @return 成功したかどうか
     */
    public static void reloadConfig() {

        // config.yml が無い場合に、デフォルトを読み出し
        File configFile = new File(ColorTeaming.instance.getDataFolder(), "config.yml");
        if ( !configFile.exists() ) {
            Utility.copyFileFromJar(ColorTeaming.getPluginJarFile(), configFile, "config_ja.yml", false);
        }

        // config取得
        ColorTeaming.instance.reloadConfig();
        FileConfiguration config = ColorTeaming.instance.getConfig();

        worldNames = config.getStringList("worlds");
        if ( worldNames == null ) {
            worldNames = new ArrayList<String>();
        }

        isTeamChatMode = config.getBoolean("teamChatMode", false);
        isOPDisplayMode = config.getBoolean("opDisplayMode", false);
        isTeamChatLogMode = config.getBoolean("teamChatLogMode", true);
        isFriendlyFireDisabler = config.getBoolean("friendlyFireDisabler", true);
        canSeeFriendlyInvisibles = config.getBoolean("seeFriendlyInvisible", true);

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
        listCriteria = PlayerCriteria.fromString(criteriaTemp);
        criteriaTemp = config.getString("belowCriteria", "none");
        belowCriteria = PlayerCriteria.fromString(criteriaTemp);

        killTrophy = config.getInt("killTrophy", 0);
        if ( killTrophy > 0 ) {
            killReachTrophy = config.getInt("killReachTrophy", 0);
            if ( killReachTrophy > killTrophy ) {
                killReachTrophy = 0;
            }
        } else {
            killReachTrophy = 0;
        }

        worldSpawn = config.getBoolean("worldSpawn", false);

        showJapanizeGlobalChat = config.getBoolean("showJapanizeGlobalChat", false);
        showJapanizeTeamChat = config.getBoolean("showJapanizeTeamChat", true);
    }

    /**
     * config.yml に、設定値を保存する
     * @param key 設定値のキー
     * @param value 設定値の値
     */
    public static void setConfigValue(String key, Object value) {

        FileConfiguration config = ColorTeaming.instance.getConfig();
        config.set(key, value);

        // ヘッダー再設定
        String header = Utility.getYamlHeader("config_ja.yml");
        config.options().header(header);

        ColorTeaming.instance.saveConfig();
    }
}
