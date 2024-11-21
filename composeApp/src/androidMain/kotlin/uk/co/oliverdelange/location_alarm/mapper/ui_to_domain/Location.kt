import com.mapbox.geojson.Point
import model.domain.Location

fun Point.toLocation(): Location = Location(latitude(), longitude())
