/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct;


/**
 * @author ucchy
 * ゲーム終了条件
 */
public enum GameGoalKind {

    /** 他のチーム全滅 */
    DEFEAT("defeat"),

    /** リーダー全滅 */
    LEADER("leader"),

    /** 制限時間 */
    TIME("time"),

    /** キル数達成 */
    KILL("kill"),

    /** 設定オフ */
    NONE("none");

    /**
     * ID
     */
    private String id;

    /**
     * コンストラクタ
     * @param id ID
     */
    GameGoalKind(String id) {
        this.id = id;
    }

    /**
     * 文字列表現を返す
     * @return 文字列表現
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return id;
    }

    /**
     * 日本語の文字列表現を返す
     * @return 日本語の文字列表現
     */
    public String toJapaneseString() {

        switch (this) {
        case DEFEAT:
            return "他チーム全滅";
        case LEADER:
            return "リーダー全滅";
        case TIME:
            return "制限時間";
        case KILL:
            return "キル数達成";
        case NONE:
        default:
            return "設定なし";
        }
    }

    /**
     * 文字列表現からGameGoalKindに変換して返す
     * @param id 文字列表現
     * @return 該当するGameGoalKind、該当しない場合はGameGoalKind.NONE
     */
    public static GameGoalKind fromString(String id) {

        if ( id == null ) {
            return NONE;
        }

        for (GameGoalKind value : GameGoalKind.values()) {
            if (id.equalsIgnoreCase(value.id)) {
                return value;
            }
        }

        return NONE;
    }
}
