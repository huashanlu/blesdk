package com.yuntongxun.ble;

import android.content.Context;

import com.yuntongxun.ble.core.YYBLEManagerImpl;
import com.yuntongxun.ble.core.YYSDKCoreCache;
import com.yuntongxun.ble.log.YYLogger;


/**
 * Created by luhuashan on 16/12/16.
 */
public class YYBleSDK {


    public static  final String TAG = YYLogger.getLogger(YYBleSDK.class);

    public static void initial(Context context){
        if(context==null){
            YYLogger.e(TAG,"context can not be null");
           throw new IllegalArgumentException();
        }
        YYSDKCoreCache.getInstance().setContext(context);

    }


    public static YYBLEManager getYoYoBLECentralManager(){
        return YYBLEManagerImpl.getInstance();
    }



}
