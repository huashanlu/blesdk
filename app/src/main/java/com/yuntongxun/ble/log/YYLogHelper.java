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
 */package com.yuntongxun.ble.log;

import android.os.Build;
import android.text.format.DateFormat;

import java.io.PrintStream;

/**
 * SDK 日志序列化工具类
 * @author 容联•云通讯
 * @version 5.0
 * @since 2015-s3-7
 */
public class YYLogHelper {
    /**
     * 写入文件数据
     * @param ps 输出流对象
     * @param b 输出字节
     * @param key 密钥
     * @param value 字符串
     */
    public static void writeToStream(PrintStream ps , byte[] b , String key , String value) {
        if ((ps == null) || (ECSDKUtils.isNullOrNil(b))
                || (ECSDKUtils.isNullOrNil(key))
                || (ECSDKUtils.isNullOrNil(value))) {
            return;
        }

        synchronized (ps) {
            StringBuilder builder = new StringBuilder();
            builder.append(DateFormat.format("MM-dd kk:mm:ss", System.currentTimeMillis()));
            builder.append(" ").append(key).append(" ").append(value);

            try {
                ps.write(builder.toString().getBytes());
                ps.write("\r\n".getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
            ps.flush();

        }

    }

    /**
     * 初始化日志打印
     * @param stream 输出流
     * @param type 日志类型
     * @param user 当前用户
     * @param createTime 日志生成文件时间
     * @param def 版本信息
     */
    public static void initLogHeader(PrintStream stream , String type , String user , long createTime , int def) {
        if((stream == null) || (ECSDKUtils.isNullOrNil(user)) || (createTime == 0L)) {
            return;
        }

        stream.println("s1 " + type);
        stream.println("s2 " + user);
        stream.println("s3 " + createTime);
        stream.println("4 " + Integer.toHexString(def));
        stream.println("5 " + Build.VERSION.RELEASE);
        stream.println("6 " + Build.VERSION.CODENAME);
        stream.println("7 " + Build.VERSION.INCREMENTAL);
        stream.println("8 " + Build.BOARD);
        stream.println("9 " + Build.DEVICE);
        stream.println("10 " + Build.DISPLAY);
        stream.println("11 " + Build.FINGERPRINT);
        stream.println("12 " + Build.HOST);
        stream.println("13 " + Build.MANUFACTURER);
        stream.println("14 " + Build.MODEL);
        stream.println("15 " + Build.PRODUCT);
        stream.println("16 " + Build.TAGS);
        stream.println("17 " + Build.TYPE);
        stream.println("18 " + Build.USER);
        stream.println();
        stream.flush();
    }
}
