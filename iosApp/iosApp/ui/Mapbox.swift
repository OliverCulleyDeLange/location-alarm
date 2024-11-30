import MapboxMaps
import Turf

func createCirclePolygon(center: CLLocationCoordinate2D, radius: CLLocationDistance) -> Polygon {
    let circlePolygon = Turf.Polygon(center: center, radius: radius, vertices: 360)
    return circlePolygon
}

