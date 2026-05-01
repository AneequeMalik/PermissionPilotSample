package com.aneeque.permissionpilot

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import java.lang.ref.WeakReference

/**
 * PermissionPilot — A modern, lifecycle-aware Android permission handling library.
 *
 * Simplifies runtime permission requests using the Activity Result API with support
 * for [ComponentActivity], [Fragment], and Jetpack Compose.
 *
 * ## Usage with Activity
 * ```kotlin
 * class MyActivity : AppCompatActivity() {
 *     // Must be created before onStart (as a property or in onCreate)
 *     private val PermissionPilot = PermissionPilot.create(this)
 *
 *     fun requestCamera() {
 *         PermissionPilot.requestPermission(
 *             permission = Manifest.permission.CAMERA,
 *             onGranted = { /* use camera */ },
 *             onDenied = { /* show explanation */ },
 *             onPermanentlyDenied = { /* direct to settings */ }
 *         )
 *     }
 * }
 * ```
 *
 * ## Usage with Fragment
 * ```kotlin
 * class MyFragment : Fragment() {
 *     private val PermissionPilot = PermissionPilot.create(this)
 *     // ... same API as Activity
 * }
 * ```
 *
 * @see compose.PermissionPilotCompose for Jetpack Compose usage
 */
class PermissionPilot private constructor(
    private val activityRef: WeakReference<ComponentActivity>?,
    private val fragmentRef: WeakReference<Fragment>?,
    private val singlePermissionLauncher: ActivityResultLauncher<String>,
    private val multiplePermissionsLauncher: ActivityResultLauncher<Array<String>>
) {

    // Pending callbacks for single permission request
    private var pendingSingleOnGranted: (() -> Unit)? = null
    private var pendingSingleOnDenied: ((String) -> Unit)? = null
    private var pendingSingleOnPermanentlyDenied: ((String) -> Unit)? = null
    private var pendingSinglePermission: String? = null

    // Pending callbacks for multiple permissions request
    private var pendingMultipleOnAllGranted: (() -> Unit)? = null
    private var pendingMultipleOnPartiallyGranted: ((List<String>, List<String>) -> Unit)? = null
    private var pendingMultipleOnDenied: ((List<String>) -> Unit)? = null
    private var pendingMultipleOnPermanentlyDenied: ((List<String>) -> Unit)? = null
    private var pendingMultiplePermissions: Array<String>? = null

    companion object {

        /**
         * Creates a [PermissionPilot] instance bound to the given [ComponentActivity].
         *
         * **Must be called before the activity reaches the STARTED state**
         * (e.g., as a class property or in `onCreate`).
         *
         * @param activity The [ComponentActivity] to bind to.
         * @return A new [PermissionPilot] instance.
         */
        fun create(activity: ComponentActivity): PermissionPilot {
            val activityRef = WeakReference(activity)
            var instance: PermissionPilot? = null

            val singleLauncher = activity.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                instance?.handleSingleResult(isGranted)
            }

            val multipleLauncher = activity.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { results ->
                instance?.handleMultipleResults(results)
            }

            return PermissionPilot(
                activityRef = activityRef,
                fragmentRef = null,
                singlePermissionLauncher = singleLauncher,
                multiplePermissionsLauncher = multipleLauncher
            ).also { instance = it }
        }

        /**
         * Creates a [PermissionPilot] instance bound to the given [Fragment].
         *
         * **Must be called before the fragment reaches the STARTED state**
         * (e.g., as a class property or in `onCreate`).
         *
         * @param fragment The [Fragment] to bind to.
         * @return A new [PermissionPilot] instance.
         */
        fun create(fragment: Fragment): PermissionPilot {
            val fragmentRef = WeakReference(fragment)
            var instance: PermissionPilot? = null

            val singleLauncher = fragment.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                instance?.handleSingleResult(isGranted)
            }

            val multipleLauncher = fragment.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { results ->
                instance?.handleMultipleResults(results)
            }

            return PermissionPilot(
                activityRef = null,
                fragmentRef = fragmentRef,
                singlePermissionLauncher = singleLauncher,
                multiplePermissionsLauncher = multipleLauncher
            ).also { instance = it }
        }
    }

    /**
     * The [Activity] associated with this instance, resolved from either the
     * activity or fragment reference.
     */
    private val activity: Activity?
        get() = activityRef?.get() ?: fragmentRef?.get()?.activity

    /**
     * The [Context] associated with this instance.
     */
    private val context: Context?
        get() = activityRef?.get() ?: fragmentRef?.get()?.context

    /**
     * Requests a single runtime permission.
     *
     * If the permission is already granted, [onGranted] is called immediately.
     * If a rationale should be shown and [onRationale] is provided, it is called with
     * a `proceed` callback. Calling `proceed()` will launch the system permission dialog.
     *
     * @param permission The permission to request (e.g., `Manifest.permission.CAMERA`).
     * @param onGranted Called when the permission is granted.
     * @param onDenied Called when the permission is denied but can be re-requested.
     * @param onPermanentlyDenied Called when the permission is permanently denied.
     * @param onRationale Called when a rationale should be shown before requesting.
     *   The `proceed` lambda should be invoked to continue with the actual request.
     */
    fun requestPermission(
        permission: String,
        onGranted: () -> Unit,
        onDenied: (permission: String) -> Unit = {},
        onPermanentlyDenied: (permission: String) -> Unit = {},
        onRationale: ((permission: String, proceed: () -> Unit) -> Unit)? = null
    ) {
        val ctx = context ?: return
        val act = activity

        // Already granted
        if (PermissionHelper.isGranted(ctx, permission)) {
            onGranted()
            return
        }

        // Check if rationale should be shown
        if (act != null && onRationale != null && PermissionHelper.shouldShowRationale(act, permission)) {
            onRationale(permission) {
                launchSingleRequest(permission, onGranted, onDenied, onPermanentlyDenied)
            }
            return
        }

        // Launch permission request
        launchSingleRequest(permission, onGranted, onDenied, onPermanentlyDenied)
    }

    /**
     * Requests multiple runtime permissions simultaneously.
     *
     * If all permissions are already granted, [onAllGranted] is called immediately.
     * If a rationale should be shown for any permission and [onRationale] is provided,
     * it is called with the permissions requiring rationale and a `proceed` callback.
     *
     * @param permissions The permissions to request.
     * @param onAllGranted Called when all permissions are granted.
     * @param onPartiallyGranted Called when some permissions are granted and some are denied.
     *   Receives lists of granted and denied permissions.
     * @param onDenied Called with the list of denied (but re-requestable) permissions.
     * @param onPermanentlyDenied Called with the list of permanently denied permissions.
     * @param onRationale Called when a rationale should be shown before requesting.
     *   The `proceed` lambda should be invoked to continue with the actual request.
     */
    fun requestPermissions(
        permissions: Array<String>,
        onAllGranted: () -> Unit,
        onPartiallyGranted: (granted: List<String>, denied: List<String>) -> Unit = { _, _ -> },
        onDenied: (denied: List<String>) -> Unit = {},
        onPermanentlyDenied: (permanentlyDenied: List<String>) -> Unit = {},
        onRationale: ((permissions: List<String>, proceed: () -> Unit) -> Unit)? = null
    ) {
        val ctx = context ?: return
        val act = activity

        // Check if all are already granted
        val allGranted = permissions.all { PermissionHelper.isGranted(ctx, it) }
        if (allGranted) {
            onAllGranted()
            return
        }

        // Check if rationale should be shown for any permission
        if (act != null && onRationale != null) {
            val rationalePermissions = permissions.filter {
                PermissionHelper.shouldShowRationale(act, it)
            }
            if (rationalePermissions.isNotEmpty()) {
                onRationale(rationalePermissions) {
                    launchMultipleRequest(
                        permissions, onAllGranted, onPartiallyGranted, onDenied, onPermanentlyDenied
                    )
                }
                return
            }
        }

        // Launch permissions request
        launchMultipleRequest(
            permissions, onAllGranted, onPartiallyGranted, onDenied, onPermanentlyDenied
        )
    }

    /**
     * Checks if a permission is currently granted.
     *
     * @param permission The permission to check.
     * @return `true` if the permission is granted.
     */
    fun isGranted(permission: String): Boolean {
        val ctx = context ?: return false
        return PermissionHelper.isGranted(ctx, permission)
    }

    /**
     * Checks if a rationale should be shown for the given permission.
     *
     * @param permission The permission to check.
     * @return `true` if a rationale should be shown.
     */
    fun shouldShowRationale(permission: String): Boolean {
        val act = activity ?: return false
        return PermissionHelper.shouldShowRationale(act, permission)
    }

    /**
     * Gets the current [PermissionStatus] of a permission.
     *
     * @param permission The permission to check.
     * @return The current [PermissionStatus], or [PermissionStatus.DENIED] if context is unavailable.
     */
    fun getPermissionStatus(permission: String): PermissionStatus {
        val act = activity ?: return PermissionStatus.DENIED
        return PermissionHelper.getPermissionStatus(act, permission)
    }

    /**
     * Opens the app's system settings page where the user can manually
     * grant or revoke permissions.
     */
    fun openAppSettings() {
        val ctx = context ?: return
        PermissionHelper.openAppSettings(ctx)
    }

    // ========================================================================
    // Internal Implementation
    // ========================================================================

    private fun launchSingleRequest(
        permission: String,
        onGranted: () -> Unit,
        onDenied: (String) -> Unit,
        onPermanentlyDenied: (String) -> Unit
    ) {
        pendingSinglePermission = permission
        pendingSingleOnGranted = onGranted
        pendingSingleOnDenied = onDenied
        pendingSingleOnPermanentlyDenied = onPermanentlyDenied
        singlePermissionLauncher.launch(permission)
    }

    private fun launchMultipleRequest(
        permissions: Array<String>,
        onAllGranted: () -> Unit,
        onPartiallyGranted: (List<String>, List<String>) -> Unit,
        onDenied: (List<String>) -> Unit,
        onPermanentlyDenied: (List<String>) -> Unit
    ) {
        pendingMultiplePermissions = permissions
        pendingMultipleOnAllGranted = onAllGranted
        pendingMultipleOnPartiallyGranted = onPartiallyGranted
        pendingMultipleOnDenied = onDenied
        pendingMultipleOnPermanentlyDenied = onPermanentlyDenied
        multiplePermissionsLauncher.launch(permissions)
    }

    /**
     * Handles the result of a single permission request.
     * Differentiates between denied and permanently denied using shouldShowRequestPermissionRationale.
     */
    internal fun handleSingleResult(isGranted: Boolean) {
        val permission = pendingSinglePermission ?: return

        if (isGranted) {
            pendingSingleOnGranted?.invoke()
        } else {
            val act = activity
            if (act != null && PermissionHelper.shouldShowRationale(act, permission)) {
                // User denied but can be asked again
                pendingSingleOnDenied?.invoke(permission)
            } else {
                // User selected "Don't ask again" or system permanently blocked
                pendingSingleOnPermanentlyDenied?.invoke(permission)
            }
        }

        clearSingleCallbacks()
    }

    /**
     * Handles the result of a multiple permissions request.
     * Categorizes each permission as granted, denied, or permanently denied.
     */
    internal fun handleMultipleResults(results: Map<String, Boolean>) {
        val granted = mutableListOf<String>()
        val denied = mutableListOf<String>()
        val permanentlyDenied = mutableListOf<String>()

        val act = activity

        results.forEach { (permission, isGranted) ->
            when {
                isGranted -> granted.add(permission)
                act != null && PermissionHelper.shouldShowRationale(act, permission) -> {
                    denied.add(permission)
                }
                else -> permanentlyDenied.add(permission)
            }
        }

        when {
            // All granted
            denied.isEmpty() && permanentlyDenied.isEmpty() -> {
                pendingMultipleOnAllGranted?.invoke()
            }
            // Some granted, some denied
            granted.isNotEmpty() && (denied.isNotEmpty() || permanentlyDenied.isNotEmpty()) -> {
                pendingMultipleOnPartiallyGranted?.invoke(granted, denied + permanentlyDenied)
            }
        }

        // Always notify about denied permissions if any
        if (denied.isNotEmpty()) {
            pendingMultipleOnDenied?.invoke(denied)
        }

        // Always notify about permanently denied permissions if any
        if (permanentlyDenied.isNotEmpty()) {
            pendingMultipleOnPermanentlyDenied?.invoke(permanentlyDenied)
        }

        clearMultipleCallbacks()
    }

    private fun clearSingleCallbacks() {
        pendingSinglePermission = null
        pendingSingleOnGranted = null
        pendingSingleOnDenied = null
        pendingSingleOnPermanentlyDenied = null
    }

    private fun clearMultipleCallbacks() {
        pendingMultiplePermissions = null
        pendingMultipleOnAllGranted = null
        pendingMultipleOnPartiallyGranted = null
        pendingMultipleOnDenied = null
        pendingMultipleOnPermanentlyDenied = null
    }
}
