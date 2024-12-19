package uk.co.oliverdelange.locationalarm.model.domain

sealed interface PermissionState {
    data object Unknown : PermissionState
    data object Granted : PermissionState
    data class Denied(val shouldShowRationale: Boolean) : PermissionState
}


fun PermissionState.granted(): Boolean = this is PermissionState.Granted
fun PermissionState.unknown(): Boolean = this is PermissionState.Unknown
fun PermissionState.denied(): Boolean = this is PermissionState.Denied
fun PermissionState.shouldShowRationale(): Boolean = this is PermissionState.Denied && this.shouldShowRationale

