import Shared
import CoreLocation

extension CLLocationCoordinate2D {
    func toLocation() -> Shared.Location {
        return Location(lat: self.latitude, lng: self.longitude)
    }
}

