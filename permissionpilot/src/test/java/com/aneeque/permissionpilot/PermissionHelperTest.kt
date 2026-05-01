package com.aneeque.permissionpilot

import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

/**
 * Unit tests for [PermissionHelper] utility object.
 *
 * Uses Robolectric to simulate Android framework behavior without a device.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class PermissionHelperTest {

    private lateinit var application: Application

    @Before
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun `isGranted returns false for non-granted permission`() {
        // By default, permissions are not granted in Robolectric
        val result = PermissionHelper.isGranted(application, Manifest.permission.CAMERA)
        assertFalse(result)
    }

    @Test
    fun `isGranted returns true for granted permission`() {
        val shadowApp = shadowOf(application)
        shadowApp.grantPermissions(Manifest.permission.CAMERA)

        val result = PermissionHelper.isGranted(application, Manifest.permission.CAMERA)
        assertTrue(result)
    }

    @Test
    fun `createAppSettingsIntent has correct action`() {
        val intent = PermissionHelper.createAppSettingsIntent(application)

        assertEquals(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, intent.action)
    }

    @Test
    fun `createAppSettingsIntent has correct data URI`() {
        val intent = PermissionHelper.createAppSettingsIntent(application)

        assertNotNull(intent.data)
        assertEquals("package", intent.data?.scheme)
        assertEquals(application.packageName, intent.data?.schemeSpecificPart)
    }

    @Test
    fun `createAppSettingsIntent has NEW_TASK flag`() {
        val intent = PermissionHelper.createAppSettingsIntent(application)

        assertTrue(intent.flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0)
    }

    @Test
    fun `isGranted works with multiple different permissions`() {
        val shadowApp = shadowOf(application)

        // Grant only CAMERA
        shadowApp.grantPermissions(Manifest.permission.CAMERA)

        assertTrue(PermissionHelper.isGranted(application, Manifest.permission.CAMERA))
        assertFalse(PermissionHelper.isGranted(application, Manifest.permission.RECORD_AUDIO))
        assertFalse(PermissionHelper.isGranted(application, Manifest.permission.ACCESS_FINE_LOCATION))
    }
}
