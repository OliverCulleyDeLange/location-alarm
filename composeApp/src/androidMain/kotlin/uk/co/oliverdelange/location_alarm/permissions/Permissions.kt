package uk.co.oliverdelange.location_alarm.permissions

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import uk.co.oliverdelange.location_alarm.permissions.AndroidPermission.AccessCoarseLocation
import uk.co.oliverdelange.location_alarm.permissions.AndroidPermission.AccessFineLocation
import uk.co.oliverdelange.location_alarm.permissions.AndroidPermission.PostNotifications
import uk.co.oliverdelange.locationalarm.logging.SLog
import uk.co.oliverdelange.locationalarm.model.domain.PermissionState
import uk.co.oliverdelange.locationalarm.model.domain.RequestablePermission
import uk.co.oliverdelange.locationalarm.model.domain.RequestablePermission.Location
import uk.co.oliverdelange.locationalarm.model.domain.RequestablePermission.Notifications

/** An attempt to contain the madness of Android permissions
 * - Mainly adds functionality to detect when the user hasn't yet decided on a permission: [PermissionState.Unknown]
 * TODO Unit tests
 * */
class Permissions(
    val requiredPermissions: RequestablePermission,
    val onPermissionChanged: (PermissionState) -> Unit
) {
    private var previousPermissionState: PermissionState? = null
    private val currentPermissionState = requiredPermissions
        .androidPermissions()
        .associateWith { AndroidPermissionState(it) }
        .toMutableMap()


    /** Call this when we're certain the user has responded to a permission dialog.
     * If we're certain the user should have responded to a permission dialog,
     * and after doing so the permission is not granted and shouldShowRationale is unchanged
     * This means that the user has fully denied the permission.
     * This handles the case where a user has already denied a permission and opens the app fresh.
     * Since we can't initially tell the difference between unknown and denied, we allow the user to try and grant the permission.
     * The system won't even show the permission UI if it has been shown too many times already,
     * but will still call the permission changed listener,
     * and therefore nothing will change in the permission state, meaning we can assume they've denied it.
     */
    fun onRequestedPermissions() {
        if (previousPermissionState !is PermissionState.Granted) {
            val allUnchanged = currentPermissionState.values.all { state ->
                !state.granted && state.previousShouldShowRationale == state.shouldShowRationale
            }
            if (allUnchanged) {
                val shouldShowRationale = currentPermissionState.values.any { it.shouldShowRationale }
                onPermissionChanged(PermissionState.Denied(shouldShowRationale))
            }
        }
    }

    /** Call this when permission state changes
     * Eg `
     * rememberMultiplePermissionsState(...) { permissions.onRequestedPermissions() }
     * `
     * Emits [PermissionState]'s when the overall state changes.
     * Handles multiple sub-permissions eg fine and coarse location.
     * */
    fun onPermissionsChanged(newPermissionStates: List<AndroidSystemPermissionState>) {
        newPermissionStates.forEach { newPermissionState ->
            val androidPermission = AndroidPermission.fromAndroidString(newPermissionState.permission)
            val newShouldShowRationale = newPermissionState.shouldShowRationale
            val previousState = currentPermissionState[androidPermission]
            if (previousState == null) {
                SLog.e("currentPermissionState is missing ${newPermissionState.permission}")
                return@forEach
            }
            val previousShouldShowRationale = previousState.shouldShowRationale
            var newInternalState: AndroidPermissionState = previousState.copy(
                granted = newPermissionState.granted,
                shouldShowRationale = newPermissionState.shouldShowRationale
            )
            // If shouldShowRationale goes from true to false, user has denied permission for realsies.
            if (previousShouldShowRationale && !newShouldShowRationale) {
                newInternalState = newInternalState.copy(shouldShowRationaleBecameFalse = true)
            }
            newInternalState = newInternalState.copy(previousShouldShowRationale = newShouldShowRationale)
            currentPermissionState.replace(androidPermission, newInternalState)
        }

        val newState = calculatePermissionState()
        if (newState != previousPermissionState) {
            onPermissionChanged(newState)
            previousPermissionState = newState
        }
    }

    private fun calculatePermissionState(): PermissionState {
        val anyShouldShowRationale = currentPermissionState.values.any { it.shouldShowRationale }
        return if (currentPermissionState.values.any { it.denied }) {
            PermissionState.Denied(anyShouldShowRationale)
        } else if (currentPermissionState.values.all { it.granted }) {
            PermissionState.Granted
        } else if (anyShouldShowRationale) {
            PermissionState.Denied(true)
        } else {
            PermissionState.Unknown
        }
    }
}

fun RequestablePermission.androidPermissionStrings(): List<String> {
    return androidPermissions().map { it.string }
}

fun RequestablePermission.androidPermissions(): List<AndroidPermission> {
    return when (this) {
        Notifications -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(PostNotifications)
        } else {
            emptyList()
        }

        Location -> listOf(AccessFineLocation, AccessCoarseLocation)
    }
}

/** Enum wrapping the magic android permission strings*/
enum class AndroidPermission(val string: String) {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    PostNotifications(POST_NOTIFICATIONS),
    AccessFineLocation(ACCESS_FINE_LOCATION),
    AccessCoarseLocation(ACCESS_COARSE_LOCATION), ;

    companion object {
        @SuppressLint("NewApi")
        fun fromAndroidString(permission: String): AndroidPermission {
            return when (permission) {
                POST_NOTIFICATIONS -> PostNotifications
                ACCESS_FINE_LOCATION -> AccessFineLocation
                ACCESS_COARSE_LOCATION -> AccessCoarseLocation
                else -> throw AndroidPermissionMappingException(permission)
            }
        }
    }
}

/** Exactly how the android permission system models permissions */
data class AndroidSystemPermissionState(
    val permission: String,
    val granted: Boolean,
    val shouldShowRationale: Boolean,
)

/** An extension on how the Android system represents permissions */
data class AndroidPermissionState(
    val permission: AndroidPermission,
    val granted: Boolean = false,
    val shouldShowRationale: Boolean = false,
    /** We track the previous value to know when it changes. See [shouldShowRationaleBecameFalse] */
    val previousShouldShowRationale: Boolean = false,
    /** Indicates when shouldShowRationale goes from true to false
     * This indicates a user has fully denied the permission */
    val shouldShowRationaleBecameFalse: Boolean = false,
) {
    val denied: Boolean = !granted && shouldShowRationaleBecameFalse
}