package uk.co.oliverdelange.location_alarm.mapper.domain_to_ui

import com.mapbox.geojson.Point
import model.domain.Location

fun Location.toPoint(): Point = Point.fromLngLat(lng, lat)
