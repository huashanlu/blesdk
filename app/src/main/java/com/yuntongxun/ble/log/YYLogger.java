/*
 *  Copyright (c) 2015 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */package com.yuntongxun.ble.log;

import android.os.Build;
import android.util.Log;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.MessageDigest;

/**
 * SDK 日志工具类
 * @author 容联•云通讯
 * @version 5.0
 * @since 2015-s3-5
 */
public class YYLogger {

    public static final String TAG = "YYBLE.Logger";

    public static final int LEVEL_VERBOSE = 0;
    /** 调试信息 */
    public static final int LEVEL_DEBUG = 1;
    /** 一般信息 */
    public static final int LEVEL_INFO = 2;
    /** 警告 */
    public static final int LEVEL_WARNING = 3;
    /** 一般错误 */
    public static final int LEVEL_ERROR = 4;
    /** 致命错误 */
    public static final int LEVEL_FATAL = 5;
    public static final int LEVEL_NONE = 6;

    /**日志等级*/
    private static int mLevel =0;
    private static PrintStream pStream;
    private static String sysInfo ;
    private static byte[] encryptKey;


    /**
     * @param path 日志保存路径
     * @param type 日志类型
     * @param user 账号
     */
    public static void setOutputPath(String path , String type , String user /*, int createTime*/) {
        if ((path == null) || (path.length() == 0) || (user == null)
                || (user.length() == 0)) {
            return;
        }
        try {

            File logFile = new File(path);
            if(!logFile.exists()) {
                return ;
            }

            FileInputStream fis = (logFile.length() > 0) ? new FileInputStream(path) : null;
            FileOutputStream fos = new FileOutputStream(path , true);
            setOutputStream(fis , fos , type , user , /*createTime*/1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param is 日志输入流
     * @param os 日志输出流
     * @param type 日志类型
     * @param user 用户
     */
    private static void setOutputStream(InputStream is , OutputStream os , String type , String user , int ceTime) {
        try {
            pStream = new PrintStream(new BufferedOutputStream(os));
            long createTime = 0L;
            if(is != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                type = bufferedReader.readLine().substring(2).trim();
                user = bufferedReader.readLine().substring(2).trim();
                createTime = getLong(bufferedReader.readLine().substring(2).trim(), 0L);

                d(TAG, "using provided info, type=%s, user=%s, createtime=%d", type ,user , createTime);
            } else {
                createTime = System.currentTimeMillis();
                YYLogHelper.initLogHeader(pStream, type, user, createTime, ceTime);
            }
            encryptKey = getMessageDigest((user + createTime + "").getBytes()).substring(7, 21).getBytes();
            Log.d(TAG, "set up out put stream");
        } catch (Exception e) {
            printErrStackTrace(TAG, e, "get Exception");
        }
    }

    public static String getMessageDigest(byte[] input) {
        char[] source = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(input);
            byte[] digest = mDigest.digest();
            int length = digest.length;
            char[] result = new char[length * 2];
            int j = 0;
            for (byte l : digest) {
                result[(j++)] = source[(l >>> 4 & 0xF)];
                result[(j++)] = source[(l & 0xF)];
            }
            return new String(result);
        } catch (Exception e) {
            YYLogger.printErrStackTrace(TAG, e, "get Exception");
        }
        return null;
    }

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

    /**
     * 释放
     */
    public static void reset() {
        pStream = null;
        encryptKey = null;
    }

    /**
     * 设置打印日志等级
     * @param level 日志等级
     */
    public static void setLevel(int level) {
        mLevel = level;
        w(TAG, "new log level: " + level);
    }

    /**
     * 打印{@link #LEVEL_FATAL}日志信息
     * @param tag 日志TAG
     * @param msg 日志内容
     */
    public static void f(String tag, String msg) {
        f(tag, msg, new Object[]{});
    }

    /**
     * 打印{@link #LEVEL_ERROR}日志信息
     * @param tag 日志TAG
     * @param msg 日志内容
     */
    public static void e(String tag, String msg) {
        e(tag, msg, new Object[]{});
    }

    /**
     * 打印{@link #LEVEL_WARNING}日志信息
     * @param tag 日志TAG
     * @param msg 日志内容
     */
    public static void w(String tag, String msg) {
        w(tag, msg, new Object[]{});
    }

    /**
     * 打印{@link #LEVEL_INFO}日志信息
     * @param tag 日志TAG
     * @param msg 日志内容
     */
    public static void i(String tag, String msg) {
        i(tag, msg, new Object[]{});
    }

    /**
     * 打印{@link #LEVEL_DEBUG}日志信息
     * @param tag 日志TAG
     * @param msg 日志内容
     */
    public static void d(String tag, String msg) {
        d(tag, msg, new Object[]{});
    }

    /**
     * 打印{@link #LEVEL_VERBOSE}日志信息
     * @param tag 日志TAG
     * @param msg 日志内容
     */
    public static void v(String tag, String msg) {
        v(tag, msg, new Object[]{});
    }

    private static boolean isEmptyFormat(Object... args) {
        return (args == null || args.length == 0);
    }

    /**
     * 打印致命错误日志信息
     * @param tag 日志TAG
     * @param msg 日志内容
     * @param args 日志内容格式化参数
     */
    public static void f(String tag, String msg, Object... args) {
        if (mLevel > LEVEL_FATAL) {
            return;
        }
        msg = (isEmptyFormat(args)) ? msg : String.format(msg, args);
        Log.e(tag, msg);
        YYLogHelper.writeToStream(pStream, encryptKey, "F/" + tag, msg);
    }

    /**
     * 打印一般错误日志信息
     * @param tag 日志TAG
     * @param msg 日志内容
     * @param args 日志内容格式化参数
     */
    public static void e(String tag, String msg, Object... args) {
        if (mLevel > LEVEL_ERROR) {
            return;
        }
        msg = (isEmptyFormat(args)) ? msg : String.format(msg, args);
        Log.e(tag, msg);
        YYLogHelper.writeToStream(pStream, encryptKey, "E/" + tag, msg);
    }

    /**
     * 打印警告日志信息
     * @param tag 日志TAG
     * @param msg 日志内容
     * @param args 日志内容格式化参数
     */
    public static void w(String tag, String msg, Object... args) {
        if (mLevel > LEVEL_WARNING) {
            return;
        }
        msg = (isEmptyFormat(args)) ? msg : String.format(msg, args);
        Log.w(tag, msg);
        YYLogHelper.writeToStream(pStream, encryptKey, "W/" + tag, msg);
    }

    /**
     * 打印一般日志信息
     * @param tag 日志TAG
     * @param msg 日志内容
     * @param args 日志内容格式化参数
     */
    public static void i(String tag, String msg, Object... args) {
        if (mLevel > LEVEL_INFO) {
            return;
        }
        msg = (isEmptyFormat(args)) ? msg : String.format(msg, args);
        Log.i(tag, msg);
        YYLogHelper.writeToStream(pStream, encryptKey, "I/" + tag, msg);
    }

    /**
     * 打印调试日志信息
     * @param tag 日志TAG
     * @param msg 日志内容
     * @param args 日志内容格式化参数
     */
    public static void d(String tag, String msg, Object... args) {
        if (mLevel > LEVEL_DEBUG) {
            return;
        }
        msg = (isEmptyFormat(args)) ? msg : String.format(msg, args);
        Log.d(tag, msg);
        YYLogHelper.writeToStream(pStream, encryptKey, "D/" + tag, msg);
    }

    /**
     * 打印日志信息
     * @param tag 日志TAG
     * @param msg 日志内容
     * @param args 日志内容格式化参数
     */
    public static void v(String tag, String msg, Object... args) {
        if (mLevel > LEVEL_VERBOSE) {
            return;
        }
        msg = (isEmptyFormat(args)) ? msg : String.format(msg, args);
        Log.v(tag, msg);
        YYLogHelper.writeToStream(pStream, encryptKey, "V/" + tag, msg);
    }

    /**
     * 打印异常捕获日志信息
     * @param tag 日志TAG
     * @param msg 日志内容
     * @param args 日志内容格式化参数
     */
    public static void printErrStackTrace(String tag , Throwable throwable , String msg, Object... args) {
        if (mLevel > LEVEL_ERROR) {
            return;
        }
        msg = (isEmptyFormat(args)) ? msg : String.format(msg, args);
        msg = msg + "  " + Log.getStackTraceString(throwable);
        Log.e(tag, msg);
        YYLogHelper.writeToStream(pStream, encryptKey, "E/" + tag, msg);
    }


    /**
     * 设备版本信息
     * @return 日志头信息
     */
    public static String getSysInfo() {
        return sysInfo;
    }


    /**
     * 返回SDK打印Log Tag。
     * @param clazz Class
     * @return Tag
     */
    public static String getLogger(Class<?> clazz) {
        if(clazz == null) {
            return "YYBLE.";
        }
        return "YYBLE." + clazz.getSimpleName();
    }

    static {
//        mLevel = LEVEL_NONE;
        encryptKey = null;
        sysInfo = "VERSION.RELEASE:[" + Build.VERSION.RELEASE + "] " +
                "VERSION.CODENAME:[" + Build.VERSION.CODENAME + "] " +
                "VERSION.INCREMENTAL:[" + Build.VERSION.INCREMENTAL + "] " +
                "BOARD:[" + Build.BOARD + "] " +
                "DEVICE:[" + Build.DEVICE + "] " +
                "DISPLAY:[" + Build.DISPLAY + "] " +
                "FINGERPRINT:[" + Build.FINGERPRINT + "] " +
                "HOST:[" + Build.HOST + "] " +
                "MANUFACTURER:[" + Build.MANUFACTURER + "] " +
                "MODEL:[" + Build.MODEL + "] " +
                "PRODUCT:[" + Build.PRODUCT + "] " +
                "TAGS:[" + Build.TAGS + "] " +
                "TYPE:[" + Build.TYPE + "] " +
                "USER:[" + Build.USER + "]";
    }


}
