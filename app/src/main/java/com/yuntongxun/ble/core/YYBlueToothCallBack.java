package com.yuntongxun.ble.core;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import com.yuntongxun.ble.OnYYblueStateListener;
import com.yuntongxun.ble.log.YYLogger;

/**
 * Created by luhuashan on 16/12/21.
 */
public class YYBlueToothCallBack extends BluetoothGattCallback {


    public static final String TAG = YYLogger.getLogger(YYBlueToothCallBack.class);


    private OnYYblueStateListener listener;

    public void setListener(OnYYblueStateListener listener) {
        this.listener = listener;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        YYLogger.d(TAG,"onConnectionStateChange---state ="+status+"---newstate="+newState);
        if(listener!=null&&newState==2){
            listener.onConnectionStateChange(gatt, status, newState);
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        YYLogger.d(TAG,"onServicesDiscovered--status="+status);
        if(listener!=null){
            listener.onServicesDiscovered(gatt, status);
        }

    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        YYLogger.d(TAG,"onCharacteristicRead");
        if(listener!=null){
            listener.onCharacteristicRead(gatt, characteristic, status);
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

        YYLogger.d(TAG,"onCharacteristicWrite");
        if(status==0){
            if(listener!=null){
                listener.onCharacteristicWrite(gatt, characteristic, status);
            }
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

        YYLogger.d(TAG,"onCharacteristicChanged");

        if(listener!=null){
            listener.onCharacteristicChanged(gatt, characteristic);
        }
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

        YYLogger.d(TAG,"onDescriptorRead");

        if(listener!=null){
            listener.onDescriptorRead(gatt, descriptor, status);
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

        YYLogger.d(TAG,"onDescriptorWrite");
        if(listener!=null){
            listener.onDescriptorWrite(gatt, descriptor, status);
        }
    }

    @Override
    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {

        YYLogger.d(TAG,"onReliableWriteCompleted");
        if(listener!=null){
            listener.onReliableWriteCompleted(gatt, status);
        }
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {

        YYLogger.d(TAG,"onReadRemoteRssi");
        if(listener!=null){
            listener.onReadRemoteRssi(gatt, rssi, status);
        }
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {

        YYLogger.d(TAG,"onMtuChanged");
        if(listener!=null){
            listener.onMtuChanged(gatt, mtu, status);
        }

    }







}
