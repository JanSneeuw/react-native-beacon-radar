# react-native-beacon-radar

## CURRENTLY IN PROGRESS.... RIGHT NOW ONLY SCANS FOR CERTAIN IBEACON

Package to scan for iBeacons on both Android and IOS. This module is fully compatible with Expo (Will not work with Expo Go, but will work with development build.)

## Installation

```sh
npm install react-native-beacon-radar
```

## Usage

```js
import { startScanning } from 'react-native-beacon-radar';

// ...

startScanning('B9407F30-F5F8-466E-AFF9-25556B57FE6D', {
  useForegroundService: true,
  useBackgroundScanning: true,
});

DeviceEventEmitter.addListener('onBeaconsDetected', (beacons) => {
  console.log('onBeaconsDetected', beacons);
});
```

## Current API:
| Method                            | Description                                                                                                                                                                                                           |
|:----------------------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **requestWhenInUseAuthorization** | This method should be called before anything else is called. It handles to request the use of beacons while the application is open. If the application is in the background, you will not get a signal from beacons. |
| **requestAlwaysAuthorization**    | This method should be called before anything else is called. It handles to request the use of beacons while the application is open or in the background.                                                             |
| **getAuthorizationStatus**        | This methods gets the current authorization status.                                                                                                                                                                   |
| **startScanning**                 | This method starts scanning for a certain beacon based on its UUID.                                                                                                                                                   |
| **startRadar (Android only)**     | This method starts scanning for all beacons in range. This is only available on Android.                                                                                                                              |


| Event                 | Description                                                               |
|:----------------------|:--------------------------------------------------------------------------|
| **onBeaconsDetected** | This event gets called when the beacon you are searching for is in range. |


## Expo
This module will work with the Expo managed workflow. It will not however work with Expo Go, since it needs native features. You can use the development build of Expo to test this module. More about this can be found [here](https://docs.expo.dev/develop/development-builds/create-a-build/). To use this module in expo managed add the following to your app.json:
```json
"expo": {
  "ios": {
    "infoPlist": {
      "NSLocationWhenInUseUsageDescription": "We need your location to detect nearby beacons.",
      "NSLocationAlwaysUsageDescription": "We need your location to detect nearby beacons even when the app is in the background.",
      "NSLocationAlwaysAndWhenInUseUsageDescription": "We need your location to detect nearby beacons even when the app is in the background."
    }
  },
  "plugins": [
    "react-native-beacon-radar"
  ]
}
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
