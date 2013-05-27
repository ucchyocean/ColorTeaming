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
    private List<String> worldNames;

    /** チームチャットのオンオフ */
    private boolean isTeamChatMode;

    /** チームチャットのOP傍聴オンオフ */
    private boolean isOPDisplayMode;

    /** チームチャットのロギング オンオフ */
    private boolean isTeamChatLogMode;

    /** FriendlyFire無効機能の オンオフ */
    private boolean isFriendlyFireDisabler;

    /** 仲間の透明が見えるかどうか のオンオフ */
    private boolean canSeeFriendlyInvisibles;

    /** クラスのアイテム設定 */
    private Map<String, String> classItems;

    /** クラスのアーマー設定 */
    private Map<String, String> classArmors;

    /** 死亡時のチーム離脱 オンオフ */
    private boolean colorRemoveOnDeath;

    /** ログアウト時のチーム離脱 オンオフ */
    private boolean colorRemoveOnQuit;

    /** リスポーン後の無敵時間(秒) */
    private int noDamageSeconds;

    /** /cjoin (group) を一般ユーザーに使用させるかどうか */
    private boolean allowPlayerJoinAny;

    /** /cjoin  を一般ユーザーに使用させるかどうか */
    private boolean allowPlayerJoinRandom;

    /** キル時のポイント設定 */
    private int killPoint;

    /** デス時のポイント設定 */
    private int deathPoint;

    /** チームメンバーキル時のポイント設定 */
    private int tkPoint;

    /** サイドバーのスコア設定 */
    private SidebarCriteria sideCriteria;

    /** Tabキーリストのスコア設定 */
    private PlayerCriteria listCriteria;

    /** 名前下のスコア設定 */
    private PlayerCriteria belowCriteria;

    /** キル数目標の設定 */
    private int killTrophy;

    /** キル数リーチの設定 */
    private int killReachTrophy;

    /** ワールドのリスポーン地点への初期リスポーン設定 */
    private boolean worldSpawn;

    /** グローバルチャットをローマ字かな変換するかどうか */
    private boolean showJapanizeGlobalChat;

    /** チームチャットをローマ字かな変換するかどうか */
    private boolean showJapanizeTeamChat;

    /**
     * config.ymlの読み出し処理。
     * @throws IOException
     * @return 成功したかどうか
     */
    public static ColorTeamingConfig loadConfig() {

        ColorTeamingConfig ctconfig = new ColorTeamingConfig();

        // config.yml が無い場合に、デフォルトを読み出し
        File configFile = new File(ColorTeaming.instance.getDataFolder(), "config.yml");
        if ( !configFile.exists() ) {
            Utility.copyFileFromJar(ColorTeaming.getPluginJarFile(), configFile, "config_ja.yml", false);
        }

        // config取得
        ColorTeaming.instance.reloadConfig();
        FileConfiguration config = ColorTeaming.instance.getConfig();

        ctconfig.worldNames = config.getStringList("worlds");
        if ( ctconfig.worldNames == null ) {
            ctconfig.worldNames = new ArrayList<String>();
        }

        ctconfig.isTeamChatMode = config.getBoolean("teamChatMode", false);
        ctconfig.isOPDisplayMode = config.getBoolean("opDisplayMode", false);
        ctconfig.isTeamChatLogMode = config.getBoolean("teamChatLogMode", true);
        ctconfig.isFriendlyFireDisabler = config.getBoolean("friendlyFireDisabler", true);
        ctconfig.canSeeFriendlyInvisibles = config.getBoolean("seeFriendlyInvisible", true);

        ctconfig.classItems = new HashMap<String, String>();
        ctconfig.classArmors = new HashMap<String, String>();
        ConfigurationSection section = config.getConfigurationSection("classes");
        if ( section != null ) {
            Iterator<String> i = section.getValues(false).keySet().iterator();
            while (i.hasNext()) {
                String clas = i.next();
                ctconfig.classItems.put(clas, config.getString("classes." + clas + ".items", "") );
                if ( config.contains("classes." + clas + ".armor") ) {
                    ctconfig.classArmors.put(clas, config.getString("classes." + clas + ".armor") );
                }
            }
        }

        ctconfig.colorRemoveOnDeath = config.getBoolean("colorRemoveOnDeath", false);
        ctconfig.colorRemoveOnQuit = config.getBoolean("colorRemoveOnQuit", false);
        ctconfig.noDamageSeconds = config.getInt("noDamageSeconds", 5);

        ctconfig.allowPlayerJoinAny = config.getBoolean("allowPlayerJoinAny", false);
        ctconfig.allowPlayerJoinRandom = config.getBoolean("allowPlayerJoinRandom", true);

        ctconfig.killPoint = config.getInt("points.killPoint", 1);
        ctconfig.deathPoint = config.getInt("points.deathPoint", -1);
        ctconfig.tkPoint = config.getInt("points.tkPoint", -3);

        String criteriaTemp = config.getString("sideCriteria", "rest");
        ctconfig.sideCriteria = SidebarCriteria.fromString(criteriaTemp);
        criteriaTemp = config.getString("listCriteria", "point");
        ctconfig.listCriteria = PlayerCriteria.fromString(criteriaTemp);
        criteriaTemp = config.getString("belowCriteria", "none");
        ctconfig.belowCriteria = PlayerCriteria.fromString(criteriaTemp);

        ctconfig.killTrophy = config.getInt("killTrophy", 0);
        if ( ctconfig.killTrophy > 0 ) {
            ctconfig.killReachTrophy = config.getInt("killReachTrophy", 0);
            if ( ctconfig.killReachTrophy > ctconfig.killTrophy ) {
                ctconfig.killReachTrophy = 0;
            }
        } else {
            ctconfig.killReachTrophy = 0;
        }

        ctconfig.worldSpawn = config.getBoolean("worldSpawn", false);

        ctconfig.showJapanizeGlobalChat = config.getBoolean("showJapanizeGlobalChat", false);
        ctconfig.showJapanizeTeamChat = config.getBoolean("showJapanizeTeamChat", true);

        return ctconfig;
    }

    /**
     * config.yml に、設定値を保存する
     * @param key 設定値のキー
     * @param value 設定値の値
     */
    private static void setConfigValue(String key, Object value) {

        FileConfiguration config = ColorTeaming.instance.getConfig();
        config.set(key, value);

        // ヘッダー再設定
        String header = Utility.getYamlHeader("config_ja.yml");
        config.options().header(header);

        ColorTeaming.instance.saveConfig();
    }

    public List<String> getWorldNames() {
        return worldNames;
    }

    public boolean isTeamChatMode() {
        return isTeamChatMode;
    }

    public boolean isOPDisplayMode() {
        return isOPDisplayMode;
    }

    public boolean isTeamChatLogMode() {
        return isTeamChatLogMode;
    }

    public boolean isFriendlyFireDisabler() {
        return isFriendlyFireDisabler;
    }

    public boolean isCanSeeFriendlyInvisibles() {
        return canSeeFriendlyInvisibles;
    }

    public Map<String, String> getClassItems() {
        return classItems;
    }

    public Map<String, String> getClassArmors() {
        return classArmors;
    }

    public boolean isColorRemoveOnDeath() {
        return colorRemoveOnDeath;
    }

    public boolean isColorRemoveOnQuit() {
        return colorRemoveOnQuit;
    }

    public int getNoDamageSeconds() {
        return noDamageSeconds;
    }

    public boolean isAllowPlayerJoinAny() {
        return allowPlayerJoinAny;
    }

    public boolean isAllowPlayerJoinRandom() {
        return allowPlayerJoinRandom;
    }

    public int getKillPoint() {
        return killPoint;
    }

    public int getDeathPoint() {
        return deathPoint;
    }

    public int getTkPoint() {
        return tkPoint;
    }

    public SidebarCriteria getSideCriteria() {
        return sideCriteria;
    }

    public PlayerCriteria getListCriteria() {
        return listCriteria;
    }

    public PlayerCriteria getBelowCriteria() {
        return belowCriteria;
    }

    public int getKillTrophy() {
        return killTrophy;
    }

    public int getKillReachTrophy() {
        return killReachTrophy;
    }

    public boolean isWorldSpawn() {
        return worldSpawn;
    }

    public boolean isShowJapanizeGlobalChat() {
        return showJapanizeGlobalChat;
    }

    public boolean isShowJapanizeTeamChat() {
        return showJapanizeTeamChat;
    }

    public void setTeamChatMode(boolean isTeamChatMode) {
        this.isTeamChatMode = isTeamChatMode;
        setConfigValue("teamChatMode", isTeamChatMode);
    }

    public void setOPDisplayMode(boolean isOPDisplayMode) {
        this.isOPDisplayMode = isOPDisplayMode;
        setConfigValue("opDisplayMode", isOPDisplayMode);
    }

    public void setTeamChatLogMode(boolean isTeamChatLogMode) {
        this.isTeamChatLogMode = isTeamChatLogMode;
        setConfigValue("teamChatLogMode", isTeamChatLogMode);
    }

    public void setFriendlyFireDisabler(boolean isFriendlyFireDisabler) {
        this.isFriendlyFireDisabler = isFriendlyFireDisabler;
        setConfigValue("friendlyFireDisabler", isFriendlyFireDisabler);
    }

    public void setCanSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
        this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
        setConfigValue("seeFriendlyInvisible", canSeeFriendlyInvisibles);
    }

    public void setClassItems(Map<String, String> classItems) {
        this.classItems = classItems;
    }

    public void setClassArmors(Map<String, String> classArmors) {
        this.classArmors = classArmors;
    }

    public void setColorRemoveOnDeath(boolean colorRemoveOnDeath) {
        this.colorRemoveOnDeath = colorRemoveOnDeath;
        setConfigValue("colorRemoveOnDeath", colorRemoveOnDeath);
    }

    public void setColorRemoveOnQuit(boolean colorRemoveOnQuit) {
        this.colorRemoveOnQuit = colorRemoveOnQuit;
        setConfigValue("colorRemoveOnQuit", colorRemoveOnQuit);
    }

    public void setNoDamageSeconds(int noDamageSeconds) {
        this.noDamageSeconds = noDamageSeconds;
    }

    public void setAllowPlayerJoinAny(boolean allowPlayerJoinAny) {
        this.allowPlayerJoinAny = allowPlayerJoinAny;
        setConfigValue("allowPlayerJoinAny", allowPlayerJoinAny);
    }

    public void setAllowPlayerJoinRandom(boolean allowPlayerJoinRandom) {
        this.allowPlayerJoinRandom = allowPlayerJoinRandom;
        setConfigValue("allowPlayerJoinRandom", allowPlayerJoinRandom);
    }

    public void setKillPoint(int killPoint) {
        this.killPoint = killPoint;
    }

    public void setDeathPoint(int deathPoint) {
        this.deathPoint = deathPoint;
    }

    public void setTkPoint(int tkPoint) {
        this.tkPoint = tkPoint;
    }

    public void setSideCriteria(SidebarCriteria sideCriteria) {
        this.sideCriteria = sideCriteria;
        setConfigValue("sideCriteria", sideCriteria.toString());
    }

    public void setListCriteria(PlayerCriteria listCriteria) {
        this.listCriteria = listCriteria;
        setConfigValue("listCriteria", listCriteria.toString());
    }

    public void setBelowCriteria(PlayerCriteria belowCriteria) {
        this.belowCriteria = belowCriteria;
        setConfigValue("belowCriteria", belowCriteria);
    }

    public void setKillTrophy(int killTrophy) {
        this.killTrophy = killTrophy;
        setConfigValue("killTrophy", killTrophy);
    }

    public void setKillReachTrophy(int killReachTrophy) {
        this.killReachTrophy = killReachTrophy;
        setConfigValue("killReachTrophy", killReachTrophy);
    }

    public void setWorldSpawn(boolean worldSpawn) {
        this.worldSpawn = worldSpawn;
    }

    public void setShowJapanizeGlobalChat(boolean showJapanizeGlobalChat) {
        this.showJapanizeGlobalChat = showJapanizeGlobalChat;
    }

    public void setShowJapanizeTeamChat(boolean showJapanizeTeamChat) {
        this.showJapanizeTeamChat = showJapanizeTeamChat;
    }
}
