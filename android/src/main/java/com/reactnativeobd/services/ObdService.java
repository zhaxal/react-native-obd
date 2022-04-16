package com.reactnativeobd.services;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.github.pires.obd.commands.engine.RPMCommand;

import java.io.IOException;

public class ObdService {
  private BluetoothSocket socket;
  private ReactApplicationContext context;
  private RPMCommand rpmCommand;

  public ObdService(BluetoothService bluetoothService, ReactApplicationContext context){
    this.socket = bluetoothService.socket;
    this.rpmCommand = new RPMCommand();
    this.context = context;
  }

  private final Runnable rpmRunnable = new Runnable() {
    public void run() {
      try {
        rpmCommand.run(socket.getInputStream(), socket.getOutputStream());
        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("rpmUpdate", rpmCommand.getFormattedResult());
      } catch (IOException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      // run again in period defined in preferences
      new Handler().postDelayed(rpmRunnable, 1000);
    }
  };

  public void trackRPM(){
    new Handler().post(rpmRunnable);
  }
}
