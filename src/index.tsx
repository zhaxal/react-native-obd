import { NativeModules, Platform } from 'react-native';
import { BluetoothProvider, useBluetooth } from './contexts/bluetooth-context';

const LINKING_ERROR =
  `The package 'react-native-obd' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const Obd = NativeModules.Obd
  ? NativeModules.Obd
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function getPairedDevices(): Promise<Array<object>> {
  return Obd.getPairedDevices();
}

export function enableBluetooth(): void {
  return Obd.enableBluetooth();
}

export function connectDevice(address: string): void {
  return Obd.connectDevice(address);
}

export function startLiveData(): void {
  return Obd.startLiveData();
}

export function stopLiveData(): void {
  return Obd.stopLiveData();
}

export { Obd, BluetoothProvider, useBluetooth };
