package com.example.service

import com.example.model.ArmState
import com.example.model.BatteryStatus
import com.example.model.Detection
import com.example.model.MpuStability
import com.example.model.MpuStatus
import com.example.model.RelayState
import com.example.model.RobotDirection
import com.example.model.RobotMode
import com.example.model.RobotSpeed
import com.example.model.VoltageStatus
import org.json.JSONObject

data class PicoStatusResponse(
    val mode: RobotMode,
    val robotMotion: String,
    val batteryPercent: Int,
    val batteryVoltage: Float,
    val voltageStatus: VoltageStatus,
    val soilMoisture: Int,
    val mpuStatus: MpuStability,
    val relay1AutoDrill: Boolean,
    val relay2Soil: Boolean,
    val relay3Water: Boolean,
    val drillActive: Boolean,
    val armState: ArmState,
    val cameraOnline: Boolean,
    val aiReady: Boolean,
    val emergencyStopActive: Boolean
)

class PicoService(private val networkService: NetworkService) {

    suspend fun fetchStatus(picoBaseUrl: String): Result<PicoStatusResponse> {
        val url = "$picoBaseUrl/api/status"
        val result = networkService.get(url)
        return result.mapCatching { jsonStr ->
            val json = JSONObject(jsonStr)
            val modeStr = json.optString("mode", "MANUAL")
            val mode = runCatching { RobotMode.valueOf(modeStr) }.getOrDefault(RobotMode.MANUAL)
            val motion = json.optString("robot", "IDLE")
            val battery = json.optInt("battery", 85)
            val voltage = json.optDouble("voltage", 12.4).toFloat()
            val voltStatusStr = json.optString("voltage_status", "SAFE")
            val voltStatus = when (voltStatusStr.uppercase()) {
                "SAFE" -> VoltageStatus.VOLTAGE_SAFE
                "LOW" -> VoltageStatus.VOLTAGE_LOW
                "CRITICAL" -> VoltageStatus.VOLTAGE_CRITICAL
                else -> VoltageStatus.VOLTAGE_SAFE
            }
            val soil = json.optInt("soil", 60)
            val mpuStr = json.optString("mpu", "STABLE")
            val mpu = when (mpuStr.uppercase()) {
                "STABLE" -> MpuStability.STABLE
                "WARNING" -> MpuStability.WARNING
                else -> MpuStability.UNSTABLE
            }
            val relay1 = json.optBoolean("relay1", false)
            val relay2 = json.optBoolean("relay2", false)
            val relay3 = json.optBoolean("relay3", false)
            val drill = json.optBoolean("drill", false)
            val armStr = json.optString("arm", "READY")
            val arm = runCatching { ArmState.valueOf(armStr) }.getOrDefault(ArmState.READY)
            val cam = json.optBoolean("camera", true) || json.optString("camera") == "ONLINE"
            val ai = json.optBoolean("ai", true) || json.optString("ai") == "READY"
            val eStop = json.optBoolean("emergency_stop", false)

            PicoStatusResponse(
                mode = mode,
                robotMotion = motion,
                batteryPercent = battery,
                batteryVoltage = voltage,
                voltageStatus = voltStatus,
                soilMoisture = soil,
                mpuStatus = mpu,
                relay1AutoDrill = relay1,
                relay2Soil = relay2,
                relay3Water = relay3,
                drillActive = drill,
                armState = arm,
                cameraOnline = cam,
                aiReady = ai,
                emergencyStopActive = eStop
            )
        }
    }

    suspend fun sendMovement(
        picoBaseUrl: String,
        direction: RobotDirection,
        speed: RobotSpeed,
        mode: RobotMode
    ): Result<String> {
        val url = "$picoBaseUrl/api/robot"
        val json = JSONObject().apply {
            put("command", direction.name)
            put("speed", speed.name)
            put("mode", mode.name)
        }
        return networkService.postJson(url, json)
    }

    suspend fun sendServo4(
        picoBaseUrl: String,
        angle: Float,
        mode: RobotMode
    ): Result<String> {
        val clampedAngle = angle.coerceIn(0f, 45f)
        val url = "$picoBaseUrl/api/servo4"
        val json = JSONObject().apply {
            put("servo", 4)
            put("angle", clampedAngle)
            put("mode", mode.name)
        }
        return networkService.postJson(url, json)
    }

    suspend fun sendRelay(
        picoBaseUrl: String,
        relayIndex: Int,
        relayName: String,
        state: Boolean,
        mode: RobotMode,
        reason: String? = null
    ): Result<String> {
        val url = "$picoBaseUrl/api/relay"
        val json = JSONObject().apply {
            put("relay", relayIndex)
            put("name", relayName)
            put("state", state)
            put("mode", mode.name)
            if (reason != null) {
                put("reason", reason)
            }
        }
        return networkService.postJson(url, json)
    }

    suspend fun sendMode(picoBaseUrl: String, mode: RobotMode): Result<String> {
        val url = "$picoBaseUrl/api/mode"
        val json = JSONObject().apply {
            put("mode", mode.name)
        }
        return networkService.postJson(url, json)
    }

    suspend fun sendEmergencyStop(picoBaseUrl: String): Result<String> {
        val url = "$picoBaseUrl/api/emergency_stop"
        val json = JSONObject().apply {
            put("command", "EMERGENCY_STOP")
            put("timestamp", System.currentTimeMillis())
        }
        return networkService.postJson(url, json)
    }

    suspend fun sendDetection(picoBaseUrl: String, detection: Detection): Result<String> {
        val url = "$picoBaseUrl/api/detection"
        val json = JSONObject().apply {
            put("object", detection.objectClass.name)
            put("confidence", detection.confidence)
            put("x", detection.x)
            put("y", detection.y)
            put("w", detection.w)
            put("h", detection.h)
        }
        return networkService.postJson(url, json)
    }
}
