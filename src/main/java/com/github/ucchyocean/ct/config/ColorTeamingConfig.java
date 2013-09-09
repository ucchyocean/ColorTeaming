/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.Utility;
import com.github.ucchyocean.ct.scoreboard.PlayerCriteria;
import com.github.ucchyocean.ct.scoreboard.SidebarCriteria;

/**
 * ColorMeTeaming の設定ハンドルクラス
 * @author ucchy
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
    
    /** クラス設定時の全回復設定 のオンオフ */
    private boolean healOnSetClass;

    /** クラス設定 */
    private Map<String, ClassData> classes;

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

    /** /cleave  を一般ユーザーに使用させるかどうか */
    private boolean allowPlayerLeave;

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

    /** テレポート実行時のディレイ間隔 */
    private int teleportDelay;

    /**
     * config.ymlの読み出し処理。
     * @return 読み込んだ ColorTeamingConfig オブジェクト
     */
    public static ColorTeamingConfig loadConfig() {

        ColorTeamingConfig ctconfig = new ColorTeamingConfig();

        // config.yml が無い場合に、デフォルトを読み出し
        File configFile = new File(ColorTeaming.instance.getDataFolder(), "config.yml");
        if ( !configFile.exists() ) {
            Utility.copyFileFromJar(
                    ColorTeaming.instance.getPluginJarFile(),
                    configFile, "config_ja.yml", false);
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

        ctconfig.healOnSetClass = config.getBoolean("healOnSetClass", true);
        ctconfig.classes = new HashMap<String, ClassData>();
        ConfigurationSection section = config.getConfigurationSection("classes");
        if ( section != null ) {
            for ( String clas : section.getKeys(false) ) {
                ConfigurationSection c = section.getConfigurationSection(clas);
                String i = c.getString("items");
                String a = c.getString("armor");
                String e = c.getString("effect");
                int x = c.getInt("experience", 0);
                ClassData data = new ClassData(i, a, e, x);
                ctconfig.classes.put(clas, data);
            }
        }

        ctconfig.colorRemoveOnDeath = config.getBoolean("colorRemoveOnDeath", false);
        ctconfig.colorRemoveOnQuit = config.getBoolean("colorRemoveOnQuit", false);
        ctconfig.noDamageSeconds = config.getInt("noDamageSeconds", 5);

        ctconfig.allowPlayerJoinAny = config.getBoolean("allowPlayerJoinAny", false);
        ctconfig.allowPlayerJoinRandom = config.getBoolean("allowPlayerJoinRandom", true);
        ctconfig.allowPlayerLeave = config.getBoolean("allowPlayerLeave", false);

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
        ctconfig.teleportDelay = config.getInt("teleportDelay", 2);

        return ctconfig;
    }

    /**
     * 設定を保存する
     */
    public void saveConfig() {

        // ファイルのロード
        File configFile = new File(ColorTeaming.instance.getDataFolder(), "config.yml");
        YamlConfiguration config = new YamlConfiguration();
        if ( configFile.exists() ) {
            config = YamlConfiguration.loadConfiguration(configFile);
        }

        // 設定のデシリアライズ
        config.set("worlds", worldNames);
        config.set("teamChatMode", isTeamChatMode);
        config.set("opDisplayMode", isOPDisplayMode);
        config.set("teamChatLogMode", isTeamChatLogMode);
        config.set("friendlyFireDisabler", isFriendlyFireDisabler);
        config.set("seeFriendlyInvisible", canSeeFriendlyInvisibles);
        config.set("healOnSetClass", healOnSetClass);
        for ( String clas : classes.keySet() ) {
            ClassData data = classes.get(clas);
            if ( data.items != null ) {
                config.set("classes." + clas + ".items", data.items);
            }
            if ( data.armor != null ) {
                config.set("classes." + clas + ".armor", data.armor);
            }
            if ( data.effect != null ) {
                config.set("classes." + clas + ".effect", data.effect);
            }
        }
        config.set("colorRemoveOnDeath", colorRemoveOnDeath);
        config.set("colorRemoveOnQuit", colorRemoveOnQuit);
        config.set("noDamageSeconds", noDamageSeconds);
        config.set("allowPlayerJoinAny", allowPlayerJoinAny);
        config.set("allowPlayerJoinRandom", allowPlayerJoinRandom);
        config.set("allowPlayerLeave", allowPlayerLeave);
        config.set("points.killPoint", killPoint);
        config.set("points.deathPoint", deathPoint);
        config.set("points.tkPoint", tkPoint);
        config.set("sideCriteria", sideCriteria.toString());
        config.set("listCriteria", listCriteria.toString());
        config.set("belowCriteria", belowCriteria.toString());
        config.set("killTrophy", killTrophy);
        config.set("killReachTrophy", killReachTrophy);
        config.set("worldSpawn", worldSpawn);
        config.set("showJapanizeGlobalChat", showJapanizeGlobalChat);
        config.set("showJapanizeTeamChat", showJapanizeTeamChat);
        config.set("teleportDelay", teleportDelay);

        // 保存処理
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public boolean isHealOnSetClass() {
        return healOnSetClass;
    }

    public Map<String, ClassData> getClasses() {
        return classes;
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

    public boolean isAllowPlayerLeave() {
        return allowPlayerLeave;
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

    public int getTeleportDelay() {
        return teleportDelay;
    }

    public void setWorldNames(List<String> worldNames) {
        this.worldNames = worldNames;
    }

    public void setTeamChatMode(boolean isTeamChatMode) {
        this.isTeamChatMode = isTeamChatMode;
    }

    public void setOPDisplayMode(boolean isOPDisplayMode) {
        this.isOPDisplayMode = isOPDisplayMode;
    }

    public void setTeamChatLogMode(boolean isTeamChatLogMode) {
        this.isTeamChatLogMode = isTeamChatLogMode;
    }

    public void setFriendlyFireDisabler(boolean isFriendlyFireDisabler) {
        this.isFriendlyFireDisabler = isFriendlyFireDisabler;
    }

    public void setCanSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
        this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
    }

    public void setHealOnSetClass(boolean healOnSetClass) {
        this.healOnSetClass = healOnSetClass;
    }

    public void setClasses(Map<String, ClassData> classes) {
        this.classes = classes;
    }

    public void setColorRemoveOnDeath(boolean colorRemoveOnDeath) {
        this.colorRemoveOnDeath = colorRemoveOnDeath;
    }

    public void setColorRemoveOnQuit(boolean colorRemoveOnQuit) {
        this.colorRemoveOnQuit = colorRemoveOnQuit;
    }

    public void setNoDamageSeconds(int noDamageSeconds) {
        this.noDamageSeconds = noDamageSeconds;
    }

    public void setAllowPlayerJoinAny(boolean allowPlayerJoinAny) {
        this.allowPlayerJoinAny = allowPlayerJoinAny;
    }

    public void setAllowPlayerJoinRandom(boolean allowPlayerJoinRandom) {
        this.allowPlayerJoinRandom = allowPlayerJoinRandom;
    }

    public void setAllowPlayerLeave(boolean allowPlayerLeave) {
        this.allowPlayerLeave = allowPlayerLeave;
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
    }

    public void setListCriteria(PlayerCriteria listCriteria) {
        this.listCriteria = listCriteria;
    }

    public void setBelowCriteria(PlayerCriteria belowCriteria) {
        this.belowCriteria = belowCriteria;
    }

    public void setKillTrophy(int killTrophy) {
        this.killTrophy = killTrophy;
    }

    public void setKillReachTrophy(int killReachTrophy) {
        this.killReachTrophy = killReachTrophy;
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

    public void setTeleportDelay(int teleportDelay) {
        this.teleportDelay = teleportDelay;
    }

}