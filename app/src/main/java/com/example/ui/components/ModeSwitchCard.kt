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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RobotMode
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.DarkAgriCard
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextLightGrey
import com.example.ui.theme.TextWhite

@Composable
fun ModeSwitchCard(
    currentMode: RobotMode,
    onRequestStartAuto: () -> Unit,
    onRequestStopAuto: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAuto = currentMode == RobotMode.AUTO || currentMode == RobotMode.AUTO_SAFETY_CHECK

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 18.dp,
        containerColor = if (isAuto) Color(0xFF0F311C) else DarkAgriCard,
        borderColor = if (isAuto) AgriGreenPrimary else GlassBorder
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "OPERATIONAL MODE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLightGrey,
                    letterSpacing = 1.sp
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isAuto) Color(0xFF047857) else Color(0xFF1E3A27)
                ) {
                    Text(
                        text = if (isAuto) "AUTONOMOUS ONION DEFENSE" else "MANUAL TELEOPERATION",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isAuto) Color(0xFFA7F3D0) else TextLightGrey
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Large Toggle Track
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, Color(0xFF265434), RoundedCornerShape(14.dp)),
                color = Color(0xFF071B0F)
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // MANUAL Option
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (!isAuto) Brush.horizontalGradient(
                                    listOf(Color(0xFF1E452B), Color(0xFF15331E))
                                ) else SolidColor(Color.Transparent)
                            )
                            .border(
                                width = if (!isAuto) 1.5.dp else 0.dp,
                                color = if (!isAuto) StatusGreen else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                if (isAuto) onRequestStopAuto()
                            }
                            .testTag("mode_manual_tab"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Handyman,
                                contentDescription = "Manual Mode",
                                tint = if (!isAuto) AgriGreenPrimary else TextLightGrey,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MANUAL",
                                fontSize = 14.sp,
                                fontWeight = if (!isAuto) FontWeight.Bold else FontWeight.Normal,
                                color = if (!isAuto) TextWhite else TextLightGrey
                            )
                        }
                    }

                    // Center Switch Icon
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Switch Mode",
                        tint = if (isAuto) AgriGreenPrimary else TextLightGrey,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(20.dp)
                    )

                    // AUTO Option
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isAuto) Brush.horizontalGradient(
                                    listOf(Color(0xFF047857), Color(0xFF065F46))
                                ) else SolidColor(Color.Transparent)
                            )
                            .border(
                                width = if (isAuto) 1.5.dp else 0.dp,
                                color = if (isAuto) AgriGreenPrimary else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                if (!isAuto) onRequestStartAuto()
                            }
                            .testTag("mode_auto_tab"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Auto Mode",
                                tint = if (isAuto) Color(0xFFA7F3D0) else TextLightGrey,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AUTO",
                                fontSize = 14.sp,
                                fontWeight = if (isAuto) FontWeight.Bold else FontWeight.Normal,
                                color = if (isAuto) Color.White else TextLightGrey
                            )
                        }
                    }
                }
            }
        }
    }
}
