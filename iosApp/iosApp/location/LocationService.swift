import Foundation
import Shared
import MapboxMaps

class LocationService: NSObject, CLLocationManagerDelegate {
    var locationManager: CLLocationManager = CLLocationManager()
    public var delegate: LocationServiceDelegate? = nil
    
    private var listeningForLocationUpdates: Bool = false
    
    protocol LocationServiceDelegate {
        func onLocationUpdate(locations: Array<Shared.Location>)
    }
    
    override init() {
        super.init()
        logger.debug("LocationService init")
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
            requestPermissions()
        @unknown default:
            fatalError()
        }
    }
    
    func getCurrentPermissions() -> CLAuthorizationStatus {
        logger.debug("getCurrentPermissions")
        return locationManager.authorizationStatus
    }
    
    func requestPermissions() {
        logger.debug("requestalways Permissions")
        locationManager.requestAlwaysAuthorization()
    }
    
    func listenForUpdates() {
        logger.debug("listenForUpdates")
        if (!listeningForLocationUpdates){
            logger.debug("actually listening for updates")
            locationManager.allowsBackgroundLocationUpdates = true
            locationManager.startUpdatingLocation()
            listeningForLocationUpdates = true
        }
    }
    
    func stopListeningForUpdates() {
        logger.debug("stopListeningForUpdates")
        locationManager.stopUpdatingLocation()
        listeningForLocationUpdates = false
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
        logger.error("Failed to get location: \(error.localizedDescription)")
    }
    
    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        switch manager.authorizationStatus {
        case .authorizedWhenInUse, .authorizedAlways:
            logger.debug("Location access granted")
            listenForUpdates()
        case .denied, .restricted:
            logger.debug("Location access denied or restricted")
        case .notDetermined:
            logger.debug("Location access not determined yet")
        @unknown default:
            logger.debug("Unknown authorization status")
        }
    }
}
