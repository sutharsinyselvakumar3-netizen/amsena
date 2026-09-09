package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AppSettings
import com.example.model.DeviceConnection
import com.example.model.EventLogItem
import com.example.model.LogSeverity
import com.example.service.DiagnosticReport
import com.example.service.EventLogRepository
import com.example.ui.components.GlassCard
import com.example.ui.components.RobotModeHeader
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.DarkAgriBackground
import com.example.ui.theme.DarkAgriBorder
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.RobotTheme
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGrey
import com.example.ui.theme.StatusOrange
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextLightGrey
import com.example.ui.theme.TextWhite
import com.example.viewmodel.RobotUiState

@Composable
fun SettingsScreen(
    uiState: RobotUiState,
    eventLogRepo: EventLogRepository,
    onSaveSettings: (AppSettings) -> Unit,
    onToggleDemoMode: () -> Unit,
    onScanNetwork: () -> Unit,
    onApplyScannedDevice: (DeviceConnection) -> Unit,
    onRunDiagnostics: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val eventLogs by eventLogRepo.logs.collectAsState()

    var esp32Ip by remember(uiState.settings.esp32Ip) { mutableStateOf(uiState.settings.esp32Ip) }
    var streamPort by remember(uiState.settings.streamPort) { mutableStateOf(uiState.settings.streamPort.toString()) }
    var capturePort by remember(uiState.settings.capturePort) { mutableStateOf(uiState.settings.capturePort.toString()) }
    var picoIp by remember(uiState.settings.picoIp) { mutableStateOf(uiState.settings.picoIp) }
    var picoPort by remember(uiState.settings.picoPort) { mutableStateOf(uiState.settings.picoPort.toString()) }

    var weedThreshold by remember(uiState.settings.weedConfidenceThreshold) {
        mutableStateOf(uiState.settings.weedConfidenceThreshold.toFloat())
    }
    var confirmationFrames by remember(uiState.settings.confirmationFrames) {
        mutableStateOf(uiState.settings.confirmationFrames.toFloat())
    }
    var drillDuration by remember(uiState.settings.drillDurationMs) {
        mutableStateOf(uiState.settings.drillDurationMs.toFloat())
    }

    var selectedSeverityFilter by remember { mutableStateOf<LogSeverity?>(null) }
    var showClearLogsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RobotTheme.colors.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Mode Header Indicator (Section 2 & 3)
        RobotModeHeader(
            currentMode = uiState.mode,
            eStopActive = uiState.eStopActive
        )

        // Screen Header
        Text(
            text = "SYSTEM SETTINGS & DIAGNOSTICS",
            fontSize = 17.sp,
            fontWeight = FontWeight.Black,
            color = TextWhite,
            letterSpacing = 0.5.sp
        )

        // 1. DEMO HARDWARE SIMULATION TOGGLE
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "DEMO SIMULATION MODE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "Generates live camera feed, weed telemetry, & autonomous cycle without hardware",
                        fontSize = 10.sp,
                        color = TextLightGrey
                    )
                }
                Switch(
                    checked = uiState.settings.demoMode,
                    onCheckedChange = { onToggleDemoMode() },
                    modifier = Modifier.testTag("toggle_demo_mode"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AgriGreenPrimary
                    )
                )
            }
        }

        // 2. NETWORK & ENDPOINTS CONFIGURATION
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Dns,
                        contentDescription = "Network",
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "HARDWARE IP & PORTS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }

                Button(
                    onClick = onScanNetwork,
                    enabled = !uiState.isScanningNetwork,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF163E24)),
                    modifier = Modifier.testTag("scan_network_button")
                ) {
                    if (uiState.isScanningNetwork) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(12.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text("SCAN SUBNET", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (uiState.scannedDevices.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("DISCOVERED NODES (TAP TO APPLY):", fontSize = 9.sp, color = AgriGreenPrimary)
                    uiState.scannedDevices.forEach { dev ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF0F2E1B),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onApplyScannedDevice(dev) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(dev.deviceName, fontSize = 10.sp, color = TextWhite, fontWeight = FontWeight.Bold)
                                Text("${dev.ipAddress}:${dev.port}", fontSize = 10.sp, color = Color(0xFFA7F3D0))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ESP32-CAM Inputs
            Text("ESP32-CAM (Camera Feed):", fontSize = 10.sp, color = TextLightGrey)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedTextField(
                    value = esp32Ip,
                    onValueChange = { esp32Ip = it },
                    label = { Text("IP Address", fontSize = 9.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(2f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AgriGreenPrimary,
                        unfocusedBorderColor = DarkAgriBorder
                    )
                )
                OutlinedTextField(
                    value = streamPort,
                    onValueChange = { streamPort = it },
                    label = { Text("Stream", fontSize = 9.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AgriGreenPrimary,
                        unfocusedBorderColor = DarkAgriBorder
                    )
                )
                OutlinedTextField(
                    value = capturePort,
                    onValueChange = { capturePort = it },
                    label = { Text("Capture", fontSize = 9.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AgriGreenPrimary,
                        unfocusedBorderColor = DarkAgriBorder
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Pico W Inputs
            Text("Raspberry Pi Pico W (Safety Controller):", fontSize = 10.sp, color = TextLightGrey)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedTextField(
                    value = picoIp,
                    onValueChange = { picoIp = it },
                    label = { Text("Pico IP Address", fontSize = 9.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(3f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AgriGreenPrimary,
                        unfocusedBorderColor = DarkAgriBorder
                    )
                )
                OutlinedTextField(
                    value = picoPort,
                    onValueChange = { picoPort = it },
                    label = { Text("Port", fontSize = 9.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AgriGreenPrimary,
                        unfocusedBorderColor = DarkAgriBorder
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    val updated = uiState.settings.copy(
                        esp32Ip = esp32Ip,
                        streamPort = streamPort.toIntOrNull() ?: 81,
                        capturePort = capturePort.toIntOrNull() ?: 80,
                        picoIp = picoIp,
                        picoPort = picoPort.toIntOrNull() ?: 80
                    )
                    onSaveSettings(updated)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .testTag("save_network_settings_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StatusGreen)
            ) {
                Text("SAVE NETWORK CONFIGURATION", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }

        // 3. VISION & SAFETY PARAMETERS SLIDERS
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Parameters",
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "WEEDING & SAFETY PARAMETERS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Weed Confidence Threshold
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Weed Confidence Threshold:", fontSize = 10.sp, color = TextLightGrey)
                Text("${weedThreshold.toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AgriGreenPrimary)
            }
            Slider(
                value = weedThreshold,
                onValueChange = {
                    weedThreshold = it
                    onSaveSettings(uiState.settings.copy(weedConfidenceThreshold = it.toInt()))
                },
                valueRange = 50f..95f,
                colors = SliderDefaults.colors(thumbColor = AgriGreenPrimary, activeTrackColor = AgriGreenPrimary)
            )

            // Confirmation Frames
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Multi-Frame Confirmation:", fontSize = 10.sp, color = TextLightGrey)
                Text("${confirmationFrames.toInt()} consecutive frames", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AgriGreenPrimary)
            }
            Slider(
                value = confirmationFrames,
                onValueChange = {
                    confirmationFrames = it
                    onSaveSettings(uiState.settings.copy(confirmationFrames = it.toInt()))
                },
                valueRange = 1f..5f,
                steps = 3,
                colors = SliderDefaults.colors(thumbColor = AgriGreenPrimary, activeTrackColor = AgriGreenPrimary)
            )

            // Auto Drill Duration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Timed Auto Drill Duration:", fontSize = 10.sp, color = TextLightGrey)
                Text("${drillDuration.toInt()} ms", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AgriGreenPrimary)
            }
            Slider(
                value = drillDuration,
                onValueChange = {
                    drillDuration = it
                    onSaveSettings(uiState.settings.copy(drillDurationMs = it.toLong()))
                },
                valueRange = 500f..3000f,
                colors = SliderDefaults.colors(thumbColor = AgriGreenPrimary, activeTrackColor = AgriGreenPrimary)
            )
        }

        // 4. FULL SYSTEM DIAGNOSTICS TEST
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = "Diagnostics",
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "HARDWARE INTEGRITY TEST",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onRunDiagnostics,
                enabled = !uiState.isRunningDiagnostics,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag("run_system_diagnostics_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E4B2D))
            ) {
                if (uiState.isRunningDiagnostics) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("EXECUTING FULL SYSTEM TEST...", color = Color.White, fontSize = 11.sp)
                } else {
                    Text("RUN FULL SYSTEM TEST", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }

            if (uiState.diagnosticReport != null) {
                Spacer(modifier = Modifier.height(10.dp))
                DiagnosticsReportView(report = uiState.diagnosticReport)
            }
        }

        // 5. EVENT LOGS VIEWER
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HARDWARE & AI EVENT LOGS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLightGrey
                )
                Button(
                    onClick = { showClearLogsDialog = true },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF331414)),
                    modifier = Modifier.testTag("clear_logs_button")
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Clear", tint = EmergencyRed, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("CLEAR", fontSize = 9.sp, color = EmergencyRed, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Severity Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilterChipItem("ALL", isSelected = selectedSeverityFilter == null) {
                    selectedSeverityFilter = null
                }
                FilterChipItem("INFO", isSelected = selectedSeverityFilter == LogSeverity.INFO) {
                    selectedSeverityFilter = LogSeverity.INFO
                }
                FilterChipItem("WARN", isSelected = selectedSeverityFilter == LogSeverity.WARNING) {
                    selectedSeverityFilter = LogSeverity.WARNING
                }
                FilterChipItem("CRIT", isSelected = selectedSeverityFilter == LogSeverity.CRITICAL) {
                    selectedSeverityFilter = LogSeverity.CRITICAL
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val filteredLogs = if (selectedSeverityFilter == null) eventLogs
            else eventLogs.filter { it.severity == selectedSeverityFilter }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                filteredLogs.take(15).forEach { logItem ->
                    EventLogRow(logItem)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }

    if (showClearLogsDialog) {
        Dialog(onDismissRequest = { showClearLogsDialog = false }) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF192B1F),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkAgriBorder),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("CLEAR EVENT LOGS?", fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("This will purge recorded telemetry and detection history.", color = TextLightGrey, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(onClick = { showClearLogsDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                            Text("CANCEL", color = TextLightGrey)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                eventLogRepo.clear()
                                showClearLogsDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                        ) {
                            Text("CONFIRM PURGE", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChipItem(name: String, isSelected: Boolean, onSelect: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (isSelected) AgriGreenPrimary else Color(0xFF0F2617),
        modifier = Modifier.clickable { onSelect() }
    ) {
        Text(
            text = name,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.Black else TextLightGrey,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun DiagnosticsReportView(report: DiagnosticReport) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF07190F),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E482D)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("SYSTEM DIAGNOSTIC RESULTS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = AgriGreenPrimary)
            DiagRow("ESP32-CAM Network", report.espNetwork)
            DiagRow("ESP32-CAM Stream", report.espStream)
            DiagRow("ESP32-CAM Capture", report.espCapture)
            DiagRow("Pico W Network", report.picoNetwork)
            DiagRow("Pico W REST API", report.picoApi)
            DiagRow("AI Vision Engine", report.aiEngineReady)
            DiagRow("Motor System Subsystem", report.motorSystemReady)
            DiagRow("Arm Servo Articulation", report.armReady)
            DiagRow("Drill Safety Ready", report.drillSafetyReady)
            DiagRow("MPU6050 Stability Sensor", report.mpuStable)
            DiagRow("Battery Safe Precondition", report.batterySafe)
            DiagRow("E-Stop Released", report.eStopReleased)
        }
    }
}

@Composable
private fun DiagRow(label: String, passed: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 10.sp, color = TextWhite)
        Text(
            if (passed) "✓ PASS" else "✗ FAIL",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (passed) StatusGreen else StatusRed
        )
    }
}

@Composable
private fun EventLogRow(item: EventLogItem) {
    val severityColor = when (item.severity) {
        LogSeverity.INFO -> StatusGreen
        LogSeverity.WARNING -> StatusOrange
        LogSeverity.ERROR, LogSeverity.CRITICAL -> StatusRed
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF081C10),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(severityColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = EventLogRepository.formatTimestamp(item.timestamp),
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                color = TextLightGrey
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.event, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                Text(text = item.description, fontSize = 9.sp, color = TextLightGrey, maxLines = 2)
            }
        }
    }
}
