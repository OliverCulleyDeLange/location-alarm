package uk.co.oliverdelange.locationalarm.store

import uk.co.oliverdelange.locationalarm.model.domain.AppState
import uk.co.oliverdelange.locationalarm.navigation.Navigate

/** Watch OS doesn't currently navigate */
actual fun AppStateStore.platformDoNavigation(
    appState: AppState,
    route: Navigate
): AppState {
    return appState
}