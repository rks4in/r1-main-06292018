package com.tomtom.navui.r1systemport.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import com.tomtom.navui.sigappkit.BuildConfig
import com.tomtom.navui.systemport.SystemContext
import com.tomtom.navui.systemport.systemcomponents.PermissionSystemComponent
import com.tomtom.navui.systemport.systemcomponents.permissions.PermissionResultListener
import com.tomtom.navui.test.utils.NavRobolectricTestCase
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.Test
import org.robolectric.annotation.Config


private val REQUIRED_PERMISSIONS = RequestPermissionOnReadyListenerWrapper.REQUIRED_PERMISSIONS;

private const val TEST_PACKAGE_NAME = "com.tomtom.navui.r1systemport"

@Config(constants = BuildConfig::class, application = android.app.Application::class)
class RequestPermissionOnReadyListenerWrapperTest : NavRobolectricTestCase() {
    @RelaxedMockK
    private lateinit var mockOnReadyListener: SystemContext.OnReadyListener

    @MockK
    private lateinit var mockSystemContext: SystemContext

    @MockK
    private lateinit var mockContext: Context

    @RelaxedMockK
    private lateinit var mockPermissionSystemComponent: PermissionSystemComponent

    private lateinit var listenerWrapper: RequestPermissionOnReadyListenerWrapper

    private val permissionResultListenerSlot = slot<PermissionResultListener>()
    private val startActivityIntentSlot = slot<Intent>()

    override fun setUp() {
        super.setUp()

        MockKAnnotations.init(this)

        listenerWrapper = RequestPermissionOnReadyListenerWrapper(mockOnReadyListener)

        every { mockContext.packageName } returns TEST_PACKAGE_NAME
        every { mockContext.startActivity(capture(startActivityIntentSlot)) } just runs
        every { mockSystemContext.getComponent(PermissionSystemComponent::class.java) } returns mockPermissionSystemComponent
        every { mockSystemContext.applicationContext } returns mockContext
        every { mockPermissionSystemComponent.shouldUseRuntimePermissionModel() } returns true
        every { mockPermissionSystemComponent.requestPermissions(any(), any(), capture(permissionResultListenerSlot)) } returns mockk(relaxed = true)
        every { mockPermissionSystemComponent.shouldShowRationale(any()) } returns true
    }

    /**
     * Tests that if all permissions are already granted, the wrapped listener is called
     */
    @Test
    fun allPermissionsGranted() {
        // GIVEN
        REQUIRED_PERMISSIONS.forEach { permission ->
            every { mockPermissionSystemComponent.isGranted(permission) } returns true
        }

        // WHEN
        listenerWrapper.onReady(mockSystemContext)

        // THEN
        verify { mockOnReadyListener.onReady(mockSystemContext) }
    }

    /**
     * Tests that missing permissions are requested
     */
    @Suppress("MemberVisibilityCanBePrivate")
    @Test
    fun missingPermissionsAtStart() {
        // GIVEN
        REQUIRED_PERMISSIONS.forEach { permission ->
            every { mockPermissionSystemComponent.isGranted(permission) } returns false
        }

        // WHEN
        listenerWrapper.onReady(mockSystemContext)

        // THEN
        verify { mockPermissionSystemComponent.requestPermissions(REQUIRED_PERMISSIONS, null, any()) }
        assertTrue(permissionResultListenerSlot.isCaptured)
    }

    /**
     * Tests that the wrapped listener is called when missing permissions are granted after being requested
     */
    @Test
    fun permissionRequestGranted() {
        // GIVEN
        missingPermissionsAtStart()

        REQUIRED_PERMISSIONS.forEach { permission ->
            every { mockPermissionSystemComponent.isGranted(permission) } returns true
        }

        // WHEN
        permissionResultListenerSlot.captured.onRequestPermissionsResult(1, REQUIRED_PERMISSIONS,
                IntArray(REQUIRED_PERMISSIONS.size, { PackageManager.PERMISSION_GRANTED }))

        // THEN
        verify { mockOnReadyListener.onReady(mockSystemContext) }
    }

    /**
     * Tests that permission are re-requested if the request is denied
     */
    @Test
    fun denyPermissionRetries() {
        // GIVEN
        missingPermissionsAtStart()

        // WHEN
        permissionResultListenerSlot.captured.onRequestPermissionsResult(1, REQUIRED_PERMISSIONS,
                IntArray(REQUIRED_PERMISSIONS.size, { PackageManager.PERMISSION_DENIED }))

        // THEN
        verify(exactly = 2) { mockPermissionSystemComponent.requestPermissions(REQUIRED_PERMISSIONS, null, any()) }
    }

    /**
     * Tests that the settings screen is started if permissions are permanently denied
     */
    @Test
    fun denyAlways() {
        // GIVEN
        missingPermissionsAtStart()
        every { mockPermissionSystemComponent.shouldShowRationale(any()) } returns false

        // WHEN
        permissionResultListenerSlot.captured.onRequestPermissionsResult(1, REQUIRED_PERMISSIONS,
                IntArray(REQUIRED_PERMISSIONS.size, { PackageManager.PERMISSION_DENIED }))

        // THEN
        verify { mockContext.startActivity(any()) }
        assertTrue(startActivityIntentSlot.isCaptured)
        assertEquals(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, startActivityIntentSlot.captured.action)
        assertEquals(Uri.fromParts("package", TEST_PACKAGE_NAME, null), startActivityIntentSlot.captured.data)
    }

    /**
     * Tests that the wrapper won't call the listener if is has been dismissed
     */
    @Test
    fun dismiss() {
        // GIVEN
        missingPermissionsAtStart()

        REQUIRED_PERMISSIONS.forEach { permission ->
            every { mockPermissionSystemComponent.isGranted(permission) } returns true
        }

        listenerWrapper.dismiss()

        // WHEN
        permissionResultListenerSlot.captured.onRequestPermissionsResult(1, REQUIRED_PERMISSIONS,
                IntArray(REQUIRED_PERMISSIONS.size, { PackageManager.PERMISSION_GRANTED }))

        // THEN
        verify(exactly = 0) { mockOnReadyListener.onReady(mockSystemContext) }
    }
}
