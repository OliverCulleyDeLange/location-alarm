package uk.co.oliverdelange.locationalarm.store

import uk.co.oliverdelange.locationalarm.model.domain.AppState
import uk.co.oliverdelange.locationalarm.navigation.Navigate

actual fun AppStateStore.platformDoNavigation(
    appState: AppState,
    route: Navigate
): AppState {
    return appState.copy(navigateTo = route)
}