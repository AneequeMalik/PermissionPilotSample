package com.aneeque.permissionpilot

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Utility helper object for common permission-related operations.
 *
 * Provides static methods for checking permission status, determining rationale needs,
 * and navigating to app settings.
 */
object PermissionHelper {

    /**
     * Checks if a given permission is currently granted.
     *
     * @param context The context to check against.
     * @param permission The permission string (e.g., `Manifest.permission.CAMERA`).
     * @return `true` if the permission is granted, `false` otherwise.
     */
    fun isGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Checks if a rationale should be shown for the given permission.
     *
     * This returns `true` when the user has previously denied the permission
     * without selecting "Don't ask again". Use this to show an explanation
     * before re-requesting.
     *
     * @param activity The activity to check rationale against.
     * @param permission The permission string.
     * @return `true` if a rationale should be shown.
     */
    fun shouldShowRationale(activity: Activity, permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    /**
     * Determines the current [PermissionStatus] for a given permission.
     *
     * - [PermissionStatus.GRANTED] if already granted.
     * - [PermissionStatus.DENIED] if denied but rationale can be shown (re-requestable).
     * - [PermissionStatus.PERMANENTLY_DENIED] if denied and rationale is not shown
     *   (user selected "Don't ask again" or system blocks further prompts).
     *
     * @param activity The activity to check against.
     * @param permission The permission string.
     * @return The current [PermissionStatus].
     */
    fun getPermissionStatus(activity: Activity, permission: String): PermissionStatus {
        return when {
            isGranted(activity, permission) -> PermissionStatus.GRANTED
            shouldShowRationale(activity, permission) -> PermissionStatus.DENIED
            else -> PermissionStatus.PERMANENTLY_DENIED
        }
    }

    /**
     * Opens the application's system settings page where the user can manually
     * grant or revoke permissions.
     *
     * @param context The context to use for launching the settings intent.
     */
    fun openAppSettings(context: Context) {
        val intent = createAppSettingsIntent(context)
        context.startActivity(intent)
    }

    /**
     * Creates an [Intent] that opens the application's system settings page.
     * Useful when you need to customize the intent before launching.
     *
     * @param context The context to derive the package name from.
     * @return An [Intent] configured to open app settings.
     */
    fun createAppSettingsIntent(context: Context): Intent {
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}
