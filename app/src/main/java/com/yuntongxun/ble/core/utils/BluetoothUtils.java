package com.yuntongxun.ble.core.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

public final class BluetoothUtils
{
  private final Context mContext;
  private final BluetoothAdapter mBluetoothAdapter;


  public BluetoothUtils(Context paramContext)
  {
    this.mContext = paramContext;
    BluetoothManager localBluetoothManager = (BluetoothManager)this.mContext.getSystemService(Context.BLUETOOTH_SERVICE);
    this.mBluetoothAdapter = localBluetoothManager.getAdapter();
  }

  public BluetoothAdapter getBluetoothAdapter()
  {
    return this.mBluetoothAdapter;
  }

  public boolean isBluetoothLeSupported()
  {
    return this.mContext.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le");
  }

  public boolean isBluetoothOn()
  {
    if (this.mBluetoothAdapter == null)
      return false;
    return this.mBluetoothAdapter.isEnabled();
  }


}