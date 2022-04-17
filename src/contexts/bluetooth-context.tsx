import React, { useEffect } from 'react';
import { createContext, useContext, useState } from 'react';
import { Obd } from '../index';
import { NativeEventEmitter } from 'react-native';

const BluetoothContext = createContext({
  status: 'loading',
});

export function useBluetooth() {
  return useContext(BluetoothContext);
}

export function BluetoothProvider(props: { children: {} }) {
  const [status, setStatus] = useState('loading');

  useEffect(() => {
    const eventEmitter = new NativeEventEmitter(Obd);
    const eventListener = eventEmitter.addListener(
      'bluetoothConnected',
      (isConnected) => {
        setStatus(isConnected);
      }
    );

    return function cleanup() {
      eventListener.remove();
    };
  }, []);

  const context = {
    status,
  };
  return (
    <BluetoothContext.Provider value={context}>
      {props.children}
    </BluetoothContext.Provider>
  );
}
