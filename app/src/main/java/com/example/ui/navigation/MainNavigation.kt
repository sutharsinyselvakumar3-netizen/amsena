package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.model.RobotMode
import com.example.ui.components.StartAutoDialog
import com.example.ui.components.StopAutoDialog
import com.example.ui.screens.AiVisionScreen
import com.example.ui.screens.ArmScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.RobotScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.RobotAppTheme
import com.example.ui.theme.RobotTheme
import com.example.ui.theme.TextLightGrey
import com.example.viewmodel.RobotViewModel

sealed class Screen(val route: String, val label: String, val icon: ImageVector, val tag: String) {
    object Home : Screen("home", "Home", Icons.Default.Home, "nav_home")
    object Robot : Screen("robot", "Robot", Icons.Default.SmartToy, "nav_robot")
    object AiVision : Screen("ai_vision", "AI Vision", Icons.Default.Visibility, "nav_ai_vision")
    object Arm : Screen("arm", "Arm", Icons.Default.PrecisionManufacturing, "nav_arm")
    object Settings : Screen("settings", "Settings", Icons.Default.Settings, "nav_settings")
}

@Composable
fun MainApp(viewModel: RobotViewModel) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    val screens = listOf(
        Screen.Home,
        Screen.Robot,
        Screen.AiVision,
        Screen.Arm,
        Screen.Settings
    )

    RobotAppTheme(
        mode = uiState.mode,
        eStopActive = uiState.eStopActive
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(RobotTheme.colors.background),
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

                NavigationBar(
                    containerColor = RobotTheme.colors.surface,
                    tonalElevation = 8.dp
                ) {
                    screens.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.label
                            )
                        },
                        label = {
                            Text(
                                text = screen.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag(screen.tag),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = if (RobotTheme.colors.isAuto) Color(0xFF001E3C) else Color.Black,
                            selectedTextColor = RobotTheme.colors.primary,
                            indicatorColor = RobotTheme.colors.primary,
                            unselectedIconColor = TextLightGrey,
                            unselectedTextColor = TextLightGrey
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    uiState = uiState,
                    onTriggerEStop = { viewModel.triggerEmergencyStop() },
                    onResetEStop = { viewModel.resetEmergencyStop() },
                    onRequestStartAuto = { viewModel.requestStartAuto() },
                    onRequestStopAuto = { viewModel.requestStopAuto() },
                    onTestAllConnections = { viewModel.testAllConnections() }
                )
            }

            composable(Screen.Robot.route) {
                RobotScreen(
                    uiState = uiState,
                    onDirectionTap = { direction -> viewModel.onMovementTap(direction) },
                    onSpeedSelected = { speed -> viewModel.setSpeed(speed) },
                    onToggleSoil = { viewModel.toggleSoil() },
                    onToggleWater = { viewModel.toggleWaterPump() },
                    onTriggerEStop = { viewModel.triggerEmergencyStop() },
                    onResetEStop = { viewModel.resetEmergencyStop() },
                    onRequestStartAuto = { viewModel.requestStartAuto() },
                    onRequestStopAuto = { viewModel.requestStopAuto() }
                )
            }

            composable(Screen.AiVision.route) {
                AiVisionScreen(
                    uiState = uiState
                )
            }

            composable(Screen.Arm.route) {
                ArmScreen(
                    uiState = uiState,
                    onServo4AngleChange = { angle -> viewModel.setServo4Angle(angle) },
                    onServo4Home = { viewModel.servo4Home() },
                    onServo4Center = { viewModel.servo4Center() },
                    onTriggerEStop = { viewModel.triggerEmergencyStop() },
                    onResetEStop = { viewModel.resetEmergencyStop() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    uiState = uiState,
                    eventLogRepo = viewModel.eventLogRepo,
                    onSaveSettings = { newSettings -> viewModel.updateSettings(newSettings) },
                    onToggleDemoMode = { viewModel.toggleDemoMode() },
                    onScanNetwork = { viewModel.scanLocalNetwork() },
                    onApplyScannedDevice = { dev -> viewModel.applyScannedDevice(dev) },
                    onRunDiagnostics = { viewModel.runFullSystemTest() }
                )
            }
        }

        // Global Modal Confirmation Dialogs
        if (uiState.mode == RobotMode.AUTO_CONFIRMING) {
            StartAutoDialog(
                onConfirm = { viewModel.confirmStartAuto() },
                onDismiss = { viewModel.cancelAutoStart() }
            )
        }

        if (uiState.mode == RobotMode.MANUAL_CONFIRMING) {
            StopAutoDialog(
                onConfirm = { viewModel.confirmStopAuto() },
                onDismiss = { viewModel.cancelAutoStop() }
            )
        }
    }
}
}
