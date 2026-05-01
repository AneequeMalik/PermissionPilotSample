package com.aneeque.permissionpilot

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [PermissionResult] sealed class.
 */
class PermissionResultTest {

    @Test
    fun `Granted result has correct permission`() {
        val result = PermissionResult.Granted("android.permission.CAMERA")
        assertEquals("android.permission.CAMERA", result.permission)
    }

    @Test
    fun `Denied result has correct permission`() {
        val result = PermissionResult.Denied("android.permission.CAMERA")
        assertEquals("android.permission.CAMERA", result.permission)
    }

    @Test
    fun `PermanentlyDenied result has correct permission`() {
        val result = PermissionResult.PermanentlyDenied("android.permission.CAMERA")
        assertEquals("android.permission.CAMERA", result.permission)
    }

    @Test
    fun `isGranted returns true only for Granted`() {
        assertTrue(PermissionResult.Granted("test").isGranted)
        assertFalse(PermissionResult.Denied("test").isGranted)
        assertFalse(PermissionResult.PermanentlyDenied("test").isGranted)
    }

    @Test
    fun `isDenied returns true for Denied and PermanentlyDenied`() {
        assertFalse(PermissionResult.Granted("test").isDenied)
        assertTrue(PermissionResult.Denied("test").isDenied)
        assertTrue(PermissionResult.PermanentlyDenied("test").isDenied)
    }

    @Test
    fun `isPermanentlyDenied returns true only for PermanentlyDenied`() {
        assertFalse(PermissionResult.Granted("test").isPermanentlyDenied)
        assertFalse(PermissionResult.Denied("test").isPermanentlyDenied)
        assertTrue(PermissionResult.PermanentlyDenied("test").isPermanentlyDenied)
    }

    @Test
    fun `Granted data class equality works`() {
        val a = PermissionResult.Granted("android.permission.CAMERA")
        val b = PermissionResult.Granted("android.permission.CAMERA")
        val c = PermissionResult.Granted("android.permission.RECORD_AUDIO")

        assertEquals(a, b)
        assertNotEquals(a, c)
    }

    @Test
    fun `Denied data class equality works`() {
        val a = PermissionResult.Denied("android.permission.CAMERA")
        val b = PermissionResult.Denied("android.permission.CAMERA")
        val c = PermissionResult.Denied("android.permission.RECORD_AUDIO")

        assertEquals(a, b)
        assertNotEquals(a, c)
    }

    @Test
    fun `PermanentlyDenied data class equality works`() {
        val a = PermissionResult.PermanentlyDenied("android.permission.CAMERA")
        val b = PermissionResult.PermanentlyDenied("android.permission.CAMERA")
        val c = PermissionResult.PermanentlyDenied("android.permission.RECORD_AUDIO")

        assertEquals(a, b)
        assertNotEquals(a, c)
    }

    @Test
    fun `different result types are not equal`() {
        val granted = PermissionResult.Granted("test")
        val denied = PermissionResult.Denied("test")
        val permanentlyDenied = PermissionResult.PermanentlyDenied("test")

        assertNotEquals(granted, denied)
        assertNotEquals(granted, permanentlyDenied)
        assertNotEquals(denied, permanentlyDenied)
    }

    @Test
    fun `when expression covers all branches`() {
        val results = listOf<PermissionResult>(
            PermissionResult.Granted("a"),
            PermissionResult.Denied("b"),
            PermissionResult.PermanentlyDenied("c")
        )

        val messages = results.map { result ->
            when (result) {
                is PermissionResult.Granted -> "granted:${result.permission}"
                is PermissionResult.Denied -> "denied:${result.permission}"
                is PermissionResult.PermanentlyDenied -> "permanent:${result.permission}"
            }
        }

        assertEquals(listOf("granted:a", "denied:b", "permanent:c"), messages)
    }
}
