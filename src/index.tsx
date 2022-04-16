import { NativeModules, Platform } from 'react-native';

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

export function trackRPM(): void {
  return Obd.trackRPM();
}

export function socketCheck(): Promise<string> {
  return Obd.socketCheck();
}

export function isConnected(): Promise<boolean> {
  return Obd.isConnected();
}

export function getArrObj(): Promise<Array<object>> {
  return Obd.getArrObj();
}

export { Obd };
