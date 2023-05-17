import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-beacon-radar' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const BeaconRadar = NativeModules.BeaconRadar
  ? NativeModules.BeaconRadar
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function startScanning(uuid: string, config: any) {
  return BeaconRadar.startScanning(uuid, config);
}

export function stopScanning() {
  return BeaconRadar.stopScanning();
}

export function startForegroundService() {
  return BeaconRadar.startForegroundService();
}

export function stopForegroundService() {
  return BeaconRadar.stopForegroundService();
}

export function requestAlwaysAuthorization(): Promise<{ status: string }> {
  return BeaconRadar.requestAlwaysAuthorization();
}

export function requestWhenInUseAuthorization(): Promise<{ status: string }> {
  return BeaconRadar.requestWhenInUseAuthorization();
}

export function getAuthorizationStatus(): Promise<{ status: string }> {
  return BeaconRadar.getAuthorizationStatus();
}

export function isBluetoothEnabled(): Promise<boolean> {
  return BeaconRadar.isBluetoothEnabled();
}

export function getBluetoothState(): Promise<string> {
  return BeaconRadar.getBluetoothState();
}

export function startRadar(config: any) {
  if (Platform.OS === 'android') {
    return BeaconRadar.startRadar(config);
  } else {
    console.warn('startRadar is only available on Android');
    return;
  }
}

export function initializeBluetoothManager() {
  return BeaconRadar.initializeBluetoothManager();
}
