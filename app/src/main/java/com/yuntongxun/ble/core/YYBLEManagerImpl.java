package com.yuntongxun.ble.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.yuntongxun.ble.OnYYblueStateListener;
import com.yuntongxun.ble.YYBLEManager;
import com.yuntongxun.ble.YYBlueToothInfo;
import com.yuntongxun.ble.YYError;
import com.yuntongxun.ble.core.jni.IYYNative;
import com.yuntongxun.ble.core.utils.BluetoothUtils;
import com.yuntongxun.ble.core.utils.ECSDKUtils;
import com.yuntongxun.ble.log.YYLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by luhuashan on 16/12/16.
 */
public class YYBLEManagerImpl implements YYBLEManager, OnYYblueStateListener {


    private static final String TAG = YYLogger.getLogger(YYBLEManagerImpl.class);
    private static volatile YYBLEManagerImpl sInstance;

    private static boolean isScanning = false;
    private BluetoothUtils bluetoothUtils;
    private OnScanPeripheralListener onScanPeripheralListener;

    private BluetoothAdapter mBluetoothAdapter;
    private Context context;
    private BluetoothGatt gatt;

    private static Set<BluetoothDevice> set = new LinkedHashSet<BluetoothDevice>();

    private static List<YYBlueToothInfo> list = new ArrayList<YYBlueToothInfo>();

    private YYBlueToothCallBack blueToothCallBack = new YYBlueToothCallBack();


    private BluetoothGattCharacteristic mWriteCharacteristic = null;
    private BluetoothGattCharacteristic mReadCharacteristic = null;

    private final Semaphore semTrans = new Semaphore(1, true);

    private volatile boolean bWriteSuccess = false;

    public static YYBLEManagerImpl getInstance() {
        if (sInstance == null) {
            synchronized (YYBLEManagerImpl.class) {
                sInstance = new YYBLEManagerImpl();
            }
        }
        return sInstance;
    }

    @Override
    public void destory() {

        if (set != null) {
            set.clear();
            set = null;
        }
        if (list != null) {
            list.clear();
            list = null;
        }
        if (gatt != null) {
            gatt.disconnect();
            gatt.close();
            gatt = null;
        }
        mWriteCharacteristic = null;
        mReadCharacteristic = null;


        isScanning = false;
        bluetoothUtils = null;
        context = null;

        blueToothCallBack = null;

        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.disable();
            mBluetoothAdapter = null;
        }

        onConnectDeviceListener = null;
        onGetCardStateListener = null;
        onActiveCardListener = null;
        onGetEsimCardListener = null;

        onReleaseCardListener = null;
        onScanPeripheralListener = null;
        onWriteKIListener = null;

