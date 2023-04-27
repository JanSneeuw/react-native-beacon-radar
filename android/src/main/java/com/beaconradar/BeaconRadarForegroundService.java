package com.beaconradar;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public class BeaconRadarForegroundService extends Service {
  public static final String CHANNEL_ID = "BeaconRadarChannel";
  public static final String ACTION_START_FOREGROUND_SERVICE = "com.beaconradar.action.start_foreground_service";
  int icon = android.R.drawable.ic_notification_overlay;


  private BeaconRadarModule beaconRadarModule;

  @Override
  public void onCreate() {
    super.onCreate();
    this.beaconRadarModule = BeaconRadarModule.getInstance();
    createNotificationChannel();
  }

  private void sendBroadcast(String beaconsJson) {
    Intent broadcastIntent = new Intent();
    broadcastIntent.setAction(BeaconRadarModule.UPDATE);
    broadcastIntent.putExtra(BeaconRadarModule.BEACONS, beaconsJson);
    sendBroadcast(broadcastIntent);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    String text = intent.getStringExtra("foregroundMessage");
    String title = intent.getStringExtra("foregroundTitle");
    Intent notificationIntent = new Intent(this, BeaconRadarForegroundService.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

    Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
      .setContentTitle(title)
      .setContentText(text)
      .setSmallIcon(icon)
      .setContentIntent(pendingIntent)
      .build();

    startForeground(1, notification);


    // runScan with empty promise
    beaconRadarModule.runScan("B9407F30-F5F8-466E-AFF9-25556B57FE6D", new Promise() {
      @Override
      public void resolve(Object value) {
        // Scanning started
      }

      @Override
      public void reject(String s, String s1) {

      }

      @Override
      public void reject(String s, Throwable throwable) {

      }

      @Override
      public void reject(String code, String message, Throwable e) {
        // Handle error
      }

      @Override
      public void reject(Throwable throwable) {

      }

      @Override
      public void reject(Throwable throwable, WritableMap writableMap) {

      }

      @Override
      public void reject(String s, @NonNull WritableMap writableMap) {

      }

      @Override
      public void reject(String s, Throwable throwable, WritableMap writableMap) {

      }

      @Override
      public void reject(String s, String s1, @NonNull WritableMap writableMap) {

      }

      @Override
      public void reject(String s, String s1, Throwable throwable, WritableMap writableMap) {

      }

      @Override
      public void reject(String s) {

      }
    });


    return START_NOT_STICKY;
  }


  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onDestroy() {
    beaconRadarModule.stopScanning(new Promise() {
      @Override
      public void resolve(Object value) {
        // Scanning stopped
      }

      @Override
      public void reject(String s, String s1) {

      }

      @Override
      public void reject(String s, Throwable throwable) {

      }

      @Override
      public void reject(String code, String message, Throwable e) {
        // Handle error
      }

      @Override
      public void reject(Throwable throwable) {

      }

      @Override
      public void reject(Throwable throwable, WritableMap writableMap) {

      }

      @Override
      public void reject(String s, @NonNull WritableMap writableMap) {

      }

      @Override
      public void reject(String s, Throwable throwable, WritableMap writableMap) {

      }

      @Override
      public void reject(String s, String s1, @NonNull WritableMap writableMap) {

      }

      @Override
      public void reject(String s, String s1, Throwable throwable, WritableMap writableMap) {

      }

      @Override
      public void reject(String s) {

      }
    });
    super.onDestroy();
  }

  private void createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel serviceChannel = new NotificationChannel(
        CHANNEL_ID,
        "Beacon Radar Service Channel",
        NotificationManager.IMPORTANCE_DEFAULT
      );

      NotificationManager manager = getSystemService(NotificationManager.class);
      if (manager != null) {
        manager.createNotificationChannel(serviceChannel);
      }
    }
  }
}

