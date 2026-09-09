package com.example.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.example.model.RobotMode

data class RobotColors(
    val background: Color,
    val surface: Color,
    val card: Color,
    val cardElevated: Color,
    val border: Color,
    val primary: Color,
    val primaryVariant: Color,
    val secondary: Color,
    val accent: Color,
    val statusIndicator: Color,
    val isAuto: Boolean,
    val isEStop: Boolean
)

val LocalRobotColors = compositionLocalOf {
    RobotColors(
        background = Color(0xFF062E20),
        surface = Color(0xFF0B3D2E),
        card = Color(0xCC0B3D2E),
        cardElevated = Color(0xFF164434),
        border = Color(0x6636D98A),
        primary = Color(0xFF36D98A),
        primaryVariant = Color(0xFF126B4A),
        secondary = Color(0xFF84CC16),
        accent = Color(0xFF34D399),
        statusIndicator = Color(0xFF36D98A),
        isAuto = false,
        isEStop = false
    )
}

object RobotTheme {
    val colors: RobotColors
        @Composable
        @ReadOnlyComposable
        get() = LocalRobotColors.current
}

@Composable
fun RobotAppTheme(
    mode: RobotMode = RobotMode.MANUAL,
    eStopActive: Boolean = false,
    content: @Composable () -> Unit
) {
    val isAuto = mode == RobotMode.AUTO || mode == RobotMode.AUTO_SAFETY_CHECK
    val isEStop = eStopActive || mode == RobotMode.EMERGENCY_STOP

    // Smooth animated color transition between MANUAL (Dark Green) and AUTO (Dark Blue) or E-STOP (Dark Red)
    val animBackground by animateColorAsState(
        targetValue = when {
            isEStop -> Color(0xFF1F0808)
            isAuto -> Color(0xFF061A33) // AUTO Mode: Dark Blue
            else -> Color(0xFF062E20)   // MANUAL Mode: Dark Green
        },
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "animBackground"
    )

    val animSurface by animateColorAsState(
        targetValue = when {
            isEStop -> Color(0xFF2E0C0C)
            isAuto -> Color(0xFF08264A)
            else -> Color(0xFF0B3D2E)
        },
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "animSurface"
    )

    val animCard by animateColorAsState(
        targetValue = when {
            isEStop -> Color(0xDD3A0E0E)
            isAuto -> Color(0xDD08264A)
            else -> Color(0xDD0B3D2E)
        },
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "animCard"
    )

    val animCardElevated by animateColorAsState(
        targetValue = when {
            isEStop -> Color(0xFF4C1414)
            isAuto -> Color(0xFF0E386B)
            else -> Color(0xFF144B39)
        },
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "animCardElevated"
    )

    val animBorder by animateColorAsState(
        targetValue = when {
            isEStop -> Color(0x88EF4444)
            isAuto -> Color(0x662E9BFF)
            else -> Color(0x6636D98A)
        },
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "animBorder"
    )

    val animPrimary by animateColorAsState(
        targetValue = when {
            isEStop -> Color(0xFFEF4444)
            isAuto -> Color(0xFF2E9BFF) // AUTO Mode Accent Blue
            else -> Color(0xFF36D98A)   // MANUAL Mode Accent Green
        },
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "animPrimary"
    )

    val animPrimaryVariant by animateColorAsState(
        targetValue = when {
            isEStop -> Color(0xFF991B1B)
            isAuto -> Color(0xFF0B3B73)
            else -> Color(0xFF126B4A)
        },
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "animPrimaryVariant"
    )

    val animSecondary by animateColorAsState(
        targetValue = when {
            isEStop -> Color(0xFFF87171)
            isAuto -> Color(0xFF60A5FA)
            else -> Color(0xFF84CC16)
        },
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "animSecondary"
    )

    val animAccent by animateColorAsState(
        targetValue = when {
            isEStop -> Color(0xFFFCA5A5)
            isAuto -> Color(0xFF38BDF8)
            else -> Color(0xFF34D399)
        },
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "animAccent"
    )

    val animStatusIndicator by animateColorAsState(
        targetValue = when {
            isEStop -> Color(0xFFEF4444)
            isAuto -> Color(0xFF38BDF8)
            else -> Color(0xFF34D399)
        },
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "animStatusIndicator"
    )

    val robotColors = RobotColors(
        background = animBackground,
        surface = animSurface,
        card = animCard,
        cardElevated = animCardElevated,
        border = animBorder,
        primary = animPrimary,
        primaryVariant = animPrimaryVariant,
        secondary = animSecondary,
        accent = animAccent,
        statusIndicator = animStatusIndicator,
        isAuto = isAuto,
        isEStop = isEStop
    )

    val dynamicM3ColorScheme = darkColorScheme(
        primary = animPrimary,
        onPrimary = if (isAuto) Color(0xFF001E3C) else Color(0xFF021B0E),
        primaryContainer = animPrimaryVariant,
        onPrimaryContainer = Color.White,
        secondary = animSecondary,
        onSecondary = Color.Black,
        background = animBackground,
        onBackground = Color.White,
        surface = animSurface,
        onSurface = Color.White,
        surfaceVariant = animCard,
        onSurfaceVariant = Color(0xFFD1D5DB),
        outline = animBorder,
        error = StatusRed,
        onError = Color.White
    )

    CompositionLocalProvider(LocalRobotColors provides robotColors) {
        MaterialTheme(
            colorScheme = dynamicM3ColorScheme,
            typography = Typography,
            content = content
        )
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    RobotAppTheme(
        mode = RobotMode.MANUAL,
        eStopActive = false,
        content = content
    )
}
