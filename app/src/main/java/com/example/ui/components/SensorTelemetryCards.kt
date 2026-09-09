package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ArmState
import com.example.model.BatteryStatus
import com.example.model.Detection
import com.example.model.MpuStability
import com.example.model.MpuStatus
import com.example.model.RobotMode
import com.example.model.VoltageStatus
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.DarkAgriBorder
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusOrange
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextLightGrey
import com.example.ui.theme.TextWhite

@Composable
fun MetricTile(
    title: String,
    primaryValue: String,
    secondaryValue: String,
    badgeText: String,
    badgeColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF092012),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkAgriBorder)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = title,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextLightGrey
                    )
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = badgeColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = primaryValue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = TextWhite
            )
            Text(
                text = secondaryValue,
                fontSize = 10.sp,
                color = TextLightGrey
            )
        }
    }
}

@Composable
fun BatteryMetricCard(
    battery: BatteryStatus,
    modifier: Modifier = Modifier
) {
    val statusColor = when (battery.status) {
        VoltageStatus.VOLTAGE_SAFE -> StatusGreen
        VoltageStatus.VOLTAGE_LOW -> StatusOrange
        VoltageStatus.VOLTAGE_CRITICAL, VoltageStatus.VOLTAGE_ERROR -> StatusRed
    }

    MetricTile(
        title = "BATTERY",
        primaryValue = "${battery.voltage} V",
        secondaryValue = "${battery.percentage}% CAPACITY",
        badgeText = when (battery.status) {
            VoltageStatus.VOLTAGE_SAFE -> "SAFE"
            VoltageStatus.VOLTAGE_LOW -> "LOW"
            VoltageStatus.VOLTAGE_CRITICAL -> "CRITICAL"
            VoltageStatus.VOLTAGE_ERROR -> "SENSOR ERR"
        },
        badgeColor = statusColor,
        icon = Icons.Default.BatteryChargingFull,
        modifier = modifier
    )
}

@Composable
fun SoilMoistureCard(
    moisturePercent: Int,
    modifier: Modifier = Modifier
) {
    MetricTile(
        title = "SOIL SENSOR",
        primaryValue = "$moisturePercent%",
        secondaryValue = "ROOT ZONE MOISTURE",
        badgeText = if (moisturePercent in 40..80) "OPTIMAL" else if (moisturePercent < 40) "DRY" else "WET",
        badgeColor = if (moisturePercent in 40..80) StatusGreen else StatusOrange,
        icon = Icons.Default.Grass,
        modifier = modifier
    )
}

@Composable
fun MpuStabilityCard(
    mpu: MpuStatus,
    modifier: Modifier = Modifier
) {
    val statusColor = when (mpu.status) {
        MpuStability.STABLE -> StatusGreen
        MpuStability.WARNING -> StatusOrange
        MpuStability.UNSTABLE -> StatusRed
    }

    MetricTile(
        title = "MPU6050",
        primaryValue = mpu.status.name,
        secondaryValue = "P: ${mpu.pitch}° | R: ${mpu.roll}°",
        badgeText = when (mpu.status) {
            MpuStability.STABLE -> "STABLE"
            MpuStability.WARNING -> "WARNING"
            MpuStability.UNSTABLE -> "UNSTABLE"
        },
        badgeColor = statusColor,
        icon = Icons.Default.CompassCalibration,
        modifier = modifier
    )
}

@Composable
fun ArmMetricCard(
    armState: ArmState,
    modifier: Modifier = Modifier
) {
    MetricTile(
        title = "ARM (SERVO 1-3)",
        primaryValue = armState.name,
        secondaryValue = "3-DOF WEED REMOVAL",
        badgeText = if (armState == ArmState.READY || armState == ArmState.HOME) "READY" else "ACTIVE",
        badgeColor = if (armState == ArmState.READY || armState == ArmState.HOME) StatusGreen else StatusBlue,
        icon = Icons.Default.PrecisionManufacturing,
        modifier = modifier
    )
}
