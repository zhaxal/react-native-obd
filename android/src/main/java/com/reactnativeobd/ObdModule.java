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
import com.reactnativeobd.services.BluetoothService;
import com.reactnativeobd.services.ObdService;

import org.json.JSONException;

import java.io.IOException;


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
  public void trackRPM() {
    bluetoothService.trackRPM();
  }

  @ReactMethod
  public void connectDevice(String address) throws IOException {
    bluetoothService.connectDevice(address);
  }

  @ReactMethod
  public void socketCheck(Promise promise) throws IOException, InterruptedException {
    String res = bluetoothService.socketCheck();
    promise.resolve(res);
  }

  public static native String socketCheck();

  @ReactMethod
  public void isConnected(Promise promise) {
    Boolean res = bluetoothService.isConnected();
    promise.resolve(res);
  }

  public static native boolean isConnected();

  @ReactMethod
  public void getArrObj(Promise promise) {
    WritableMap object = new WritableNativeMap();
    object.putString("test", "test");
    object.putString("test1", "test1");

    WritableMap map = new WritableNativeMap();
    map.putMap("object", object);
    map.putMap("object1", object);

    promise.resolve(map);
  }

  public static native WritableMap getArrObj();

}
