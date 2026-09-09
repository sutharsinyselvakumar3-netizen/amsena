package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RobotDirection
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.DarkAgriBorder
import com.example.ui.theme.DarkAgriCard
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextLightGrey
import com.example.ui.theme.TextWhite

@Composable
fun DirectionPad(
    currentDirection: RobotDirection,
    isMoving: Boolean,
    enabled: Boolean,
    onDirectionTap: (RobotDirection) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TOP: FORWARD (▲)
        DirectionButton(
            label = "FORWARD",
            icon = Icons.Default.ArrowUpward,
            direction = RobotDirection.FORWARD,
            isActive = isMoving && currentDirection == RobotDirection.FORWARD,
            enabled = enabled,
            onTap = { onDirectionTap(RobotDirection.FORWARD) },
            testTag = "button_forward"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // MIDDLE: LEFT (◀) and RIGHT (▶)
        Row(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DirectionButton(
                label = "LEFT",
                icon = Icons.Default.ArrowBack,
                direction = RobotDirection.LEFT,
                isActive = isMoving && currentDirection == RobotDirection.LEFT,
                enabled = enabled,
                onTap = { onDirectionTap(RobotDirection.LEFT) },
                testTag = "button_left"
            )

            // Center Status Indicator (Not a button! No normal stop button)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF07190F))
                    .border(1.dp, if (isMoving) StatusGreen else DarkAgriBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isMoving) StatusGreen else Color.Gray)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = if (isMoving) "ACTIVE" else "IDLE",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isMoving) StatusGreen else TextLightGrey
                    )
                }
            }

            DirectionButton(
                label = "RIGHT",
                icon = Icons.Default.ArrowForward,
                direction = RobotDirection.RIGHT,
                isActive = isMoving && currentDirection == RobotDirection.RIGHT,
                enabled = enabled,
                onTap = { onDirectionTap(RobotDirection.RIGHT) },
                testTag = "button_right"
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // BOTTOM: REVERSE (▼)
        DirectionButton(
            label = "REVERSE",
            icon = Icons.Default.ArrowDownward,
            direction = RobotDirection.REVERSE,
            isActive = isMoving && currentDirection == RobotDirection.REVERSE,
            enabled = enabled,
            onTap = { onDirectionTap(RobotDirection.REVERSE) },
            testTag = "button_reverse"
        )
    }
}

@Composable
private fun DirectionButton(
    label: String,
    icon: ImageVector,
    direction: RobotDirection,
    isActive: Boolean,
    enabled: Boolean,
    onTap: () -> Unit,
    testTag: String
) {
    val bgColor = if (isActive) Color(0xFF065F46) else Color(0xFF0D2818)
    val borderColor = if (isActive) AgriGreenPrimary else Color(0xFF1E482D)

    Surface(
        modifier = Modifier
            .size(width = 110.dp, height = 62.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(enabled = enabled) { onTap() }
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        shadowElevation = if (isActive) 6.dp else 2.dp
    ) {
        Column(
            modifier = Modifier
                .background(
                    if (isActive) Brush.verticalGradient(
                        listOf(Color(0xFF059669), Color(0xFF064E3B))
                    ) else Brush.verticalGradient(
                        listOf(Color(0xFF133821), Color(0xFF0A1F13))
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color.White else AgriGreenPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isActive) Color.White else TextWhite,
                letterSpacing = 0.5.sp
            )
        }
    }
}
