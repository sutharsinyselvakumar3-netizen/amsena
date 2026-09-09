package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RobotMode
import com.example.ui.theme.RobotTheme

@Composable
fun RobotModeHeader(
    currentMode: RobotMode,
    eStopActive: Boolean,
    modifier: Modifier = Modifier
) {
    val isAuto = currentMode == RobotMode.AUTO || currentMode == RobotMode.AUTO_SAFETY_CHECK

    val infiniteTransition = rememberInfiniteTransition(label = "mode_indicator_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val containerBg by animateColorAsState(
        targetValue = when {
            eStopActive -> Color(0xDD3A0A0A)
            isAuto -> Color(0xDD08264A)
            else -> Color(0xDD0B3D2E)
        },
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "containerBg"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            eStopActive -> Color(0x99EF4444)
            isAuto -> Color(0x882E9BFF)
            else -> Color(0x8836D98A)
        },
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "borderColor"
    )

    val indicatorColor by animateColorAsState(
        targetValue = when {
            eStopActive -> Color(0xFFEF4444)
            isAuto -> Color(0xFF2E9BFF)
            else -> Color(0xFF36D98A)
        },
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "indicatorColor"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .testTag("robot_mode_header"),
        color = containerBg
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pulsing Active Indicator Dot
                Box(
                    modifier = Modifier.size(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(indicatorColor.copy(alpha = 0.25f))
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(indicatorColor)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = when {
                            eStopActive -> "EMERGENCY STOP"
                            isAuto -> "AUTO MODE"
                            else -> "MANUAL MODE"
                        },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = when {
                            eStopActive -> "ALL HARDWARE POWER CUT"
                            isAuto -> "AI AUTONOMOUS OPERATION"
                            else -> "SAFE HUMAN CONTROL"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = indicatorColor,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Status Badge
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = indicatorColor.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, indicatorColor.copy(alpha = 0.4f))
            ) {
                Text(
                    text = when {
                        eStopActive -> "E-STOP"
                        isAuto -> "AI SCOUTING"
                        else -> "HUMAN ACTIVE"
                    },
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = indicatorColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
