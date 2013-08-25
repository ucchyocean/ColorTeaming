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
    private ArrayList<TeamName> config;
    
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
    public ArrayList<TeamName> getTeamNames() {
        return config;
    }
    
    /**
     * teamcolors.txt を読み込む
     * @param file 読み込むファイル
     * @return 読み込み結果
     */
    private ArrayList<TeamName> load(File file) {

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
        ArrayList<TeamName> config = new ArrayList<TeamName>();
        
        for ( String c : contents ) {
            
            if ( !c.contains(",") ) {
                continue;
            }
            
            int index = c.indexOf(",");
            String name = c.substring(0, index).trim();
            String color_temp = c.substring(index+1).trim();
            ChatColor color = Utility.replaceColors(color_temp);
            
            config.add(new TeamName(name, color));
        }
        
        return config;
    }
}
