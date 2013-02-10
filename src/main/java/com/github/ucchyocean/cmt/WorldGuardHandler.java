/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.cmt;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;


/**
 * @author ucchy
 * WorldGuardに接続して、WorldGuardのAPIを実行するクラス
 */
public class WorldGuardHandler {

    private static final String REGION_PREFIX = "_team_spawn_region";

    private WorldGuardPlugin wg;

    /**
     * コンストラクタ
     * @param worldguard ワールドガードのプラグインインスタンス
     */
    protected WorldGuardHandler(WorldGuardPlugin worldguard) {
        this.wg = worldguard;
    }

    public void makeTeamRegion(String group, Location center, int range) {

        Hashtable<String, ArrayList<Player>> members = ColorMeTeaming.getAllColorMembers();

        // メンバーが存在しないグループなら、何もせずに終了する
        if ( !members.containsKey(group) ) {
            return;
        }

        // 領域を定義して、WorldGuardに領域を登録する
        String regionName = group + REGION_PREFIX;

        BlockVector pt1 = new BlockVector(
                center.getX() - range,
                center.getY() - range,
                center.getZ() - range);
        BlockVector pt2 = new BlockVector(
                center.getX() + range,
                center.getY() + range,
                center.getZ() + range);
        ProtectedCuboidRegion region =
                new ProtectedCuboidRegion(regionName, pt1, pt2);

        RegionManager manager = wg.getRegionManager(center.getWorld());
        manager.addRegion(region);

        // メンバーを設定する
        region.setMembers(makeDomain(members.get(group)));

        // メンバー外の進入を拒否に設定する
        region.setFlag(DefaultFlag.ENTRY, StateFlag.State.DENY);
    }

    /**
     * 全保護領域のメンバーを更新する
     * @param defaultWorld 対象の世界
     */
    public void refreshGroupMembers(World defaultWorld) {

        // メンバーを取得して領域に再設定していく
        RegionManager manager = wg.getRegionManager(defaultWorld);
        Hashtable<String, ArrayList<Player>> members = ColorMeTeaming.getAllColorMembers();
        Enumeration<String> keys = members.keys();

        while ( keys.hasMoreElements() ) {
            String key = keys.nextElement();
            ProtectedRegion region = manager.getRegion(key + REGION_PREFIX);
            if ( region == null ) {
                continue;
            }
            region.setMembers(makeDomain(members.get(key)));
        }
    }

    /**
     * DefaultDomain（グループみたいなもの？）を作成する
     * @param players グループのメンバー
     * @return 作成したDefaultDomain
     */
    private DefaultDomain makeDomain(ArrayList<Player> players) {

        DefaultDomain domain = new DefaultDomain();
        for ( Player p : players ) {
            domain.addPlayer(p.getName());
        }
        return domain;
    }
}
