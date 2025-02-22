package uk.co.oliverdelange.location_alarm.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.Builder.IMPLICIT_MIN_UPDATE_INTERVAL
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import uk.co.oliverdelange.location_alarm.mapper.ui_to_domain.toLocation
import uk.co.oliverdelange.locationalarm.logging.SLog
import uk.co.oliverdelange.locationalarm.store.AppStateStore

/** Listens to app state and requests location updates appropriately using FusedLocationProvider */
class FusedLocationService(context: Context, private val appStateStore: AppStateStore) : LocationService {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest = LocationRequest.Builder(1000)
        .setGranularity(Granularity.GRANULARITY_FINE)
        .setWaitForAccurateLocation(false)
        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        .setMaxUpdateAgeMillis(0)
        .setMaxUpdateDelayMillis(0)
        .setMinUpdateDistanceMeters(0f)
        .setMinUpdateIntervalMillis(IMPLICIT_MIN_UPDATE_INTERVAL)
        .build()

    private val locationListener = LocationListener { location ->
        onLocationUpdate(location.toLocation())
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun listenToStateAndListenForLocationUpdates() {
        SLog.d("LocationService init")
        serviceScope.launch {
            appStateStore.state
                .map { it.shouldListenForLocationUpdates }
                .distinctUntilChanged()
                .collect {
                    if (it) {
                        listenForUpdates()
                    } else {
                        stopListeningForUpdates()
                    }
                }
        }
    }

    @SuppressLint("MissingPermission")
    private fun listenForUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationListener,
            Looper.getMainLooper()
        )
    }

    private fun stopListeningForUpdates() {
        fusedLocationClient.removeLocationUpdates(locationListener)
    }

    override fun onLocationUpdate(location: uk.co.oliverdelange.locationalarm.model.domain.Location) {
        appStateStore.onLocationChange(listOf(location))
    }
}
