package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import com.example.model.AiObjectClass
import com.example.model.Detection
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusOrange

@Composable
fun DetectionOverlay(
    detections: List<Detection>,
    showLabels: Boolean = true,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasW = size.width
        val canvasH = size.height

        // Assume standard coordinates normalized against 320x240 or scale dynamically
        val scaleX = canvasW / 320f
        val scaleY = canvasH / 240f

        for (det in detections) {
            val left = det.x * scaleX
            val top = det.y * scaleY
            val width = det.w * scaleX
            val height = det.h * scaleY

            val boxColor = when (det.objectClass) {
                AiObjectClass.ONION -> StatusGreen
                AiObjectClass.WEED -> if (det.confirmed) EmergencyRed else StatusOrange
                AiObjectClass.UNKNOWN -> Color.LightGray
            }

            // Draw bounding rectangle
            drawRoundRect(
                color = boxColor,
                topLeft = Offset(left, top),
                size = Size(width, height),
                cornerRadius = CornerRadius(4f, 4f),
                style = Stroke(
                    width = 2.5f,
                    pathEffect = if (!det.confirmed && det.objectClass == AiObjectClass.WEED)
                        PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f) else null
                )
            )

            // Draw crosshair at center
            val cx = (det.centerX) * scaleX
            val cy = (det.centerY) * scaleY
            val chLength = 8f
            drawLine(
                color = boxColor,
                start = Offset(cx - chLength, cy),
                end = Offset(cx + chLength, cy),
                strokeWidth = 2f
            )
            drawLine(
                color = boxColor,
                start = Offset(cx, cy - chLength),
                end = Offset(cx, cy + chLength),
                strokeWidth = 2f
            )

            // Draw text tag badge using native canvas
            if (showLabels) {
                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 22f
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                        isAntiAlias = true
                    }
                    val bgPaint = android.graphics.Paint().apply {
                        color = when (det.objectClass) {
                            AiObjectClass.ONION -> android.graphics.Color.argb(200, 16, 185, 129)
                            AiObjectClass.WEED -> if (det.confirmed) android.graphics.Color.argb(220, 220, 38, 38)
                            else android.graphics.Color.argb(200, 245, 158, 11)
                            AiObjectClass.UNKNOWN -> android.graphics.Color.argb(200, 107, 114, 128)
                        }
                    }

                    val label = when (det.objectClass) {
                        AiObjectClass.ONION -> "ONION ${det.confidence}% [SAFE]"
                        AiObjectClass.WEED -> "WEED ${det.confidence}% ${if (det.confirmed) "[TARGET]" else "[CONFIRMING]"}"
                        AiObjectClass.UNKNOWN -> "UNKNOWN ${det.confidence}%"
                    }

                    val textWidth = paint.measureText(label)
                    val textHeight = 24f
                    val badgeTop = (top - 28f).coerceAtLeast(4f)

                    drawRoundRect(
                        left,
                        badgeTop,
                        left + textWidth + 12f,
                        badgeTop + textHeight + 6f,
                        4f,
                        4f,
                        bgPaint
                    )
                    drawText(label, left + 6f, badgeTop + textHeight - 2f, paint)
                }
            }
        }
    }
}
