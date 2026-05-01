package com.aneeque.permissionpilot

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [PermissionStatus] enum.
 */
class PermissionStatusTest {

    @Test
    fun `GRANTED isGranted returns true`() {
        assertTrue(PermissionStatus.GRANTED.isGranted)
    }

    @Test
    fun `DENIED isGranted returns false`() {
        assertFalse(PermissionStatus.DENIED.isGranted)
    }

    @Test
    fun `PERMANENTLY_DENIED isGranted returns false`() {
        assertFalse(PermissionStatus.PERMANENTLY_DENIED.isGranted)
    }

    @Test
    fun `GRANTED isDenied returns false`() {
        assertFalse(PermissionStatus.GRANTED.isDenied)
    }

    @Test
    fun `DENIED isDenied returns true`() {
        assertTrue(PermissionStatus.DENIED.isDenied)
    }

    @Test
    fun `PERMANENTLY_DENIED isDenied returns true`() {
        assertTrue(PermissionStatus.PERMANENTLY_DENIED.isDenied)
    }

    @Test
    fun `GRANTED isPermanentlyDenied returns false`() {
        assertFalse(PermissionStatus.GRANTED.isPermanentlyDenied)
    }

    @Test
    fun `DENIED isPermanentlyDenied returns false`() {
        assertFalse(PermissionStatus.DENIED.isPermanentlyDenied)
    }

    @Test
    fun `PERMANENTLY_DENIED isPermanentlyDenied returns true`() {
        assertTrue(PermissionStatus.PERMANENTLY_DENIED.isPermanentlyDenied)
    }

    @Test
    fun `enum has exactly three values`() {
        assertEquals(3, PermissionStatus.entries.size)
    }

    @Test
    fun `valueOf works for all entries`() {
        assertEquals(PermissionStatus.GRANTED, PermissionStatus.valueOf("GRANTED"))
        assertEquals(PermissionStatus.DENIED, PermissionStatus.valueOf("DENIED"))
        assertEquals(PermissionStatus.PERMANENTLY_DENIED, PermissionStatus.valueOf("PERMANENTLY_DENIED"))
    }

    @Test
    fun `ordinal values are correct`() {
        assertEquals(0, PermissionStatus.GRANTED.ordinal)
        assertEquals(1, PermissionStatus.DENIED.ordinal)
        assertEquals(2, PermissionStatus.PERMANENTLY_DENIED.ordinal)
    }
}
