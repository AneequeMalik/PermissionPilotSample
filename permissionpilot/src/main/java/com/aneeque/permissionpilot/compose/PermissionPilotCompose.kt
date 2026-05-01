package com.aneeque.permissionpilot.compose

import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aneeque.permissionpilot.PermissionHelper
import com.aneeque.permissionpilot.PermissionResult
import com.aneeque.permissionpilot.PermissionStatus

/**
 * State holder for a single permission request in Jetpack Compose.
 *
 * @property permission The permission this state represents.
 * @property status The current [PermissionStatus] of the permission.
 */
@Stable
class PermissionPilotState internal constructor(
    val permission: String,
    status: PermissionStatus
) {
    /**
     * The current status of the permission.
     * Updates automatically after a request is made.
     */
    var status by mutableStateOf(status)
        internal set

    /**
     * The latest result from a permission request, or null if no request has been made yet.
     */
    var result by mutableStateOf<PermissionResult?>(null)
        internal set

    /**
     * Whether the permission is currently granted.
     */
    val isGranted: Boolean
        get() = status == PermissionStatus.GRANTED

    /**
     * Whether a rationale should be shown to the user.
     */
    var shouldShowRationale by mutableStateOf(false)
        internal set

    /**
     * Lambda to launch the permission request. Set internally by [rememberPermissionPilot].
     */
    var launchRequest: () -> Unit = {}
        internal set
}

/**
 * State holder for multiple permission requests in Jetpack Compose.
 *
 * @property permissions The list of permissions this state represents.
 * @property statuses A map of each permission to its current [PermissionStatus].
 */
@Stable
class MultiplePermissionPilotState internal constructor(
    val permissions: List<String>,
    statuses: Map<String, PermissionStatus>
) {
    /**
     * Map of each permission to its current [PermissionStatus].
     * Updates automatically after a request is made.
     */
    var statuses by mutableStateOf(statuses)
        internal set

    /**
     * Whether all permissions are currently granted.
     */
    val allGranted: Boolean
        get() = statuses.values.all { it == PermissionStatus.GRANTED }

    /**
     * List of permissions that are currently granted.
     */
    val grantedPermissions: List<String>
        get() = statuses.filter { it.value == PermissionStatus.GRANTED }.keys.toList()

    /**
     * List of permissions that are currently denied (either temporarily or permanently).
     */
    val deniedPermissions: List<String>
        get() = statuses.filter { it.value != PermissionStatus.GRANTED }.keys.toList()

    /**
     * List of permissions that are permanently denied.
     */
    val permanentlyDeniedPermissions: List<String>
        get() = statuses.filter { it.value == PermissionStatus.PERMANENTLY_DENIED }.keys.toList()

    /**
     * Whether any permission requires a rationale to be shown.
     */
    var shouldShowRationale by mutableStateOf(false)
        internal set

    /**
     * Lambda to launch the permission request for all permissions. Set internally.
     */
    var launchRequest: () -> Unit = {}
        internal set
}

