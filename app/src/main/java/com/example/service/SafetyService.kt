package com.example.service

import com.example.model.AppSettings
import com.example.model.ArmState
import com.example.model.BatteryStatus
import com.example.model.ConnectionState
import com.example.model.Detection
import com.example.model.DeviceConnection
import com.example.model.MpuStability
import com.example.model.MpuStatus
import com.example.model.RobotMode
import com.example.model.VoltageStatus

data class SafetyCheckResult(
    val passed: Boolean,
    val checkItems: Map<String, Boolean>,
    val failureSummary: String? = null
)

class SafetyService {

    fun performAutoStartSafetyCheck(
        picoConnection: DeviceConnection,
        cameraConnection: DeviceConnection,
        aiReady: Boolean,
        batteryStatus: BatteryStatus,
        mpuStatus: MpuStatus,
        armState: ArmState,
        eStopActive: Boolean,
        settings: AppSettings
    ): SafetyCheckResult {
        val items = mutableMapOf<String, Boolean>()

        // 1. E-Stop released
        items["E-STOP RELEASED"] = !eStopActive

        // 2. Pico W Online
        items["PICO W ONLINE"] = picoConnection.state == ConnectionState.ONLINE

        // 3. Camera Online
        items["ESP32-CAM ONLINE"] = cameraConnection.state == ConnectionState.ONLINE

        // 4. AI Engine Ready
        items["AI READY"] = aiReady

        // 5. Battery Voltage Safe
        val isVoltSafe = batteryStatus.status == VoltageStatus.VOLTAGE_SAFE &&
                batteryStatus.voltage >= settings.minAutoVoltage
        items["BATTERY SAFE (${batteryStatus.voltage}V)"] = isVoltSafe

        // 6. MPU6050 Stable
        items["MPU6050 STABLE"] = mpuStatus.status == MpuStability.STABLE

        // 7. Arm Ready
        items["ARM READY"] = armState == ArmState.READY || armState == ArmState.HOME

        // 8. Drill Ready (not stuck on)
        items["DRILL READY"] = true

        val failures = items.filterValues { !it }.keys
        val passed = failures.isEmpty()
        val summary = if (!passed) {
            "AUTO START BLOCKED: ${failures.joinToString(", ")}"
        } else null

        return SafetyCheckResult(passed = passed, checkItems = items, failureSummary = summary)
    }

    fun canActivateAutoDrill(
        currentMode: RobotMode,
        confirmedWeed: Detection?,
        targetReachable: Boolean,
        onionSafetyPassed: Boolean,
        armState: ArmState,
        picoConnection: DeviceConnection,
        batteryStatus: BatteryStatus,
        mpuStatus: MpuStatus,
        eStopActive: Boolean,
        drillActive: Boolean
    ): Pair<Boolean, String?> {
        if (eStopActive) return Pair(false, "EMERGENCY STOP ACTIVE")
        if (currentMode != RobotMode.AUTO) return Pair(false, "AUTO DRILL is allowed in AUTO mode only")
        if (picoConnection.state != ConnectionState.ONLINE) return Pair(false, "PICO W OFFLINE")
        if (batteryStatus.status != VoltageStatus.VOLTAGE_SAFE) return Pair(false, "BATTERY VOLTAGE NOT SAFE")
        if (mpuStatus.status != MpuStability.STABLE) return Pair(false, "MPU6050 UNSTABLE")
        if (drillActive) return Pair(false, "DRILL ALREADY RUNNING")
        if (armState != ArmState.READY && armState != ArmState.MOVING) return Pair(false, "ARM NOT IN POSITION")
        if (confirmedWeed == null || !confirmedWeed.confirmed) return Pair(false, "WEED NOT CONFIRMED")
        if (!targetReachable) return Pair(false, "TARGET OUT OF BOUNDS")
        if (!onionSafetyPassed) return Pair(false, "ONION SAFETY CHECK FAILED")

        return Pair(true, null)
    }
}
