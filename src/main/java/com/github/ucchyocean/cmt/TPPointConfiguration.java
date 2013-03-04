/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.cmt;

/**
 * @author ucchy
 * ctpコマンドで使用する登録ポイントをファイルで保存し、値の取得・設定を仲介するためのクラス
 */
public class TPPointConfiguration extends PointConfigAbst {

    @Override
    public String getConfigFileName() {
        return "tppoint.yml";
    }
}
