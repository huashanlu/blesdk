/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.yuntongxun.ble.core.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcel;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.yuntongxun.ble.YYError;
import com.yuntongxun.ble.log.YYLogger;

import junit.framework.Assert;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * SDK工具类
 * @author 容联•云通讯
 * @since 2015-s3-5
 * @version 4.0
 */
public class ECSDKUtils {

    public static final String TAG = YYLogger.getLogger(ECSDKUtils.class);

    /**一秒所对应的毫秒值*/
    public static final long MILLSECONDS_OF_SECOND = 1000L;
    /**一分钟所对应的毫秒值*/
    public static final long MILLSECONDS_OF_MINUTE = 60000L;
    /**一小时所对应的毫秒值*/
    public static final long MILLSECONDS_OF_HOUR = 3600000L;
    /**一天所对应的毫秒值*/
    public static final long MILLSECONDS_OF_DAY = 86400000L;

    /**一分钟所对应的秒值*/
    public static final long SECOND_OF_MINUTE = 60L;
    /**一小时60分钟*/
    public static final long MINUTE_OF_HOUR = 60L;

    public static final int BIT_OF_KB = 10;
    public static final int BIT_OF_MB = 20;
    public static final int BYTE_OF_KB = 1024;
    public static final int BYTE_OF_MB = 1048576;

    /**手机震动参数*/
    private static final long[] pattern = { 300L, 200L, 300L, 200L };
    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");

    private static final char[] tabChar = { '\t', '\n', '\r' };
    private static final char[] bTransfer = { '<', '>', '"', '\'', '&' };
    private static final String[] transferResult = { "&lt;", "&gt;", "&quot;", "&apos;", "&amp;" };

    /**
     * 过滤字符
     * @param sqlValue
     * @return
     */
    public static String escapeSqlValue(String sqlValue) {
        if (sqlValue != null)
            sqlValue = sqlValue.replace("\\[", "[[]").replace("%", "")
                    .replace("\\^", "").replace("'", "").replace("\\{", "")
                    .replace("\\}", "").replace("\"", "");
        return sqlValue;
    }

    /**
     * 将集合转换成字符串，用特殊字符做分隔符
     * @param srcList 转换前集合
     * @param separator 分隔符
     * @return 字符串
     */
    public static String listToString(List<String> srcList, String separator) {
        if (srcList == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < srcList.size(); ++i)
            if (i == srcList.size() - 1) {
                sb.append(((String) srcList.get(i)).trim());
            } else {
                sb.append(((String) srcList.get(i)).trim() + separator);
            }
        return sb.toString();
    }

    /**
     * 将集合转换成字符串，用特殊字符做分隔符
     * @param srcList 转换前集合
     * @param separator 分隔符
     * @return 字符串
     */
    public static String intListToString(List<Integer> srcList, String separator) {
        if (srcList == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < srcList.size(); ++i)
            if (i == srcList.size() - 1) {
                sb.append((srcList.get(i)));
            } else {
                sb.append(( srcList.get(i)) + separator);
            }
        return sb.toString();
    }

    public static YYError buildNoError() {

       return   buildError(0);
    }

    public static YYError buildError(int state) {
        return buildError(state, "");
    }

    public static YYError buildError(int state , String errMsg) {
        return new YYError(state,errMsg);
    }


    /**
     * 数组转字符串
     * @param srcArray
     * @param separator
     * @return
     */
    public static String arrayToString(String[] srcArray, String separator) {
        if (srcArray == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < srcArray.length; ++i)
            if (i == srcArray.length - 1) {
                sb.append((srcArray[i]).trim());
            } else {
                sb.append((srcArray[i]).trim() + separator);
            }
        return sb.toString();
    }

    /**
     * 数组转字符串
     * @param srcArray
     * @param separator
     * @return
     */
    public static String arrayToString(int[] srcArray, String separator) {
        if (srcArray == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < srcArray.length; ++i)
            if (i == srcArray.length - 1) {
                sb.append((srcArray[i]));
            } else {
                sb.append((srcArray[i]) + separator);
            }
        return sb.toString();
    }

    /**
     * 将字符串数组转换成字符串集合o
     * @param src
     * @return
     */
    public static List<String> stringsToList(String[] src) {
        if ((src == null) || (src.length == 0)) {
            return null;
        }
        ArrayList<String> dest = new ArrayList<String>();
        for (int i = 0; i < src.length; ++i) {
            dest.add(src[i]);
        }
        return dest;
    }

    /**
     * 根据给定的区间返回随机数
     * @param max 最大值
     * @param min 最小值
     * @return
     */
    public static int getIntRandom(int max, int min) {
        Assert.assertTrue(max > min);
        return (new Random(System.currentTimeMillis()).nextInt(max - min + 1) + min);
    }

    /**
     * 判断是否白天 6点 -- 18点
     * @return
     */
    public static boolean isDayTimeNow(){
        int dayTime = new GregorianCalendar().get(Calendar.HOUR_OF_DAY);
        return ((dayTime >= 6L) && (dayTime < 18L));

    }

    /**
     * 判断是否夜间
     * @param time
     * @param start
     * @param end
     * @return
     */
    public static boolean isNightTime(int time, int start, int end) {
        if (start > end) {
            return ((time < start) && (time > end));
        }
        if (start < end) {
            return ((time > end) || (time < start));
        }
        return true;
    }

    /**
     *
     * @return
     */
    public static String getTimeZoneOffset() {
        TimeZone tZone = TimeZone.getDefault();
        double d = tZone.getRawOffset() * 100 / MILLSECONDS_OF_HOUR / 100.0D
                + ((tZone.useDaylightTime()) ? 1 : 0);
        return String.format("%.2f", new Object[]{Double.valueOf(d)});
    }

