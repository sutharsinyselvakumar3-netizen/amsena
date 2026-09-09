package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.model.AutoSequenceStep
import com.example.model.RobotDirection
import com.example.model.RobotMode
import com.example.viewmodel.RobotViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleUnitTest {

    @Test
    fun testModeSwitchingAndLockoutRules() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = RobotViewModel(app)
        
        // Initial state is safe MANUAL mode
        assertEquals(RobotMode.MANUAL, vm.uiState.value.mode)
        assertFalse(vm.uiState.value.eStopActive)

        // Requesting AUTO requires explicit confirmation dialog
        vm.requestStartAuto()
        assertEquals(RobotMode.AUTO_CONFIRMING, vm.uiState.value.mode)

        // Cancelling returns safely to MANUAL
        vm.cancelAutoStart()
        assertEquals(RobotMode.MANUAL, vm.uiState.value.mode)

        // Confirming transitions to AUTO safety verification
        vm.requestStartAuto()
        vm.confirmStartAuto()
        assertEquals(RobotMode.AUTO_SAFETY_CHECK, vm.uiState.value.mode)

        // In non-manual modes, manual movement is locked out
        vm.onMovementTap(RobotDirection.FORWARD)
        assertFalse("Chassis movement must be locked outside MANUAL mode", vm.uiState.value.isMoving)

        // In non-manual modes, manual relays (Soil/Water) are locked out
        vm.toggleSoil()
        assertFalse("Soil relay must be locked out outside MANUAL mode", vm.uiState.value.relays.soil)

        // E-STOP immediately cuts all functions
        vm.triggerEmergencyStop()
        assertEquals(RobotMode.EMERGENCY_STOP, vm.uiState.value.mode)
        assertTrue(vm.uiState.value.eStopActive)
        assertFalse(vm.uiState.value.relays.autoDrill)
        assertFalse(vm.uiState.value.relays.soil)

        // Reset returns safely to MANUAL
        vm.resetEmergencyStop()
        assertEquals(RobotMode.MANUAL, vm.uiState.value.mode)
        assertFalse(vm.uiState.value.eStopActive)
    }

    @Test
    fun testAutonomousSequenceDefinition() {
        val steps = AutoSequenceStep.values()
        assertTrue(steps.contains(AutoSequenceStep.CAMERA_READY))
        assertTrue(steps.contains(AutoSequenceStep.WEED_DETECTED))
        assertTrue(steps.contains(AutoSequenceStep.ONION_SAFETY_PASSED))
        assertTrue(steps.contains(AutoSequenceStep.AUTO_DRILL))
        assertTrue(steps.contains(AutoSequenceStep.DRILL_COMPLETE))
        assertTrue(steps.contains(AutoSequenceStep.RESUME_SCANNING))
    }
}
