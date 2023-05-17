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
    conf.modResults.NSLocationAlwaysAndWhenInUseUsageDescription =
      'We use your location to find nearby beacons even when the app is not running';
    conf.modResults.NSLocationWhenInUseUsageDescription =
      "We use your location to find nearby beacons while you're using the app";
    conf.modResults.NSBluetoothAlwaysUsageDescription =
      'We use Bluetooth to scan for nearby beacons';
    conf.modResults.NSBluetoothPeripheralUsageDescription =
      'We use Bluetooth to connect to nearby devices';

    // Ensure UIBackgroundModes is an array
    if (!Array.isArray(conf.modResults.UIBackgroundModes)) {
      conf.modResults.UIBackgroundModes = [];
    }

    // Add necessary keys to UIBackgroundModes
    if (!conf.modResults.UIBackgroundModes.includes('location')) {
      conf.modResults.UIBackgroundModes.push('location');
    }
    if (!conf.modResults.UIBackgroundModes.includes('bluetooth-central')) {
      conf.modResults.UIBackgroundModes.push('bluetooth-central');
    }
    if (!conf.modResults.UIBackgroundModes.includes('bluetooth-peripheral')) {
      conf.modResults.UIBackgroundModes.push('bluetooth-peripheral');
    }

    return conf;
  });

  return config;
};

module.exports = withBeaconRadar;
