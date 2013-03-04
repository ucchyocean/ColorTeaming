/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.cmt;


/**
 * @author ucchy
 * 各色グループのリスポーンポイントをファイルで保存し、値の取得・設定を仲介するためのクラス
 */
public class RespawnConfiguration extends PointConfigAbst {

    @Override
    public String getConfigFileName() {
        return "respawn.yml";
    }
}
