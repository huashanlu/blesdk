package com.yuntongxun.ble.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.yuntongxun.ble.R;
import com.yuntongxun.ble.YYBLEManager;
import com.yuntongxun.ble.YYBleSDK;
import com.yuntongxun.ble.YYBlueToothInfo;
import com.yuntongxun.ble.YYError;
import com.yuntongxun.ble.utils.Hex;

import java.util.List;

public class SampleActivity extends Activity implements YYBLEManager.OnScanPeripheralListener, YYBLEManager.OnConnectDeviceListener {

    private static final String TAG = SampleActivity.class.getSimpleName();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void onClick(View v){
        switch (v.getId()){

            case R.id.bu_kaishi:
                YYBleSDK.initial(this);
                YYBleSDK.getYoYoBLECentralManager().startScanPeripheral(this);
                break;
            case R.id.bu_write:
                break;
            case R.id.bu_read:
                YYBleSDK.getYoYoBLECentralManager().getEsimCardId(new YYBLEManager.OnGetEsimCardListener() {
                    @Override
                    public void onGetEsimCardId(YYError error, String cardId) {
                        Log.e(TAG,"cardId =" +cardId);
                    }
                });

                break;
            case R.id.bu_write_ki:

                String b = "E08CA87956ECAAE7F47E9E1421392900A20ACD88B5DEEBD2B436C770BAE544E78FFC6D1CCB50CBF7A72A4AAD0B0EF5A122DE085EAF492D34E88D3D961F88F23FB06ECE508EAF83DD";
                byte[] s=  Hex.decodeHex(b.toCharArray());

                YYBleSDK.getYoYoBLECentralManager().writeKI(s, new YYBLEManager.OnWriteKIListener() {
                    @Override
                    public void onWriteKIResult(YYError error) {
                        Log.e(TAG,error.errorMsg);
                    }
                });

                break;
            case R.id.bu_jihuo:
                YYBleSDK.getYoYoBLECentralManager().active(new YYBLEManager.OnActiveCardListener() {
                    @Override
                    public void onActiveResult(YYError error) {

                    }
                });
                break;
            case R.id.bu_shifang:
                YYBleSDK.getYoYoBLECentralManager().release(new YYBLEManager.OnReleaseCardListener() {
                    @Override
                    public void onReleaseResult(YYError error) {

                    }
                });
                break;

        }

    }
    @Override
    public void onScanPeripheral(YYError error, List<YYBlueToothInfo> list) {

        YYBleSDK.getYoYoBLECentralManager().connectPeripheral(list.get(0),this);

    }

    @Override
    public void onConnectResult(YYError error) {



    }
}
