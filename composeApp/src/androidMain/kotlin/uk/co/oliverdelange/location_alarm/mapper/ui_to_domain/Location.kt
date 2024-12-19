package uk.co.oliverdelange.location_alarm.mapper.ui_to_domain

import com.mapbox.geojson.Point

fun Point.toLocation(): uk.co.oliverdelange.locationalarm.model.domain.Location =
    uk.co.oliverdelange.locationalarm.model.domain.Location(latitude(), longitude())
