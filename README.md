# react-native-beacon-radar

## CURRENTLY IN PROGRESS.... RIGHT NOW ONLY SCANS FOR CERTAIN IBEACON

Package to scan for iBeacons

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


| Event                 | Description                                                               |
|:----------------------|:--------------------------------------------------------------------------|
| **onBeaconsDetected** | This event gets called when the beacon you are searching for is in range. |


## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
