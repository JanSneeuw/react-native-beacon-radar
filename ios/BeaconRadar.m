#import "React/RCTBridgeModule.h"

@interface RCT_EXTERN_MODULE(BeaconRadar, NSObject)

RCT_EXTERN_METHOD(startScanning:(NSString *)uuid config:(NSDictionary *)config)
RCT_EXTERN_METHOD(stopScanning)
RCT_EXTERN_METHOD(requestAlwaysAuthorization:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(requestWhenInUseAuthorization:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(getAuthorizationStatus:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(initializeBluetoothManager)

+ (BOOL)requiresMainQueueSetup {
  return YES;
}

@end
