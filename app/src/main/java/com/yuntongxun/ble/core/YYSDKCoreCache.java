package com.yuntongxun.ble.core;

import android.content.Context;

/**
 * Created by luhuashan on 16/12/16.
 */
public class YYSDKCoreCache {


    private static volatile YYSDKCoreCache sInstance;

    private Context context;

    public static YYSDKCoreCache getInstance() {
        if(sInstance == null) {
            synchronized (YYBLEManagerImpl.class) {
                sInstance = new YYSDKCoreCache();
            }
        }
        return  sInstance;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    protected void destory(){

        this.context = null;
    }
}
