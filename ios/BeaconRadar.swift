import CoreLocation
import React

@objc(BeaconRadar)
class BeaconRadar: NSObject, RCTBridgeModule, CLLocationManagerDelegate {
  
  static func moduleName() -> String {
    return "BeaconRadar"
  }

  private var locationManager: CLLocationManager!
  private var beaconRegion: CLBeaconRegion!
  public var bridge: RCTBridge!

    @objc func startScanning(_ uuid: String, config: NSDictionary) {
      if #available(iOS 13.0, *) {
          if let useBackgroundScanning = config["useBackgroundScanning"] as? Bool, useBackgroundScanning {
              locationManager.allowsBackgroundLocationUpdates = true
              locationManager.pausesLocationUpdatesAutomatically = false
          }
          
          DispatchQueue.main.async {
            self.locationManager = CLLocationManager()
            self.locationManager.delegate = self
            self.locationManager.requestAlwaysAuthorization()
            
            let uuid = UUID(uuidString: uuid)!
              self.beaconRegion = CLBeaconRegion(proximityUUID: uuid, identifier: "RNIbeaconScannerRegion")
            
            self.locationManager.startMonitoring(for: self.beaconRegion)
            self.locationManager.startRangingBeacons(in: self.beaconRegion)
          }
      }else {
          //TODO Handling older versions
      }
  }

  // CLLocationManagerDelegate methods

  func locationManager(_ manager: CLLocationManager, didRangeBeacons beacons: [CLBeacon], in region: CLBeaconRegion) {
    let beaconArray = beacons.map { beacon -> [String: Any] in
        if #available(iOS 13.0, *) {
            return [
                "uuid": beacon.uuid.uuidString,
                "major": beacon.major.intValue,
                "minor": beacon.minor.intValue,
                "distance": beacon.accuracy,
                "rssi": beacon.rssi,
            ]
        } else {
            // Fallback on earlier versions
            return [:]
        }
    }

    if let bridge = bridge {
      bridge.eventDispatcher().sendAppEvent(withName: "onBeaconsDetected", body: beaconArray)
    }
  }
  
  func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
    if #available(iOS 14.0, *) {
      if manager.authorizationStatus == .authorizedAlways || manager.authorizationStatus == .authorizedWhenInUse {
        locationManager.startMonitoring(for: beaconRegion)
        locationManager.startRangingBeacons(in: beaconRegion)
      }
    } else {
      if CLLocationManager.authorizationStatus() == .authorizedAlways || CLLocationManager.authorizationStatus() == .authorizedWhenInUse {
        locationManager.startMonitoring(for: beaconRegion)
        locationManager.startRangingBeacons(in: beaconRegion)
      }
    }
  }
    
    func locationManagerDidPauseLocationUpdates(_ manager: CLLocationManager) {
        // Restart ranging when the app is paused
        locationManager.startRangingBeacons(in: beaconRegion)
    }

    func locationManagerDidResumeLocationUpdates(_ manager: CLLocationManager) {
        // Restart ranging when the app resumes from a paused state
        locationManager.startRangingBeacons(in: beaconRegion)
    }
}
