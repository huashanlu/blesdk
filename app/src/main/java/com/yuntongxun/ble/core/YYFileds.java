package com.yuntongxun.ble.core;

import java.util.UUID;

/**
 * Created by luhuashan on 16/12/21.
 */
public class YYFileds {


    public static final String YY_SERVICE_UUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String YY_SERVICE_UUID_W = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String YY_READ_CHARACTERISTIC_UUID = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String YY_CHARACTERISTIC_UUID = "6e400004-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String YY_WRITE_CHARACTERISTIC_UUID = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";

    public static final UUID[] UUIDS  = new UUID[]{UUID.fromString(YY_SERVICE_UUID)};


}
