/*
 * Copyright ucchy 2013
 */
package com.github.ucchyocean.ct;

import java.util.ArrayList;

import org.bukkit.Location;


/**
 * @author ucchy
 * WorldGuardに接続して、WorldGuardのAPIを実行するクラス
 */
public class WorldGuardHandler {

    private static final String REGION_PREFIX = "_team_spawn_region";

//    private WorldGuardPlugin wg;
    private ArrayList<String> regionNames;

    /**
     * コンストラクタ
     * @param worldguard ワールドガードのプラグインインスタンス
     */
//    protected WorldGuardHandler(WorldGuardPlugin worldguard) {
//        this.wg = worldguard;
    protected WorldGuardHandler() {
        regionNames = new ArrayList<String>();
    }

    /**
     * 色グループの保護領域を作成します
     * @param group グループ名
     * @param center 保護領域の中心
     * @param range 保護領域の半径
     */
    public void makeTeamRegion(String group, Location center, int range) {

//        String regionName = group + REGION_PREFIX;
//
//        Hashtable<String, ArrayList<Player>> members = ColorMeTeaming.getAllColorMembers();
//
//        // 領域を定義して、WorldGuardに領域を登録する
//        BlockVector pt1 = new BlockVector(
//                center.getX() - range - 1,
//                center.getY() - range - 1,
//                center.getZ() - range - 1);
//        BlockVector pt2 = new BlockVector(
//                center.getX() + range - 1,
//                center.getY() + range - 1,
//                center.getZ() + range - 1);
//        ProtectedCuboidRegion region =
//                new ProtectedCuboidRegion(regionName, pt1, pt2);
//
//        RegionManager manager = wg.getRegionManager(center.getWorld());
//
//        // 既に領域がある場合は、消しておく。
//        if ( manager.hasRegion(regionName) ) {
//            manager.removeRegion(regionName);
//        }
//
//        // 領域を登録
//        manager.addRegion(region);
//
//        if ( !regionNames.contains(regionName) ) {
//            regionNames.add(regionName);
//        }
//
//        // メンバーを設定する
//        region.setMembers(makeDomain(members.get(group)));
//
//        // メンバー以外の進入を拒否に設定する、PVPを不可にする
//        region.setFlag(DefaultFlag.ENTRY, StateFlag.State.DENY);
//        region.setFlag(DefaultFlag.PVP, StateFlag.State.DENY);
//
//        // WorldGuardのsaveを実行する
//        try {
//            manager.save();
//        } catch (ProtectionDatabaseException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 全保護領域のメンバーを更新する
     */
    public void refreshGroupMembers() {

//        // regionNames のコピー作成
//        ArrayList<String> regionNameRemain = new ArrayList<String>();
//        for ( String r : regionNames ) {
//            regionNameRemain.add(r);
//        }
//
//        // メンバーを取得して領域に再設定していく
//        RegionManager manager = wg.getRegionManager(ColorMeTeaming.getWorld(
//                ColorMeTeamingConfig.defaultWorldName));
//        Hashtable<String, ArrayList<Player>> members = ColorMeTeaming.getAllColorMembers();
//        Enumeration<String> keys = members.keys();
//
//        while ( keys.hasMoreElements() ) {
//            String key = keys.nextElement();
//            ProtectedRegion region = manager.getRegion(key + REGION_PREFIX);
//            if ( region == null ) {
//                continue;
//            }
//            region.setMembers(makeDomain(members.get(key)));
//
//            regionNameRemain.remove(key + REGION_PREFIX);
//        }
//
//        // メンバーが居ない領域は削除する
//        for ( String r : regionNameRemain ) {
//            manager.removeRegion(r);
//            regionNames.remove(r);
//        }
//
//        // WorldGuardのsaveを実行する
//        try {
//            manager.save();
//        } catch (ProtectionDatabaseException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * DefaultDomain（グループみたいなもの？）を作成する
     * @param players グループのメンバー
     * @return 作成したDefaultDomain
     */
//    private DefaultDomain makeDomain(ArrayList<Player> players) {
    private void makeDomain() {
//        DefaultDomain domain = new DefaultDomain();
//        if ( players == null || players.size() <= 0 ) {
//            return domain;
//        }
//        for ( Player p : players ) {
//            domain.addPlayer(p.getName());
//        }
//        return domain;
        return;
    }
}
