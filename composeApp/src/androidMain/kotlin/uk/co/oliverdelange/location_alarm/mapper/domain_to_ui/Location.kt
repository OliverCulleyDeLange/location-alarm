package uk.co.oliverdelange.location_alarm.mapper.domain_to_ui

import com.mapbox.geojson.Point

fun uk.co.oliverdelange.locationalarm.model.domain.Location.toPoint(): Point = Point.fromLngLat(lng, lat)
