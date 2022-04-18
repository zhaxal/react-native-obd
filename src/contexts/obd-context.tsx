import React, { useEffect } from 'react';
import { createContext, useContext, useState } from 'react';
import { Obd } from '../index';
import { NativeEventEmitter } from 'react-native';

const ObdContext = createContext({
  status: 'loading',
  data: null,
  dataErr: null,
  getPairedDevices: () => {},
  enableBluetooth: () => {},
  connectDevice: (address: string) => {
    return Obd.connectDevice(address);
  },
  disconnectDevice: () => {},
  startLiveData: () => {},
  stopLiveData: () => {},
});

export function useObd() {
  return useContext(ObdContext);
}

export function ObdProvider(props: { children: {} }) {
  const [status, setStatus] = useState('loading');
  const [data, setData] = useState(null);
  const [dataErr, setDataErr] = useState(null);

  useEffect(() => {
    const eventEmitter = new NativeEventEmitter(Obd);
    const bluetoothListener = eventEmitter.addListener(
      'bluetoothConnected',
      (isConnected) => {
        setStatus(isConnected);
      }
    );

    const dataListener = eventEmitter.addListener('liveData', (liveData) => {
      setData(liveData);
    });

    const dataErrListener = eventEmitter.addListener(
      'liveDataErr',
      (liveDataErr) => {
        setDataErr(liveDataErr);
      }
    );

    return function cleanup() {
      bluetoothListener.remove();
      dataListener.remove();
      dataErrListener.remove();
    };
  }, []);

  function getPairedDevices(): Promise<Array<object>> {
    return Obd.getPairedDevices();
  }

  function enableBluetooth(): void {
    return Obd.enableBluetooth();
  }

  function connectDevice(address: string): void {
    return Obd.connectDevice(address);
  }

  function disconnectDevice(): void {
    return Obd.disconnectDevice();
  }

  function startLiveData(): void {
    return Obd.startLiveData();
  }

  function stopLiveData(): void {
    return Obd.stopLiveData();
  }

  const context = {
    status,
    data,
    dataErr,
    getPairedDevices,
    enableBluetooth,
    connectDevice,
    startLiveData,
    stopLiveData,
    disconnectDevice,
  };
  return (
    <ObdContext.Provider value={context}>{props.children}</ObdContext.Provider>
  );
}
