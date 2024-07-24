package uk.co.oliverdelange.location_alarm

enum class PermissionState {
    Unknown, Granted, Denied
}


fun PermissionState.granted(): Boolean = this == PermissionState.Granted

