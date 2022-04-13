package com.reactnativeobd;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;


import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name = ObdModule.NAME)
public class ObdModule extends ReactContextBaseJavaModule {
  private final static int REQUEST_ENABLE_BT = 1;
  public static final String NAME = "Obd";
  private BluetoothAdapter bluetoothAdapter;
  private BluetoothManager bluetoothManager;
  ReactApplicationContext context;

  public ObdModule(ReactApplicationContext context) {
    super(context);
    this.context = context;
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }


  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  public void multiply(int a, int b, Promise promise) {
    promise.resolve(a * b);
  }

  public static native int nativeMultiply(int a, int b);

  @ReactMethod
  public void initializeBluetooth(Promise promise) {
    bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
    bluetoothAdapter = bluetoothManager.getAdapter();
    if (bluetoothAdapter == null) {
      promise.resolve(false);

    }else{
      enableBluetooth();
      promise.resolve(true);
    }
  }

  public static native boolean initializeBluetooth();

  public void enableBluetooth() {
    if (!bluetoothAdapter.isEnabled()) {
      Activity activity = context.getCurrentActivity();
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

  }
}
