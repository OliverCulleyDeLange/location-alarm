package uk.co.oliverdelange.location_alarm.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uk.co.oliverdelange.location_alarm.screens.debug.DebugScreen
import uk.co.oliverdelange.location_alarm.screens.map.MapScreen
import uk.co.oliverdelange.locationalarm.model.domain.AppState
import uk.co.oliverdelange.locationalarm.navigation.Route
import uk.co.oliverdelange.locationalarm.store.AppStateStore

@Composable
fun Navigation(appStateStore: AppStateStore, state: AppState) {
    val navController = rememberNavController().apply {
        addOnDestinationChangedListener { _, destination, args ->
            destination.toRoute()?.let { appStateStore.didNavigate(it) }
        }
    }

    NavHost(navController = navController, startDestination = Route.MapScreen) {
        composable<Route.MapScreen> { MapScreen() }
        composable<Route.DebugScreen> { DebugScreen() }
    }
    LaunchedEffect(state.navigateTo) {
        if (navController.currentDestination != state.navigateTo) {
            state.navigateTo?.let { navController.navigate(it) }
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
