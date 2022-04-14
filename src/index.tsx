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

export function multiply(a: number, b: number): Promise<number> {
  return Obd.multiply(a, b);
}

export function initializeBluetooth(): Promise<boolean> {
  return Obd.initializeBluetooth();
}

export function getPairedDevices(): Promise<Array<object>> {
  return Obd.getPairedDevices();
}

export function startScan(): void {
  return Obd.startScan();
}

export function stopScan(): void {
  return Obd.stopScan();
}

export function getScannedDevices(): Promise<Array<object>> {
  return Obd.getScannedDevices();
}

export function getValue(): Promise<number> {
  return Obd.getValue();
}

export function updateValue(value: number): void {
  return Obd.updateValue(value);
}
