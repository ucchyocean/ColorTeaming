/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.ChatColor;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.Utility;

/**
 * チーム名設定ファイルのハンドリングクラス
 * @author ucchy
 */
public class TeamNameConfig {

    private static final String FILE_NAME = "teamcolors.txt";
    
    private File file;
    private ArrayList<TeamNameSetting> config;
    
    /**
     * コンストラクタ
     */
    public TeamNameConfig() {
        
        file = new File(ColorTeaming.instance.getDataFolder(), FILE_NAME);

        if ( !file.exists() ) {
            Utility.copyFileFromJar(
                    ColorTeaming.instance.getPluginJarFile(), 
                    file, FILE_NAME, false);
        }

        config = load(file);
    }
    
    /**
     * チーム名設定を取得する
     * @return チーム名設定
     */
    public ArrayList<TeamNameSetting> getTeamNames() {
        return config;
    }
    
    /**
     * teamcolors.txt を読み込む
     * @param file 読み込むファイル
     * @return 読み込み結果
     */
    private ArrayList<TeamNameSetting> load(File file) {

        // ファイルの内容を読み出す
        ArrayList<String> contents = new ArrayList<String>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ( (line = reader.readLine()) != null ) {
                
                line = line.trim();
                
                // 頭にシャープが付いている行は、コメントとして読み飛ばす
                if ( !line.startsWith("#") ) {
                    contents.add(line);
                }
            }
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

        // 内容の解析
        ArrayList<TeamNameSetting> config = new ArrayList<TeamNameSetting>();
        Logger logger = ColorTeaming.instance.getLogger();
        
        for ( String c : contents ) {
            
            String[] data = c.split(",");
            
            if ( data == null || data.length < 3 ) {
                continue;
            }
            
            String id = data[0].trim();
            String name = data[1].trim();
            String color_temp = data[2].trim();
            ChatColor color = Utility.getChatColorFromColorCode(color_temp);
            
            // チームIDが重複していないことを確認する
            if ( containsID(config, id) ) {
                logger.warning("teamcolors.txt：チームIDが重複しています。：" + c);
                continue;
            }
            
            // 表示名が長すぎないか確認する
            if ( name.length() > 10 ) {
                logger.warning("teamcolors.txt：表示名が長すぎます。：" + c);
                continue;
            }
            
            config.add(new TeamNameSetting(id, name, color));
        }
        
        // TODO: 最低チーム設定数（9チーム）に満たない場合は、デフォルト設定で補完する。
        
        return config;
    }
    
    /**
     * TeamNameを返す。
     * 指定した名前に一致するTeamNameが存在しない場合は、
     * color=whiteのTeamNameが返されることに注意すること。
     * @param id チームID
     * @return TeamNameオブジェクト
     */
    public TeamNameSetting getTeamNameFromID(String id) {
        
        for ( TeamNameSetting tn : config ) {
            if ( tn.getID().equals(id) ) {
                return tn;
            }
        }
        return new TeamNameSetting(id, id, ChatColor.WHITE);
    }
    
    /**
     * TeamNameを返す。
     * 指定した名前に一致するTeamNameが存在しない場合は、
     * color=whiteのTeamNameが返されることに注意すること。
     * @param config 確認対象のTeamNameSetting配列
     * @param id チームID
     * @return TeamNameオブジェクト
     */
    public static TeamNameSetting getTeamNameFromID(Set<TeamNameSetting> config, String id) {
        
        for ( TeamNameSetting tn : config ) {
            if ( tn.getID().equals(id) ) {
                return tn;
            }
        }
        return new TeamNameSetting(id, id, ChatColor.WHITE);
    }
    
    /**
     * 指定したIDが存在するかどうかを確認する
     * @param id ID
     * @return 存在するかどうか
     */
    public boolean containsID(String id) {
        
        for ( TeamNameSetting tn : config ) {
            if ( tn.getID().equals(id) ) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 指定したIDが既に存在するかどうかを確認する
     * @param config 確認対象のTeamNameSetting配列
     * @param id ID
     * @return 存在するかどうか
     */
    public static boolean containsID(ArrayList<TeamNameSetting> config, String id) {
        
        for ( TeamNameSetting tn : config ) {
            if ( tn.getID().equals(id) ) {
                return true;
            }
        }
        return false;
    }
}
