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
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
