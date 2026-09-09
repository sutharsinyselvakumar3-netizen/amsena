package com.example.model

enum class RobotMode {
    MANUAL,
    AUTO_CONFIRMING,
    AUTO_SAFETY_CHECK,
    AUTO,
    MANUAL_CONFIRMING,
    EMERGENCY_STOP,
    ERROR,
    VOLTAGE_LOCKED
}

enum class RobotDirection {
    IDLE,
    FORWARD,
    LEFT,
    RIGHT,
    REVERSE
}

enum class RobotSpeed(val label: String, val speedValue: Int) {
    SLOW("SLOW", 30),
    MEDIUM("MEDIUM", 60),
    FAST("FAST", 100)
}

enum class ConnectionState {
    ONLINE,
    CONNECTING,
    OFFLINE,
    ERROR
}

data class DeviceConnection(
    val deviceName: String,
    val ipAddress: String,
    val port: Int,
    val state: ConnectionState = ConnectionState.OFFLINE,
    val lastSeen: Long = 0L,
    val latencyMs: Long = 0L,
    val errorMessage: String? = null
)

enum class VoltageStatus {
    VOLTAGE_SAFE,
    VOLTAGE_LOW,
    VOLTAGE_CRITICAL,
    VOLTAGE_ERROR
}

data class BatteryStatus(
    val voltage: Float = 12.4f,
    val percentage: Int = 85,
    val status: VoltageStatus = VoltageStatus.VOLTAGE_SAFE
)

enum class MpuStability {
    STABLE,
    WARNING,
    UNSTABLE
}

data class MpuStatus(
    val pitch: Float = 0.5f,
    val roll: Float = -0.2f,
    val status: MpuStability = MpuStability.STABLE
)

enum class ArmState {
    HOME,
    MOVING,
    READY,
    ERROR,
    SAFE_POSITION
}

data class RelayState(
    val autoDrill: Boolean = false, // Relay 1 (AUTO only)
    val soil: Boolean = false,      // Relay 2 (MANUAL only)
    val water: Boolean = false      // Relay 3 (MANUAL only)
)

enum class AiObjectClass {
    ONION,
    WEED,
    UNKNOWN
}

data class Detection(
    val objectClass: AiObjectClass,
    val confidence: Int,
    val x: Float,
    val y: Float,
    val w: Float,
    val h: Float,
    val centerX: Float = x + w / 2f,
    val centerY: Float = y + h / 2f,
    val timestamp: Long = System.currentTimeMillis(),
    val consecutiveFrames: Int = 1,
    val confirmed: Boolean = false
)

enum class AutoSequenceStep(val title: String) {
    CAMERA_READY("CAMERA READY"),
    AI_READY("AI READY"),
    SCANNING("SLOW SCANNING"),
    WEED_DETECTED("WEED DETECTED"),
    TARGET_CALCULATED("TARGET CALCULATED"),
    ONION_SAFETY_PASSED("ONION SAFETY PASSED"),
    ROBOT_STOPPED("ROBOT STOPPED"),
    ARM_POSITIONING("ARM POSITIONING"),
    TARGET_VERIFIED("TARGET VERIFIED"),
    AUTO_DRILL("AUTO DRILL (07s)"),
    DRILL_COMPLETE("DRILL COMPLETE"),
    ARM_HOME("ARM HOME"),
    RESUME_SCANNING("RESUME SCANNING")
}

enum class LogSeverity {
    INFO,
    WARNING,
    ERROR,
    CRITICAL
}

data class EventLogItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val event: String,
    val severity: LogSeverity,
    val description: String
)

data class AppSettings(
    val esp32Ip: String = "192.168.1.50",
    val streamPort: Int = 81,
    val capturePort: Int = 80,
    val picoIp: String = "192.168.1.51",
    val picoPort: Int = 80,
    val timeoutMs: Long = 3000L,
    val reconnectMs: Long = 2000L,
    val weedConfidenceThreshold: Int = 85,
    val confirmationFrames: Int = 3,
    val onionSafetyThreshold: Int = 75,
    val drillDurationMs: Long = 7000L,
    val minAutoVoltage: Float = 10.5f,
    val criticalVoltage: Float = 10.0f,
    val fullVoltage: Float = 12.6f,
    val emptyVoltage: Float = 9.6f,
    val tiltLimitDegrees: Float = 25.0f,
    val servo4HomeAngle: Float = 0.0f,
    val servo4MaxAngle: Float = 45.0f,
    val demoMode: Boolean = true // Starts in Demo Mode with realistic simulation out of the box until hardware IP is active
)