    /**
     * 格式化时间
     * @param time
     * @return
     */
    public static String formatUnixTime(long time) {
        return new SimpleDateFormat("[yy-MM-dd HH:mm:ss]").format(new Date(time * MILLSECONDS_OF_SECOND));
    }

    public static String formatUnixTime(long time , SimpleDateFormat s) {
        return s.format(new Date(time));
    }

    /**
     * 转换毫秒
     * @param source
     * @return
     */
    public static long getTimeMills(String source) {
        if(isNullOrNil(source)) {
            return 0;
        }
        long mills = 0;
        try {
            mills = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(source).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mills;
    }

    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return "";
    }


    /**
     * 获取文件名
     * @param pathName
     * @return
     */
    public static String getFileName(String pathName) {
        if(isNullOrNil(pathName)) {
            return "";
        }
        int start = pathName.lastIndexOf("/");
        if (start != -1) {
            return pathName.substring(start + 1, pathName.length());
        }
        return pathName;

    }

    /**
     * 将yy-MM-dd HH:mm:ss 转毫秒
     * @param time
     * @return
     */
    public static long formatTime(String time){
        try {

            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sf.parse(time).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            YYLogger.e(TAG, "formatTime " + time + " to long error " + e.getLocalizedMessage());
        }
        return System.currentTimeMillis();
    }

    /**
     * 转换
     * @param time
     * @param fomart
     * @return
     */
    public static long formatTime(String time , String fomart){
        try {

            SimpleDateFormat sf = new SimpleDateFormat(fomart);
            return sf.parse(time).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            YYLogger.e(TAG, "formatTime " + time + " to long error " + e.getLocalizedMessage());
        }
        return System.currentTimeMillis();
    }



    /**
     * 判断是否包含中文字符
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock unicodeBlock = Character.UnicodeBlock.of(c);
        return ((unicodeBlock != Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
                && (unicodeBlock != Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS)
                && (unicodeBlock != Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A)
                && (unicodeBlock != Character.UnicodeBlock.GENERAL_PUNCTUATION)
                && (unicodeBlock != Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION)
                && (unicodeBlock != Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS));
    }

    /**
     * 判断是否是字母
     * @param alpha 字符
     * @return 字母
     */
    public static boolean isAlpha(char alpha) {
        return ((((alpha < 'a') || (alpha > 'z'))) && (((alpha < 'A') || (alpha > 'Z'))));
    }

    /**
     * 判断是否是数字 [0 - 9]
     * @param number 字符
     * @return 是否是数字
     */
    public static boolean isNum(char number) {
        return ((number < '0') || (number > '9'));
    }

    /**
     * 验证邮件地址的合法性
     * @param str 账号
     * @return 是否邮箱
     */
    public static boolean isValidEmail(String str) {
        if (isNullOrNil(str)) {
            return false;
        }
        return str.trim() .matches( "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$");
    }

    /**
     * 匹配ASCII范围/是否中英文
     * s1、匹配中文字符的正则表达式： [\x00-\x7F\u4e00-\u9fa5\uFE30-\uFFA0a-zA-Z、]
     * s2、匹配部分中文标点符号 ： [\u3002\uff1b\uff0c\uff1a\u201c\u201d\uff08\uff09\u3001\uff1f\u300a\u300b]
     * @param str 待验证
     * @return 是否匹配
     */
    public static boolean isValidASCIIChineseEnglish(String str) {
        if (isNullOrNil(str)) {
            return false;
        }
        return str.trim() .matches("^[\\u3002\\uff1b\\uff0c\\uff1a\\u201c\\u201d\\uff08\\uff09\\u3001\\uff1f\\u300a\\u300b \\x00-\\x7F\\u4e00-\\u9fa5\\uFE30-\\uFFA0a-zA-Z、]+") /*|| chineseSymbol(str)*/;
    }

    /**
     * 部分中文标点符号
     * @param str 待验证
     * @return 是否匹配
     */
    public static boolean chineseSymbol(String str) {
        if (isNullOrNil(str)) {
            return false;
        }
        return str.trim() .matches("^[\\u3002\\uff1b\\uff0c\\uff1a\\u201c\\u201d\\uff08\\uff09\\u3001\\uff1f\\u300a\\u300b]+");
    }


    /**
     * 匹配ASCII范围
     * @param str 待验证
     * @return 是否匹配
     */
    public static boolean isASCII(String str) {
        if (isNullOrNil(str)) {
            return false;
        }
        return str.trim() .matches("^[\\x00-\\x7F]+");
    }

    /**
     * 是否中英文
     * @param str 待验证
     * @return 是否匹配
     */
    public static boolean isChineseEnglish(String str) {
        if (isNullOrNil(str)) {
            return false;
        }
        return str.trim().matches("^[\\u4e00-\\u9fa5a-zA-Z]+");
    }

