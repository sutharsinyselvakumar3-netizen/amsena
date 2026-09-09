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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RobotMode
import com.example.ui.components.ArmMetricCard
import com.example.ui.components.AutoSequenceTimeline
import com.example.ui.components.BatteryMetricCard
import com.example.ui.components.CameraView
import com.example.ui.components.ConnectionCard
import com.example.ui.components.EmergencyStopButton
import com.example.ui.components.ModeSwitchCard
import com.example.ui.components.MpuStabilityCard
import com.example.ui.components.RobotModeHeader
import com.example.ui.components.SoilMoistureCard
import com.example.ui.theme.RobotTheme
import com.example.ui.theme.TextLightGrey
import com.example.ui.theme.TextWhite
import com.example.viewmodel.RobotUiState

@Composable
fun HomeScreen(
    uiState: RobotUiState,
    onTriggerEStop: () -> Unit,
    onResetEStop: () -> Unit,
    onRequestStartAuto: () -> Unit,
    onRequestStopAuto: () -> Unit,
    onTestAllConnections: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RobotTheme.colors.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("home_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. BRAND HERO HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(RobotTheme.colors.primaryVariant, RobotTheme.colors.surface)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = "Onion Robot",
                        tint = RobotTheme.colors.primary,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "AI COMPANION",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextWhite,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "SMART ONION GARDEN ROBOT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RobotTheme.colors.primary,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (uiState.settings.demoMode) Color(0xFF1E3A25) else RobotTheme.colors.surface
            ) {
                Text(
                    text = if (uiState.settings.demoMode) "DEMO SIM" else "HARDWARE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.settings.demoMode) Color(0xFFFBBF24) else RobotTheme.colors.primary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Tagline Banner
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = RobotTheme.colors.cardElevated.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Defense",
                    tint = RobotTheme.colors.primary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "“See the Weed. Protect the Onion. Work Smart.”",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextLightGrey
                )
            }
        }

        // 2. CLEAR MODE HEADER (Section 2 & 3: MANUAL MODE / AUTO MODE with active indicators)
        RobotModeHeader(
            currentMode = uiState.mode,
            eStopActive = uiState.eStopActive
        )

        // 3. EMERGENCY STOP BUTTON (CRITICAL & PROMINENT)
        EmergencyStopButton(
            isActive = uiState.eStopActive,
            onTriggerEStop = onTriggerEStop,
            onResetEStop = onResetEStop
        )

        // 4. LARGE MODE SWITCH CARD (MANUAL ⇄ AUTO)
        ModeSwitchCard(
            currentMode = uiState.mode,
            onRequestStartAuto = onRequestStartAuto,
            onRequestStopAuto = onRequestStopAuto
        )

        // 5. LIVE CAMERA VIEW WITH BOUNDING BOX OVERLAY
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ROBOT VISION STREAM",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLightGrey,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "ONION SHIELD ACTIVE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = RobotTheme.colors.primary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            CameraView(
                state = uiState.esp32Connection.state,
                detections = uiState.currentDetections,
                isDemoMode = uiState.settings.demoMode
            )
        }

        // 6. IF IN AUTO: AUTONOMOUS SEQUENCE TIMELINE (Section 47 & 28)
        if (uiState.mode == RobotMode.AUTO || uiState.mode == RobotMode.AUTO_SAFETY_CHECK) {
            AutoSequenceTimeline(
                activeStep = uiState.autoSequenceStep,
                statusMessage = uiState.autoStatusMessage,
                drillCountdownSeconds = uiState.drillCountdownSeconds
            )
        }

        // 7. SENSOR TELEMETRY METRIC GRID (2x2)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BatteryMetricCard(
                    battery = uiState.battery,
                    modifier = Modifier.weight(1f)
                )
                SoilMoistureCard(
                    moisturePercent = uiState.soilMoisture,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MpuStabilityCard(
                    mpu = uiState.mpu,
                    modifier = Modifier.weight(1f)
                )
                ArmMetricCard(
                    armState = uiState.arm,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 8. WI-FI & HARNESS CONNECTION CARD
        ConnectionCard(
            wifi = uiState.wifiConnection,
            esp32 = uiState.esp32Connection,
            pico = uiState.picoConnection,
            aiEngine = uiState.aiEngineConnection,
            isTesting = uiState.isTestingConnections,
            onTestAll = onTestAllConnections
        )

        Spacer(modifier = Modifier.height(10.dp))
    }
}