/**
 * Remembers and creates a [PermissionPilotState] for a single permission request.
 *
 * This composable registers an [ActivityResultLauncher] and manages the permission
 * state lifecycle automatically.
 *
 * ## Example
 * ```kotlin
 * @Composable
 * fun CameraScreen() {
 *     val cameraPermission = rememberPermissionPilot(
 *         permission = Manifest.permission.CAMERA,
 *         onResult = { result ->
 *             when (result) {
 *                 is PermissionResult.Granted -> { /* use camera */ }
 *                 is PermissionResult.Denied -> { /* show rationale */ }
 *                 is PermissionResult.PermanentlyDenied -> { /* direct to settings */ }
 *             }
 *         }
 *     )
 *
 *     Button(onClick = { cameraPermission.launchRequest() }) {
 *         Text("Request Camera")
 *     }
 * }
 * ```
 *
 * @param permission The permission to request.
 * @param onResult Callback invoked with the [PermissionResult] after the user responds.
 * @return A [PermissionPilotState] representing the current state of the permission.
 */
@Composable
fun rememberPermissionPilot(
    permission: String,
    onResult: (PermissionResult) -> Unit
): PermissionPilotState {
    val context = LocalContext.current
    val activity = context as? Activity

    val initialStatus = if (ContextCompat.checkSelfPermission(context, permission)
        == PackageManager.PERMISSION_GRANTED
    ) {
        PermissionStatus.GRANTED
    } else {
        PermissionStatus.DENIED
    }

    val state = remember(permission) {
        PermissionPilotState(permission, initialStatus)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            state.status = PermissionStatus.GRANTED
            val result = PermissionResult.Granted(permission)
            state.result = result
            onResult(result)
        } else {
            val showRationale = activity != null &&
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
            state.shouldShowRationale = showRationale

            if (showRationale) {
                state.status = PermissionStatus.DENIED
                val result = PermissionResult.Denied(permission)
                state.result = result
                onResult(result)
            } else {
                state.status = PermissionStatus.PERMANENTLY_DENIED
                val result = PermissionResult.PermanentlyDenied(permission)
                state.result = result
                onResult(result)
            }
        }
    }

    state.launchRequest = { launcher.launch(permission) }

    // Update rationale state
    state.shouldShowRationale = activity != null &&
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)

    return state
}

/**
 * Remembers and creates a [MultiplePermissionPilotState] for requesting multiple permissions.
 *
 * ## Example
 * ```kotlin
 * @Composable
 * fun MediaScreen() {
 *     val mediaPermissions = rememberMultiplePermissionsPilot(
 *         permissions = listOf(
 *             Manifest.permission.CAMERA,
 *             Manifest.permission.RECORD_AUDIO
 *         ),
 *         onResult = { statuses ->
 *             if (statuses.values.all { it == PermissionStatus.GRANTED }) {
 *                 // All granted
 *             }
 *         }
 *     )
 *
 *     Button(onClick = { mediaPermissions.launchRequest() }) {
 *         Text("Request Permissions")
 *     }
 * }
 * ```
 *
 * @param permissions The list of permissions to request.
 * @param onResult Callback invoked with a map of each permission to its [PermissionStatus].
 * @return A [MultiplePermissionPilotState] representing the current state of all permissions.
 */
@Composable
fun rememberMultiplePermissionsPilot(
    permissions: List<String>,
    onResult: (Map<String, PermissionStatus>) -> Unit
): MultiplePermissionPilotState {
    val context = LocalContext.current
    val activity = context as? Activity

    val initialStatuses = permissions.associateWith { permission ->
        if (ContextCompat.checkSelfPermission(context, permission)
            == PackageManager.PERMISSION_GRANTED
        ) {
            PermissionStatus.GRANTED
        } else {
            PermissionStatus.DENIED
        }
    }

    val state = remember(permissions) {
        MultiplePermissionPilotState(permissions, initialStatuses)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val statusMap = results.map { (permission, isGranted) ->
            val status = when {
                isGranted -> PermissionStatus.GRANTED
                activity != null && ActivityCompat.shouldShowRequestPermissionRationale(
                    activity, permission
                ) -> PermissionStatus.DENIED
                else -> PermissionStatus.PERMANENTLY_DENIED
            }
            permission to status
        }.toMap()

        state.statuses = statusMap
        state.shouldShowRationale = statusMap.values.any { it == PermissionStatus.DENIED }
        onResult(statusMap)
    }

    state.launchRequest = { launcher.launch(permissions.toTypedArray()) }

    // Update rationale state
    state.shouldShowRationale = activity != null && permissions.any {
        ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
    }

    return state
}

/**
 * Extension function to open app settings from a composable context.
 * Useful when a permission is permanently denied.
 */
@Composable
fun rememberOpenAppSettings(): () -> Unit {
    val context = LocalContext.current
    return remember {
        { PermissionHelper.openAppSettings(context) }
    }
}
