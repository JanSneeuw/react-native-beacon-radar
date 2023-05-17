package com.beaconradar;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

@ReactModule(name = BeaconRadarModule.NAME)
public class BeaconRadarModule extends ReactContextBaseJavaModule implements PermissionListener {
  public static final String NAME = "BeaconRadar";
  private static final String BLUETOOTH_ERROR = "BLUETOOTH_ERROR";
  private static final int PERMISSION_REQUEST_CODE = 1;
  private BeaconManager beaconManager;
  private Region region;

  public static final String UPDATE = "updateBeacons";
  public static final String BEACONS = "beacons";

  private Promise permissionPromise;

  private IntentFilter intentFilter;
  private BroadcastReceiver receiver;

  private static BeaconRadarModule instance;

  private final ReactContext reactContext;
  private final BluetoothAdapter bluetoothAdapter;


  public BeaconRadarModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    beaconManager = BeaconManager.getInstanceForApplication(reactContext);
    beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
    region = new Region("RNIbeaconScannerRegion", null, null, null);

    instance = this;

    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
    if (beaconManager.isAnyConsumerBound()) {
      try {
        Log.d("BeaconRadarModule", "Stopping ranging");
        beaconManager.stopRangingBeacons(region);
        beaconManager.removeAllRangeNotifiers();
        Log.d("BeaconRadarModule", "Stopped monitoring");
      } catch (Exception e) {
        promise.reject("STOP_SCANNING_FAILED", "Failed to stop scanning for iBeacons.", e);
        return;
      }
    }

