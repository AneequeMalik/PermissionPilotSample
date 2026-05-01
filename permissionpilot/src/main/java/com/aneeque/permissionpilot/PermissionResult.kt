package com.aneeque.permissionpilot

/**
 * Represents the result of a single permission request.
 *
 * Use pattern matching to handle different outcomes:
 * ```kotlin
 * when (result) {
 *     is PermissionResult.Granted -> { /* handle granted */ }
 *     is PermissionResult.Denied -> { /* handle denied */ }
 *     is PermissionResult.PermanentlyDenied -> { /* handle permanently denied */ }
 * }
 * ```
 */
sealed class PermissionResult {

    /**
     * The permission string associated with this result.
     */
    abstract val permission: String

    /**
     * The permission was granted by the user.
     * @param permission The permission string that was granted.
     */
    data class Granted(override val permission: String) : PermissionResult()

    /**
     * The permission was denied by the user, but can be requested again.
     * A rationale can be shown before re-requesting.
     * @param permission The permission string that was denied.
     */
    data class Denied(override val permission: String) : PermissionResult()

    /**
     * The permission was permanently denied by the user (selected "Don't ask again").
     * The user must be directed to app settings to grant this permission.
     * @param permission The permission string that was permanently denied.
     */
    data class PermanentlyDenied(override val permission: String) : PermissionResult()

    /**
     * Returns true if the permission was granted.
     */
    val isGranted: Boolean
        get() = this is Granted

    /**
     * Returns true if the permission was denied (either temporarily or permanently).
     */
    val isDenied: Boolean
        get() = this is Denied || this is PermanentlyDenied

    /**
     * Returns true if the permission was permanently denied.
     */
    val isPermanentlyDenied: Boolean
        get() = this is PermanentlyDenied
}
