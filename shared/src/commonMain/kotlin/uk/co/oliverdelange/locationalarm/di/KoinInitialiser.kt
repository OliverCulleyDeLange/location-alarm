package uk.co.oliverdelange.locationalarm.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import uk.co.oliverdelange.locationalarm.mapper.domain_to_ui.MapAppStateToMapUiState
import uk.co.oliverdelange.locationalarm.model.domain.AppStateStore
import uk.co.oliverdelange.locationalarm.model.ui.MapViewModel
import uk.co.oliverdelange.locationalarm.provider.SystemTimeProvider

val sharedModule = module {
    single { AppStateStore() }
    single { MapViewModel(get(), get()) }

    single { SystemTimeProvider() }

    single { MapAppStateToMapUiState() }
}

fun appModule(): List<Module> = listOf(sharedModule)

fun initKoin() {
    startKoin {
        modules(appModule())
    }
}