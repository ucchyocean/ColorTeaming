/*
 * Copyright ucchy 2012
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
import java.util.ArrayList;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author ucchy
 * ユーティリティクラス
 */
public class Utility {

    private static final String[] VALID_COLORS = {
        "red", "blue", "yellow", "green", "aqua", "gray", "dark_red",
        "dark_green", "dark_aqua", "black", "dark_blue", "dark_gray",
        "dark_purple", "gold", "light_purple", "white"
    };

    /**
     * jarファイルの中に格納されているファイルを、jarファイルの外にコピーするメソッド
     * @param jarFile jarファイル
     * @param targetFile コピー先
     * @param sourceFilePath コピー元
     * @param isBinary バイナリファイルかどうか
     */
    public static void copyFileFromJar(
            File jarFile, File targetFile, String sourceFilePath, boolean isBinary) {

        InputStream is = null;
        FileOutputStream fos = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;

        File parent = targetFile.getParentFile();
        if ( !parent.exists() ) {
            parent.mkdirs();
        }

        try {
            JarFile jar = new JarFile(jarFile);
            ZipEntry zipEntry = jar.getEntry(sourceFilePath);
            is = jar.getInputStream(zipEntry);

            fos = new FileOutputStream(targetFile);

            if ( isBinary ) {
                byte[] buf = new byte[8192];
                int len;
                while ( (len = is.read(buf)) != -1 ) {
                    fos.write(buf, 0, len);
                }

            } else {
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                writer = new BufferedWriter(new OutputStreamWriter(fos));

                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();
                }

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

    /**
     * 文字列内のカラーコードを置き換えする
     * @param source 置き換え元の文字列
     * @return 置き換え後の文字列
     */
    public static String replaceColorCode(String source) {

        return source.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
    }

    /**
     * 文字列が整数値に変換可能かどうかを判定する
     * @param source 変換対象の文字列
     * @return 整数に変換可能かどうか
     */
    public static boolean tryIntParse(String source) {

        return source.matches("^-?[0-9]+$");
    }

    /**
     * ColorMeの色設定を、ChatColorクラスに変換する
     * @param color ColorMeの色設定
     * @return ChatColorクラス
     */
    public static ChatColor replaceColors(String color) {

        if ( isValidColor(color) ) {
            return ChatColor.valueOf(color.toUpperCase());
        }
        return ChatColor.WHITE;
    }

    /**
     * ColorMeで指定可能な色かどうかを判断する
     * @param color ColorMeの色設定
     * @return 指定可能かどうか
     */
    public static boolean isValidColor(String color) {

        for ( String s : VALID_COLORS ) {
            if ( s.equals(color) ) {
                return true;
            }
        }
        return false;
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
     * YamlファイルをJarの中から読み込んで、ヘッダー部分を返す
     * @param name 読み込むファイル
     * @return ヘッダー部分
     */
    public static String getYamlHeader(String name) {

        ArrayList<String> contents = getContentsFromJar(name);
        StringBuilder results = new StringBuilder();
        for ( String line : contents ) {
            if ( !line.startsWith("#") ) {
                break;
            }
            results.append(line + "\n");
        }
        return results.toString();
    }


    /**
     * Jarファイル内から指定したymlファイルを直接読み込み、内容を返すメソッド
     * @param ymlファイルの名前
     * @return ファイルの内容
     * @deprecated このメソッドでUTF-8のYamlを読み込んではいけない
     */
    public static YamlConfiguration getYamlFromJar(String name) {

        YamlConfiguration config = new YamlConfiguration();
        JarFile jarFile = null;
        InputStream inputStream = null;
        try {
            jarFile = new JarFile(ColorTeaming.instance.getPluginJarFile());
            ZipEntry zipEntry = jarFile.getEntry(name);
            inputStream = jarFile.getInputStream(zipEntry);
            config = YamlConfiguration.loadConfiguration(inputStream);
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

        return config;
    }
}
