#import <React/RCTBridgeModule.h>
#import <React/RCTViewManager.h>
#import <CoreLocation/CoreLocation.h>

@interface BeaconRadar : NSObject <RCTBridgeModule, CLLocationManagerDelegate>
@end
