package uk.co.oliverdelange.location_alarm.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uk.co.oliverdelange.location_alarm.screens.debug.DebugScreen
import uk.co.oliverdelange.location_alarm.screens.map.MapScreen
import uk.co.oliverdelange.location_alarm.screens.permissions.LocationPermissionsDeniedScreen
import uk.co.oliverdelange.location_alarm.screens.permissions.LocationPermissionsRequiredScreen
import uk.co.oliverdelange.locationalarm.model.domain.AppState
import uk.co.oliverdelange.locationalarm.navigation.Route

@Composable
fun Navigation(state: AppState, didNavigate: (Route) -> Unit) {
    val navController = rememberNavController().apply {
        addOnDestinationChangedListener { _, destination, args ->
            destination.toRoute()?.let { didNavigate(it) }
        }
    }

    NavHost(navController = navController, startDestination = Route.LocationPermissionRequiredScreen) {
        composable<Route.LocationPermissionDeniedScreen> { LocationPermissionsDeniedScreen() }
        composable<Route.LocationPermissionRequiredScreen> { LocationPermissionsRequiredScreen() }
        composable<Route.MapScreen> { MapScreen() }
        composable<Route.DebugScreen> { DebugScreen() }
    }
    LaunchedEffect(state.navigateTo) {
        state.navigateTo?.let { navigate ->
            if (navController.currentDestination?.toRoute() != navigate.route) {
                state.navigateTo?.let { navigate ->
                    val navOptions = NavOptions.Builder()
                        .run {
                            navigate.popUpTo?.let { popUpToRoute ->
                                setPopUpTo(popUpToRoute, true)
                            } ?: this
                        }
                        .build()
                    navController.navigate(navigate.route, navOptions)
                }
            }
        }
    }
}

/** Compose nav doesn't expose the route object directlty
 * This might get more complex if the route needs args. */
fun NavDestination.toRoute(): Route? {
    return when (route) {
        Route.MapScreen::class.qualifiedName -> Route.MapScreen
        Route.DebugScreen::class.qualifiedName -> Route.DebugScreen
        else -> null
    }
}
