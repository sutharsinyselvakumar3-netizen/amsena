package com.example.service

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AppSettings

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("ai_companion_robot_prefs", Context.MODE_PRIVATE)

    fun loadSettings(): AppSettings {
        return AppSettings(
            esp32Ip = prefs.getString("esp32_ip", "192.168.1.50") ?: "192.168.1.50",
            streamPort = prefs.getInt("stream_port", 81),
            capturePort = prefs.getInt("capture_port", 80),
            picoIp = prefs.getString("pico_ip", "192.168.1.51") ?: "192.168.1.51",
            picoPort = prefs.getInt("pico_port", 80),
            timeoutMs = prefs.getLong("timeout_ms", 3000L),
            reconnectMs = prefs.getLong("reconnect_ms", 2000L),
            weedConfidenceThreshold = prefs.getInt("weed_threshold", 85),
            confirmationFrames = prefs.getInt("confirmation_frames", 3),
            onionSafetyThreshold = prefs.getInt("onion_threshold", 75),
            drillDurationMs = prefs.getLong("drill_duration_ms", 1500L),
            minAutoVoltage = prefs.getFloat("min_auto_voltage", 10.5f),
            criticalVoltage = prefs.getFloat("critical_voltage", 10.0f),
            fullVoltage = prefs.getFloat("full_voltage", 12.6f),
            emptyVoltage = prefs.getFloat("empty_voltage", 9.6f),
            tiltLimitDegrees = prefs.getFloat("tilt_limit", 25.0f),
            servo4HomeAngle = prefs.getFloat("servo4_home", 0.0f),
            servo4MaxAngle = prefs.getFloat("servo4_max", 45.0f),
            demoMode = prefs.getBoolean("demo_mode", true)
        )
    }

    fun saveSettings(settings: AppSettings) {
        prefs.edit()
            .putString("esp32_ip", settings.esp32Ip)
            .putInt("stream_port", settings.streamPort)
            .putInt("capture_port", settings.capturePort)
            .putString("pico_ip", settings.picoIp)
            .putInt("pico_port", settings.picoPort)
            .putLong("timeout_ms", settings.timeoutMs)
            .putLong("reconnect_ms", settings.reconnectMs)
            .putInt("weed_threshold", settings.weedConfidenceThreshold)
            .putInt("confirmation_frames", settings.confirmationFrames)
            .putInt("onion_threshold", settings.onionSafetyThreshold)
            .putLong("drill_duration_ms", settings.drillDurationMs)
            .putFloat("min_auto_voltage", settings.minAutoVoltage)
            .putFloat("critical_voltage", settings.criticalVoltage)
            .putFloat("full_voltage", settings.fullVoltage)
            .putFloat("empty_voltage", settings.emptyVoltage)
            .putFloat("tilt_limit", settings.tiltLimitDegrees)
            .putFloat("servo4_home", settings.servo4HomeAngle)
            .putFloat("servo4_max", settings.servo4MaxAngle)
            .putBoolean("demo_mode", settings.demoMode)
            .apply()
    }
}
