/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.config;

/**
 * ctpコマンドで使用する登録ポイントをファイルで保存し、値の取得・設定を仲介するためのクラス
 * @author ucchy
 */
public class TPPointConfiguration extends PointConfigAbst {

    @Override
    public String getConfigFileName() {
        return "tppoint.yml";
    }
}
