package com.reactnativeobd;


import androidx.annotation.NonNull;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import com.facebook.react.bridge.WritableArray;
import com.facebook.react.module.annotations.ReactModule;
import com.reactnativeobd.services.BluetoothService;

import org.json.JSONException;


@ReactModule(name = ObdModule.NAME)
public class ObdModule extends ReactContextBaseJavaModule {
  public static final String NAME = "Obd";

  private BluetoothService bluetoothService;
  private ReactApplicationContext context;

  public ObdModule(ReactApplicationContext context) {
    super(context);
    this.context = context;
    this.bluetoothService = new BluetoothService(context);

  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }


  @ReactMethod
  public void getPairedDevices(Promise promise) throws JSONException {
    WritableArray res = bluetoothService.getPairedDevices();
    promise.resolve(res);
  }

  public static native WritableArray getPairedDevices();

  @ReactMethod
  public void enableBluetooth() {
    bluetoothService.enableBluetooth();
  }



}
