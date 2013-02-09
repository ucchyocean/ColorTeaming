/*
 * Copyright ucchy 2012
 */
package com.github.ucchyocean.cmt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.bukkit.ChatColor;

/**
 * @author ucchy
 * ユーティリティクラス
 */
public class Utility {


    /**
     * jarファイルの中に格納されているファイルを、jarファイルの外にコピーするメソッド
     * @param jarFile jarファイル
     * @param targetFile コピー先
     * @param sourceFilePath コピー元
     * @param isBinary バイナリファイルかどうか
     */
    protected static void copyFileFromJar(
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
                fos.flush();
                fos.close();
                is.close();

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

        if (color.equalsIgnoreCase("red")) {
            return ChatColor.RED;
        } else if (color.equalsIgnoreCase("blue")) {
            return ChatColor.BLUE;
        } else if (color.equalsIgnoreCase("yellow")) {
            return ChatColor.YELLOW;
        } else if (color.equalsIgnoreCase("green")) {
            return ChatColor.GREEN;
        } else if (color.equalsIgnoreCase("aqua")) {
            return ChatColor.AQUA;
        } else if (color.equalsIgnoreCase("gray")) {
            return ChatColor.GRAY;
        } else if (color.equalsIgnoreCase("dark_red")) {
            return ChatColor.DARK_RED;
        } else if (color.equalsIgnoreCase("dark_green")) {
            return ChatColor.DARK_GREEN;
        } else if (color.equalsIgnoreCase("dark_aqua")) {
            return ChatColor.DARK_AQUA;
        } else if (color.equalsIgnoreCase("black")) {
            return ChatColor.BLACK;
        } else if (color.equalsIgnoreCase("dark_blue")) {
            return ChatColor.DARK_BLUE;
        } else if (color.equalsIgnoreCase("dark_gray")) {
            return ChatColor.DARK_GRAY;
        } else if (color.equalsIgnoreCase("dark_purple")) {
            return ChatColor.DARK_PURPLE;
        } else if (color.equalsIgnoreCase("gold")) {
            return ChatColor.GOLD;
        } else if (color.equalsIgnoreCase("light_purple")) {
            return ChatColor.LIGHT_PURPLE;
        } else {
            return ChatColor.WHITE;
        }
    }
}
