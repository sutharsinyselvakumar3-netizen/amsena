package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ArmState
import com.example.model.RobotMode
import com.example.ui.components.EmergencyStopButton
import com.example.ui.components.GlassCard
import com.example.ui.components.RobotModeHeader
import com.example.ui.components.Servo4Control
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.DarkAgriBackground
import com.example.ui.theme.DarkAgriBorder
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.RobotTheme
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusOrange
import com.example.ui.theme.TextLightGrey
import com.example.ui.theme.TextWhite
import com.example.viewmodel.RobotUiState

@Composable
fun ArmScreen(
    uiState: RobotUiState,
    onServo4AngleChange: (Float) -> Unit,
    onServo4Home: () -> Unit,
    onServo4Center: () -> Unit,
    onTriggerEStop: () -> Unit,
    onResetEStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val isManual = uiState.mode == RobotMode.MANUAL && !uiState.eStopActive

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RobotTheme.colors.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("arm_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Mode Header Indicator (Section 2 & 3)
        RobotModeHeader(
            currentMode = uiState.mode,
            eStopActive = uiState.eStopActive
        )

        // Header
        Text(
            text = "ROBOT ARM & DRILL END-EFFECTOR",
            fontSize = 17.sp,
            fontWeight = FontWeight.Black,
            color = TextWhite,
            letterSpacing = 0.5.sp
        )

        // 1. EMERGENCY STOP (ALWAYS ACCESSIBLE)
        EmergencyStopButton(
            isActive = uiState.eStopActive,
            onTriggerEStop = onTriggerEStop,
            onResetEStop = onResetEStop
        )

        // 2. ARM STATUS CARD
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PrecisionManufacturing,
                        contentDescription = "Arm State",
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "3-DOF WEEDING ARM STATUS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }

                val statusColor = when (uiState.arm) {
                    ArmState.READY, ArmState.HOME -> StatusGreen
                    ArmState.MOVING -> StatusBlue
                    ArmState.ERROR, ArmState.SAFE_POSITION -> EmergencyRed
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = uiState.arm.name,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sub-actuator angles preview
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ArmServoStatusChip("Servo 1 (Base)", "90° Azimuth", Modifier.weight(1f))
                ArmServoStatusChip("Servo 2 (Shoulder)", "45° Elevation", Modifier.weight(1f))
                ArmServoStatusChip("Servo 3 (Elbow)", "60° Drop", Modifier.weight(1f))
            }
        }

        // 3. MANUAL SERVO 4 CONTROL (TOOL TILT)
        Servo4Control(
            angle = uiState.servo4Angle,
            maxAngle = uiState.settings.servo4MaxAngle,
            enabled = isManual,
            onAngleChange = onServo4AngleChange,
            onHomeTap = onServo4Home,
            onCenterTap = onServo4Center
        )

        // 4. AUTO DRILL SAFETY AUTHORITY CARD (RELAY 1)
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Auto Drill",
                        tint = if (uiState.relays.autoDrill) EmergencyRed else AgriGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RELAY 1: AUTO WEED DRILL",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (uiState.relays.autoDrill) EmergencyRed else Color(0xFF132B1C)
                ) {
                    Text(
                        text = if (uiState.relays.autoDrill) "DRILL ENERGIZED" else "DRILL SAFE OFF",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = if (uiState.relays.autoDrill) Color.White else StatusGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Safety conditions audit list
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SafetyRuleBullet("Autonomous Safety Rule: Relay 1 is AUTO ONLY (no manual trigger allowed).")
                SafetyRuleBullet("Pre-strike condition: 3-frame verified weed detection required.")
                SafetyRuleBullet("Onion Shield: Aborts instantly if onion is within 65mm strike radius.")
                SafetyRuleBullet("Hardware watchdog: Maximum pulse capped at ${uiState.settings.drillDurationMs} ms.")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF081C10),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Pico Safety",
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Raspberry Pi Pico W enforces independent hardware timer shutoff. Indefinite drill activation is physically prohibited.",
                        fontSize = 10.sp,
                        color = TextLightGrey
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun ArmServoStatusChip(name: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF091F11),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkAgriBorder)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = name, fontSize = 9.sp, color = TextLightGrey)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
        }
    }
}

@Composable
private fun SafetyRuleBullet(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .size(5.dp)
                .clip(CircleShape)
                .background(AgriGreenPrimary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 11.sp, color = TextLightGrey, lineHeight = 16.sp)
    }
}
