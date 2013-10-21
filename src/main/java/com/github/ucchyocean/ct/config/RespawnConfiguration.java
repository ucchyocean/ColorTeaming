/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.config;

import java.util.ArrayList;

import org.bukkit.Location;


/**
 * 各色チームのリスポーンポイントをファイルで保存し、値の取得・設定を仲介するためのクラス
 * @author ucchy
 */
public class RespawnConfiguration extends PointConfigAbst {

    /**
     * コンストラクタ
     * @see com.github.ucchyocean.ct.config.PointConfigAbst#getConfigFileName()
     */
    @Override
    public String getConfigFileName() {
        return "respawn.yml";
    }

    /**
     * 指定したチーム名とマップ名の組み合わせのリスポーン地点を取得します。
     * @param team チーム名
     * @param map マップ名
     * @return リスポーン地点
     */
    public Location get(String team, String map) {

        if ( map != null && !map.equals("") ) {
            return get(map + "-" + team);
        } else {
            return get(team);
        }
    }

    /**
     * 指定したチーム名とマップ名の組み合わせで、リスポーン地点を設定します。
     * @param team チーム名
     * @param map マップ名
     * @param respawn リスポーン地点
     */
    public void set(String team, String map, Location respawn) {

        if ( map != null && !map.equals("") ) {
            set(map + "-" + team, respawn);
        } else {
            set(team, respawn);
        }
    }

    /**
     * 指定したマップのリスポーンポイントのリストを取得します
     * @param map マップ名
     * @return リスポーンポイントのリスト
     */
    public ArrayList<String> list(String map) {

        ArrayList<String> mkeys = new ArrayList<String>();
        ArrayList<String> keys = keys();
        if ( map == null || map.equals("") ) {
            for ( String key : keys ) {
                if ( !key.contains("-") ) {
                    mkeys.add(key);
                }
            }
        } else {
            for ( String key : keys ) {
                if ( key.startsWith(map + "-") ) {
                    mkeys.add(key);
                }
            }
        }

        return list(mkeys);
    }
    
    /**
     * 現在登録されている全てのマップ名を取得します
     * @return マップ名
     */
    public ArrayList<String> getAllMapNames() {
        
        ArrayList<String> results = new ArrayList<String>();
        ArrayList<String> keys = keys();
        
        for ( String key : keys ) {
            if ( key.contains("-") ) {
                String map = key.substring(0, key.indexOf("-"));
                if ( !results.contains(map) ) {
                    results.add(map);
                }
            }
        }
        
        return results;
    }
}
