/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.cmt;

import java.io.File;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author ucchy
 * 各色グループのリスポーンポイントをファイルで保存し、値の取得・設定を仲介するためのクラス
 */
public class RespawnConfiguration {

    private static final String CONFIG_FILE_NAME = "respawn.yml";

    private static final String KEY_WORLD = "world";
    private static final String KEY_LOCX = "x";
    private static final String KEY_LOCY = "y";
    private static final String KEY_LOCZ = "z";

    private File file;
    private YamlConfiguration config;

    /**
     * コンストラクタ
     */
    public RespawnConfiguration() {

        file = new File(
                ColorMeTeaming.instance.getDataFolder() +
                File.separator + CONFIG_FILE_NAME);

        if ( !file.exists() ) {
            YamlConfiguration conf = new YamlConfiguration();
            try {
                conf.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * グループのリスポーンポイントを取得する。未設定の場合はnullが返ることに注意すること。
     * @param name グループ名
     * @return グループのリスポーンポイント
     */
    public Location get(String name) {

        ConfigurationSection section = config.getConfigurationSection(name);
        if ( section == null ) {
            return null;
        }
        String w = section.getString(KEY_WORLD, "world");
        World world = ColorMeTeaming.getWorld(w);
        if ( world == null ) {
            return null;
        }
        double x = section.getDouble(KEY_LOCX, 0);
        double y = section.getDouble(KEY_LOCY, 65);
        double z = section.getDouble(KEY_LOCZ, 0);
        return new Location(world, x, y, z);
    }

    /**
     * グループのリスポーンポイントを設定してコンフィグの保存を行う。
     * @param name グループ名
     * @param location グループのリスポーンポイント
     */
    public void set(String name, Location location) {

        String world = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        config.set(name + "." + KEY_WORLD, world);
        config.set(name + "." + KEY_LOCX, x);
        config.set(name + "." + KEY_LOCY, y);
        config.set(name + "." + KEY_LOCZ, z);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
