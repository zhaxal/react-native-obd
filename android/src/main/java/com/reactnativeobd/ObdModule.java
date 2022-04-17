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

  private static WritableMap convertJsonToMap(JSONObject jsonObject) throws JSONException {
    WritableMap map = new WritableNativeMap();

    Iterator<String> iterator = jsonObject.keys();
    while (iterator.hasNext()) {
      String key = iterator.next();
      Object value = jsonObject.get(key);
      if (value instanceof JSONObject) {
        map.putMap(key, convertJsonToMap((JSONObject) value));
      } else if (value instanceof Boolean) {
        map.putBoolean(key, (Boolean) value);
      } else if (value instanceof Integer) {
        map.putInt(key, (Integer) value);
      } else if (value instanceof Double) {
        map.putDouble(key, (Double) value);
      } else if (value instanceof String) {
        map.putString(key, (String) value);
      } else {
        map.putString(key, value.toString());
      }
    }
    return map;
  }

  @ReactMethod
  public void getArrObj(Promise promise) throws JSONException {
    Gson g = new Gson();

    JSONObject jo = new JSONObject(g.toJson(new ObdData("1","blya", "200rpm")));
    WritableMap wm = convertJsonToMap(jo);
    WritableMap map = new WritableNativeMap();
    map.putMap("object", wm);

    promise.resolve(map);
  }

  public static native WritableMap getArrObj();

}
