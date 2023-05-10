const { withAndroidManifest, withInfoPlist } = require('@expo/config-plugins');

const withBeaconRadar = (config) => {
  // Android configurations
  config = withAndroidManifest(config, (conf) => {
    /*const mainApplication = AndroidConfig.Manifest.getMainApplicationOrThrow(
      config.modResults
    );
    mainApplication.permissions = [
      ...mainApplication.permissions,
      "android.permission.ACCESS_FINE_LOCATION",
      "android.permission.BLUETOOTH",
      "android.permission.BLUETOOTH_ADMIN",
      "android.permission.ACCESS_COARSE_LOCATION",
      "android.permission.BLUETOOTH_SCAN",
      "android.permission.BLUETOOTH_CONNECT",
    ];*/
    return conf;
  });

  // iOS configurations
  config = withInfoPlist(config, (conf) => {
    config.modResults.NSLocationAlwaysAndWhenInUseUsageDescription =
      'We use your location to find nearby beacons even when the app is not running';
    config.modResults.NSLocationWhenInUseUsageDescription =
      "We use your location to find nearby beacons while you're using the app";
    config.modResults.NSBluetoothAlwaysUsageDescription =
      'We use Bluetooth to scan for nearby beacons';
    config.modResults.NSBluetoothPeripheralUsageDescription =
      'We use Bluetooth to connect to nearby devices';

    conf.modResults.UIBackgroundModes = ['location'];

    return conf;
  });

  return config;
};

module.exports = withBeaconRadar;