    /**
     * Bitmap转换成字节数组
     * @param bitmap
     * @param filter 是否回收原Bitmap对象
     * @return
     */
    public static byte[] bmpToByteArray(Bitmap bitmap, boolean filter) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        if (filter) {
            bitmap.recycle();
        }
        byte[] byteArray = bos.toByteArray();
        try {
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    /**
     * 检验账号的有效性
     * @param account 账号
     * @return 是否非法
     */
    public static boolean isValidAccount(String account) {
        if (account == null) {
            return false;
        }
        if (((account = account.trim()).length() < 6)
                || (account.length() > 20)) {
            return false;
        }
        if (!(isAlpha(account.charAt(0)))) {
            return false;
        }
        for (int i = 0; i < account.length(); ++i) {
            char c = account.charAt(i);
            if ((!(isAlpha(c))) && (!(isNum(c)))
                    && (c != '-') && (c != '_')) {
                return false;
            }
        }
        return true;
    }

    /**
     * 验证云通讯注册账号是否非法
     * 仅能输入英文、下划线、@、数字。
     * @param account 账号
     * @return 是否非法
     */
    public static boolean isValidNormalAccount(String account) {
        if (account == null) {
            return false;
        }

        if (((account = account.trim()).length() < 1)
                || (account.length() > 64)) {
            return false;
        }

        //return isValidEmail(account) || account.trim().matches("^[A-Za-z0-9\\.][\\w_\\-\\.]*[a-zA-Z0-9\\.]$");
        return isValidAccount(account) || account.trim().matches("^[A-Za-z0-9\\w_\\-\\.]+$");
    }

    /**
     * 验证密码的合法性（密码不能为纯数字）
     * @param pwd
     * @return
     */
    public static boolean isValidPassword(String pwd) {
        if (pwd == null) {
            return false;
        }
        if (pwd.length() < 4) {
            return false;
        }
        if (pwd.length() >= 9) {
            return true;
        }
        try {
            Integer.parseInt(pwd);
            return false;
        } catch (NumberFormatException nfe) {
        }
        return true;
    }

    /**
     * 根据给定路径获取图片的分辨率大小
     * @param pathName
     * @return
     */
    public static BitmapFactory.Options getImageOptions(String pathName) {
        Assert.assertTrue((pathName != null) && (!(pathName.equals(""))));
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true;
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(pathName, option);
            if (bitmap != null) {
                bitmap.recycle();
            }
        } catch (OutOfMemoryError oom) {
            YYLogger.e(TAG, "decode bitmap failed: " + oom.getMessage());
        }
        return option;
    }

    /**
     * 保存图像到本地
     * @param srcBitmap 将要保存的图片
     * @param quality 图像的质量
     * @param format 图像的格式
     * @param folder 文件夹
     * @param pathName 文件名称
     * @param filter 是否回收图片
     */
    public static void saveBitmapToImage(Bitmap srcBitmap, int quality,
                                         Bitmap.CompressFormat format, String folder,
                                         String pathName, boolean filter) {
        Assert.assertTrue((folder != null) && (pathName != null));
        YYLogger.d(TAG, "saving to " + folder + pathName);
        File imageFile = new File(folder + pathName);
        FileOutputStream fos = null;
        try {
            imageFile.createNewFile();
            fos = new FileOutputStream(imageFile);
            srcBitmap.compress(format, quality, fos);
            fos.flush();

            if(filter) {
                srcBitmap.recycle();
            }
            return;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 格式化文件大小输出
     * @param length
     * @return
     */
    public static String getSizeKB(long length) {
        if (length >> 20 > 0L) {
            return getSizeMB(length);
        }
        if (length >> 9 > 0L) {
            float size = Math.round((float) length * 10.0F / BYTE_OF_KB) / 10.0F;
            return size + "KB";
        }
        return length + "B";
    }

    /**
     * 格式化文件大小输出
     * @param length
     * @return
     */
    public static String getSizeMB(long length) {
        float size = Math.round((float) length * 10.0F / BYTE_OF_MB) / 10.0F;
        return size + "MB";
    }

    /**
     * 数组转换成字符串，用","分隔
     * @param src
     * @return
     */
    public static String dumpArray(Object[] src) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : src) {
            sb.append(obj);
            sb.append(",");
        }
        return sb.toString();
    }

    /**
     * 遍历hex
     * @param hex
     * @return
     */
    public static String dumpHex(byte[] hex) {
        if(hex == null) {
            return "(null)";
        }
        int srcLength = hex.length;
        char[] sourceHex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        char[] dump = new char[ srcLength * 3 + srcLength / 16];
        int destIndex = 0;
        for(int index = 0 ; index < srcLength ; index ++) {
            int b = hex[index];
            dump[(destIndex++)] = ' ';
            dump[(destIndex++)] = sourceHex[(b >>> 4 & 0xF)];
            dump[(destIndex++)] = sourceHex[(b & 0xF)];
            if ((index % 16 != 0) || (index <= 0)) {
                continue;
            }
            dump[(destIndex++)] = '\n';
        }
        return new String(dump);
    }

