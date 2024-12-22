package uk.co.oliverdelange.locationalarm.model.ui

import uk.co.oliverdelange.locationalarm.model.domain.Location
import uk.co.oliverdelange.locationalarm.model.domain.PermissionState

/** Combined [UserEvent]s and [UiResult]s to describe things that happen in the UI
 * that will be sent to the viewmodel to be handled */
interface UiEvents

/** User did something on screen
 * All events should be named such that they make sense when the word 'User' is added to the front */
sealed interface UserEvent : UiEvents {
    object TappedAllowLocationPermissions : UserEvent
    data class DraggedRadiusControl(val radius: Int) : UserEvent
    data class TappedMap(val location: Location) : UserEvent
    object TappedLocationIcon : UserEvent
    object TappedStopAlarm : UserEvent
    object ToggledAlarm : UserEvent
    object ToggledAlarmWithDelay : UserEvent

    /** Currently only IOS uses this as a mechanism to stop the alarm as android can used pending intents */
    object OpenedDeepLinkStopAlarm : UserEvent
}

/** Something happened as an indirect result of the user doing something */
sealed interface UiResult : UiEvents {
    /** Locations are updated while the app or alarm is active */
    data class LocationChanged(val location: List<Location>) : UserEvent

    /** User has responded to the system permissions dialog */
    data class LocationPermissionResult(val state: PermissionState) : UserEvent

    /** User has responded to the system permissions dialog */
    data class NotificationPermissionResult(val state: PermissionState) : UserEvent

    /** Fired when we attempt to ask the user for location permissions
     * This may or may not result in a dialog being shown */
    object RequestedLocationPermission : UserEvent

    /** Fired when we attempt to ask the user for notification permissions
     * This may or may not result in a dialog being shown */
    object RequestedNotificationPermission : UserEvent

    /** Fired when the map finishes flying to the users location */
    object FinishedFLyingToUsersLocation : UserEvent

    /** UI Event fired when the map comes into view or the app is bought to the foreground */
    object MapShown : UserEvent

    /** UI Event fired when the map is removed from view or the app is backgrounded */
    object MapNotShown : UserEvent
}