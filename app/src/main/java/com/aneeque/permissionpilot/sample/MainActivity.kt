package com.aneeque.permissionpilot.sample

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aneeque.permissionpilot.PermissionPilot
import com.aneeque.permissionpilot.sample.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Main sample activity demonstrating PermissionPilot usage with XML views.
 *
 * Demonstrates ALL categories of Android dangerous permissions:
 * - Camera & Microphone
 * - Location (Fine, Coarse, Background)
 * - Storage & Media (legacy + Android 13 granular)
 * - Phone (Call, State, Call Log)
 * - SMS (Send, Read, Receive)
 * - Contacts (Read, Write)
 * - Calendar (Read, Write)
 * - Body Sensors & Activity Recognition
 * - Bluetooth (Scan, Connect, Advertise — Android 12+)
 * - Notifications (Android 13+)
 * - Nearby Devices (Android 12+)
 * - Fragment integration
 * - Compose demo navigation
 */
class MainActivity : AppCompatActivity() {

    private val permissionPilot = PermissionPilot.create(this)
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCameraAndMic()
        setupLocation()
        setupStorage()
        setupPhone()
        setupSms()
        setupContacts()
        setupCalendar()
        setupSensors()
        setupBluetooth()
        setupNotifications()
        setupNearby()
        setupNavigation()
    }
    
    // CAMERA & MICROPHONE

    private fun setupCameraAndMic() {
        binding.btnCamera.setOnClickListener {
            requestSingle(Manifest.permission.CAMERA, "Camera", binding.tvCameraMicStatus)
        }
        binding.btnMicrophone.setOnClickListener {
            requestSingle(Manifest.permission.RECORD_AUDIO, "Microphone", binding.tvCameraMicStatus)
        }
        binding.btnCameraAndMic.setOnClickListener {
            requestMultiple(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                "Camera + Mic",
                binding.tvCameraMicStatus
            )
        }
    }
    
    // LOCATION

    private fun setupLocation() {
        binding.btnFineLocation.setOnClickListener {
            requestSingle(
                Manifest.permission.ACCESS_FINE_LOCATION,
                "Fine Location",
                binding.tvLocationStatus
            )
        }
        binding.btnCoarseLocation.setOnClickListener {
            requestSingle(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                "Coarse Location",
                binding.tvLocationStatus
            )
        }
        binding.btnBothLocation.setOnClickListener {
            requestMultiple(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                "Fine + Coarse Location",
                binding.tvLocationStatus
            )
        }
        binding.btnBackgroundLocation.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestSingle(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    "Background Location",
                    binding.tvLocationStatus
                )
            } else {
                toast("Background Location requires Android 10+")
            }
        }
    }
    
    // STORAGE & MEDIA
    
    private fun setupStorage() {
        binding.btnReadStorage.setOnClickListener {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                requestSingle(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    "Read Storage",
                    binding.tvStorageStatus
                )
            } else {
                toast("READ_EXTERNAL_STORAGE is deprecated on Android 13+. Use granular media permissions.")
            }
        }
        binding.btnMediaImages.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestSingle(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    "Media Images",
                    binding.tvStorageStatus
                )
            } else {
                toast("READ_MEDIA_IMAGES requires Android 13+")
            }
        }
        binding.btnMediaVideo.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestSingle(
                    Manifest.permission.READ_MEDIA_VIDEO,
                    "Media Video",
                    binding.tvStorageStatus
                )
            } else {
                toast("READ_MEDIA_VIDEO requires Android 13+")
            }
        }
        binding.btnMediaAudio.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestSingle(
                    Manifest.permission.READ_MEDIA_AUDIO,
                    "Media Audio",
                    binding.tvStorageStatus
                )
            } else {
                toast("READ_MEDIA_AUDIO requires Android 13+")
            }
        }
        binding.btnAllMedia.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestMultiple(
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_AUDIO
                    ),
                    "All Media",
                    binding.tvStorageStatus
                )
            } else {
                requestSingle(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    "Read Storage (legacy)",
                    binding.tvStorageStatus
                )
            }
        }
    }
    
    // PHONE

    private fun setupPhone() {
        binding.btnCallPhone.setOnClickListener {
            requestSingle(Manifest.permission.CALL_PHONE, "Call Phone", binding.tvPhoneStatus)
        }
        binding.btnReadPhoneState.setOnClickListener {
            requestSingle(
                Manifest.permission.READ_PHONE_STATE,
                "Read Phone State",
                binding.tvPhoneStatus
            )
        }
        binding.btnReadCallLog.setOnClickListener {
            requestSingle(
                Manifest.permission.READ_CALL_LOG,
                "Read Call Log",
                binding.tvPhoneStatus
            )
        }
        binding.btnAllPhone.setOnClickListener {
            requestMultiple(
                arrayOf(
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_CALL_LOG
                ),
                "All Phone",
                binding.tvPhoneStatus
            )
        }
    }
    
    // SMS
    
    private fun setupSms() {
        binding.btnSendSms.setOnClickListener {
            requestSingle(Manifest.permission.SEND_SMS, "Send SMS", binding.tvSmsStatus)
        }
        binding.btnReadSms.setOnClickListener {
            requestSingle(Manifest.permission.READ_SMS, "Read SMS", binding.tvSmsStatus)
        }
        binding.btnReceiveSms.setOnClickListener {
            requestSingle(Manifest.permission.RECEIVE_SMS, "Receive SMS", binding.tvSmsStatus)
        }
        binding.btnAllSms.setOnClickListener {
            requestMultiple(
                arrayOf(
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS
                ),
                "All SMS",
                binding.tvSmsStatus
            )
        }
    }
    
    // CONTACTS

    private fun setupContacts() {
        binding.btnReadContacts.setOnClickListener {
            requestSingle(
                Manifest.permission.READ_CONTACTS,
                "Read Contacts",
                binding.tvContactsStatus
            )
        }
        binding.btnWriteContacts.setOnClickListener {
            requestSingle(
                Manifest.permission.WRITE_CONTACTS,
                "Write Contacts",
                binding.tvContactsStatus
            )
        }
        binding.btnAllContacts.setOnClickListener {
            requestMultiple(
                arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS),
                "All Contacts",
                binding.tvContactsStatus
            )
        }
    }
    
    // CALENDAR

    private fun setupCalendar() {
        binding.btnReadCalendar.setOnClickListener {
            requestSingle(
                Manifest.permission.READ_CALENDAR,
                "Read Calendar",
                binding.tvCalendarStatus
            )
        }
        binding.btnWriteCalendar.setOnClickListener {
            requestSingle(
                Manifest.permission.WRITE_CALENDAR,
                "Write Calendar",
                binding.tvCalendarStatus
            )
        }
        binding.btnAllCalendar.setOnClickListener {
            requestMultiple(
                arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR),
                "All Calendar",
                binding.tvCalendarStatus
            )
        }
    }
    
    // BODY SENSORS & ACTIVITY RECOGNITION

    private fun setupSensors() {
        binding.btnBodySensors.setOnClickListener {
            requestSingle(
                Manifest.permission.BODY_SENSORS,
                "Body Sensors",
                binding.tvSensorsStatus
            )
        }
        binding.btnActivityRecognition.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestSingle(
                    Manifest.permission.ACTIVITY_RECOGNITION,
                    "Activity Recognition",
                    binding.tvSensorsStatus
                )
            } else {
                toast("Activity Recognition requires Android 10+")
            }
        }
        binding.btnSensorsAndActivity.setOnClickListener {
            val perms = mutableListOf(Manifest.permission.BODY_SENSORS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                perms.add(Manifest.permission.ACTIVITY_RECOGNITION)
            }
            requestMultiple(perms.toTypedArray(), "Sensors + Activity", binding.tvSensorsStatus)
        }
    }
    
    // BLUETOOTH (Android 12+)

    private fun setupBluetooth() {
        binding.btnBluetoothScan.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestSingle(
                    Manifest.permission.BLUETOOTH_SCAN,
                    "Bluetooth Scan",
                    binding.tvBluetoothStatus
                )
            } else {
                toast("Bluetooth Scan permission requires Android 12+")
            }
        }
        binding.btnBluetoothConnect.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestSingle(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    "Bluetooth Connect",
                    binding.tvBluetoothStatus
                )
            } else {
                toast("Bluetooth Connect permission requires Android 12+")
            }
        }
        binding.btnBluetoothAdvertise.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestSingle(
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    "Bluetooth Advertise",
                    binding.tvBluetoothStatus
                )
            } else {
                toast("Bluetooth Advertise permission requires Android 12+")
            }
        }
        binding.btnAllBluetooth.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestMultiple(
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_ADVERTISE
                    ),
                    "All Bluetooth",
                    binding.tvBluetoothStatus
                )
            } else {
                toast("Bluetooth permissions require Android 12+")
            }
        }
    }
    
    // NOTIFICATIONS (Android 13+)

    private fun setupNotifications() {
        binding.btnPostNotifications.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestSingle(
                    Manifest.permission.POST_NOTIFICATIONS,
                    "Post Notifications",
                    binding.tvNotificationsStatus
                )
            } else {
                toast("POST_NOTIFICATIONS requires Android 13+. Notifications are allowed by default on older versions.")
            }
        }
    }
    
    // NEARBY DEVICES
    
    private fun setupNearby() {
        binding.btnNearbyWifi.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestSingle(
                    Manifest.permission.NEARBY_WIFI_DEVICES,
                    "Nearby Wi-Fi Devices",
                    binding.tvNearbyStatus
                )
            } else {
                toast("NEARBY_WIFI_DEVICES requires Android 13+")
            }
        }
    }
    
    // NAVIGATION

    private fun setupNavigation() {
        binding.btnComposeDemo.setOnClickListener {
            startActivity(Intent(this, ComposeActivity::class.java))
        }
        binding.btnOpenSettings.setOnClickListener {
            permissionPilot.openAppSettings()
        }
    }

    // HELPERS

    /**
     * Requests a single permission and updates the given status TextView.
     */
    private fun requestSingle(
        permission: String,
        displayName: String,
        statusView: android.widget.TextView
    ) {
        permissionPilot.requestPermission(
            permission = permission,
            onGranted = {
                statusView.text = "$displayName: Granted"
                statusView.setBackgroundColor(getColor(R.color.status_granted))
                toast("$displayName granted!")
            },
            onDenied = { _ ->
                statusView.text = "$displayName: Denied"
                statusView.setBackgroundColor(getColor(R.color.status_denied))
                toast("$displayName denied. You can request again.")
            },
            onPermanentlyDenied = { _ ->
                statusView.text = "$displayName: Permanently Denied"
                statusView.setBackgroundColor(getColor(R.color.status_permanently_denied))
                showPermanentlyDeniedDialog(displayName)
            },
            onRationale = { _, proceed ->
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.rationale_title)
                    .setMessage("$displayName permission is needed for this feature. Grant it?")
                    .setPositiveButton(R.string.rationale_proceed) { _, _ -> proceed() }
                    .setNegativeButton(R.string.rationale_cancel, null)
                    .show()
            }
        )
    }

    /**
     * Requests multiple permissions and updates the given status TextView.
     */
    private fun requestMultiple(
        permissions: Array<String>,
        displayName: String,
        statusView: android.widget.TextView
    ) {
        permissionPilot.requestPermissions(
            permissions = permissions,
            onAllGranted = {
                statusView.text = "$displayName: All Granted"
                statusView.setBackgroundColor(getColor(R.color.status_granted))
                toast("$displayName — all granted!")
            },
            onPartiallyGranted = { granted, denied ->
                statusView.text = "$displayName: ${granted.size} granted, ${denied.size} denied"
                statusView.setBackgroundColor(getColor(R.color.status_denied))
            },
            onDenied = { denied ->
                val names = denied.map { it.substringAfterLast('.') }
                toast("Denied: ${names.joinToString()}")
            },
            onPermanentlyDenied = { permanentlyDenied ->
                val names = permanentlyDenied.map { it.substringAfterLast('.') }
                statusView.text = "Permanently denied: ${names.joinToString()}"
                statusView.setBackgroundColor(getColor(R.color.status_permanently_denied))
                showPermanentlyDeniedDialog(names.joinToString())
            },
            onRationale = { _, proceed ->
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.rationale_title)
                    .setMessage("$displayName permissions are needed for this feature. Grant them?")
                    .setPositiveButton(R.string.rationale_proceed) { _, _ -> proceed() }
                    .setNegativeButton(R.string.rationale_cancel, null)
                    .show()
            }
        )
    }

    private fun showPermanentlyDeniedDialog(permissionName: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Permission Required")
            .setMessage(
                "$permissionName permission has been permanently denied. " +
                        "Please enable it in app settings."
            )
            .setPositiveButton("Open Settings") { _, _ ->
                permissionPilot.openAppSettings()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
