package com.aneeque.permissionpilot.sample

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aneeque.permissionpilot.PermissionResult
import com.aneeque.permissionpilot.PermissionStatus
import com.aneeque.permissionpilot.compose.rememberMultiplePermissionsPilot
import com.aneeque.permissionpilot.compose.rememberOpenAppSettings
import com.aneeque.permissionpilot.compose.rememberPermissionPilot

/**
 * Compose activity demonstrating PermissionPilot with ALL permission categories.
 */
class ComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PermissionPilotTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AllPermissionsDemoScreen()
                }
            }
        }
    }
}

@Composable
private fun PermissionPilotTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF6750A4),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFEADDFF),
            onPrimaryContainer = Color(0xFF21005D),
            secondary = Color(0xFF625B71),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFE8DEF8),
            onSecondaryContainer = Color(0xFF1D192B),
            background = Color(0xFFFFFBFE),
            surface = Color(0xFFFFFBFE),
            surfaceVariant = Color(0xFFE7E0EC),
        ),
        content = content
    )
}

@Composable
private fun AllPermissionsDemoScreen() {
    val context = LocalContext.current
    val openSettings = rememberOpenAppSettings()
    val sdkInt = Build.VERSION.SDK_INT

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text("PermissionPilot", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text("Compose — All Permissions Demo", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.height(8.dp))

        // Camera
        SinglePermissionCard(
            title = "Camera",
            permission = Manifest.permission.CAMERA
        )

        // Microphone
        SinglePermissionCard(
            title = "Microphone",
            permission = Manifest.permission.RECORD_AUDIO
        )

        // Camera + Mic (multiple)
        MultiPermissionCard(
            title = "Camera + Microphone",
            permissions = listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        )

        // Fine Location
        SinglePermissionCard(title = "Fine Location", permission = Manifest.permission.ACCESS_FINE_LOCATION)

        // Coarse Location
        SinglePermissionCard(title = "Coarse Location", permission = Manifest.permission.ACCESS_COARSE_LOCATION)

        // Fine + Coarse Location
        MultiPermissionCard(
            title = "Fine + Coarse Location",
            permissions = listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )

        // Background Location (API 29+)
        if (sdkInt >= Build.VERSION_CODES.Q) {
            SinglePermissionCard(title = "Background Location", permission = Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        // Storage (legacy, API ≤ 32)
        if (sdkInt <= Build.VERSION_CODES.S_V2) {
            SinglePermissionCard(title = "Read Storage (legacy)", permission = Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        // Granular Media (API 33+)
        if (sdkInt >= Build.VERSION_CODES.TIRAMISU) {
            SinglePermissionCard(title = "Media: Images", permission = Manifest.permission.READ_MEDIA_IMAGES)
            SinglePermissionCard(title = "Media: Video", permission = Manifest.permission.READ_MEDIA_VIDEO)
            SinglePermissionCard(title = "Media: Audio", permission = Manifest.permission.READ_MEDIA_AUDIO)
            MultiPermissionCard(
                title = "All Media Permissions",
                permissions = listOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO
                )
            )
        }

        // Phone
        SinglePermissionCard(title = "Call Phone", permission = Manifest.permission.CALL_PHONE)
        SinglePermissionCard(title = "Read Phone State", permission = Manifest.permission.READ_PHONE_STATE)
        SinglePermissionCard(title = "Read Call Log", permission = Manifest.permission.READ_CALL_LOG)
        MultiPermissionCard(
            title = "All Phone",
            permissions = listOf(Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG)
        )

        // SMS
        SinglePermissionCard(title = "Send SMS", permission = Manifest.permission.SEND_SMS)
        SinglePermissionCard(title = "Read SMS", permission = Manifest.permission.READ_SMS)
        SinglePermissionCard(title = "Receive SMS", permission = Manifest.permission.RECEIVE_SMS)
        MultiPermissionCard(
            title = "All SMS",
            permissions = listOf(Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS)
        )

        // Contacts
        SinglePermissionCard(title = "Read Contacts", permission = Manifest.permission.READ_CONTACTS)
        SinglePermissionCard(title = "Write Contacts", permission = Manifest.permission.WRITE_CONTACTS)
        MultiPermissionCard(
            title = "All Contacts",
            permissions = listOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)
        )

        // Calendar
        SinglePermissionCard(title = "Read Calendar", permission = Manifest.permission.READ_CALENDAR)
        SinglePermissionCard(title = "Write Calendar", permission = Manifest.permission.WRITE_CALENDAR)
        MultiPermissionCard(
            title = "All Calendar",
            permissions = listOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)
        )

        // Sensors
        SinglePermissionCard(title = "Body Sensors", permission = Manifest.permission.BODY_SENSORS)
        if (sdkInt >= Build.VERSION_CODES.Q) {
            SinglePermissionCard(title = "Activity Recognition", permission = Manifest.permission.ACTIVITY_RECOGNITION)
        }

        // Bluetooth (API 31+)
        if (sdkInt >= Build.VERSION_CODES.S) {
            SinglePermissionCard(title = "Bluetooth Scan", permission = Manifest.permission.BLUETOOTH_SCAN)
            SinglePermissionCard(title = "Bluetooth Connect", permission = Manifest.permission.BLUETOOTH_CONNECT)
            SinglePermissionCard(title = "Bluetooth Advertise", permission = Manifest.permission.BLUETOOTH_ADVERTISE)
            MultiPermissionCard(
                title = "All Bluetooth",
                permissions = listOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADVERTISE)
            )
        }

        // Notifications (API 33+)
        if (sdkInt >= Build.VERSION_CODES.TIRAMISU) {
            SinglePermissionCard(title = "Post Notifications", permission = Manifest.permission.POST_NOTIFICATIONS)
        }

        // Nearby Wi-Fi (API 33+)
        if (sdkInt >= Build.VERSION_CODES.TIRAMISU) {
            SinglePermissionCard(title = "Nearby Wi-Fi Devices", permission = Manifest.permission.NEARBY_WIFI_DEVICES)
        }

        // Settings
        OutlinedButton(
            onClick = { openSettings() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Open App Settings")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// Reusable single-permission card

@Composable
private fun SinglePermissionCard(title: String, permission: String) {
    var statusMsg by remember { mutableStateOf("Not requested") }

    val permState = rememberPermissionPilot(
        permission = permission,
        onResult = { result ->
            statusMsg = when (result) {
                is PermissionResult.Granted -> "Granted"
                is PermissionResult.Denied -> "Denied (can retry)"
                is PermissionResult.PermanentlyDenied -> "Permanently Denied"
            }
        }
    )

    PermissionCardUI(
        title = title,
        statusMessage = statusMsg,
        isGranted = permState.isGranted,
        onRequest = { permState.launchRequest() }
    )
}

// Reusable multi-permission card

@Composable
private fun MultiPermissionCard(title: String, permissions: List<String>) {
    var statusMsg by remember { mutableStateOf("Not requested") }

    val multiState = rememberMultiplePermissionsPilot(
        permissions = permissions,
        onResult = { statuses ->
            val granted = statuses.count { it.value == PermissionStatus.GRANTED }
            val total = statuses.size
            statusMsg = when {
                statuses.values.all { it == PermissionStatus.GRANTED } -> "All $total granted"
                statuses.values.any { it == PermissionStatus.PERMANENTLY_DENIED } -> {
                    val denied = statuses.filter { it.value == PermissionStatus.PERMANENTLY_DENIED }
                        .keys.joinToString { it.substringAfterLast('.') }
                    "Permanently denied: $denied"
                }
                else -> "$granted/$total granted"
            }
        }
    )

    PermissionCardUI(
        title = title,
        statusMessage = statusMsg,
        isGranted = multiState.allGranted,
        onRequest = { multiState.launchRequest() }
    )
}

// Shared card UI

@Composable
private fun PermissionCardUI(
    title: String,
    statusMessage: String,
    isGranted: Boolean,
    onRequest: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                val dotColor by animateColorAsState(
                    targetValue = if (isGranted) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
                    label = "dot"
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
            }

            if (statusMessage != "Not requested") {
                Text(
                    text = statusMessage,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                )
            }

            Button(
                onClick = onRequest,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Request")
            }
        }
    }
}
