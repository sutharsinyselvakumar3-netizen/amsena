package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.model.RelayState
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.DarkAgriBorder
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGrey
import com.example.ui.theme.TextLightGrey
import com.example.ui.theme.TextWhite

@Composable
fun RelayControlCard(
    relays: RelayState,
    isManualMode: Boolean,
    onToggleSoil: () -> Unit,
    onToggleWater: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ACTUATOR RELAYS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextLightGrey,
                letterSpacing = 0.5.sp
            )
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isManualMode) Color(0xFF133620) else Color(0xFF261D13)
            ) {
                Text(
                    text = if (isManualMode) "MANUAL INTERACTIVE" else "AUTO OVERRIDE LOCKED",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isManualMode) AgriGreenPrimary else Color(0xFFF59E0B),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // RELAY 1: AUTO DRILL (AUTO ONLY - NO MANUAL TRIGGER)
        RelayRow(
            icon = Icons.Default.Build,
            name = "RELAY 1: AUTO DRILL",
            subtitle = if (relays.autoDrill) "DRILLING ACTIVE (TIMED 1500ms)" else "AUTO ONLY (NO MANUAL TRIGGER)",
            isActive = relays.autoDrill,
            activeColor = EmergencyRed,
            isInteractive = false,
            badgeText = if (relays.autoDrill) "DRILL ON" else "SAFE OFF"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // RELAY 2: SOIL (MANUAL ONLY)
        RelayInteractiveRow(
            icon = Icons.Default.Grass,
            name = "RELAY 2: SOIL ACTUATOR",
            subtitle = if (isManualMode) (if (relays.soil) "SOIL ENGAGED" else "SOIL IDLE") else "DISABLED IN AUTO",
            isActive = relays.soil,
            isEnabled = isManualMode,
            onToggle = onToggleSoil,
            testTag = "toggle_soil"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // RELAY 3: WATER PUMP (MANUAL ONLY)
        RelayInteractiveRow(
            icon = Icons.Default.Opacity,
            name = "RELAY 3: WATER PUMP",
            subtitle = if (isManualMode) (if (relays.water) "PUMP DISPENSING" else "PUMP IDLE") else "DISABLED IN AUTO",
            isActive = relays.water,
            isEnabled = isManualMode,
            onToggle = onToggleWater,
            testTag = "toggle_water"
        )
    }
}

@Composable
private fun RelayRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    name: String,
    subtitle: String,
    isActive: Boolean,
    activeColor: Color,
    isInteractive: Boolean,
    badgeText: String
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF0A1F13),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkAgriBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isActive) activeColor.copy(alpha = 0.2f) else Color(0xFF132B1B)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = name,
                        tint = if (isActive) activeColor else TextLightGrey,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    Text(text = subtitle, fontSize = 10.sp, color = TextLightGrey)
                }
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isActive) activeColor else Color(0xFF163120)
            ) {
                Text(
                    text = badgeText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) Color.White else StatusGrey,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun RelayInteractiveRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    name: String,
    subtitle: String,
    isActive: Boolean,
    isEnabled: Boolean,
    onToggle: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF0A1F13),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkAgriBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isActive) StatusGreen.copy(alpha = 0.2f) else Color(0xFF132B1B)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = name,
                        tint = if (isActive && isEnabled) StatusGreen else TextLightGrey,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isEnabled) TextWhite else StatusGrey
                    )
                    Text(
                        text = subtitle,
                        fontSize = 10.sp,
                        color = if (isEnabled) TextLightGrey else StatusGrey
                    )
                }
            }

            Switch(
                checked = isActive && isEnabled,
                onCheckedChange = { onToggle() },
                enabled = isEnabled,
                modifier = Modifier.testTag(testTag),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = StatusGreen,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color(0xFF1B3D28),
                    disabledCheckedTrackColor = Color.DarkGray,
                    disabledUncheckedTrackColor = Color(0xFF13241A)
                )
            )
        }
    }
}
