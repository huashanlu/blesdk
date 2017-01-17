package com.yuntongxun.ble.core.jni;

/**
 * Created by luhuashan on 16/12/22.
 */
public class IYYNative {


    static {
        System.loadLibrary("yuntongxunble");
    }


    public static native byte[] buildSimCardID();
    public static native byte[] buildSimIMSI();

    public static native byte[] receiveBlueToothData(byte[] data);

    public static native String getCmdTypeDecodeData(byte[] data);

    public static native boolean isValidData(byte[] data);

    public static native byte[] getSimCardKICmd(byte[] data);

    public static native byte[] getActiveSimCardCmd();

    public static native byte[] getReleaseSimCardCmd();


    public static native byte[] buildState() ;

}
