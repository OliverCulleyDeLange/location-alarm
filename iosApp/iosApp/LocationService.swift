import Foundation
import Shared
import MapboxMaps

class LocationService: AppleLocationProviderDelegate {
    var locationProvider: AppleLocationProvider = AppleLocationProvider()
    var locationObserver: AnyCancelable? = nil
    
    init() {
        locationProvider.delegate = self
    }
    
    func listenForUpdates(onUpdate: @escaping (Array<Shared.Location>) -> Void) {
        locationObserver = locationProvider.onLocationUpdate.observe { locations in
            let mapped = locations.map { (location) -> Shared.Location in
                Shared.Location(lat: location.coordinate.latitude, lng: location.coordinate.longitude)
            }
            onUpdate(mapped)
        }
    }
    
    func stopListeningForUpdates() {
        locationObserver?.cancel()
    }
    
    func appleLocationProvider(_ locationProvider: MapboxMaps.AppleLocationProvider, didFailWithError error: any Error) {
        print("did fail with error \(error)")
    }
    
    func appleLocationProvider(_ locationProvider: MapboxMaps.AppleLocationProvider, didChangeAccuracyAuthorization accuracyAuthorization: CLAccuracyAuthorization) {
        print("didChangeAccuracyAuthorization \(accuracyAuthorization)")

    }
    
    func appleLocationProviderShouldDisplayHeadingCalibration(_ locationProvider: MapboxMaps.AppleLocationProvider) -> Bool {
        return false
    }
} 
