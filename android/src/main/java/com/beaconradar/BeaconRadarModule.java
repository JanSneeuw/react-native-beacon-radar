package com.beaconradar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

@ReactModule(name = BeaconRadarModule.NAME)
public class BeaconRadarModule extends ReactContextBaseJavaModule {
  public static final String NAME = "BeaconRadar";
  private BeaconManager beaconManager;
  private Region region;

  public static final String UPDATE = "updateBeacons";
  public static final String BEACONS = "beacons";

  private IntentFilter intentFilter;
  private BroadcastReceiver receiver;

  private static BeaconRadarModule instance;

  public BeaconRadarModule(ReactApplicationContext reactContext) {
    super(reactContext);
    beaconManager = BeaconManager.getInstanceForApplication(reactContext);
    beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
    region = new Region("RNIbeaconScannerRegion", null, null, null);

    instance = this;

  }

  public static BeaconRadarModule getInstance() {
    return instance;
  }
  @Override
  @NonNull
  public String getName() {
    return NAME;
  }


  @ReactMethod
  public void startScanning(String uuid, ReadableMap config, final Promise promise) {
      if (config != null && config.hasKey("useForegroundService") && config.getBoolean("useForegroundService")) {
        Intent intent = new Intent(getReactApplicationContext(), BeaconRadarForegroundService.class);
        beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.setBackgroundScanPeriod(1100);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          getReactApplicationContext().startForegroundService(intent);
        }
      }else {
        runScan(uuid, promise);
      }
  }

  public void runScan(String uuid, final Promise promise) {
    if (!beaconManager.isAnyConsumerBound()) {
      beaconManager.addRangeNotifier(new RangeNotifier() {
        @Override
        public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
          WritableArray beaconArray = Arguments.createArray();

          for (Beacon beacon : beacons) {
            if (beacon.getId1().toString().equalsIgnoreCase(uuid)) {
              WritableMap beaconMap = Arguments.createMap();
              beaconMap.putString("uuid", beacon.getId1().toString());
              beaconMap.putInt("major", beacon.getId2().toInt());
              beaconMap.putInt("minor", beacon.getId3().toInt());
              beaconMap.putDouble("distance", beacon.getDistance());
              beaconMap.putString("name", beacon.getBluetoothName());

              beaconArray.pushMap(beaconMap);
            }
          }

          getReactApplicationContext()
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit("onBeaconsDetected", beaconArray);
        }
      });
      if (uuid != null) {
        region = new Region("RNIbeaconScannerRegion", Identifier.parse(uuid), null, null);
      }
      try {
        beaconManager.startRangingBeacons(region);
        promise.resolve(null);
      } catch (Exception e) {
        promise.reject("START_SCANNING_FAILED", "Failed to start scanning for iBeacons.", e);
      }
    } else {
      promise.reject("ALREADY_BOUND", "Beacon scanning is already active.");
    }
  }

  @ReactMethod
  public void stopScanning(Promise promise) {
    if (beaconManager.isAnyConsumerBound()) {
      try {
        beaconManager.stopRangingBeacons(region);
        beaconManager.removeAllRangeNotifiers();
        promise.resolve(null);
      } catch (Exception e) {
        promise.reject("STOP_SCANNING_FAILED", "Failed to stop scanning for iBeacons.", e);
      }
    } else {
      promise.reject("NOT_BOUND", "Beacon scanning is not active.");
    }
  }

  @ReactMethod
  public void startForegroundService() {
    Intent intent = new Intent(getReactApplicationContext(), BeaconRadarForegroundService.class);
    beaconManager.setBackgroundBetweenScanPeriod(0);
    beaconManager.setBackgroundScanPeriod(1100);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      getReactApplicationContext().startForegroundService(intent);
    }
  }
  @ReactMethod
  public void stopForegroundService() {
    Intent serviceIntent = new Intent(getReactApplicationContext(), BeaconRadarForegroundService.class);
    getReactApplicationContext().stopService(serviceIntent);
  }

}
