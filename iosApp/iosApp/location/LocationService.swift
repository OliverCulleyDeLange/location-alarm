import Foundation
import Shared
import MapboxMaps

/// Handles requesting location updates and permissions.
/// A wrapper for CLLocationManager
class LocationService: NSObject, CLLocationManagerDelegate {
    public var delegate: LocationServiceDelegate? = nil
    
    private var locationManager: CLLocationManager = CLLocationManager()
    private var listeningForLocationUpdates: Bool = false
    private var shouldStartListeningAfterPermissionGranted: Bool = false
    
    /// A delegate which receives location updates and permission changes
    protocol LocationServiceDelegate {
        func onLocationUpdate(locations: Array<Shared.Location>)
        func onLocationPermissionChanged(state: PermissionState)
    }
    
    override init() {
        super.init()
        SLog.d("LocationService init")
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.distanceFilter = 0
    }
    
    
    func checkLocationPermissionsAndStartListening() {
        switch locationManager.authorizationStatus{
        case CLAuthorizationStatus.authorizedAlways,
            CLAuthorizationStatus.authorizedWhenInUse:
            listenForUpdates()
        case CLAuthorizationStatus.notDetermined,
            CLAuthorizationStatus.denied,
            CLAuthorizationStatus.restricted:
            shouldStartListeningAfterPermissionGranted = true
            requestPermissions()
        @unknown default:
            fatalError()
        }
    }
    
    func getCurrentPermissions() -> CLAuthorizationStatus {
        return locationManager.authorizationStatus
    }
    
    func requestPermissions() {
        locationManager.requestWhenInUseAuthorization()
    }
    
    func listenForUpdates() {
        SLog.d("listenForUpdates request")
        if (!listeningForLocationUpdates){
            SLog.d("actually listening for updates")
            locationManager.allowsBackgroundLocationUpdates = true
            locationManager.startUpdatingLocation()
            listeningForLocationUpdates = true
        }
    }
    
    func stopListeningForUpdates() {
        if (listeningForLocationUpdates){
            SLog.d("stopListeningForUpdates")
            locationManager.stopUpdatingLocation()
            listeningForLocationUpdates = false
        } else {
            SLog.d("Request to stop listening for location updates when not listening")
        }
    }
    
    // CLLocationManagerDelegate method for location updates
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let mapped = locations.map { (location) -> Shared.Location in
            Shared.Location(lat: location.coordinate.latitude, lng: location.coordinate.longitude)
        }
        delegate?.onLocationUpdate(locations: mapped)
    }
    
    // CLLocationManagerDelegate method for error handling
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        SLog.e("Failed to get location: \(error.localizedDescription)")
    }
    
    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        switch manager.authorizationStatus {
        case .authorizedWhenInUse, .authorizedAlways:
            SLog.d("Location access granted")
            delegate?.onLocationPermissionChanged(state: PermissionStateGranted())
            if (shouldStartListeningAfterPermissionGranted){
                listenForUpdates()
                shouldStartListeningAfterPermissionGranted = false
            }
        case .denied, .restricted:
            SLog.d("Location access denied or restricted")
            delegate?.onLocationPermissionChanged(state: PermissionStateDenied(shouldShowRationale: false))
        case .notDetermined:
            SLog.d("Location access not determined yet")
            delegate?.onLocationPermissionChanged(state: PermissionStateUnknown())
        @unknown default:
            SLog.e("Unknown authorization status")
        }
    }
}
