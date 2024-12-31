package uk.co.oliverdelange.locationalarm.navigation

import kotlinx.serialization.Serializable
import uk.co.oliverdelange.locationalarm.model.ui.UiEvents

data class Navigate(val route: Route, val popUpTo: Route? = null) : UiEvents

@Serializable
sealed interface Route {
    fun name() = toString()

    @Serializable
    data object LocationPermissionRequiredScreen : Route

    @Serializable
    data object LocationPermissionDeniedScreen : Route

    @Serializable
    data object MapScreen : Route

    @Serializable
    data object DebugScreen : Route
}
