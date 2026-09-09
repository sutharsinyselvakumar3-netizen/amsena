package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.DarkAgriBorder
import com.example.ui.theme.StatusGrey
import com.example.ui.theme.TextLightGrey
import com.example.ui.theme.TextWhite

@Composable
fun Servo4Control(
    angle: Float,
    maxAngle: Float = 45f,
    enabled: Boolean,
    onAngleChange: (Float) -> Unit,
    onHomeTap: () -> Unit,
    onCenterTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SERVO 4 (MANUAL TOOL TILT)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) TextWhite else StatusGrey,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = if (enabled) "MANUAL ONLY" else "DISABLED IN AUTO",
                    fontSize = 9.sp,
                    color = if (enabled) AgriGreenPrimary else StatusGrey,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (enabled) Color(0xFF072413) else Color(0xFF1E2420)
            ) {
                Text(
                    text = "${angle.toInt()}°",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = if (enabled) AgriGreenPrimary else StatusGrey,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Angle Slider (0° to 45°)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "0°", fontSize = 10.sp, color = TextLightGrey)
            Slider(
                value = angle,
                onValueChange = onAngleChange,
                valueRange = 0f..maxAngle,
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .testTag("servo4_slider"),
                colors = SliderDefaults.colors(
                    thumbColor = AgriGreenPrimary,
                    activeTrackColor = AgriGreenPrimary,
                    inactiveTrackColor = DarkAgriBorder,
                    disabledThumbColor = StatusGrey,
                    disabledActiveTrackColor = Color.DarkGray
                )
            )
            Text(text = "${maxAngle.toInt()}°", fontSize = 10.sp, color = TextLightGrey)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Preset Quick Action Buttons: HOME (0°) & CENTER (22.5°)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onHomeTap,
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .testTag("servo4_home_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF163E24),
                    disabledContainerColor = Color(0xFF121F17)
                )
            ) {
                Text(
                    text = "HOME (0°)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) Color.White else StatusGrey
                )
            }

            Button(
                onClick = onCenterTap,
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .testTag("servo4_center_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF163E24),
                    disabledContainerColor = Color(0xFF121F17)
                )
            ) {
                Text(
                    text = "CENTER (22°)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) Color.White else StatusGrey
                )
            }
        }
    }
}
