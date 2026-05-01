package com.aneeque.permissionpilot.sample

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.aneeque.permissionpilot.PermissionPilot
import com.aneeque.permissionpilot.sample.databinding.FragmentSampleBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Sample fragment demonstrating PermissionPilot usage within a Fragment.
 *
 * Demonstrates:
 * - Creating PermissionPilot with a Fragment
 * - Requesting location permission from a Fragment
 * - Rationale handling within Fragment context
 * - Permanently denied handling with settings navigation
 */
class SampleFragment : Fragment() {

    // Create PermissionPilot bound to this fragment — before STARTED state
    private val permissionPilot = PermissionPilot.create(this)

    private var _binding: FragmentSampleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSampleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRequestLocation.setOnClickListener {
            permissionPilot.requestPermission(
                permission = Manifest.permission.ACCESS_FINE_LOCATION,
                onGranted = {
                    binding.tvLocationStatus.text = getString(R.string.status_granted)
                    binding.tvLocationStatus.setBackgroundColor(
                        requireContext().getColor(R.color.status_granted)
                    )
                    toast("Location permission granted!")
                },
                onDenied = { permission ->
                    binding.tvLocationStatus.text = getString(R.string.status_denied)
                    binding.tvLocationStatus.setBackgroundColor(
                        requireContext().getColor(R.color.status_denied)
                    )
                    toast("Location denied. You can request again.")
                },
                onPermanentlyDenied = { permission ->
                    binding.tvLocationStatus.text = getString(R.string.status_permanently_denied)
                    binding.tvLocationStatus.setBackgroundColor(
                        requireContext().getColor(R.color.status_permanently_denied)
                    )
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Permission Required")
                        .setMessage("Location permission has been permanently denied. Please enable it in app settings.")
                        .setPositiveButton("Open Settings") { _, _ ->
                            permissionPilot.openAppSettings()
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                },
                onRationale = { permission, proceed ->
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.rationale_title)
                        .setMessage(R.string.rationale_location_message)
                        .setPositiveButton(R.string.rationale_proceed) { _, _ -> proceed() }
                        .setNegativeButton(R.string.rationale_cancel, null)
                        .show()
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
