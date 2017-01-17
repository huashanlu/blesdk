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



/**
 * SDK调试工具
 * @author 容联•云通讯
 * @since 2015-s3-12
 * @version 5.s1.4
 */
public class YYDebuggerTrace {

    private static final String TAG = YYLogger.getLogger(YYDebuggerTrace.class);

    public static final String TRACE_TAG = "TRACE_LOG";
    public static final String DEBUG_TAG = "DEBUG_LOG";
    public static final String LOG_LEVEL = "LOG_LEVEL";





    /**
     * 初始化文件
     */
    private static void intTraceFile() {

    }



    /**
     * 打印日志堆栈信息
     * @return 信息
     */
    public static String printStackTrace() {
        StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
        if(stackTraceElements == null || stackTraceElements.length < 4) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 3 ; i < stackTraceElements.length ; ++i) {
            if (!(stackTraceElements[i].getClassName().contains("com.yuntongxun.yuya")))
                continue;
            sb.append("[");
            sb.append(stackTraceElements[i].getClassName().substring(15));
            sb.append(":");
            sb.append(stackTraceElements[i].getMethodName());
            sb.append("(").append(stackTraceElements[i].getLineNumber()).append(")]");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return printStackTrace();
    }

}
