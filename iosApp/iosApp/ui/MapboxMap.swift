import SwiftUI
import Shared
import Turf
@_spi(Experimental) import MapboxMaps

struct MapboxMap: View {
    var geofenceLocation: Shared.Location?
    var perimeterRadiusMeters: Double
    var onMapTap: (Shared.Location) -> Void
    
    @Environment(\.colorScheme) var colorScheme

    @State var viewport: Viewport = .followPuck(zoom: 16)
    
    var body: some View {
        MapReader { map in
            Map(viewport: $viewport)
            {
                Puck2D(bearing: .heading).showsAccuracyRing(true)
                if let geofence = geofenceLocation {
                    let circle = createCirclePolygon(center: geofence.toCLLocationCoordinate2D(), radius: perimeterRadiusMeters)
                    
                    GeoJSONSource(id: MapboxIDs.shared.SOURCE_GEOFENCE)
                        .data(.feature(Feature(geometry: .polygon(circle))))
                    FillLayer(id: MapboxIDs.shared.LAYER_GEOFENCE_FILL, source: MapboxIDs.shared.SOURCE_GEOFENCE)
                        .fillColor(UIColor(Color(.primary)))
                        .fillOpacity(0.3)
                    LineLayer(id: MapboxIDs.shared.LAYER_GEOFENCE_LINE, source: MapboxIDs.shared.SOURCE_GEOFENCE)
                        .lineWidth(5.0)
                        .lineColor(UIColor(Color(.primary)))
                }
                
            }
            .ornamentOptions(OrnamentOptions.init(
                attributionButton: AttributionButtonOptions.init(position: OrnamentPosition.topRight, margins: CGPoint())
            ))
            .onMapTapGesture(perform: { MapContentGestureContext in
                onMapTap(MapContentGestureContext.coordinate.toLocation())
            })
            .mapStyle(colorScheme == .dark ? MapStyle.dark : MapStyle.light)
            .ignoresSafeArea()
        }
    }
}

func createCirclePolygon(center: CLLocationCoordinate2D, radius: CLLocationDistance) -> Polygon {
    let circlePolygon = Turf.Polygon(center: center, radius: radius, vertices: 360)
    return circlePolygon
}
