package uk.co.oliverdelange.locationalarm.model.domain

import uk.co.oliverdelange.locationalarm.model.domain.PermissionState.Denied
import uk.co.oliverdelange.locationalarm.model.domain.PermissionState.Granted

sealed interface PermissionState {
    /** We haven't checked the permission state yet. The default */
    data object Unchecked : PermissionState

    /** We have checked the state, but the state is unknown - ie the user hasn't explicitly granted or denied yet */
    data object Unknown : PermissionState
    data object Granted : PermissionState
    data class Denied(val shouldShowRationale: Boolean) : PermissionState
}

fun permissionStateFrom(granted: Boolean) = if (granted) Granted else Denied(false)

fun PermissionState.granted(): Boolean = this is Granted
fun PermissionState.unchecked(): Boolean = this is PermissionState.Unchecked
fun PermissionState.unknown(): Boolean = this is PermissionState.Unknown
fun PermissionState.denied(): Boolean = this is Denied
fun PermissionState.shouldShowRationale(): Boolean = this is Denied && this.shouldShowRationale

