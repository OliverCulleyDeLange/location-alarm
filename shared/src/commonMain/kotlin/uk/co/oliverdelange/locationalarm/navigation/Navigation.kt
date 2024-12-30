package uk.co.oliverdelange.locationalarm.navigation

import kotlinx.serialization.Serializable
import uk.co.oliverdelange.locationalarm.model.ui.UiEvents

data class Navigate(val route: Route, val popUpTo: Route? = null) : UiEvents

@Serializable
sealed interface Route {
    @Serializable
    object LocationPermissionRequiredScreen : Route

    @Serializable
    object LocationPermissionDeniedScreen : Route

    @Serializable
    object MapScreen : Route

    @Serializable
    object DebugScreen : Route
}
