import * as React from 'react';
import { DeviceEventEmitter, Platform } from 'react-native';
import { PERMISSIONS, request, RESULTS } from 'react-native-permissions';

import { StyleSheet, View, Text, ScrollView } from 'react-native';
import { startScanning } from 'react-native-beacon-radar';

export default function App() {
  const [beacons, setBeacons] = React.useState<any[]>([]);

  React.useEffect(() => {
    const requestLocationPermission = async () => {
      /*const permission = Platform.select({
        android: PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION,
        ios: PERMISSIONS.IOS.LOCATION_WHEN_IN_USE,
      });*/
      let result;
      if (Platform.OS === 'android') {
        const permission = PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION;
        result = await request(permission);
      } else if (Platform.OS === 'ios') {
        const permission = PERMISSIONS.IOS.LOCATION_WHEN_IN_USE;
        result = await request(permission);
      }

      if (result === RESULTS.GRANTED) {
        //request bluetooth specific permissions for android 12. These are "android.permission.BLUETOOTH_CONNECT" and "android.permission.BLUETOOTH_SCAN"
        if (Platform.OS === 'android') {
          const bluetoothPermission = PERMISSIONS.ANDROID.BLUETOOTH_CONNECT;
          const bluetoothScanPermission = PERMISSIONS.ANDROID.BLUETOOTH_SCAN;
          const bluetoothResult = await request(bluetoothPermission);
          const bluetoothScanResult = await request(bluetoothScanPermission);
          if (
            bluetoothResult === RESULTS.GRANTED &&
            bluetoothScanResult === RESULTS.GRANTED
          ) {
            // Permission granted, start scanning
            startScanning('B9407F30-F5F8-466E-AFF9-25556B57FE6D', {
              useForegroundService: true,
              useBackgroundScanning: true,
            });
            //startForegroundService();
          } else {
            // Handle permission denial
          }
        } else {
          startScanning('B9407F30-F5F8-466E-AFF9-25556B57FE6D', {});
        }
      } else {
        // Handle permission denial
      }
    };

    requestLocationPermission();

    DeviceEventEmitter.addListener('onBeaconsDetected', (beacons) => {
      console.log('onBeaconsDetected', beacons);
      setBeacons(beacons);
    });
  }, []);

  return (
    <View style={styles.container}>
      <Text>Beacons:</Text>
      <ScrollView>
        {beacons.map((beacon) => (
          <View key={beacon.uuid} style={styles.box}>
            <Text>{beacon.uuid}</Text>
          </View>
        ))}
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
