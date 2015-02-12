/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.ct;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author ucchy
 * ユーティリティクラス
 */
public class Utility {

    /**
     * jarファイルの中に格納されているテキストファイルを、jarファイルの外にコピーするメソッド<br/>
     * WindowsだとS-JISで、MacintoshやLinuxだとUTF-8で保存されます。
     * @param jarFile jarファイル
     * @param targetFile コピー先
     * @param sourceFilePath コピー元
     */
    public static void copyFileFromJar(
            File jarFile, File targetFile, String sourceFilePath) {

        JarFile jar = null;
        InputStream is = null;
        FileOutputStream fos = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;

        File parent = targetFile.getParentFile();
        if ( !parent.exists() ) {
            parent.mkdirs();
        }

        try {
            jar = new JarFile(jarFile);
            ZipEntry zipEntry = jar.getEntry(sourceFilePath);
            is = jar.getInputStream(zipEntry);

            fos = new FileOutputStream(targetFile);

            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            writer = new BufferedWriter(new OutputStreamWriter(fos));

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ( jar != null ) {
                try {
                    jar.close();
                } catch (IOException e) {
                    // do nothing.
                }
            }
            if ( writer != null ) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    // do nothing.
                }
            }
            if ( reader != null ) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // do nothing.
                }
            }
            if ( fos != null ) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    // do nothing.
                }
            }
            if ( is != null ) {
                try {
                    is.close();
                } catch (IOException e) {
                    // do nothing.
                }
            }
        }
    }

    /**
     * jarファイルの中に格納されているフォルダを、中のファイルごとまとめてjarファイルの外にコピーするメソッド<br/>
     * テキストファイルは、WindowsだとS-JISで、MacintoshやLinuxだとUTF-8で保存されます。
     * @param jarFile jarファイル
     * @param targetFile コピー先のフォルダ
     * @param sourceFilePath コピー元のフォルダ
     */
    public static void copyFolderFromJar(
            File jarFile, File targetFilePath, String sourceFilePath) {

        JarFile jar = null;

        if ( !targetFilePath.exists() ) {
            targetFilePath.mkdirs();
        }

        try {
            jar = new JarFile(jarFile);
            Enumeration<JarEntry> entries = jar.entries();

            while ( entries.hasMoreElements() ) {

                JarEntry entry = entries.nextElement();
                if ( !entry.isDirectory() && entry.getName().startsWith(sourceFilePath) ) {

                    File targetFile = new File(targetFilePath,
                            entry.getName().substring(sourceFilePath.length() + 1));
                    if ( !targetFile.getParentFile().exists() ) {
                        targetFile.getParentFile().mkdirs();
                    }

                    InputStream is = null;
                    FileOutputStream fos = null;
                    BufferedReader reader = null;
                    BufferedWriter writer = null;

                    try {
                        is = jar.getInputStream(entry);
                        reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                        fos = new FileOutputStream(targetFile);
                        writer = new BufferedWriter(new OutputStreamWriter(fos));

                        String line;
                        while ((line = reader.readLine()) != null) {
                            writer.write(line);
                            writer.newLine();
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if ( writer != null ) {
                            try {
                                writer.flush();
                                writer.close();
                            } catch (IOException e) {
                                // do nothing.
                            }
                        }
                        if ( reader != null ) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                // do nothing.
                            }
                        }
                        if ( fos != null ) {
                            try {
                                fos.flush();
                                fos.close();
                            } catch (IOException e) {
                                // do nothing.
                            }
                        }
                        if ( is != null ) {
                            try {
                                is.close();
                            } catch (IOException e) {
                                // do nothing.
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ( jar != null ) {
                try {
                    jar.close();
                } catch (IOException e) {
                    // do nothing.
                }
            }
        }
    }

    /**
     * Jarファイル内から指定したファイルを直接読み込み、内容を返すメソッド
     * @return ファイルの内容
     */
    public static ArrayList<String> getContentsFromJar(String name) {

        ArrayList<String> contents = new ArrayList<String>();
        JarFile jarFile = null;
        InputStream inputStream = null;
        try {
            jarFile = new JarFile(ColorTeaming.instance.getPluginJarFile());
            ZipEntry zipEntry = jarFile.getEntry(name);
            inputStream = jarFile.getInputStream(zipEntry);
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;
            while ( (line = reader.readLine()) != null ) {
                contents.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ( jarFile != null ) {
                try {
                    jarFile.close();
                } catch (IOException e) {
                    // do nothing.
                }
            }
            if ( inputStream != null ) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // do nothing.
                }
            }
        }

        return contents;
    }

    /**
     * 文字列内のカラーコードを置き換えする
     * @param source 置き換え元の文字列
     * @return 置き換え後の文字列
     */
    public static String replaceColorCode(String source) {

        return ChatColor.translateAlternateColorCodes('&', source);
    }

    /**
     * 文字列が整数値に変換可能かどうかを判定する
     * @param source 変換対象の文字列
     * @return 整数に変換可能かどうか
     */
    public static boolean checkIntParse(String source) {

        return source.matches("^-?[0-9]{1,9}$");
    }

    /**
     * カラーコードをChatColorに変換する
     * @param colorCode カラーコード
     * @return ChatColorオブジェクト
     */
    public static ChatColor getChatColorFromColorCode(String colorCode) {

        if ( !colorCode.matches("&[0-9a-fk-or]") ) {
            return ChatColor.WHITE;
        }

        char code = colorCode.charAt(1);
        return ChatColor.getByChar(code);
    }

    /**
     * プレイヤーの状態リセットを行う
     * @param player 対象プレイヤー
     */
    public static void resetPlayerStatus(final Player player) {

        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setFallDistance(0);
        player.setRemainingAir(player.getMaximumAir());

        // ポーション効果の除去は、ここでは行わない。see issue #120
//        Collection<PotionEffect> effects = player.getActivePotionEffects();
//        for ( PotionEffect e : effects ) {
//            player.removePotionEffect(e.getType());
//        }

        // NOTE: ゲームオーバー画面をスキップする場合、Fire ticks は少し遅れて設定しないと火が消えない。
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setFireTicks(0);
            }
        }.runTaskLater(ColorTeaming.instance, 1L);
    }

    /**
     * 経験値表示を更新する
     * @param player 更新対象のプレイヤー
     */
    public static void updateExp(Player player) {

        int total = player.getTotalExperience();
        player.setLevel(0);
        player.setExp(0);
        while ( total > player.getExpToLevel() ) {
            total -= player.getExpToLevel();
            player.setLevel(player.getLevel()+1);
        }
        float xp = (float)total / (float)player.getExpToLevel();
        player.setExp(xp);
    }

    /**
     * 指定された名前のプレイヤーを返す
     * @param name プレイヤー名
     * @return プレイヤー、該当プレイヤーがオンラインでない場合はnullになる。
     */
    @SuppressWarnings("deprecation")
    public static Player getPlayerExact(String name) {
        return Bukkit.getPlayerExact(name);
    }

    /**
     * 現在動作中のCraftBukkitが、v1.7.8 以上かどうかを確認する
     * @return v1.7.8以上ならtrue、そうでないならfalse
     */
    public static boolean isCB178orLater() {

        int[] borderNumbers = {1, 7, 8};

        String version = Bukkit.getBukkitVersion();
        int hyphen = version.indexOf("-");
        if ( hyphen > 0 ) {
            version = version.substring(0, hyphen);
        }

        String[] versionArray = version.split("\\.");
        int[] versionNumbers = new int[versionArray.length];
        for ( int i=0; i<versionArray.length; i++ ) {
            if ( !versionArray[i].matches("[0-9]+") )
                return false;
            versionNumbers[i] = Integer.parseInt(versionArray[i]);
        }

        int index = 0;
        while ( (versionNumbers.length > index) && (borderNumbers.length > index) ) {
            if ( versionNumbers[index] > borderNumbers[index] ) {
                return true;
            } else if ( versionNumbers[index] < borderNumbers[index] ) {
                return false;
            }
            index++;
        }
        if ( borderNumbers.length == index ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 現在接続中のプレイヤーを全て取得する
     * @return 接続中の全てのプレイヤー
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Player> getOnlinePlayers() {
        // CB179以前と、CB1710以降で戻り値が異なるため、
        // リフレクションを使って互換性を（無理やり）保つ。
        try {
            if (Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).getReturnType() == Collection.class) {
                Collection<?> temp = ((Collection<?>)Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).invoke(null, new Object[0]));
                return new ArrayList<Player>((Collection<? extends Player>)temp);
            } else {
                Player[] temp = ((Player[])Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).invoke(null, new Object[0]));
                ArrayList<Player> players = new ArrayList<Player>();
                for ( Player t : temp ) {
                    players.add(t);
                }
                return players;
            }
        }
        catch (NoSuchMethodException ex){} // never happen
        catch (InvocationTargetException ex){} // never happen
        catch (IllegalAccessException ex){} // never happen
        return new ArrayList<Player>();
    }
}
