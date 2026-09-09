package com.example.service

import com.example.model.AiObjectClass
import com.example.model.Detection
import kotlin.math.hypot

data class PhysicalTarget(
    val xMm: Float,
    val yMm: Float,
    val servo1Angle: Float,
    val servo2Angle: Float,
    val servo3Angle: Float,
    val isReachable: Boolean
)

data class TargetSafetyEvaluation(
    val isSafeToDrill: Boolean,
    val failureReason: String? = null
)

class AiService {

    // Calibration layer: Camera image space (e.g., 320x240 or 640x480) to physical robot arm space in mm
    fun calculatePhysicalTarget(detection: Detection, cameraWidth: Float = 320f, cameraHeight: Float = 240f): PhysicalTarget {
        // Robot arm base is located at camera center bottom
        val centerNormX = (detection.centerX - (cameraWidth / 2f)) / (cameraWidth / 2f) // -1.0 to 1.0
        val centerNormY = (cameraHeight - detection.centerY) / cameraHeight             // 0.0 (far) to 1.0 (near)

        // Physical ground workspace is approx -120mm to +120mm laterally, 80mm to 220mm forward
        val xMm = centerNormX * 120f
        val yMm = 80f + (centerNormY * 140f)

        // Inverse kinematics calibration for Servo 1 (base azimuth), Servo 2 (shoulder lift), Servo 3 (elbow drop)
        val distance = hypot(xMm, yMm)
        val isReachable = distance in 80f..250f && kotlin.math.abs(xMm) <= 120f

        val servo1Azimuth = 90f + (centerNormX * 45f) // Center is 90 deg, swings 45 to 135 deg
        val servo2Shoulder = (45f + (centerNormY * 30f)).coerceIn(20f, 90f)
        val servo3Elbow = (60f + (centerNormY * 20f)).coerceIn(30f, 100f)

        return PhysicalTarget(
            xMm = xMm,
            yMm = yMm,
            servo1Angle = servo1Azimuth,
            servo2Angle = servo2Shoulder,
            servo3Angle = servo3Elbow,
            isReachable = isReachable
        )
    }

    // Onion Safety Check: Never drill if onion is in strike radius
    fun evaluateOnionSafety(
        targetDetection: Detection,
        allDetections: List<Detection>,
        onionSafetyMarginMm: Float = 65f
    ): TargetSafetyEvaluation {
        if (targetDetection.objectClass != AiObjectClass.WEED) {
            return TargetSafetyEvaluation(false, "Target is not WEED (${targetDetection.objectClass})")
        }

        val targetPhysical = calculatePhysicalTarget(targetDetection)
        if (!targetPhysical.isReachable) {
            return TargetSafetyEvaluation(false, "Target out of arm physical reach")
        }

        // Check proximity to all detected onions
        for (det in allDetections) {
            if (det.objectClass == AiObjectClass.ONION) {
                val onionPhysical = calculatePhysicalTarget(det)
                val distanceToOnion = hypot(
                    targetPhysical.xMm - onionPhysical.xMm,
                    targetPhysical.yMm - onionPhysical.yMm
                )
                if (distanceToOnion < onionSafetyMarginMm) {
                    return TargetSafetyEvaluation(
                        isSafeToDrill = false,
                        failureReason = "ONION SAFETY FAILED: Onion detected ${distanceToOnion.toInt()}mm from target (min safe: ${onionSafetyMarginMm.toInt()}mm)"
                    )
                }
            }
        }

        return TargetSafetyEvaluation(isSafeToDrill = true, failureReason = null)
    }
}