        sInstance = null;

    }


    private YYBLEManagerImpl() {
        this.context = YYSDKCoreCache.getInstance().getContext();
        this.bluetoothUtils = new BluetoothUtils(context);
        BluetoothManager localBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.mBluetoothAdapter = localBluetoothManager.getAdapter();
        blueToothCallBack.setListener(this);
    }


    private final BluetoothAdapter.LeScanCallback callback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            YYLogger.d(TAG, "onLeScan ---------------------");
            if (Build.VERSION.SDK_INT < 21) {
                if (device.getName() != null && device.getName().startsWith("iSky")) {
                    if (!set.contains(device)) {
                        YYLogger.d(TAG, "found ble card lower 21 ---------------------");
                        set.add(device);
                        YYBlueToothInfo blueToothInfo = new YYBlueToothInfo();
                        blueToothInfo.setDevice(device);
                        blueToothInfo.setName(ECSDKUtils.nullAsNil(device.getName()));
                        blueToothInfo.setAddress(ECSDKUtils.nullAsNil(device.getAddress()));
                        list.add(blueToothInfo);
                        stopScanPeripheral();
                        if (onScanPeripheralListener != null) {
                            onScanPeripheralListener.onScanPeripheral(ECSDKUtils.buildError(200), list);
                        }
                    }
                    YYLogger.d(TAG, "onLeScan,address is%s,name is%s ,rc is %d", device.getAddress(), device.getName(), rssi);

                }

            } else {
                if (!set.contains(device)) {
                    set.add(device);
                    YYLogger.d(TAG, "found ble card more than 21 ---------------------");
                    YYBlueToothInfo blueToothInfo = new YYBlueToothInfo();
                    blueToothInfo.setDevice(device);
                    blueToothInfo.setName(ECSDKUtils.nullAsNil(device.getName()));
                    blueToothInfo.setAddress(ECSDKUtils.nullAsNil(device.getAddress()));
                    list.add(blueToothInfo);
                    stopScanPeripheral();
                    if (onScanPeripheralListener != null) {
                        onScanPeripheralListener.onScanPeripheral(ECSDKUtils.buildError(200), list);
                    }
                }
            }
            YYLogger.d(TAG, "onLeScan,address is%s,name is%s,rs is %d ", device.getAddress(), device.getName(), rssi);

        }
    };


    public void clear() {
        if (list != null) {
            list.clear();
        }
        if (set != null) {
            set.clear();
        }
    }


    @Override
    public void startScanPeripheral(OnScanPeripheralListener listener) {
        YYLogger.e(TAG, "startScanPeripheral");
        this.onScanPeripheralListener = listener;
        if (mBluetoothAdapter == null) {
            return;
        }
        isScanning = true;
        if (Build.VERSION.SDK_INT < 21) {
            mBluetoothAdapter.startLeScan(callback);
        } else {
            mBluetoothAdapter.startLeScan(YYFileds.UUIDS, callback);
            new ECHandlerHelper().postDelayedRunnOnThead(new Runnable() {
                @Override
                public void run() {
                    if (list != null && list.size() == 0 && onScanPeripheralListener != null)
                        onScanPeripheralListener.onScanPeripheral(ECSDKUtils.buildError(9966), list);//未扫描到设备
                }
            }, 20000);
        }
        //定时


    }

    @Override
    public void stopScanPeripheral() {
        isScanning = false;
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.stopLeScan(callback);

        }

    }


    private OnConnectDeviceListener onConnectDeviceListener;

    @Override
    public void connectPeripheral(YYBlueToothInfo info, OnConnectDeviceListener listener) {
        YYLogger.e(TAG, "connectPeripheral");
        this.onConnectDeviceListener = listener;
        if (info == null || info.getDevice() == null) {
            return;
        }
        if (mBluetoothAdapter != null) {
            gatt = info.getDevice().connectGatt(context, true, blueToothCallBack);


        }
    }

    @Override
    public void cancelConnectPeripheral(OnDisConnectDeviceListener listener) {

    }

    private OnGetEsimCardListener onGetEsimCardListener;

    @Override
    public void getEsimCardId(OnGetEsimCardListener listener) {
        YYLogger.e(TAG, "getEsimCardId-----------");
        this.onGetEsimCardListener = listener;
        if (mWriteCharacteristic != null) {
            writeValue(mWriteCharacteristic, IYYNative.buildSimCardID());
        }


    }

    public void setCharacteristicNotification() {
        if (gatt != null) {
            gatt.setCharacteristicNotification(mReadCharacteristic, true);
        }
    }


    private OnWriteKIListener onWriteKIListener;

    @Override
    public void writeKI(byte[] ki, OnWriteKIListener listener) {
        YYLogger.e(TAG, "write ki-------");
        this.onWriteKIListener = listener;
        if (mWriteCharacteristic != null) {
            YYLogger.i(TAG, "begin writeKI");
            writeValue(mWriteCharacteristic, IYYNative.getSimCardKICmd(ki));
        }


    }

    private OnActiveCardListener onActiveCardListener;
    private OnReleaseCardListener onReleaseCardListener;

    @Override
    public void active(OnActiveCardListener listener) {
        YYLogger.e(TAG, "active card ----------");
        this.onActiveCardListener = listener;

        byte[] b = IYYNative.getActiveSimCardCmd();
        writeValue(mWriteCharacteristic, b);

    }

    @Override
    public void release(OnReleaseCardListener listener) {

        YYLogger.e(TAG, "release card --------------");
        this.onReleaseCardListener = listener;
        byte[] b = IYYNative.getReleaseSimCardCmd();
        writeValue(mWriteCharacteristic, b);

    }


    private synchronized void writeValue(BluetoothGattCharacteristic paramBluetoothGattCharacteristic, byte[] paramArrayOfByte) {
        if ((this.mBluetoothAdapter == null) || (this.gatt == null)) {
            return;
        }
        setCharacteristicNotification();
        byte[] arrayOfByte1 = paramArrayOfByte;
        int i = paramArrayOfByte.length;
        for (int j = 0; i > 0; j++) {
            this.bWriteSuccess = false;
            try {
                this.semTrans.acquire();
            } catch (Exception localException1) {
                localException1.printStackTrace();
                this.semTrans.release();
            }
            byte[] arrayOfByte2;
            int k;
            if (i > 20) {
                arrayOfByte2 = new byte[20];
                for (k = 0; k < 20; k++) {
                    arrayOfByte2[k] = arrayOfByte1[(j * 20 + k)];
                    i--;
                }
                paramBluetoothGattCharacteristic.setValue(arrayOfByte2);
            } else {
                arrayOfByte2 = new byte[i];
                for (k = 0; k < i; k++)
                    arrayOfByte2[k] = arrayOfByte1[(j * 20 + k)];
                paramBluetoothGattCharacteristic.setValue(arrayOfByte2);
                i = 0;
            }
            boolean isTrue = this.gatt.writeCharacteristic(paramBluetoothGattCharacteristic);
            YYLogger.d(TAG, "write value =" + isTrue);
            try {
                if (!this.semTrans.tryAcquire(1L, TimeUnit.SECONDS))
//                    this.gatt.writeCharacteristic(paramBluetoothGattCharacteristic);
                    if (!this.bWriteSuccess) {
//                    this.gatt.writeCharacteristic(paramBluetoothGattCharacteristic);
                        if (!this.semTrans.tryAcquire(1L, TimeUnit.SECONDS)) {

//                        this.gatt.writeCharacteristic(paramBluetoothGattCharacteristic);
                        }
                    }
            } catch (Exception localException2) {
                localException2.printStackTrace();
            } finally {
                this.semTrans.release();
            }
        }
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

        if (newState == BluetoothProfile.STATE_CONNECTED) {
            this.gatt.discoverServices();

        }

    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {

        if (status == 0) {
            discoverService();
            if (onConnectDeviceListener != null) {
                YYLogger.e(TAG, "call onconnectcallback-----");

                ECHandlerHelper.postRunnOnUI(new Runnable() {
                    @Override
                    public void run() {
                        onConnectDeviceListener.onConnectResult(ECSDKUtils.buildNoError());
                    }
                });


            }
        }

    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {


        byte[] rc = IYYNative.receiveBlueToothData(characteristic.getValue());
        YYLogger.d(TAG, "msg =" + Arrays
                .toString(rc));
//
        Log.e(TAG, IYYNative.getCmdTypeDecodeData(rc));


    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

    }

    @Override
    public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {

        if (characteristic != null) {
            YYLogger.d(TAG, "write value found callback-------" + Thread.currentThread().getName());

            ECHandlerHelper.postRunnOnUI(new Runnable() {
                @Override
                public void run() {
                    handleCallBack(gatt, characteristic);
                }
            });


        }
    }


    private void handleCallBack(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic characteristic) {

        YYLogger.d(TAG, "on write callback-----------------");
        byte[] response = characteristic.getValue();
        byte[] rc = IYYNative.receiveBlueToothData(response);
        YYLogger.d(TAG, "The second byte is " + rc[1]);


//        if(IYYNative.isValidData(rc)){
        byte bit = rc[1];
        YYLogger.d(TAG, "handleCallBack found bit is %d", bit);
        switch (bit) {
            case 52:
                YYLogger.d(TAG, "on get cardid callback -----");
                handleGetID(rc);
                break;
            case 53:
                YYLogger.d(TAG, "on write card callback -----");
                handleWriteCard(rc);
                break;
            case 55:
                YYLogger.d(TAG, "on active card callback -----");
                handActive(rc);
                break;
            case 56:
                YYLogger.d(TAG, "on release card callback -----");
                handleRelease(rc);
                break;
            case 70:

                writeValue(mWriteCharacteristic, IYYNative.buildState());

                break;
            case 71:

                handleGetState(rc);

                break;

        }


//        }


    }

    private void handleGetState(byte[] rc) {

        YYLogger.e(TAG, "onhandleGetState -------");
        byte bit = rc[3];
        ESimCardState state = null;
        YYError error = ECSDKUtils.buildNoError();
        if (bit == 1) {
            error.errorMsg = "联网";
            state = ESimCardState.ESimCardState_Network;

        } else if (bit == 0) {
            error.errorCode = -999;
            state = ESimCardState.ESimCardState_Main;
        } else {
            error.errorCode = -111;
            state = ESimCardState.ESimCardState_UnKnown;
        }

        if (onGetCardStateListener != null) {
            onGetCardStateListener.onGetCardStateResult(error, state);
        }

    }

    private OnGetCardStateListener onGetCardStateListener;

    public void getKICardState(OnGetCardStateListener listener) {
        YYLogger.e(TAG, "getKICardState-----------");
        this.onGetCardStateListener = listener;

        writeValue(mWriteCharacteristic, IYYNative.buildState());

    }


    private void handleRelease(byte[] rc) {

        YYLogger.e(TAG, "on");
        byte bit = rc[3];

        YYError error = ECSDKUtils.buildNoError();

        if (bit == 0x66) {
            error.errorCode = 0;
            error.errorMsg = "释放卡成功";

        } else {
            error.errorCode = -2;
            error.errorMsg = "释放卡失败";

        }
        if (onReleaseCardListener != null) {
            onReleaseCardListener.onReleaseResult(error);
        }

    }

    private void handActive(byte[] rc) {

        byte bit = rc[3];

        YYError error = ECSDKUtils.buildNoError();

        if (bit == 0x66) {

            error.errorMsg = "激活卡成功";

        } else {
            error.errorCode = -1;
            error.errorMsg = "激活卡失败";

        }
        if (onActiveCardListener != null) {
            onActiveCardListener.onActiveResult(error);
        }
    }

    private void handleWriteCard(byte[] rc) {

        byte bit = rc[3];

        YYError error = ECSDKUtils.buildNoError();

        if (bit == 0x66) {

            error.errorMsg = "写入ki数据成功";

        } else {
            error.errorCode = -3;
            error.errorMsg = "写入ki数据失败";

        }
        if (onWriteKIListener != null) {
            onWriteKIListener.onWriteKIResult(error);
        }


    }

    private void handleGetID(byte[] rc) {
        String cardId = IYYNative.getCmdTypeDecodeData(rc);
        YYLogger.e(TAG, "on get cardid is %s", cardId);
        if (onGetEsimCardListener != null) {
            onGetEsimCardListener.onGetEsimCardId(ECSDKUtils.buildError(0), cardId);
        }

    }


    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

    }

    @Override
    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {

    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {

    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {

    }


    private void setCharacteristicNotification(BluetoothGattCharacteristic paramBluetoothGattCharacteristic, boolean paramBoolean) {
        if ((this.mBluetoothAdapter == null) || (gatt == null)) {
            return;
        }
        if (gatt.setCharacteristicNotification(paramBluetoothGattCharacteristic, paramBoolean) == true)
            YYLogger.d(TAG, "setCharacteristicNotification Success.");
        else
            YYLogger.e(TAG, "setCharacteristicNotification Failed.");
        List localList = paramBluetoothGattCharacteristic.getDescriptors();
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext()) {
            BluetoothGattDescriptor localBluetoothGattDescriptor = (BluetoothGattDescriptor) localIterator.next();
            if (0 != (paramBluetoothGattCharacteristic.getProperties() & 0x20)) {
                if (localBluetoothGattDescriptor != null) {
                    Log.d(TAG, "Characteristic (" + paramBluetoothGattCharacteristic.getUuid() + ") is INDICATE");
                    localBluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                    gatt.writeDescriptor(localBluetoothGattDescriptor);
                }
            } else if (localBluetoothGattDescriptor != null) {
                Log.d(TAG, "Characteristic (" + paramBluetoothGattCharacteristic.getUuid() + ") is NOTIFY");
                localBluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(localBluetoothGattDescriptor);
            }
        }
    }


    private void discoverService() {
        List localList1 = getSupportedGattServices();
        if (localList1 == null) {
            return;
        }
        String str = null;
        Iterator localIterator1 = localList1.iterator();
        while (localIterator1.hasNext()) {
            BluetoothGattService localBluetoothGattService = (BluetoothGattService) localIterator1.next();
            str = localBluetoothGattService.getUuid().toString();
            List localList2;
            Iterator localIterator2;
            BluetoothGattCharacteristic localBluetoothGattCharacteristic;
            if (str.equalsIgnoreCase(YYFileds.YY_SERVICE_UUID)) {
                localList2 = localBluetoothGattService.getCharacteristics();
                localIterator2 = localList2.iterator();
                while (localIterator2.hasNext()) {
                    localBluetoothGattCharacteristic = (BluetoothGattCharacteristic) localIterator2.next();
                    str = localBluetoothGattCharacteristic.getUuid().toString();
                    if (str.equalsIgnoreCase(YYFileds.YY_READ_CHARACTERISTIC_UUID)) {
                        mReadCharacteristic = localBluetoothGattCharacteristic;


                        setCharacteristicNotification(mReadCharacteristic, true);
                        YYLogger.d(TAG, " has found read channel");
                    }
                    if (str.equalsIgnoreCase(YYFileds.YY_WRITE_CHARACTERISTIC_UUID)) {
                        this.mWriteCharacteristic = localBluetoothGattCharacteristic;
                        YYLogger.d(TAG, " has found write channel");


                    }
                    if (!str.equalsIgnoreCase(YYFileds.YY_CHARACTERISTIC_UUID)) ;
                }

            }
            if (str.equalsIgnoreCase(YYFileds.YY_SERVICE_UUID)) {
            }
        }
    }

    private List getSupportedGattServices() {
        if (this.gatt == null)
            return null;
        return this.gatt.getServices();
    }
}
