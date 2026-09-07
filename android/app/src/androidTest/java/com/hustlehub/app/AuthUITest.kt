package com.hustlehub.app

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.robolectric.Robolectric
import androidx.test.ext.robolectric.junit5.room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator by
import androidx.test.uiautomulator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import kotlin.test.assertTrue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(org.junit.runner.JUnit4::class)
class AuthUITest {

    private lateinit var device: UiDevice
    private lateinit var authStore: AuthStore

    @Before
    fun setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        authStore = TokenManager(ApplicationProvider.getApplicationContext())
    }

    @After
    fun tearDown() {
        authStore.clear()
    }

    @Test
    fun testLoginSuccess_navigatesToDashboard() {
        // Register a new user first
        val intent = android.content.Intent(device.context, RegisterActivity::class.java)
        ActivityScenario.launch(intent)
        
        // Fill registration form
        binding.etName.text = "Test User"
        binding.etEmail.text = "test@example.com"
        binding.etPassword.text = "TestPass1!"
        binding.etConfirmPassword.text = "TestPass1!"
        binding.spinnerRole.selectedItem = "freelancer"
        binding.btnRegister.click()
        
        // Verify registration succeeded
        // Then login
        // ... test logic
    }
}