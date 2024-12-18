package uk.co.oliverdelange.location_alarm.permissions

class AndroidPermissionMappingException(val permission: String) : RuntimeException() {
    override val message: String
        get() = "Failed to map permission string [$permission] into AndroidPermission enum"
}
