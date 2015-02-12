/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct.config;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.ucchyocean.ct.ColorTeaming;
import com.github.ucchyocean.ct.Utility;
import com.github.ucchyocean.ct.item.ItemConfigParseException;
import com.github.ucchyocean.ct.item.ItemConfigParser;

/**
 * クラスデータ
 * @author ucchy
 */
public class ClassData {

    /** キルポイント用のメタデータ名 */
    public static final String KILL_POINT_NAME = "ColorTeamingKillPoint";
    /** デスポイント用のメタデータ名 */
    public static final String DEATH_POINT_NAME = "ColorTeamingDeathPoint";

    /** ポイントの無効値を示すための数値 */
    private static final int DISABLE_POINT = -99999;

    /** タイトル */
    private String title;
    /** 説明 */
    private List<String> description;
    /** アイテムデータ */
    private List<ItemStack> items;
    /** 防具データ */
    private List<ItemStack> armors;
    /** 体力の最大値 */
    private double health;
    /** エフェクトデータ */
    private List<PotionEffect> effects;
    /** 経験値 */
    private int experience;
    /** 経験値（レベル） */
    private int level;
    /** キルしたときの得点 */
    private int killPoint;
    /** デスしたときの得点 */
    private int deathPoint;
    /** クラス設定したときに状態リセットをするかどうか */
    private boolean resetOnSetClass;

    /**
     * コンストラクタ
     */
    public ClassData() {

        this.title = "";
        this.description = null;
        this.items = null;
        this.armors = null;
        this.health = -1;
        this.effects = null;
        this.experience = -1;
        this.level = -1;
        this.killPoint = DISABLE_POINT;
        this.deathPoint = DISABLE_POINT;
        this.resetOnSetClass = false;
    }

    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return description
     */
    public List<String> getDescription() {
        return description;
    }

    /**
     * @return items
     */
    public List<ItemStack> getItems() {
        return items;
    }

    /**
     * @return armor
     */
    public List<ItemStack> getArmors() {
        return armors;
    }

    /**
     * @return health
     */
    public double getHealth() {
        return health;
    }

    /**
     * @return effects
     */
    public List<PotionEffect> getEffects() {
        return effects;
    }

    /**
     * @return experience
     */
    public int getExperience() {
        return experience;
    }

    /**
     * @return level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return killPoint
     */
    public int getKillPoint() {
        return killPoint;
    }

    /**
     * @return deathPoint
     */
    public int getDeathPoint() {
        return deathPoint;
    }

    /**
     * @param title set title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @param description set description
     */
    public void setDescription(List<String> description) {
        this.description = description;
    }

    /**
     * @param items set items
     */
    public void setItems(List<ItemStack> items) {
        this.items = items;
    }

    /**
     * @param armor set armor
     */
    public void setArmors(List<ItemStack> armors) {
        this.armors = armors;
    }

    /**
     * @param health set health
     */
    public void setHealth(double health) {
        this.health = health;
    }

    /**
     * @param effects set effects
     */
    public void setEffects(List<PotionEffect> effects) {
        this.effects = effects;
    }

    /**
     * @param experience set experience
     */
    public void setExperience(int experience) {
        this.experience = experience;
    }

    /**
     * @param level set level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * @param killPoint set killPoint
     */
    public void setKillPoint(int killPoint) {
        this.killPoint = killPoint;
    }

    /**
     * @param deathPoint set deathPoint
     */
    public void setDeathPoint(int deathPoint) {
        this.deathPoint = deathPoint;
    }

    /**
     * @return resetOnSetClass
     */
    public boolean isResetOnSetClass() {
        return resetOnSetClass;
    }

    /**
     * @param resetOnSetClass resetOnSetClass
     */
    public void setResetOnSetClass(boolean resetOnSetClass) {
        this.resetOnSetClass = resetOnSetClass;
    }

