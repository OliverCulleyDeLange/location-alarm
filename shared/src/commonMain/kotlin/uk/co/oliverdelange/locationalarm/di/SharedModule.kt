package uk.co.oliverdelange.locationalarm.di

import kotlinx.coroutines.flow.map
import org.koin.dsl.module
import uk.co.oliverdelange.locationalarm.logging.AppStateChangeLogger
import uk.co.oliverdelange.locationalarm.logging.LogStorer
import uk.co.oliverdelange.locationalarm.mapper.domain_to_ui.MapAppStateToDebugGpsUiState
import uk.co.oliverdelange.locationalarm.mapper.domain_to_ui.MapAppStateToLocationPermissionRequiredUiState
import uk.co.oliverdelange.locationalarm.mapper.domain_to_ui.MapAppStateToMapUiState
import uk.co.oliverdelange.locationalarm.mapper.domain_to_ui.MapDebugStateToDebugLogUiState
import uk.co.oliverdelange.locationalarm.model.domain.DebugMode
import uk.co.oliverdelange.locationalarm.model.ui.debug.DebugViewModel
import uk.co.oliverdelange.locationalarm.model.ui.location_permission_required.LocationPermissionRequiredViewModel
import uk.co.oliverdelange.locationalarm.model.ui.map.MapViewModel
import uk.co.oliverdelange.locationalarm.provider.SystemTimeProvider
import uk.co.oliverdelange.locationalarm.provider.TimeProvider
import uk.co.oliverdelange.locationalarm.store.AppStateStore
import uk.co.oliverdelange.locationalarm.store.DebugStateStore

val sharedModule = module {
    // State stores
    single { AppStateStore() }
    single { DebugStateStore() }

    // View Models
    factory { MapViewModel(get(), get()) }
    factory { LocationPermissionRequiredViewModel(get(), get()) }
    factory { DebugViewModel(get(), get(), get(), get()) }

    // Providers
    single<TimeProvider> { SystemTimeProvider() }

    // Mappers
    single { MapAppStateToMapUiState() }
    single { MapAppStateToLocationPermissionRequiredUiState() }
    single { MapDebugStateToDebugLogUiState() }
    single { MapAppStateToDebugGpsUiState() }

    // Tools
    single { DebugMode(get()) }
    single(createdAtStart = true) {
        val appStateStore: AppStateStore = get()
        val mapViewModel: MapViewModel = get()
        val locationPermissionRequiredViewModel: LocationPermissionRequiredViewModel = get()
        AppStateChangeLogger(
            debug = appStateStore.state.map { it.debug },
            mapUiState = mapViewModel.state,
            appState = appStateStore.state,
            locationPermissionRequiredUiState = locationPermissionRequiredViewModel.state
        )
    }
    single(createdAtStart = true) {
        val appStateStore: AppStateStore = get()
        LogStorer(
            debug = appStateStore.state.map { it.debug },
            debugStateStore = get(),
            timeProvider = get()
        )
    }
}