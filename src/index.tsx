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