    /**
     * 全てのクラスデータファイルを、指定されたフォルダからロードします。
     * @param dir フォルダ
     * @return 全てのロードされたクラスデータ
     */
    public static HashMap<String, ClassData> loadAllClasses(File dir) {

        HashMap<String, ClassData> map = new HashMap<String, ClassData>();

        if ( dir == null || !dir.exists() ) {
            return map;
        }

        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".yml");
            }
        });

        for ( File file : files ) {
            ClassData cd = loadFromConfigFile(file);
            if ( cd != null ) {
                String name = cd.getTitle();
                map.put(name, cd);
            }
        }

        return map;
    }

    /**
     * コンフィグファイルから、クラスデータをロードします。
     * @param file コンフィグファイル
     * @return クラスデータ
     */
    public static ClassData loadFromConfigFile(File file) {

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ClassData cd = loadFromConfig(config);

        if ( cd.getTitle() == null || cd.getTitle().equals("") ) {
            String title = file.getName().substring(0, file.getName().length() - 4);
            cd.setTitle(title);
        }

        return cd;
    }

    /**
     * コンフィグファイルから、クラスデータをロードします。
     * @param config コンフィグファイル
     * @return クラスデータ
     */
    private static ClassData loadFromConfig(YamlConfiguration config) {

        ClassData cd = new ClassData();
        Logger logger = ColorTeaming.instance.getLogger();

        cd.setTitle(config.getString("title"));

        if ( config.contains("description") ) {
            cd.setDescription(config.getStringList("description"));
        }

        if ( config.contains("items") ) {
            ArrayList<ItemStack> items = new ArrayList<ItemStack>();
            ConfigurationSection itemsSection =
                    config.getConfigurationSection("items");
            for ( String sub : itemsSection.getKeys(false) ) {
                try {
                    items.add(ItemConfigParser.getItemFromSection(
                                itemsSection.getConfigurationSection(sub)));
                } catch (ItemConfigParseException e) {
                    logger.log(Level.WARNING,
                            cd.getTitle() + " - items - " + sub + " に、正しくない設定があります。", e);
                }
            }
            cd.setItems(items);
        }

        if ( config.contains("armors") ) {
            ArrayList<ItemStack> armors = new ArrayList<ItemStack>();
            ConfigurationSection armorsSection =
                    config.getConfigurationSection("armors");

            for ( String parts : new String[]{"boots", "leggings", "chestplate", "helmet"} ) {
                try {
                    armors.add(ItemConfigParser.getItemFromSection(
                            armorsSection.getConfigurationSection(parts)));
                } catch (ItemConfigParseException e) {
                    logger.log(Level.WARNING,
                            cd.getTitle() + " - armors - " + parts + " に、正しくない設定があります。", e);
                }
            }
            cd.setArmors(armors);
        }

        cd.setHealth(config.getDouble("health", -1));

        if ( config.contains("effects") ) {

            ArrayList<PotionEffect> effects = new ArrayList<PotionEffect>();
            ConfigurationSection effectsSection =
                    config.getConfigurationSection("effects");

            for ( String name : effectsSection.getKeys(false) ) {

                PotionEffectType type = PotionEffectType.getByName(name);
                if ( type == null ) {
                    logger.warning(cd.getTitle() + " - effects に指定されたエフェクト形式 " + name + " が正しくありません。");
                    continue;
                }

                int level = effectsSection.getInt(name, 1) - 1;
                effects.add(new PotionEffect(type, 1000000, level));
            }

            cd.setEffects(effects);
        }

        if ( config.contains("experience") ) {
            String temp = config.getString("experience");
            int experience = -1;
            int level = -1;
            if ( temp.toLowerCase().endsWith("l") ) {
                temp = temp.substring(0, temp.length() - 1); // 後ろの1文字を削る
                if ( temp.matches("[0-9]+") ) {
                    level = Integer.parseInt(temp);
                }
            } else {
                experience = config.getInt("experience", -1);
            }
            cd.setExperience(experience);
            cd.setLevel(level);
        }

        cd.setKillPoint(config.getInt("kill_point", DISABLE_POINT));

        cd.setDeathPoint(config.getInt("death_point", DISABLE_POINT));

        cd.setResetOnSetClass(config.getBoolean("resetOnSetClass", false));

        return cd;
    }

    /**
     * 指定されたプレイヤーに指定されたクラスを設定する
     * @param player プレイヤー
     * @return クラス設定を実行したかどうか。<br/>
     * 指定されたプレイヤーがオフラインの場合は、falseになる。
     */
    public boolean setClassToPlayer(Player player) {

        // 設定対象が居ない場合は falseを返す
        if ( player == null || !player.isOnline() ) {
            return false;
        }

        boolean needToUpdateInventory = false;

        // 全回復の実行
        boolean doReset =
                ( resetOnSetClass ||
                        ColorTeaming.instance.getCTConfig().isResetOnSetClass() );
        if ( doReset ) {
            Utility.resetPlayerStatus(player);
        }

        if ( items != null ) {

            // インベントリの消去
            player.getInventory().clear();

            // アイテムの配布
            for ( ItemStack item : items ) {
                if ( item != null ) {
                    player.getInventory().addItem(item);
                }
            }

            needToUpdateInventory = true;
        }

        if ( armors != null ) {

            // 防具の消去
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setBoots(null);

            // 防具の配布
            if (armors.size() >= 1 && armors.get(0) != null ) {
                player.getInventory().setBoots(armors.get(0));
            }
            if (armors.size() >= 2 && armors.get(1) != null ) {
                player.getInventory().setLeggings(armors.get(1));
            }
            if (armors.size() >= 3 && armors.get(2) != null ) {
                player.getInventory().setChestplate(armors.get(2));
            }
            if (armors.size() >= 4 && armors.get(3) != null ) {
                player.getInventory().setHelmet(armors.get(3));
            }

            needToUpdateInventory = true;
        }

        // インベントリ更新
        if ( needToUpdateInventory ) {
            updateInventory(player);
        }

        // 体力最大値の設定
        if ( health != -1 ) {
            player.setMaxHealth(health);
            if ( doReset ) {
                player.setHealth(health);
            }
        }

        // エフェクトの設定
        if ( effects != null ) {
            player.addPotionEffects(effects);
        }

        // 経験値の設定
        if ( experience != -1 ) {
            player.setTotalExperience(experience);
            Utility.updateExp(player);
        } else if ( level != -1 ) {
            player.setTotalExperience(0);
            Utility.updateExp(player);
            player.setLevel(level);
        }

        // メタデータの設定
        if ( killPoint != DISABLE_POINT ) {
            player.setMetadata(KILL_POINT_NAME,
                    new FixedMetadataValue(ColorTeaming.instance, killPoint));
        }
        if ( deathPoint != DISABLE_POINT ) {
            player.setMetadata(DEATH_POINT_NAME,
                    new FixedMetadataValue(ColorTeaming.instance, deathPoint));
        }

        return true;
    }

    /**
     * プレイヤーの状態から、クラス情報をエクスポートする。
     * @param player エクスポート元のプレイヤー
     * @param name エクスポート先のクラス名
     * @param isOverwrite 防具やインベントリがからっぽでも、設定を上書きするかどうか
     * @return エクスポートに成功したかどうか
     */
    public static boolean exportClassFromPlayer(Player player, String name, boolean isOverwrite) {

        YamlConfiguration config = new YamlConfiguration();

        updateInventory(player); // 念のため
        PlayerInventory inv = player.getInventory();

        config.set("title", name);

        if ( countItem(inv.getContents()) > 0 ) {

            ConfigurationSection sub = config.createSection("items");

            int index = 1;

            for ( ItemStack item : inv.getContents() ) {
                if ( item != null && item.getType() != Material.AIR ) {
                    ConfigurationSection itemsec = sub.createSection("item" + index);
                    index++;
                    ItemConfigParser.setItemToSection(itemsec, item);
                }
            }

        } else if ( isOverwrite ) {
            config.set("items.item1.material", "AIR");

        }

        if ( countItem(inv.getArmorContents()) > 0 ) {

            ConfigurationSection sub = config.createSection("armors");

            String[] armorNames = new String[]{"boots", "leggings", "chestplate", "helmet"};

            for ( int i=0; i<4; i++ ) {
                ItemStack item = inv.getArmorContents()[i];
                if ( item != null && item.getType() != Material.AIR ) {
                    ConfigurationSection itemsec = sub.createSection(armorNames[i]);
                    ItemConfigParser.setItemToSection(itemsec, item);
                }
            }

        } else if ( isOverwrite ) {
            config.set("armors.helmet.material", "AIR");

        }

        if ( player.getMaxHealth() != 20.0 ) {
            config.set("health", player.getMaxHealth());
        }

        if ( !player.getActivePotionEffects().isEmpty() ) {
            ConfigurationSection sub = config.createSection("effects");
            for ( PotionEffect effect : player.getActivePotionEffects() ) {
                sub.set(effect.getType().getName(), (effect.getAmplifier() + 1));
            }
        }

        if ( player.getTotalExperience() > 0 ) {
            config.set("experience", player.getTotalExperience());
        }

        // ファイルへ書き込み
        File folder = new File(ColorTeaming.instance.getDataFolder(), "classes");
        if ( !folder.exists() ) {
            folder.mkdirs();
        }

        File file = new File(folder, name + ".yml");
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // ロードして、オンメモリにも反映する。
        ClassData cd = loadFromConfigFile(file);
        ColorTeaming.instance.getAPI().setClassData(cd);

        return true;
    }

    /**
     * ItemStackの配列で、有効なアイテムが設定されている個数を調べて返す
     * @param items
     * @return
     */
    private static int countItem(ItemStack[] items) {

        int count = 0;
        for ( ItemStack item : items ) {
            if ( item != null && item.getType() != Material.AIR ) {
                count++;
            }
        }
        return count;
    }

    /**
     * プレイヤーのインベントリを更新します。
     * @param player
     */
    @SuppressWarnings("deprecation")
    private static void updateInventory(Player player) {
        player.updateInventory();
    }
}