    /**
     * 根据给定的APK路径生成打开apk的Intent
     * @param pathName
     * @param ctx
     * @return
     */
    public static Intent getInstallPackIntent(String pathName, Context ctx) {
        Assert.assertTrue((pathName != null) && (!(pathName.equals(""))));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(pathName)), "application/vnd.android.package-archive");
        return intent;
    }


    /**
     * 安装APK
     * @param path APK所在的路径
     * @param ctx
     */
    public static void installPack(String path, Context ctx) {
        ctx.startActivity(getInstallPackIntent(path, ctx));
    }

    /**
     * 让手机振动
     * @param ctx
     * @param vibrate 是否振动
     */
    public static void shake(Context ctx, boolean vibrate) {
        shake(ctx, pattern, vibrate);
    }

    public static void shake(Context ctx,long[] pattern , boolean vibrate) {
        Vibrator mVibrator = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
        if (mVibrator == null) {
            return;
        }
        if (vibrate) {
            mVibrator.vibrate(pattern, -1);
            return;
        }
        mVibrator.cancel();
    }

    /**
     * 播放音频文件，返回播放器
     * @param ctx 上下文
     * @param resId 资源文件ID ，在String文件中定义
     * @param listener 播放完成回调接口
     * @return
     */
    public static MediaPlayer playSound(Context ctx, int resId,
                                        MediaPlayer.OnCompletionListener listener) {
        try {
            String fileName = ctx.getString(resId);
            AssetFileDescriptor fileDpt = ctx.getAssets().openFd(fileName);
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(fileDpt.getFileDescriptor(),fileDpt.getStartOffset(), fileDpt.getLength());
            fileDpt.close();
            mediaPlayer.prepare();
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(listener);
            return mediaPlayer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 播放音频文件
     * @param ctx 应用上下文
     * @param resId 播放文件定义在String文件中
     */
    public static void playSound(Context ctx, int resId) {
        playSound(ctx, resId, new MediaPlayer.OnCompletionListener() {
            public final void onCompletion(MediaPlayer paramMediaPlayer) {
                paramMediaPlayer.release();
            }
        });
    }

    /**
     * 当前时间 （秒值）
     * @return
     */
    public static long nowSecond() {
        return (System.currentTimeMillis() / MILLSECONDS_OF_SECOND);
    }

    /**
     * 格式化时间生成字符串
     * @param second
     * @return
     */
    public static String formatSecToMin(int second) {
        return String.format("%d:%02d",
                new Object[]{Long.valueOf(second / SECOND_OF_MINUTE), Long.valueOf(second % SECOND_OF_MINUTE)});
    }

    /**
     * 返回当前毫秒级时间
     * @return
     */
    public static long nowMilliSecond() {
        return System.currentTimeMillis();
    }

    /**
     * 返回给定时间与当前时间差（秒）
     * @param second
     * @return
     */
    public static long secondsToNow(long second) {
        return (System.currentTimeMillis() / 1000L - second);
    }

    /**
     * 返回给定时间与当前的时间差（毫秒）
     * @param milliseconds
     * @return
     */
    public static long milliSecondsToNow(long milliseconds) {
        return (System.currentTimeMillis() - milliseconds);
    }

    public static long ticksToNow(long milliseconds) {
        return (SystemClock.elapsedRealtime() - milliseconds);
    }

    public static long currentTicks() {
        return SystemClock.elapsedRealtime();
    }

    public static long currentDayInMills() {
        return (nowMilliSecond() / MILLSECONDS_OF_DAY * MILLSECONDS_OF_DAY);
    }

    /**
     * 当前星期的毫秒值
     * @return
     */
    public static long currentWeekInMills() {
        GregorianCalendar defaultCalendar = new GregorianCalendar();
        GregorianCalendar curCalendar = new GregorianCalendar(
                defaultCalendar.get(Calendar.YEAR),
                defaultCalendar.get(Calendar.MONTH),
                defaultCalendar.get(Calendar.DAY_OF_MONTH));
        curCalendar.setTimeZone(GMT);
        int value = defaultCalendar.get(Calendar.DAY_OF_WEEK)
                - defaultCalendar.getFirstDayOfWeek();
        curCalendar.add(Calendar.DAY_OF_YEAR, -value);
        return curCalendar.getTimeInMillis();
    }

    /**
     * 获取当前月的毫秒值
     * @return
     */
    public static long currentMonthInMills() {
        GregorianCalendar calendar = new GregorianCalendar();
        GregorianCalendar monthGregorianCalendar = new GregorianCalendar(
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        monthGregorianCalendar.setTimeZone(GMT);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取当前年到现在的毫秒值
     * @return
     */
    public static long currentYearInMills() {
        GregorianCalendar calendar = new GregorianCalendar();
        GregorianCalendar yearGregorianCalendar = new GregorianCalendar(
                calendar.get(Calendar.YEAR), 1, 1);
        yearGregorianCalendar.setTimeZone(GMT);
        return yearGregorianCalendar.getTimeInMillis();
    }



    /**
     * 获取电话服务
     * @param ctx
     * @return
     */
    public static TelephonyManager getTelephonyManager(Context ctx) {
        return (TelephonyManager) ctx .getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * 获取手机设备的唯一编号
     * @param ctx
     * @return
     */
    public static String getDeviceId(Context ctx) {
        if (ctx == null) {
            return null;
        }
        try {
            TelephonyManager tm = getTelephonyManager(ctx);
            if (tm == null) {
                return null;
            }
            String deviceId = tm.getDeviceId();
            if (deviceId == null) {
                return null;
            }
            return deviceId.trim();
        } catch (SecurityException securityException) {
            securityException.printStackTrace();
            YYLogger.e(TAG, "getDeviceId failed, security exception");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * 获取设备手机号码
     * @param ctx
     * @return
     */
    public static String getLine1Number(Context ctx) {
        if (ctx == null) {
            return null;
        }
        try {
            TelephonyManager tm = getTelephonyManager(ctx);
            if (tm == null) {
                YYLogger.e(TAG, "get line1 number failed, null tm");
                return null;
            }
            String line1Number = tm.getLine1Number();
            if (line1Number == null) {
                return null;
            }
            return line1Number.trim();
        } catch (SecurityException ecurityException) {
            YYLogger.e(TAG, "getLine1Number failed, security exception");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断是否锁屏
     * @param ctx
     * @return
     */
    public static boolean isLockScreen(Context ctx) {
        try {
            return ((KeyguardManager) ctx.getSystemService(Context.KEYGUARD_SERVICE)).inKeyguardRestrictedInputMode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断是否处于栈顶
     * @param ctx
     * @return
     */
    public static boolean isTopActivity(Context ctx) {
        String str = ctx.getClass().getName();
        String topActivityName = getTopActivityName(ctx);
        YYLogger.d(TAG, "top activity=" + topActivityName + ", context=" + str);
        return topActivityName.equalsIgnoreCase(str);
    }

    /**
     * 当前栈顶的Activity Name
     * @param ctx
     * @return
     */
    public static String getTopActivityName(Context ctx) {
        try {
            return ((ActivityManager.RunningTaskInfo) ((ActivityManager) ctx
                    .getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1).get(0)).topActivity
                    .getClassName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "(null)";
    }

    /**
     * 判断给定的服务是否运行
     * @param ctx
     * @param service 服务名称  e.q ECClientService
     * @return
     */
    public static boolean isServiceRunning(Context ctx, String service) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        Iterator<ActivityManager.RunningServiceInfo> iterator = am.getRunningServices(Integer.MAX_VALUE).iterator();
        while (iterator.hasNext()) {
            ActivityManager.RunningServiceInfo runServiceInfo = iterator.next();
            if ((runServiceInfo == null) || (runServiceInfo.service == null)) {
                continue;
            }
            if (runServiceInfo.service.getClassName().toString().equals(service)) {
                YYLogger.w(TAG, "service " + service + " is running");
                return true;
            }
        }
        YYLogger.w(TAG, "service " + service + " is not running");
        return false;
    }

    /**
     * 判断给定的进程名是否处于运行
     * @param ctx 上下文
     * @param processName 进程名
     * @return 是否运行
     */
    public static boolean isProcessRunning(Context ctx , String processName) {
        for (ActivityManager.RunningAppProcessInfo appProcess : ((ActivityManager)
                ctx.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses()) {
            if (appProcess == null || appProcess.processName == null) {
                continue;
            }

            if (appProcess.processName.startsWith(processName)) {
                YYLogger.w(TAG, "process " + processName + " is running");
                return true;
            }
        }
        YYLogger.w(TAG, "process " + processName + " is not running");
        return false;
    }

    /**
     * 当前应用程序是否处于栈顶
     * @param ctx
     * @return
     */
    public static boolean isTopApplication(Context ctx ) {
        try {

            String str = ((ActivityManager.RunningTaskInfo) ((ActivityManager) ctx
                    .getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1)
                    .get(0)).topActivity.getClassName();
            String packageName = ctx.getPackageName();
            YYLogger.d(TAG, "top activity=" + str + ", context=" + ctx);
            return str.contains(packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断Intent是否可用
     * @param ctx
     * @param intent
     * @return
     */
    public static boolean isIntentAvailable(Context ctx, Intent intent) {
        return ctx .getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() <= 0;
    }

    /**
     * 回收图片
     * @param bitmapMap
     */
    public static void freeBitmapMap(Map<String, Bitmap> bitmapMap) {
        Iterator<Map.Entry<String, Bitmap>> iterator = bitmapMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Bitmap> next = iterator.next();
            if (next.getValue() != null) {
                next.getValue().recycle();
            }
        }
        bitmapMap.clear();
    }

    /**
     * 调用图片选择程序
     * @param ctx
     * @param requestCode
     */
    public static void selectPicture(Context ctx, int requestCode) {
        Intent target = new Intent(Intent.ACTION_GET_CONTENT);
        target.setType("image/*");
        Intent chooserIntent = Intent.createChooser(target, "\"image/*\"");
        ((Activity) ctx).startActivityForResult(chooserIntent, requestCode);
    }

    /**
     * 返回Int值
     * @param integer
     * @return
     */
    public static int nullAsNil(Integer integer) {
        if (integer == null) {
            return 0;
        }
        return integer.intValue();
    }

    /**
     * Long对象转换成数字
     * @param l
     * @return
     */
    public static long nullAsNil(Long l) {
        if (l == null) {
            return 0L;
        }
        return l.longValue();
    }

    public static String nullAsNil(String str) {
        if (str == null) {
            return "";
        }
        return str;
    }

    /**
     * 如果为空返回true
     * @param b
     * @return
     */
    public static boolean nullAsTrue(Boolean b) {
        if (b == null) {
            return true;
        }
        return b.booleanValue();
    }

    public static boolean nullAsGet(int str, int retValue) {
        if(str == retValue) {
            return true;
        }
        return false;
    }

    /**
     * 如果为空返回False 否则返回当前值
     * @param b
     * @return
     */
    public static boolean nullAsFalse(Boolean b) {
        if (b == null) {
            return false;
        }
        return b.booleanValue();
    }

    /**
     * 如果为空返回默认值
     * @param iObject
     * @param defValue
     * @return
     */
    public static int nullAs(Integer iObject, int defValue) {
        if (iObject == null) {
            return defValue;
        }
        return iObject.intValue();
    }

    /**
     * 如果为空返回默认值
     * @param lObject
     * @param defValue
     * @return
     */
    public static long nullAs(Long lObject, long defValue) {
        if (lObject == null) {
            return defValue;
        }
        return lObject.longValue();
    }

    public static boolean nullAs(Boolean bObject, boolean defValue) {
        if (bObject == null) {
            return defValue;
        }
        return bObject.booleanValue();
    }

    /**
     * 根据默认值返回非空字符串
     * @param str
     * @param defValue
     * @return
     */
    public static String nullAs(String str, String defValue) {
        if (str == null) {
            return defValue;
        }
        return str;
    }

    /**
     * 将 Long  或者 Integer 转换成整型 如果为空返回默认值
     * @param obj
     * @param defInt
     * @return
     */
    public static int nullAsInt(Object obj, int defInt) {
        if (obj == null) {
            return defInt;
        }
        if (obj instanceof Integer) {
            return ((Integer) obj).intValue();
        }
        if (obj instanceof Long) {
            return ((Long) obj).intValue();
        }
        return defInt;
    }

    /**
     * 判断是否为空
     * @param value
     * @return
     */
    public static boolean isNullOrNil(String value) {
        return !((value != null) && (value.length() > 0));
    }

    /**
     * 判断是否包含数据
     * @param b
     * @return
     */
    public static boolean isNullOrNil(byte[] b) {
        return !((b != null) && (b.length > 0));
    }

    public static boolean isNullOrNil(Object[] obj) {
        return !((obj != null) && obj.length > 0);
    }

    /**
     * 将字符串转换成整型，如果为空则返回默认值
     * @param str 字符串
     * @param def 默认值
     * @return
     */
    public static int getInt(String str, int def) {
        try {
            if (str == null) {
                return def;
            }
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
        }
        return def;
    }

    /**
     * 将字符串转换成Long数据，如果为空则返回默认值
     * @param str
     * @param def
     * @return
     */
    public static long getLong(String str, long def) {
        try {
            if (str == null) {
                return def;
            }
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return def;
    }

    public static boolean getBoolean(String str, boolean b) {
        try {
            if (str == null) {
                return b;
            }
            return Boolean.parseBoolean(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return b;
    }

    /**
     * 将字符串转换成16进制数值，如果字符串为空则返回默认值
     * @param str
     * @param def
     * @return
     */
    public static int getHex(String str, int def) {
        if (str == null) {
            return def;
        }
        try {
            return (int) (Long.decode(str).longValue() & 0xFFFFFFFF);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return def;
    }

    /**
     * 是否有外存卡
     * @return
     */
    public static boolean isExistExternalStore() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }


    public static Map<String, String> parseXml(String source , String str1 , String encoding) {
        if ((source == null) || (source.length() <= 0)){
            return null;
        }
        HashMap<String, String> result = new HashMap<String, String>();
        DocumentBuilderFactory newInstance = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = newInstance.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        }

        if(documentBuilder == null) {
            YYLogger.e(TAG, "new Document Builder failed");
            return null;
        }
        Document document = null;
        try {

            InputSource inputSource = new InputSource(new ByteArrayInputStream(source.getBytes()));
            if(encoding != null) {
                inputSource.setEncoding(encoding);
            }
            document = documentBuilder.parse(inputSource);
            document.normalize();
        } catch (DOMException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if(document == null) {
            YYLogger.e(TAG, "new Document failed");
            return null;
        }

        Element element = document.getDocumentElement();
        if(element == null) {
            YYLogger.e(TAG,  "getDocumentElement failed");
            return null;
        }

        if(str1 != null && str1.equals(element.getNodeName())) {

        }

        return null;
    }

    /**
     * 流转换成字符串
     * @param in 输入流
     * @return 字符串
     */
    public static String convertStreamToString(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String str;
        try {
            while ((str = br.readLine()) != null) {
                if(sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(str);
            }
        } catch (IOException e) {
            YYLogger.printErrStackTrace(TAG , e ,"get exception");
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static String encodeString(byte[] b) {
        if(isNullOrNil(b)) {
            return "";
        }
        return new String(b);
    }

    /**
     * 16进制字节数组转换成字符串
     * @param hex
     * @return
     */
    public static String encodeHexString(byte[] hex) {
        StringBuilder sb = new StringBuilder("");
        if (hex != null) {
            for (int i = 0; i < hex.length; ++i) {
                sb.append(String.format("%02x", new Object[]{Integer.valueOf(hex[i] & 0xFF)}));
            }
        }
        return sb.toString();
    }

    /**
     * 字符串转换成16进制
     * @param src
     * @return
     */
    public static byte[] decodeHexString(String src) {
        if ((src == null) || (src.length() <= 0))
            return new byte[0];
        try {
            byte[] hexArray = new byte[src.length() / 2];
            for (int i = 0; i < hexArray.length; ++i)
                hexArray[i] = (byte) (Integer.parseInt(
                        src.substring(i * 2, i * 2 + 2), 16) & 0xFF);
            return hexArray;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static int guessHttpSendLength(int length) {
        return (224 + ((length - 1) / 1462 + 1) * 52 + length);
    }

    public static int guessHttpRecvLength(int length) {
        return (208 + ((length - 1) / 1462 + 1) * 52 + length);
    }

    public static int guessHttpContinueRecvLength(int length) {
        return (52 + ((length - 1) / 1462 + 1) * 52 + length);
    }

    public static int guessTcpConnectLength() {
        return 172;
    }

    public static int guessTcpDisconnectLength() {
        return 156;
    }

    public static int guessTcpSendLength(int length) {
        return (40 + ((length - 1) / 1462 + 1) * 52 + length);
    }

    public static int guessTcpRecvLength(int length) {
        return (40 + ((length - 1) / 1462 + 1) * 52 + length);
    }

    /**
     * 将":"分隔的数字字符串转换成数组
     * @param src
     * @return
     */
    public static int[] splitToIntArray(String src) {
        if (src == null) {
            return null;
        }
        String[] split = src.split(":");
        ArrayList<Integer> integerArrayList = new ArrayList<Integer>();
        int length = split.length;
        for (int j = 0; j < length; ++j) {
            String str = split[j];
            if (str == null || (str.length() <= 0))
                continue;
            try {
                int value = Integer.valueOf(str).intValue();
                integerArrayList.add(Integer.valueOf(value));
            } catch (Exception e) {
                e.printStackTrace();
                YYLogger.e(TAG, "invalid port num, ignore");
            }
        }
        int[] intValue = new int[integerArrayList.size()];
        for (int i = 0; i < intValue.length; i++) {
            intValue[i] = ((Integer) integerArrayList.get(i)).intValue();
        }
        return intValue;
    }

    /**
     * 读取压缩文件到指定目录
     * @param path
     * @param destPath
     * @return
     */
    public static int UnZipFolder(String path, String destPath) {
        try {
            Log.v("XZip", "UnZipFolder(String, String)");
            ZipInputStream zInputStream = new ZipInputStream(new FileInputStream(path));
            ZipEntry nextEntry = zInputStream.getNextEntry();
            while (nextEntry != null) {
                String name = nextEntry.getName();
                if (nextEntry.isDirectory()) {
                    name = name.substring(0, name.length() - 1);
                    new File(destPath + File.separator + name).mkdirs();
                }
                File file = new File(destPath + File.separator + name);
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] b = new byte[1024];
                int length = 0;
                while ((length = zInputStream.read(b)) != -1) {
                    fos.write(b, 0, length);
                    fos.flush();
                }
                fos.close();
            }
            zInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return -1;
        } catch (IOException e) {
            e.printStackTrace();
            return -2;
        }
        return 0;
    }


    public static String getStack() {
        StackTraceElement[] sTraceElement = new Throwable().getStackTrace();
        if (sTraceElement == null || (sTraceElement.length < 2)) {
            return "";
        }
        String str = "";
        for (int i = 1; (i < sTraceElement.length)
                && (sTraceElement[i].getClassName()
                .contains("com.speedtong.sdk")); ++i) {

            str = str + "[" + sTraceElement[i].getClassName().substring(15)
                    + ":" + sTraceElement[i].getMethodName() + "]";
        }
        return str;
    }

    /**
     * 检查SDCard是否满
     * @return
     */
    public static boolean checkSDCardFull() {
        String storageState = Environment.getExternalStorageState();
        if (!("mounted".equals(storageState))) {
            return false;
        }
        File storageDirectory = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs((storageDirectory).getPath());
        // 返回文件系统上总共的块
        int blockCount = statFs.getBlockCount();
        // 返回文件系统上剩下的可供程序使用的块
        int availableBlocks = statFs.getAvailableBlocks();
        if (blockCount <= 0L) {
            return false;
        }
        if (blockCount - availableBlocks < 0L) {
            return false;
        }
        int i = (int) ((blockCount - availableBlocks) * 100L / blockCount);

        // 返回文件系统 一个块的大小单位byte
        // 返回文件系统上剩余的所有块 包括预留的一般程序无法访问的
        long availSize = statFs.getBlockSize() * statFs.getFreeBlocks();
        YYLogger.d(TAG, "checkSDCardFull per:" + i + " blockCount:" + blockCount
                + " availCount:" + availableBlocks + " availSize:" + availSize);
        if (95 > i) {
            return false;
        }
        return (availSize > 52428800L);
    }

    /**
     * 检查是否声明有此权限
     * @param ctx
     * @param permName
     * @return
     */
    public static boolean checkPermission(Context ctx, String permName) {
        Assert.assertNotNull(ctx);
        String pkgName = ctx.getPackageName();
        int permission = ctx.getPackageManager().checkPermission(permName, pkgName);
        boolean hasPermission = (permission == PackageManager.PERMISSION_GRANTED ? true : false);
        Log.d(TAG, permName + " has "
                + (hasPermission ? "permission " : "no permission ")
                + permName);
        return hasPermission;
    }

    /**
     * 页面Intent跳转
     * @param ctx
     * @param uriString
     * @return
     */
    public static boolean jump(Context ctx, String uriString) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
        if (!(isIntentAvailable(ctx, intent))) {
            YYLogger.e(TAG, "jump to url failed, " + uriString);
            return false;
        }
        ctx.startActivity(intent);
        return true;
    }

    /**
     * XML特殊字符的转义
     * @param xml
     * @return
     */
    public static String escapeStringForXml(String xml) {
        if(xml == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        int length = xml.length();
        for(int i = 0 ; i < length ; i++) {
            char charAt = xml.charAt(i);
            if(((charAt < ' ') && (charAt != tabChar[0]) && (charAt != tabChar[1]) && (charAt != tabChar[2])) || (charAt > '')) {
                sb.append("&#");
                sb.append(Integer.toString(charAt));
                sb.append(';');
            } else {
                boolean transfer = false;
                for (int j = bTransfer.length - 1; j >= 0; j--) {
                    if (bTransfer[j] != charAt) {
                        continue;
                    }
                    transfer = true;
                    sb.append(transferResult[j]);
                    break;
                }
                if (transfer) {
                    continue;
                }
                sb.append(charAt);
            }
        }

        return sb.toString();
    }

    public static String processXml(String xml) {
        if ((xml == null) || (xml.length() == 0))
            return xml;
        if (Build.VERSION.SDK_INT < 8)
            return expandEntities(xml);
        return xml;
    }

    public static String expandEntities(String xml) {
        int length = xml.length();
        char[] _data = new char[length];
        int startIndex = 0;
        int temp = -1;
        for(int i = 0 ;i <length ; i ++) {
            char charAt = xml.charAt(i);
            _data[startIndex++] = charAt;

            if((charAt == '&') && (temp == -1)) {
                temp = i;
            } else {
                if ((i == -1) || (Character.isLetter(charAt)) || (Character.isDigit(charAt)) || (charAt == '#')) {
                    continue;
                }

                if (charAt == ';') {

                }
            }
        }
        return null;
    }

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    /**
     * 获取手机序列号
     * @param ctx
     * @return 手机序列号
     */
    public static String getSimSerialNumber(Context ctx) {
        TelephonyManager tm = getTelephonyManager(ctx);
        if(tm == null) {
            return null;
        }
        //序列号（sn）
        String sn = tm.getSimSerialNumber();
        if(!ECSDKUtils.isNullOrNil(sn)){
            return null;
        }
        return sn;
    }





    public static String[] split(String original, String regex) {
        return split(original, regex, false);
    }

    public static String[] split(String original, String regex,
                                 boolean isTogether) {
        try {
            int startIndex = original.indexOf(regex);
            int index = 0;
            if (startIndex < 0) {
                return new String[] { original };
            }
            ArrayList<String> v = new ArrayList<String>();
            while (startIndex < original.length() && startIndex != -1) {
                String temp = original.substring(index, startIndex);
                v.add(temp);
                index = startIndex + regex.length();
                startIndex = original.indexOf(regex, startIndex
                        + regex.length());
            }
            if (original.indexOf(regex, original.length() - regex.length()) < 0) {
                String last = original.substring(index);
                if (isTogether) {
                    last = regex + last;
                }
                v.add(last);
            }

            return v.toArray(new String[v.size()]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取函数返回值
     * @param input
     * @return
     */
    public static int getRetvalue(String input) {
        if(isNullOrNil(input)) {
            return -1;
        }
        String spit[] = input.split(",");
        if(spit.length <= 0) {
            return -1;
        }
        String ret[] = spit[0].split(":");
        try{
            return Integer.parseInt(ret[1]);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 获取函数返回值
     * @param input
     * @return
     */
    public static int getSerialNum(String input) {
        if(isNullOrNil(input)) {
            return -1;
        }
        String spit[] = input.split(",");
        if(spit.length <= 0) {
            return -1;
        }
        String ret[] = spit[1].split(":");
        try{
            return Integer.parseInt(ret[1]);
        } catch (Exception e) {
            return -1;
        }
    }


    public static String convertState(int state) {
        if(state == 200) {
            return "000000";
        }
        return String.valueOf(state);
    }




    /**
     *获取IP地址
     * @return
     */
    public static String _getHostIp() {
        String ip ;
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                Enumeration<InetAddress> inetAddresses = ((NetworkInterface) networkInterfaces
                        .nextElement()).getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            YYLogger.e(TAG , "WifiPreference IpAddress :"+ e.toString());
        } catch (Exception e) {
            YYLogger.e(TAG , "WifiPreference IpAddress :"+ e.toString());
        }
        return null;
    }

    /**
     * 当前SDK日志保存
     * @param userId
     * @return
     */
    public static String getLogFile(String userId) {

        userId = nullAsNil(userId);
        return formatUnixTime(nowMilliSecond() , new SimpleDateFormat("[yy-MM-dd]")) + "-" +userId + ".log";
    }

    /**
     * 序列化
     * @param dest
     * @param src
     */
    public static void writeBoolean(Parcel dest , boolean src) {
        dest.writeByte((byte) (src ? 1 : 0));
    }




    public static int getEnumValue(int max , int value) {
        if(value > max) {
            return max;
        }
        if(value < 1) {
            return 1;
        }
        return value;
    }



    /**
     * 获取最后一个参数
     * @param srcText
     * @param p
     * @return
     */
    public static String getLastwords(String srcText, String p) {
        try {
            String[] array = TextUtils.split(srcText, p);
            int index = (array.length - 1 < 0) ? 0 : array.length - 1;
            return array[index];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断SDCARD空间大小
     * @param sizeMb 空间大小
     * @return
     */
    public static boolean isAvaiableSpace(int sizeMb) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            String sdcard = Environment.getExternalStorageDirectory().getPath();
            StatFs statFs = new StatFs(sdcard);
            long blockSize = statFs.getBlockSize();
            long blocks = statFs.getAvailableBlocks();
            long availableSpare = (blocks * blockSize) / (1024 * 1024);
            YYLogger.v(TAG, "The current sdcard of free space :" + availableSpare);
            if (sizeMb > availableSpare) {
                return false;
            } else {
                return true;
            }
        }
        YYLogger.v(TAG, "The current is not loaded sdcard" );
        return false;
    }

    /**
     * 格式化消息ID
     * @param msgId 消息ID
     * @return 格式化后的消息ID
     */
    public static String formatMsgid(String msgId) {
        return nowMilliSecond() + "|" + msgId;
    }

    /**
     * 是否来电视频
     * @param user 账号
     * @return 是否
     */
    public static boolean isIncomingCall(String user) {
        return !ECSDKUtils.isNullOrNil(user) && user.startsWith("IncomingCall:");
    }

    /**
     * 返回账号
     * @param user 账号
     * @return 账号
     */
    public static String getCaptureUser(String user) {
        return user.replace("IncomingCall:", "");
    }

    /**
     * 随机一个序列号
     * @return 随机序列号
     */
    public static int randomSerialNumber() {
        return Math.abs(String.valueOf(ECSDKUtils.nowMilliSecond()).hashCode());
    }


    /**
     * @return 心跳间隔
     */
    public static long getIntervalMillis(int type) {
        switch (type) {
            case 1:
                return 300*1000;
            case 2:
                return 180*1000;
            case 3:
                return 90*1000;
            case 4:
                return 45*1000;
            default:
                return 90*1000;
        }
    }


}
