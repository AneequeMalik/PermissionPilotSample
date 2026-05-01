package com.aneeque.permissionpilot

/**
 * Represents the current status of a permission.
 *
 * This enum is used in multi-permission result maps to indicate the
 * status of each individually requested permission.
 */
enum class PermissionStatus {

    /**
     * The permission is currently granted.
     */
    GRANTED,

    /**
     * The permission is denied but can be requested again.
     * The system may show a rationale dialog on the next request.
     */
    DENIED,

    /**
     * The permission is permanently denied.
     * The user selected "Don't ask again" or the system will no longer show the prompt.
     * Direct the user to app settings to manually grant this permission.
     */
    PERMANENTLY_DENIED;

    /**
     * Returns true if the permission is granted.
     */
    val isGranted: Boolean
        get() = this == GRANTED

    /**
     * Returns true if the permission is denied (either temporarily or permanently).
     */
    val isDenied: Boolean
        get() = this == DENIED || this == PERMANENTLY_DENIED

    /**
     * Returns true if the permission is permanently denied.
     */
    val isPermanentlyDenied: Boolean
        get() = this == PERMANENTLY_DENIED
}