    if (config != null && config.hasKey("useForegroundService") && config.getBoolean("useForegroundService")) {
      String foregroundTitle = config.hasKey("foregroundTitle") ? config.getString("foregroundTitle") : "Beacon Radar";
      String foregroundMessage = config.hasKey("foregroundMessage") ? config.getString("foregroundMessage") : "Beacon Radar running in the background";
      Intent intent = new Intent(getReactApplicationContext(), BeaconRadarForegroundService.class);
      intent.putExtra("foregroundMessage", foregroundMessage);
      intent.putExtra("foregroundTitle", foregroundTitle);
      intent.putExtra("uuid", uuid);
      beaconManager.setBackgroundBetweenScanPeriod(0);
      beaconManager.setBackgroundScanPeriod(5000);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        getReactApplicationContext().startForegroundService(intent);
      }
    }else {
      runScan(uuid, promise);
    }
  }

  @ReactMethod
  public void requestAlwaysAuthorization(final Promise promise) {
    permissionPromise = promise;
    PermissionAwareActivity activity = (PermissionAwareActivity) reactContext.getCurrentActivity();
    if (activity != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        activity.requestPermissions(new String[]{
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.BLUETOOTH,
          Manifest.permission.BLUETOOTH_CONNECT,
          Manifest.permission.BLUETOOTH_SCAN
        }, PERMISSION_REQUEST_CODE, this);
      } else {
        activity.requestPermissions(new String[]{
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.BLUETOOTH,
        }, PERMISSION_REQUEST_CODE, this);
      }
    }
  }

  @ReactMethod
  public void initializeBluetoothManager(final Promise promise) {
    IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
    getReactApplicationContext().registerReceiver(bluetoothStateReceiver, filter);

    // Add this to get initial state
    String statusText = getBluetoothState();
    WritableMap bluetoothStateMap = Arguments.createMap();
    bluetoothStateMap.putString("state", statusText);

    getReactApplicationContext()
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit("onBluetoothStateChanged", bluetoothStateMap);
  }

  // Add this method to check Bluetooth state
  private String getBluetoothState() {
    int state = bluetoothAdapter.getState();
    String statusText = "";
    switch (state) {
      case BluetoothAdapter.STATE_OFF:
        Log.d("Bluetooth", "Bluetooth turned off");
        statusText = "poweredOff";
        break;
      case BluetoothAdapter.STATE_TURNING_OFF:
        Log.d("Bluetooth", "Bluetooth turning off");
        statusText = "turningOff";
        break;
      case BluetoothAdapter.STATE_ON:
        Log.d("Bluetooth", "Bluetooth turned on");
        statusText = "poweredOn";
        break;
      case BluetoothAdapter.STATE_TURNING_ON:
        Log.d("Bluetooth", "Bluetooth turning on");
        statusText = "turningOn";
        break;
    }
    return statusText;
  }

  @ReactMethod
  public void requestWhenInUseAuthorization(final Promise promise) {
    requestAlwaysAuthorization(promise);
  }

  @ReactMethod
  public void isBluetoothEnabled(Promise promise) {
    if (bluetoothAdapter == null) {
      promise.reject(BLUETOOTH_ERROR, "Bluetooth not supported on this device.");
    } else {
      promise.resolve(bluetoothAdapter.isEnabled());
    }
  }

  @ReactMethod
  public void getAuthorizationStatus(final Promise promise) {
    Boolean isAuthorized = false;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      isAuthorized = (ContextCompat.checkSelfPermission(reactContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(reactContext, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(reactContext, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(reactContext, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
      );
    } else {
      isAuthorized = (ContextCompat.checkSelfPermission(reactContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(reactContext, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
      );
    }

    if (isAuthorized) {
      promise.resolve("authorized");
    } else {
      promise.resolve("denied");
    }
  }

  @ReactMethod
  public void startRadar(ReadableMap config, final Promise promise) {
    if (beaconManager.isAnyConsumerBound()) {
      try {
        beaconManager.stopRangingBeacons(region);
        beaconManager.removeAllRangeNotifiers();
      } catch (Exception e) {
        promise.reject("STOP_SCANNING_FAILED", "Failed to stop scanning for iBeacons.", e);
        return;
      }
    }

    if (config != null && config.hasKey("useForegroundService") && config.getBoolean("useForegroundService")) {
      Intent intent = new Intent(getReactApplicationContext(), BeaconRadarForegroundService.class);
      beaconManager.setBackgroundBetweenScanPeriod(0);
      beaconManager.setBackgroundScanPeriod(1100);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        getReactApplicationContext().startForegroundService(intent);
      }
    } else {
      runScanForAllBeacons(promise);
    }
  }

  public void runScanForAllBeacons(final Promise promise) {
    if (!beaconManager.isAnyConsumerBound()) {
      beaconManager.addRangeNotifier(new RangeNotifier() {
        @Override
        public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
          WritableArray beaconArray = Arguments.createArray();

          for (Beacon beacon : beacons) {
            WritableMap beaconMap = Arguments.createMap();
            beaconMap.putString("uuid", beacon.getId1().toString());
            beaconMap.putInt("major", beacon.getId2().toInt());
            beaconMap.putInt("minor", beacon.getId3().toInt());
            beaconMap.putDouble("distance", beacon.getDistance());
            beaconMap.putString("name", beacon.getBluetoothName());

            beaconArray.pushMap(beaconMap);
          }

          getReactApplicationContext()
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit("onBeaconsDetected", beaconArray);
        }
      });
      try {
        beaconManager.startRangingBeacons(new Region("RNIbeaconScannerRegionAllBeacons", null, null, null));
        promise.resolve(null);
      } catch (Exception e) {
        promise.reject("START_SCANNING_ALL_BEACONS_FAILED", "Failed to start scanning for all beacons.", e);
      }
    } else {
      promise.reject("ALREADY_BOUND", "Beacon scanning is already active.");
    }
  }



  public void runScan(String uuid, final Promise promise) {
    Log.d("BeaconRadarModule", "running scan");
    if (!beaconManager.isAnyConsumerBound()) {
      Log.d("BeaconRadarModule", "is not bound");
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
          Log.d("BeaconRadarModule", "emitting beacons");

          getReactApplicationContext()
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit("onBeaconsDetected", beaconArray);
        }
      });
      if (uuid != null) {
        region = new Region("RNIbeaconScannerRegion", Identifier.parse(uuid), null, null);
      }
      try {
        Log.d("BeaconRadarModule", "starting ranging");
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

  @Override
  public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    if (requestCode == 1 && permissionPromise != null) {
      boolean allPermissionsGranted = true;
      for (int result : grantResults) {
        if (result != PackageManager.PERMISSION_GRANTED) {
          allPermissionsGranted = false;
          break;
        }
      }

      if (allPermissionsGranted) {
        permissionPromise.resolve("authorized");
      } else {
        permissionPromise.resolve("denied");
      }

      permissionPromise = null;
      return true;
    }

    return false;
  }

  private final BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
    public void onReceive(Context context, Intent intent) {
      Log.d("BeaconRadarModule", "Bluetooth state changed");
      final String action = intent.getAction();

      if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
          BluetoothAdapter.ERROR);
        String statusText = "";
        switch (state) {
          case BluetoothAdapter.STATE_OFF:
            Log.d("Bluetooth", "Bluetooth turned off");
            statusText = "poweredOff";
            break;
          case BluetoothAdapter.STATE_TURNING_OFF:
            Log.d("Bluetooth", "Bluetooth turning off");
            statusText = "turningOff";
            break;
          case BluetoothAdapter.STATE_ON:
            Log.d("Bluetooth", "Bluetooth turned on");
            statusText = "poweredOn";
            break;
          case BluetoothAdapter.STATE_TURNING_ON:
            Log.d("Bluetooth", "Bluetooth turning on");
            statusText = "turningOn";
            break;
        }
        WritableMap bluetoothStateMap = Arguments.createMap();
        bluetoothStateMap.putString("state", statusText);

        getReactApplicationContext()
          .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
          .emit("onBluetoothStateChanged", bluetoothStateMap);
      }
    }
  };

}
