package com.yuntongxun.ble;

import android.bluetooth.BluetoothDevice;

/**
 * Created by luhuashan on 16/12/16.
 */
public class YYBlueToothInfo {


    public String name;

    public BluetoothDevice device;

    public String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        YYBlueToothInfo that = (YYBlueToothInfo) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return !(device != null ? !device.equals(that.device) : that.device != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (device != null ? device.hashCode() : 0);
        return result;
    }
}
