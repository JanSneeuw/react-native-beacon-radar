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
    /*config.modResults.NSLocationWhenInUseUsageDescription =
      "We need your location to detect nearby beacons.";
    config.modResults.NSLocationAlwaysAndWhenInUseUsageDescription =
      "We need your location to detect nearby beacons even when the app is in the background.";
    config.modResults.NSLocationAlwaysUsageDescription =
      "We need your location to detect nearby beacons even when the app is not in use.";*/
    conf.modResults.UIBackgroundModes = ['location'];

    return conf;
  });

  return config;
};

module.exports = withBeaconRadar;
