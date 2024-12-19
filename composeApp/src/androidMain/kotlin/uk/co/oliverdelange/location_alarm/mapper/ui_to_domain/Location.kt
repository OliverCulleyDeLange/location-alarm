package uk.co.oliverdelange.location_alarm.mapper.ui_to_domain

import com.mapbox.geojson.Point
import model.domain.Location

fun Point.toLocation(): Location = Location(latitude(), longitude())
