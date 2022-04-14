package com.reactnativeobd;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;


import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.module.annotations.ReactModule;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ReactModule(name = ObdModule.NAME)
public class ObdModule extends ReactContextBaseJavaModule {
  private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
  private final static int REQUEST_ENABLE_BT = 1;
  public static final String NAME = "Obd";
  private BluetoothAdapter bluetoothAdapter;
  private BluetoothManager bluetoothManager;
  private final List<BluetoothDevice> scannedDevices;
  BroadcastReceiver mReceiver;
  private int testValue = 0;
  ReactApplicationContext context;

  public ObdModule(ReactApplicationContext context) {
    super(context);
    this.context = context;
    this.scannedDevices = new ArrayList<BluetoothDevice>();
  }

  private void enableBluetooth() {
    if (!bluetoothAdapter.isEnabled()) {
      Activity activity = context.getCurrentActivity();
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

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
    } else {
      enableBluetooth();
      promise.resolve(true);
    }
  }

  public static native boolean initializeBluetooth();

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
  public void getPairedDevices(Promise promise) throws JSONException {
    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
    List<Device> returnList = new ArrayList<>();
    Gson g = new Gson();

    WritableArray array = new WritableNativeArray();

    if (pairedDevices.size() > 0) {
      // There are paired devices. Get the name and address of each paired device.
      for (BluetoothDevice device : pairedDevices) {
        String deviceName = device.getName();
        String deviceHardwareAddress = device.getAddress(); // MAC address

        returnList.add(new Device(deviceName, deviceHardwareAddress));

      }

      for (Device d : returnList) {
        JSONObject jo = new JSONObject(g.toJson(d));
        WritableMap wm = convertJsonToMap(jo);
        array.pushMap(wm);
      }

    } else {
      promise.resolve(array);
    }
  }

  public static native WritableArray getPairedDevices();


  @ReactMethod
  public void startScan() {
    Activity activity = context.getCurrentActivity();
    int requestCode = 1;
    Intent discoverableIntent =
      new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
    activity.startActivityForResult(discoverableIntent, requestCode);

    bluetoothAdapter.startDiscovery();
    mReceiver = new BroadcastReceiver() {
      public void onReceive(Context context, Intent intent) {
        if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
          BluetoothDevice deviceInfo = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
          scannedDevices.add(deviceInfo);
        }
      }
    };

    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    context.registerReceiver(mReceiver, filter);

    bluetoothAdapter.startDiscovery();
  }


  @ReactMethod
  public void stopScan() {

    bluetoothAdapter.cancelDiscovery();
    context.unregisterReceiver(mReceiver);
  }


  @ReactMethod
  public void getScannedDevices(Promise promise) throws JSONException {
    List<Device> returnList = new ArrayList<>();
    Gson g = new Gson();

    WritableArray array = new WritableNativeArray();

    if (scannedDevices.size() > 0) {
      // There are paired devices. Get the name and address of each paired device.
      for (BluetoothDevice device : scannedDevices) {
        String deviceName = device.getName();
        String deviceHardwareAddress = device.getAddress(); // MAC address

        returnList.add(new Device(deviceName, deviceHardwareAddress));

      }

      for (Device d : returnList) {
        JSONObject jo = new JSONObject(g.toJson(d));
        WritableMap wm = convertJsonToMap(jo);
        array.pushMap(wm);
      }

    } else {
      promise.resolve(array);
    }

  }

  public static native WritableArray getScannedDevices();

  @ReactMethod
  public void updateValue(int value){
    testValue = value;
  }

  @ReactMethod
  public void getValue(Promise promise){
    promise.resolve(testValue);
  }

  public static native int getValue();


}
