package com.reactnativeobd;


import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.module.annotations.ReactModule;
import com.google.gson.Gson;
import com.reactnativeobd.models.Device;
import com.reactnativeobd.models.ObdData;
import com.reactnativeobd.services.BluetoothService;
import com.reactnativeobd.services.ObdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;


@ReactModule(name = ObdModule.NAME)
public class ObdModule extends ReactContextBaseJavaModule {
  public static final String NAME = "Obd";

  private final BluetoothService bluetoothService;


  public ObdModule(ReactApplicationContext context) {
    super(context);
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

  @ReactMethod
  public void connectDevice(String address) throws IOException {
    bluetoothService.connectDevice(address);
  }

  @ReactMethod
  public void disconnectDevice() throws IOException {
    bluetoothService.disconnectDevice();
  }

  @ReactMethod
  public void startLiveData() throws IOException {
    bluetoothService.startLiveData();
  }

  @ReactMethod
  public void stopLiveData() throws IOException {
    bluetoothService.stopLiveData();
  }






}
