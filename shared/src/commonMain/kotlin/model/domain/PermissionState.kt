package model.domain

sealed interface PermissionState {
    data object Unknown : PermissionState
    data object Granted : PermissionState
    data class Denied(val shouldShowRationale: Boolean) : PermissionState
}


fun PermissionState.granted(): Boolean = this is PermissionState.Granted
fun PermissionState.shouldShowRationale(): Boolean = this is PermissionState.Denied && this.shouldShowRationale

