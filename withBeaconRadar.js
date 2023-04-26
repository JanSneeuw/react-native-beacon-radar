const { withAndroidManifest, withInfoPlist } = require('@expo/config-plugins');

const withBeaconRadar = (config) => {
  // Android configurations
  config = withAndroidManifest(config, (config) => {
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
    return config;
  });

  // iOS configurations
  config = withInfoPlist(config, (config) => {
    /*config.modResults.NSLocationWhenInUseUsageDescription =
      "We need your location to detect nearby beacons.";
    config.modResults.NSLocationAlwaysAndWhenInUseUsageDescription =
      "We need your location to detect nearby beacons even when the app is in the background.";
    config.modResults.NSLocationAlwaysUsageDescription =
      "We need your location to detect nearby beacons even when the app is not in use.";*/
    config.modResults.UIBackgroundModes = ['location'];

    return config;
  });

  return config;
};

module.exports = withBeaconRadar;
