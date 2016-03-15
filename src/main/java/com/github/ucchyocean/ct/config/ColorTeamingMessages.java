/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2014
 */
package com.github.ucchyocean.ct.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.ucchyocean.ct.Utility;

/**
 * メッセージリソース管理クラス
 * @author ucchy
 */
public class ColorTeamingMessages {

    private static YamlConfiguration defaultMessages;
    private static File configFolder;
    private static File jar;

    private static ColorTeamingMessages instance;

    private YamlConfiguration resources;

    private String randomMapSelectedMessage;
    private String joinTeamMessage;
    private String leaveTeamMessage;
    private String leaderDefeatedMessage;
    private String leaderRunawayMessage;
    private String leaderDefeatedRemainMessage;
    private String leaderDefeatedAllMessage;
    private String leaderClearMessage;
    private String leaderInformationSummayMessage;
    private String leaderInformationTeamChatMessage;
    private String killReachTrophyMessage;
    private String killTrophyMessage;
    private String errorJoinRandomNotAllowMessage;
    private String errorJoinAnyNotAllowMessage;
    private String errorLeaveNotAllowMessage;
    private String errorAlreadyJoinMessage;
    private String errorNotJoinMessage;
    private String errorNoTeamMessage;
    private String errorInvalidTeamNameMessage;
    private String sidebarTitleTeamPoint;
    private String sidebarTitleTeamKill;
    private String sidebarTitleTeamDeath;
    private String sidebarTitleTeamRest;
    private String belowNameTitlePoint;
    private String belowNameTitleKill;
    private String belowNameTitleDeath;
    private String belowNameTitleHealth;

