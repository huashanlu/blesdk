package com.yuntongxun.ble;

/**
 * Created by luhuashan on 16/12/10.
 */
public enum YYErrorEnum {


    YoYoErrorType_NoError(""),

    YoYoErrorType_BLECentralFaild("未初始化蓝牙相关设置"),

    YoYoErrorType_BLECentralScanTimeOut("手机蓝牙连接外设超时"),

    YoYoErrorType_BLECentralConnectFaild("手机蓝牙连接外设失败"),

    YoYoErrorType_BLECentralDisConnectFaild("手机蓝牙断开连接外设失败"),

    YoYoErrorType_BLECentralStatePowerOnFaild("手机蓝牙未开启"),

    YoYoErrorType_BLECentralConnected("手机蓝牙中心已经连接的外设且传入外设为空"),

    YoYoErrorType_BLECentralPeriphralIsNil("手机蓝牙中心未有连接的外设且传入外设为空"),

    YoYoErrorType_QueryCardIDFaild("查询esim卡id失败"),

    YoYoErrorType_QueryCardStatueFaild("查询esim卡状态失败"),

    YoYoErrorType_WriteKiFaild("写入ki数据失败"),

    YoYoErrorType_ActivatingFaild("激活esim卡失败"),

    YoYoErrorType_ReleaseFaild("释放卡失败");


    private   String   errorDesc;

    YYErrorEnum(String desc){
        this.errorDesc   =   desc;
    }

    public   String   getDesc(){
        return   this.errorDesc;
    }

}
