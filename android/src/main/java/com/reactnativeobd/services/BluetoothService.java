package com.reactnativeobd.services;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.github.pires.obd.commands.ObdCommand;
import com.google.gson.Gson;
import com.reactnativeobd.models.Device;
import com.reactnativeobd.models.ObdData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class BluetoothService {
  private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
  private final static int REQUEST_ENABLE_BT = 1;
  private final BluetoothAdapter bluetoothAdapter;
  private Set<BluetoothDevice> pairedDevices;
  private final ReactApplicationContext context;
  private BluetoothSocket socket;
  private Handler handler;

  private final Runnable dataRunnable = new Runnable() {
    public void run() {
      try {
        Gson g = new Gson();
        WritableMap data = new WritableNativeMap();

        ArrayList<ObdCommand> commands = ObdService.getCommands();
        for (int i = 0; i < commands.size(); i++) {
          ObdCommand command = commands.get(i);
          command.run(socket.getInputStream(), socket.getOutputStream());
          JSONObject jo = new JSONObject(g.toJson(new ObdData(command.getName(), command.getFormattedResult())));
          WritableMap wm = convertJsonToMap(jo);
          data.putMap(ObdService.LookUpCommand(command.getName()), wm);
        }

        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("liveData", data);
      } catch (IOException | InterruptedException | JSONException e) {
        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("liveDataErr", e.getMessage());
        stopLiveData();
        e.printStackTrace();
      }

      new Handler().postDelayed(dataRunnable, 1000);
    }
  };

  private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context cxt, Intent intent) {
      String action = intent.getAction();

      if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("bluetoothConnected", true);
        //Device is now connected
      } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("bluetoothConnected", false);
        //Device has disconnected
      }
    }
  };

  public BluetoothService(ReactApplicationContext context) {
    this.context = context;
    BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
    initBluetoothListener();
    this.bluetoothAdapter = bluetoothManager.getAdapter();

  }

  private void initBluetoothListener() {
    IntentFilter filter = new IntentFilter();
    filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
    filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
    filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
    this.context.registerReceiver(mReceiver, filter);
  }

  public void enableBluetooth() {
    if (!bluetoothAdapter.isEnabled()) {
      Activity activity = context.getCurrentActivity();
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }
  }

  public WritableArray getPairedDevices() {

    pairedDevices = bluetoothAdapter.getBondedDevices();
    Gson g = new Gson();
    WritableArray array = new WritableNativeArray();
    try {
      if (pairedDevices.size() > 0) {
        // There are paired devices. Get the name and address of each paired device.
        for (BluetoothDevice device : pairedDevices) {
          String deviceName = device.getName();
          String deviceHardwareAddress = device.getAddress(); // MAC address
          JSONObject jo = null;

          jo = new JSONObject(g.toJson(new Device(deviceName, deviceHardwareAddress)));

          WritableMap wm = convertJsonToMap(jo);
          array.pushMap(wm);
        }

      }

    } catch (JSONException e) {
      e.printStackTrace();
    }

    return array;

  }

  private void runInitCommands() throws IOException, InterruptedException {
    ArrayList<ObdCommand> commands = ObdService.getInitCommands();
    for (int i = 0; i < commands.size(); i++) {
      ObdCommand command = commands.get(i);
      command.run(socket.getInputStream(), socket.getOutputStream());
    }
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

  public void connectDevice(String address) {
    try {
      if (pairedDevices.size() > 0) {
        // There are paired devices. Get the name and address of each paired device.
        for (BluetoothDevice device : pairedDevices) {
          String deviceHardwareAddress = device.getAddress(); // MAC address
          if (address.equals(deviceHardwareAddress)) {

            socket = device.createRfcommSocketToServiceRecord(MY_UUID);

            socket.connect();

            runInitCommands();
          }
        }

      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void disconnectDevice() {
    try {
      socket.close();
      stopLiveData();
      socket = null;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void startLiveData() {
    handler = new Handler();
    handler.post(dataRunnable);
  }

  public void stopLiveData() {

    handler.removeCallbacks(dataRunnable);

  }
}
