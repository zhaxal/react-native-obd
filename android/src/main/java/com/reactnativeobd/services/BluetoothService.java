package com.reactnativeobd.services;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.google.gson.Gson;
import com.reactnativeobd.Device;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class BluetoothService {
  private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
  private final static int REQUEST_ENABLE_BT = 1;
  private BluetoothAdapter bluetoothAdapter;
  private Set<BluetoothDevice> pairedDevices;
  private ReactApplicationContext context;

  public BluetoothService(ReactApplicationContext context) {
    this.context = context;
    BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
    this.bluetoothAdapter = bluetoothManager.getAdapter();
  }

  private void enableBluetooth() {
    Activity activity = context.getCurrentActivity();
    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
  }

  public WritableArray getPairedDevices() throws JSONException {
    if (!bluetoothAdapter.isEnabled()) {
      enableBluetooth();
    }
    pairedDevices = bluetoothAdapter.getBondedDevices();
    Gson g = new Gson();
    WritableArray array = new WritableNativeArray();

    if (pairedDevices.size() > 0) {
      // There are paired devices. Get the name and address of each paired device.
      for (BluetoothDevice device : pairedDevices) {
        String deviceName = device.getName();
        String deviceHardwareAddress = device.getAddress(); // MAC address
        JSONObject jo = new JSONObject(g.toJson(new Device(deviceName, deviceHardwareAddress)));
        WritableMap wm = convertJsonToMap(jo);
        array.pushMap(wm);
      }

    }
    return array;
  }

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
}