    /**
     * コンストラクタ
     * @param filename メッセージファイル
     */
    private ColorTeamingMessages(String filename) {

        // メッセージファイルをロード
        File file = new File(configFolder, filename);
        if ( !file.exists() ) {
            try {
                defaultMessages.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        resources = loadUTF8YamlFile(file);

        // デフォルトメッセージをデフォルトとして足す。
        resources.addDefaults(defaultMessages);

        // メッセージをロードする
        this.randomMapSelectedMessage = resources.getString("randomMapSelectedMessage");
        this.joinTeamMessage = resources.getString("joinTeamMessage");
        this.leaveTeamMessage = resources.getString("leaveTeamMessage");
        this.leaderDefeatedMessage = resources.getString("leaderDefeatedMessage");
        this.leaderRunawayMessage = resources.getString("leaderRunawayMessage");
        this.leaderDefeatedRemainMessage = resources.getString("leaderDefeatedRemainMessage");
        this.leaderDefeatedAllMessage = resources.getString("leaderDefeatedAllMessage");
        this.leaderClearMessage = resources.getString("leaderClearMessage");
        this.leaderInformationSummayMessage = resources.getString("leaderInformationSummayMessage");
        this.leaderInformationTeamChatMessage = resources.getString("leaderInformationTeamChatMessage");
        this.killReachTrophyMessage = resources.getString("killReachTrophyMessage");
        this.killTrophyMessage = resources.getString("killTrophyMessage");
        this.errorJoinRandomNotAllowMessage = resources.getString("errorJoinRandomNotAllowMessage");
        this.errorJoinAnyNotAllowMessage = resources.getString("errorJoinAnyNotAllowMessage");
        this.errorLeaveNotAllowMessage = resources.getString("errorLeaveNotAllowMessage");
        this.errorAlreadyJoinMessage = resources.getString("errorAlreadyJoinMessage");
        this.errorNotJoinMessage = resources.getString("errorNotJoinMessage");
        this.errorNoTeamMessage = resources.getString("errorNoTeamMessage");
        this.errorInvalidTeamNameMessage = resources.getString("errorInvalidTeamNameMessage");
        this.sidebarTitleTeamPoint = resources.getString("sidebarTitleTeamPoint", "&eチームポイント");
        this.sidebarTitleTeamKill = resources.getString("sidebarTitleTeamKill", "&eスコア(キル数)");
        this.sidebarTitleTeamDeath = resources.getString("sidebarTitleTeamDeath", "&eスコア(デス数)");
        this.sidebarTitleTeamRest = resources.getString("sidebarTitleTeamRest", "&eチーム人数");
        this.belowNameTitlePoint = resources.getString("belowNameTitlePoint", "&epoints");
        this.belowNameTitleKill = resources.getString("belowNameTitleKill", "&ekills");
        this.belowNameTitleDeath = resources.getString("belowNameTitleDeath", "&edeaths");
        this.belowNameTitleHealth = resources.getString("belowNameTitleHealth", "&eHP");
    }

    /**
     * Jarファイル内から直接 messages.yml を読み込み、
     * defaultMessagesとしてロードする。
     * @param _jar jarファイル
     * @param _configFolder コンフィグフォルダ
     */
    public static void initialize(File _jar, File _configFolder) {

        jar = _jar;
        configFolder = _configFolder;

        if ( !configFolder.exists() ) {
            configFolder.mkdirs();
        }

        // コンフィグフォルダにメッセージファイルがまだ無いなら、コピーしておく
        File file = new File(configFolder, "messages.yml");
        if ( !file.exists() ) {
            Utility.copyFileFromJar(jar, file, "messages.yml");
        }

        // デフォルトメッセージを、jarファイル内からロードする
        defaultMessages = new YamlConfiguration();
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jar);
            ZipEntry zipEntry = jarFile.getEntry("messages.yml");
            if ( zipEntry == null ) {
                zipEntry = jarFile.getEntry("messages.yml");
            }
            InputStream inputStream = jarFile.getInputStream(zipEntry);
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;
            while ( (line = reader.readLine()) != null ) {
                if ( line.startsWith("#") || !line.contains(":") ) {
                    continue;
                }
                String key = line.substring(0, line.indexOf(":")).trim();
                String value = line.substring(line.indexOf(":") + 1).trim();
                if ( value.startsWith("'") && value.endsWith("'") )
                    value = value.substring(1, value.length()-1);
                defaultMessages.set(key, value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ( jarFile != null ) {
                try {
                    jarFile.close();
                } catch (IOException e) {
                    // do nothing.
                }
            }
        }
    }

    /**
     * UTF8エンコードのYamlファイルから、内容をロードする。
     * @param file ファイル
     * @return ロードされたYamlデータ
     */
    private static YamlConfiguration loadUTF8YamlFile(File file) {

        YamlConfiguration config = new YamlConfiguration();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file),"UTF-8"));
            String line;
            while ( (line = reader.readLine()) != null ) {
                if ( line.startsWith("#") || !line.contains(":") ) {
                    continue;
                }
                String key = line.substring(0, line.indexOf(":")).trim();
                String value = line.substring(line.indexOf(":") + 1).trim();
                if ( value.startsWith("'") && value.endsWith("'") ) {
                    value = value.substring(1, value.length() - 1);
                }
                value = value.replace("''", "'");
                config.set(key, value);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ( reader != null ) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // do nothing.
                }
            }
        }
        return config;
    }

    /**
     * リロードを行う。
     */
    public static void reload() {
        instance = new ColorTeamingMessages("messages.yml");
    }

    public static String getRandomMapSelectedMessage(String map) {
        if ( instance.randomMapSelectedMessage == null ) return null;
        return Utility.replaceColorCode(instance.randomMapSelectedMessage.replace("%map", map));
    }

    public static String getJoinTeamMessage(String team) {
        if ( instance.joinTeamMessage == null ) return null;
        return Utility.replaceColorCode(instance.joinTeamMessage.replace("%team", team));
    }

    public static String getLeaveTeamMessage() {
        return Utility.replaceColorCode(instance.leaveTeamMessage);
    }

    public static String getLeaderDefeatedMessage(String team, String name) {
        if ( instance.leaderDefeatedMessage == null ) return null;
        return Utility.replaceColorCode(instance.leaderDefeatedMessage
                .replace("%team", team).replace("%name", name));
    }

    public static String getLeaderRunawayMessage(String team, String name) {
        if ( instance.leaderRunawayMessage == null ) return null;
        return Utility.replaceColorCode(instance.leaderRunawayMessage
                .replace("%team", team).replace("%name", name));
    }

    public static String getLeaderDefeatedRemainMessage(String team, int num) {
        if ( instance.leaderDefeatedRemainMessage == null ) return null;
        return Utility.replaceColorCode(instance.leaderDefeatedRemainMessage
                .replace("%team", team).replace("%num", num + ""));
    }

    public static String getLeaderDefeatedAllMessage(String team) {
        if ( instance.leaderDefeatedAllMessage == null ) return null;
        return Utility.replaceColorCode(instance.leaderDefeatedAllMessage.replace("%team", team));
    }

    public static String getLeaderClearMessage() {
        return Utility.replaceColorCode(instance.leaderClearMessage);
    }

    public static String getLeaderInformationSummayMessage(String team) {
        if ( instance.leaderInformationSummayMessage == null ) return null;
        return Utility.replaceColorCode(instance.leaderInformationSummayMessage.replace("%team", team));
    }

    public static String getLeaderInformationTeamChatMessage(String team, String name) {
        if ( instance.leaderInformationTeamChatMessage == null ) return null;
        return Utility.replaceColorCode(instance.leaderInformationTeamChatMessage
                .replace("%team", team).replace("%name", name));
    }

    public static String getKillReachTrophyMessage(String team, int trophy, int remain) {
        if ( instance.killReachTrophyMessage == null ) return null;
        return Utility.replaceColorCode(instance.killReachTrophyMessage
                .replace("%team", team).replace("%trophy", trophy + "").replace("%remain", remain + ""));
    }

    public static String getKillTrophyMessage(String team, int trophy) {
        if ( instance.killTrophyMessage == null ) return null;
        return Utility.replaceColorCode(instance.killTrophyMessage
                .replace("%team", team).replace("%trophy", trophy + ""));
    }

    public static String getErrorJoinRandomNotAllowMessage() {
        return Utility.replaceColorCode(instance.errorJoinRandomNotAllowMessage);
    }

    public static String getErrorJoinAnyNotAllowMessage() {
        return Utility.replaceColorCode(instance.errorJoinAnyNotAllowMessage);
    }

    public static String getErrorLeaveNotAllowMessage() {
        return Utility.replaceColorCode(instance.errorLeaveNotAllowMessage);
    }

    public static String getErrorAlreadyJoinMessage() {
        return Utility.replaceColorCode(instance.errorAlreadyJoinMessage);
    }

    public static String getErrorNotJoinMessage() {
        return Utility.replaceColorCode(instance.errorNotJoinMessage);
    }

    public static String getErrorNoTeamMessage() {
        return Utility.replaceColorCode(instance.errorNoTeamMessage);
    }

    public static String getErrorInvalidTeamNameMessage(String arg) {
        if ( instance.errorInvalidTeamNameMessage == null ) return null;
        return Utility.replaceColorCode(instance.errorInvalidTeamNameMessage.replace("%arg", arg));
    }

    public static String getSidebarTitleTeamPoint() {
        return Utility.replaceColorCode(instance.sidebarTitleTeamPoint);
    }

    public static String getSidebarTitleTeamKill() {
        return Utility.replaceColorCode(instance.sidebarTitleTeamKill);
    }

    public static String getSidebarTitleTeamDeath() {
        return Utility.replaceColorCode(instance.sidebarTitleTeamDeath);
    }

    public static String getSidebarTitleTeamRest() {
        return Utility.replaceColorCode(instance.sidebarTitleTeamRest);
    }

    public static String getBelowNameTitlePoint() {
        return Utility.replaceColorCode(instance.belowNameTitlePoint);
    }

    public static String getBelowNameTitleKill() {
        return Utility.replaceColorCode(instance.belowNameTitleKill);
    }

    public static String getBelowNameTitleDeath() {
        return Utility.replaceColorCode(instance.belowNameTitleDeath);
    }

    public static String getBelowNameTitleHealth() {
        return Utility.replaceColorCode(instance.belowNameTitleHealth);
    }
}
