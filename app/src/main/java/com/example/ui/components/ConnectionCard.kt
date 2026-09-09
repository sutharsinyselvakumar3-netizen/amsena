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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.model.ConnectionState
import com.example.model.DeviceConnection
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.DarkAgriBorder
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGrey
import com.example.ui.theme.StatusOrange
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextLightGrey
import com.example.ui.theme.TextWhite

@Composable
fun ConnectionCard(
    wifi: DeviceConnection,
    esp32: DeviceConnection,
    pico: DeviceConnection,
    aiEngine: DeviceConnection,
    isTesting: Boolean,
    onTestAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "WI-FI & HARNESS CONNECTION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextLightGrey,
                letterSpacing = 0.5.sp
            )
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF0D2817)
            ) {
                Text(
                    text = "LOCAL 2.4GHz",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AgriGreenPrimary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2x2 Grid of Nodes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ConnectionNodeChip(
                name = "Wi-Fi Network",
                device = wifi,
                modifier = Modifier.weight(1f)
            )
            ConnectionNodeChip(
                name = "ESP32-CAM",
                device = esp32,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ConnectionNodeChip(
                name = "Pico W Controller",
                device = pico,
                modifier = Modifier.weight(1f)
            )
            ConnectionNodeChip(
                name = "AI Vision Engine",
                device = aiEngine,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onTestAll,
            enabled = !isTesting,
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .testTag("test_all_connections_button"),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1B452A),
                disabledContainerColor = Color(0xFF132B1C)
            )
        ) {
            if (isTesting) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CHECKING CONNECTIONS...",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Test",
                    tint = AgriGreenPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "TEST ALL CONNECTIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun ConnectionNodeChip(
    name: String,
    device: DeviceConnection,
    modifier: Modifier = Modifier
) {
    val statusColor = when (device.state) {
        ConnectionState.ONLINE -> StatusGreen
        ConnectionState.CONNECTING -> StatusOrange
        ConnectionState.OFFLINE -> StatusGrey
        ConnectionState.ERROR -> StatusRed
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF091F11),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkAgriBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = name,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    maxLines = 1
                )
                Text(
                    text = "${device.state.name} ${if (device.latencyMs > 0) "(${device.latencyMs}ms)" else ""}",
                    fontSize = 9.sp,
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
