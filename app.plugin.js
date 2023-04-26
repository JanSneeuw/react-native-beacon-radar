const { createRunOncePlugin } = require('@expo/config-plugins');
const withBeaconRadar = require('./withBeaconRadar');

// A helper function to ensure the plugin is only run once per config
const withRunOnceBeaconRadar = createRunOncePlugin(
  withBeaconRadar,
  'react-native-beacon-radar'
);

module.exports = (config) => {
  return withRunOnceBeaconRadar(config);
};
