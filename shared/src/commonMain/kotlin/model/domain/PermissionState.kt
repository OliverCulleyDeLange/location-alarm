package model.domain

enum class PermissionState {
    Unknown, Granted, Denied
}


fun PermissionState.granted(): Boolean = this == PermissionState.Granted

