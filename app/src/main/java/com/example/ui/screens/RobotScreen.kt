package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RobotDirection
import com.example.model.RobotMode
import com.example.model.RobotSpeed
import com.example.ui.components.DirectionPad
import com.example.ui.components.EmergencyStopButton
import com.example.ui.components.GlassCard
import com.example.ui.components.ModeSwitchCard
import com.example.ui.components.RelayControlCard
import com.example.ui.components.RobotModeHeader
import com.example.ui.components.SpeedSelector
import com.example.ui.theme.RobotTheme
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextLightGrey
import com.example.ui.theme.TextWhite
import com.example.viewmodel.RobotUiState

@Composable
fun RobotScreen(
    uiState: RobotUiState,
    onDirectionTap: (RobotDirection) -> Unit,
    onSpeedSelected: (RobotSpeed) -> Unit,
    onToggleSoil: () -> Unit,
    onToggleWater: () -> Unit,
    onTriggerEStop: () -> Unit,
    onResetEStop: () -> Unit,
    onRequestStartAuto: () -> Unit,
    onRequestStopAuto: () -> Unit,
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
            .testTag("robot_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Mode Header Indicator (Section 2 & 3)
        RobotModeHeader(
            currentMode = uiState.mode,
            eStopActive = uiState.eStopActive
        )

        // Screen Header
        Text(
            text = "ROBOT MOTION & ACTUATION",
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

        // 2. MODE SWITCH CARD
        ModeSwitchCard(
            currentMode = uiState.mode,
            onRequestStartAuto = onRequestStartAuto,
            onRequestStopAuto = onRequestStopAuto
        )

        // Mode Status Warning Banner if in AUTO
        if (!isManual) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = RobotTheme.colors.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, RobotTheme.colors.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Safe Lockout",
                        tint = RobotTheme.colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (uiState.eStopActive) "MOTION LOCKED: EMERGENCY STOP ENGAGED"
                        else "MANUAL MOVEMENT LOCKED: AUTONOMOUS WEEDING ACTIVE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // 3. DISCRETE 4-DIRECTION MOVEMENT PAD
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CHASSIS DIRECTIONAL CONTROLS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextLightGrey,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Discrete single-tap command (Watchdog 800ms)",
                        fontSize = 9.sp,
                        color = RobotTheme.colors.primary
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (uiState.isMoving) RobotTheme.colors.surface else Color(0x66000000)
                ) {
                    Text(
                        text = if (uiState.isMoving) "STATUS: ${uiState.direction.name}" else "STATUS: IDLE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.isMoving) StatusGreen else TextLightGrey,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            DirectionPad(
                currentDirection = uiState.direction,
                isMoving = uiState.isMoving,
                enabled = isManual,
                onDirectionTap = onDirectionTap
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 4. SPEED SELECTOR (SLOW, MEDIUM, FAST)
            SpeedSelector(
                currentSpeed = uiState.speed,
                onSpeedSelected = onSpeedSelected,
                enabled = isManual
            )
        }

        // 5. ACTUATOR RELAY CONTROLS (SOIL, WATER PUMP, AUTO DRILL)
        RelayControlCard(
            relays = uiState.relays,
            isManualMode = isManual,
            onToggleSoil = onToggleSoil,
            onToggleWater = onToggleWater
        )

        // Safety Compliance Notice
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = RobotTheme.colors.surface.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Notice",
                    tint = TextLightGrey,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Strict Hardware Rules: No joystick or hold-to-move permitted. Pico W acts as safety authority with timed movement pulses and independent drill lockout.",
                    fontSize = 10.sp,
                    color = TextLightGrey,
                    lineHeight = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}
