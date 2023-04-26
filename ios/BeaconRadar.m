#import "React/RCTBridgeModule.h"

@interface RCT_EXTERN_MODULE(BeaconRadar, NSObject)

RCT_EXTERN_METHOD(startScanning:(NSString *)uuid config:(NSDictionary *)config)
RCT_EXTERN_METHOD(stopScanning)

+ (BOOL)requiresMainQueueSetup {
  return YES;
}

@end
