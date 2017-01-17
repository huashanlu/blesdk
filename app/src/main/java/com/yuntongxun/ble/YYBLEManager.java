package com.yuntongxun.ble;

import java.util.List;

/**
 * Created by luhuashan on 16/12/16.
 */
public  interface YYBLEManager {




     void destory();


     void startScanPeripheral(OnScanPeripheralListener listener);

     void stopScanPeripheral();

     void connectPeripheral(YYBlueToothInfo info, OnConnectDeviceListener listener);

     void cancelConnectPeripheral(OnDisConnectDeviceListener listener);




     public void writeKI(byte[] ki,OnWriteKIListener listener);

     public void active(OnActiveCardListener listener);

     public void release(OnReleaseCardListener listener);


     void getEsimCardId(OnGetEsimCardListener listener);

     void getKICardState(OnGetCardStateListener listener);


    interface  BaseListener{


    }

    interface OnGetCardStateListener extends BaseListener{

        void onGetCardStateResult(YYError error,ESimCardState state);
    }


    public enum ESimCardState{

        ESimCardState_UnKnown ,

        ESimCardState_Main ,

        ESimCardState_Virtual ,

        ESimCardState_Network,
    }

    interface OnActiveCardListener extends BaseListener{

        void onActiveResult(YYError error);

    }
    interface OnReleaseCardListener extends BaseListener{

        void onReleaseResult(YYError error);

    }

    interface OnWriteKIListener extends BaseListener{

        void onWriteKIResult(YYError error);
    }


    interface  OnScanPeripheralListener extends  BaseListener{

        void onScanPeripheral(YYError error, List<YYBlueToothInfo> list);
    }

    interface OnConnectDeviceListener extends  BaseListener{

        void onConnectResult(YYError error);
    }

    interface  OnDisConnectDeviceListener extends BaseListener{

        void onDisConnectResult(YYError error);
    }


    interface OnGetEsimCardListener extends BaseListener{

        void onGetEsimCardId(YYError error,String cardId);
    }





    public enum YoYoMobileBLEConnectedState{
        BLEConnected_None,
        BLEConnected_connected,
        BLEConnected_disConnected,
    }




}
