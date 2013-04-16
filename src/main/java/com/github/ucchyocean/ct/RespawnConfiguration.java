/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.ct;

import java.util.ArrayList;

import org.bukkit.Location;


/**
 * @author ucchy
 * 各色グループのリスポーンポイントをファイルで保存し、値の取得・設定を仲介するためのクラス
 */
public class RespawnConfiguration extends PointConfigAbst {

    /**
     * コンストラクタ
     * @see com.github.ucchyocean.ct.PointConfigAbst#getConfigFileName()
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
        for ( String key : keys ) {
            if ( key.startsWith(map + "-") ) {
                mkeys.add(key);
            }
        }

        return list(mkeys);
    }
}
