#import <React/RCTBridgeModule.h>
#import <React/RCTViewManager.h>
#import <CoreLocation/CoreLocation.h>
#import <CoreBluetooth/CoreBluetooth.h>

@interface BeaconRadar : NSObject <RCTBridgeModule, CLLocationManagerDelegate>
@end
