package com.example

import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.BatteryMonitor
import com.example.data.WidgetConfig
import com.example.data.WidgetPreferencesRepository
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WidgetsManagerScreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.HiTechTheme
import com.example.widget.HiTechBatteryWidgetProvider

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val clickedWidgetId = intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1) ?: -1

        setContent {
            HiTechTheme {
                val context = LocalContext.current
                val batteryInfo by BatteryMonitor.batteryFlow(context)
                    .collectAsState(initial = BatteryMonitor.getCurrentBatteryInfo(context))

                var currentConfig by remember {
                    mutableStateOf(
                        if (clickedWidgetId > 0) {
                            WidgetPreferencesRepository.loadWidgetConfig(context, clickedWidgetId)
                        } else {
                            WidgetPreferencesRepository.loadDefaultConfig(context)
                        }
                    )
                }

                var selectedTab by remember { mutableIntStateOf(0) }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding(),
                    containerColor = Color(0xFF080C16),
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier.navigationBarsPadding(),
                            containerColor = Color(0xFF0F172A),
                            tonalElevation = 8.dp
                        ) {
                            NavigationBarItem(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                icon = {
                                    Icon(
                                        imageVector = if (selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                                        contentDescription = "Home"
                                    )
                                },
                                label = { Text("Preview", fontWeight = FontWeight.SemiBold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = CyanAccent,
                                    indicatorColor = CyanAccent,
                                    unselectedIconColor = Color(0xFF64748B),
                                    unselectedTextColor = Color(0xFF64748B)
                                ),
                                modifier = Modifier.testTag("nav_home_tab")
                            )

                            NavigationBarItem(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                icon = {
                                    Icon(
                                        imageVector = if (selectedTab == 1) Icons.Filled.Widgets else Icons.Outlined.Widgets,
                                        contentDescription = "Widgets"
                                    )
                                },
                                label = { Text("My Widgets", fontWeight = FontWeight.SemiBold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = CyanAccent,
                                    indicatorColor = CyanAccent,
                                    unselectedIconColor = Color(0xFF64748B),
                                    unselectedTextColor = Color(0xFF64748B)
                                ),
                                modifier = Modifier.testTag("nav_widgets_tab")
                            )

                            NavigationBarItem(
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2 },
                                icon = {
                                    Icon(
                                        imageVector = if (selectedTab == 2) Icons.Filled.Settings else Icons.Outlined.Settings,
                                        contentDescription = "Settings"
                                    )
                                },
                                label = { Text("Settings", fontWeight = FontWeight.SemiBold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = CyanAccent,
                                    indicatorColor = CyanAccent,
                                    unselectedIconColor = Color(0xFF64748B),
                                    unselectedTextColor = Color(0xFF64748B)
                                ),
                                modifier = Modifier.testTag("nav_settings_tab")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (selectedTab) {
                            0 -> HomeScreen(
                                batteryInfo = batteryInfo,
                                currentConfig = currentConfig,
                                onConfigChanged = { newConfig ->
                                    currentConfig = newConfig
                                    WidgetPreferencesRepository.saveDefaultConfig(context, newConfig)
                                    if (clickedWidgetId > 0) {
                                        WidgetPreferencesRepository.saveWidgetConfig(context, clickedWidgetId, newConfig)
                                    }
                                    HiTechBatteryWidgetProvider.updateAllWidgets(context)
                                }
                            )
                            1 -> WidgetsManagerScreen(
                                batteryInfo = batteryInfo,
                                onConfigureWidget = { widgetId ->
                                    currentConfig = WidgetPreferencesRepository.loadWidgetConfig(context, widgetId)
                                    selectedTab = 0
                                }
                            )
                            2 -> SettingsScreen(
                                currentConfig = currentConfig,
                                onConfigChanged = { newConfig ->
                                    currentConfig = newConfig
                                    WidgetPreferencesRepository.saveDefaultConfig(context, newConfig)
                                    HiTechBatteryWidgetProvider.updateAllWidgets(context)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Immediately refresh all widgets when app comes to foreground
        HiTechBatteryWidgetProvider.updateAllWidgets(this)
    }
}
