/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.ucchyocean.ct.bridge.VaultChatBridge;
import com.github.ucchyocean.ct.command.CChatCommand;
import com.github.ucchyocean.ct.command.CChatGlobalCommand;
import com.github.ucchyocean.ct.command.CClassCommand;
import com.github.ucchyocean.ct.command.CCountCommand;
import com.github.ucchyocean.ct.command.CExplodeCommand;
import com.github.ucchyocean.ct.command.CFriendlyFireCommand;
import com.github.ucchyocean.ct.command.CGiveCommand;
import com.github.ucchyocean.ct.command.CJoinCommand;
import com.github.ucchyocean.ct.command.CLeaderCommand;
import com.github.ucchyocean.ct.command.CLeaveCommand;
import com.github.ucchyocean.ct.command.CPointCommand;
import com.github.ucchyocean.ct.command.CRandomCommand;
import com.github.ucchyocean.ct.command.CRemoveCommand;
import com.github.ucchyocean.ct.command.CSpawnCommand;
import com.github.ucchyocean.ct.command.CTPCommand;
import com.github.ucchyocean.ct.command.CTeamingCommand;
import com.github.ucchyocean.ct.config.ColorTeamingConfig;
import com.github.ucchyocean.ct.listener.PlayerChatListener;
import com.github.ucchyocean.ct.listener.PlayerDeathListener;
import com.github.ucchyocean.ct.listener.PlayerJoinQuitListener;
import com.github.ucchyocean.ct.listener.PlayerRespawnListener;

/**
 * 簡易PVPチーミングプラグイン
 * @author ucchy
 */
public class ColorTeaming extends JavaPlugin {

    private static final String TEAM_PERMISSION_PREFIX = "colorteaming.teammember.";

    public static ColorTeaming instance;
    protected ColorTeamingConfig config;
    private ColorTeamingManager manager;
    private HashMap<String, TabExecutor> commands;

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    @Override
    public void onEnable() {

        instance = this;

        // 設定の読み込み処理
        config = ColorTeamingConfig.loadConfig();

        // VaultChatをロード
        VaultChatBridge vaultchat = null;
        if ( getServer().getPluginManager().isPluginEnabled("Vault") ) {
            vaultchat = VaultChatBridge.load(
                    getServer().getPluginManager().getPlugin("Vault"));
        }

        // クラスフォルダが存在しない場合は、jarファイルの中からデフォルトをコピーする
        File classDir = new File(getDataFolder(), "classes");
        if ( !classDir.exists() || !classDir.isDirectory() ) {
            classDir.mkdirs();
            Utility.copyFolderFromJar(getFile(), classDir, "classes");
        }

        // マネージャの初期化
        manager = new ColorTeamingManager(this, config, vaultchat);

        // コマンドをサーバーに登録
        commands = new HashMap<String, TabExecutor>();
        commands.put("colorcount", new CCountCommand(this));
        commands.put("colorfriendlyfire", new CFriendlyFireCommand(this));
        commands.put("colorchat", new CChatCommand(this));
        commands.put("colorglobal", new CChatGlobalCommand());
        commands.put("colorleader", new CLeaderCommand(this));
        commands.put("colortp", new CTPCommand(this));
        commands.put("colorclass", new CClassCommand(this));
        commands.put("colorpoint", new CPointCommand(this));
        commands.put("colorspawn", new CSpawnCommand(this));
        commands.put("colorrandom", new CRandomCommand(this));
        commands.put("colorremove", new CRemoveCommand(this));
        commands.put("colorexplode", new CExplodeCommand(this));
        commands.put("colorgive", new CGiveCommand(this));
        commands.put("colorjoin", new CJoinCommand(this));
        commands.put("colorleave", new CLeaveCommand(this));
        commands.put("colorteaming", new CTeamingCommand(this));

        // イベント購読をサーバーに登録
        getServer().getPluginManager().registerEvents(new PlayerChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(this), this);
    }

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {
        return commands.get(command.getName()).onCommand(sender, command, label, args);
    }

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public List<String> onTabComplete(
            CommandSender sender, Command command, String label, String[] args) {
        return commands.get(command.getName()).onTabComplete(sender, command, label, args);
    }

    /**
     * このプラグインのJarファイル自身を示すFileクラスを返す。
     * @return
     */
    public File getPluginJarFile() {
        return this.getFile();
    }

    /**
     * ColorTeamingConfig を取得する
     * @return ColorTeamingConfig
     */
    public ColorTeamingConfig getCTConfig() {
        return config;
    }

    /**
     * ColorTeamingAPI を取得する
     * @return ColorTeamingAPI
     */
    public ColorTeamingAPI getAPI() {
        return manager;
    }

    /**
     * 対象のプレイヤーに、チームメンバー用の権限を与える。
     * @param player プレイヤー
     * @param teamID チームID
     */
    public void addMemberPermission(Player player, String teamID) {

        removeAllMemberPermission(player);
        player.addAttachment(this, TEAM_PERMISSION_PREFIX + teamID, true);
    }

    /**
     * 対象のプレイヤーの、チームメンバー用の権限を全て剥奪する。
     * @param player プレイヤー
     */
    public void removeAllMemberPermission(Player player) {

        for ( PermissionAttachmentInfo info : player.getEffectivePermissions() ) {
            if ( info.getPermission().startsWith(TEAM_PERMISSION_PREFIX) ) {
                player.removeAttachment(info.getAttachment());
            }
        }
    }
}
