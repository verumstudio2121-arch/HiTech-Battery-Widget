package com.example.ui.screens

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BatteryInfo
import com.example.data.WidgetColorPreset
import com.example.data.WidgetConfig
import com.example.data.WidgetStyle
import com.example.ui.components.BatteryDiagnosticsCard
import com.example.ui.components.ColorSelectorRow
import com.example.ui.components.WidgetInteractivePreview
import com.example.ui.theme.CyanAccent
import com.example.widget.HiTechBatteryWidgetProvider

@Composable
fun HomeScreen(
    batteryInfo: BatteryInfo,
    currentConfig: WidgetConfig,
    onConfigChanged: (WidgetConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showPinDialog by remember { mutableStateOf(false) }
    var pinDialogMessage by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // App Title & Live Chip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "HiTech Battery",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Live Hardware Telemetry",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            // Real battery badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, Color(0x4438BDF8), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (batteryInfo.isCharging) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = Color(0xFFFACC15),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = "${batteryInfo.percentage}%",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Live Interactive Preview
        WidgetInteractivePreview(
            config = currentConfig,
            batteryInfo = batteryInfo,
            modifier = Modifier.testTag("live_widget_preview")
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Primary Action: Add to Home Screen
        Button(
            onClick = {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && appWidgetManager.isRequestPinAppWidgetSupported) {
                    val myProvider = ComponentName(context, HiTechBatteryWidgetProvider::class.java)
                    val success = appWidgetManager.requestPinAppWidget(myProvider, null, null)
                    if (success) {
                        pinDialogMessage = "Widget pin request sent to launcher!\n\nCheck your home screen or accept the launcher popup to place it."
                    } else {
                        pinDialogMessage = "To add the widget:\n1. Long press an empty spot on your Home screen.\n2. Tap 'Widgets'.\n3. Select 'HiTech Battery Widget' and drag it to your screen."
                    }
                } else {
                    pinDialogMessage = "To add the widget:\n1. Go to your Home screen.\n2. Long press an empty space.\n3. Tap 'Widgets'.\n4. Search for 'HiTech Battery Widget' and drop it onto your screen."
                }
                showPinDialog = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("add_widget_button"),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CyanAccent,
                contentColor = Color(0xFF030712)
            )
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Widget to Home Screen", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Visual Style Selector
        Text(
            text = "Visual Style",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        StyleSelectorTabs(
            selectedStyle = currentConfig.style,
            onStyleSelected = { newStyle ->
                onConfigChanged(currentConfig.copy(style = newStyle))
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Color Customization Row
        ColorSelectorRow(
            selectedPreset = currentConfig.colorPreset,
            customHex = currentConfig.customColorHex,
            onSelectPreset = { preset ->
                onConfigChanged(currentConfig.copy(colorPreset = preset))
            },
            onCustomColorChanged = { hex ->
                onConfigChanged(currentConfig.copy(customColorHex = hex, colorPreset = WidgetColorPreset.CUSTOM))
            }
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Options & Toggles Card
        Text(
            text = "Widget Display Options",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0x22475569)))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OptionToggleRow(
                    title = "Auto Color Mode",
                    subtitle = ">50% normal, 20-50% warm, <20% red warning, charging glow",
                    icon = Icons.Default.AutoAwesome,
                    checked = currentConfig.autoColor,
                    onCheckedChange = { onConfigChanged(currentConfig.copy(autoColor = it)) },
                    testTag = "toggle_auto_color"
                )

                Spacer(modifier = Modifier.height(14.dp))

                OptionToggleRow(
                    title = "Show Battery Percentage",
                    subtitle = "Display big numeric percentage on widget",
                    icon = Icons.Default.Percent,
                    checked = currentConfig.showPercentage,
                    onCheckedChange = { onConfigChanged(currentConfig.copy(showPercentage = it)) },
                    testTag = "toggle_show_percentage"
                )

                Spacer(modifier = Modifier.height(14.dp))

                OptionToggleRow(
                    title = "Show Charging Indicator",
                    subtitle = "Display ⚡ lightning bolt when connected to charger",
                    icon = Icons.Default.Bolt,
                    checked = currentConfig.showChargingIcon,
                    onCheckedChange = { onConfigChanged(currentConfig.copy(showChargingIcon = it)) },
                    testTag = "toggle_show_charging"
                )

                Spacer(modifier = Modifier.height(14.dp))

                OptionToggleRow(
                    title = "Show Status Text",
                    subtitle = "Display 'Charging', 'Fully Charged', or battery health",
                    icon = Icons.Default.TextFields,
                    checked = currentConfig.showStatusText,
                    onCheckedChange = { onConfigChanged(currentConfig.copy(showStatusText = it)) },
                    testTag = "toggle_show_status"
                )

                Spacer(modifier = Modifier.height(14.dp))

                OptionToggleRow(
                    title = "Fluid Preview Animation",
                    subtitle = "Smooth wave oscillations in live preview",
                    icon = Icons.Default.PlayArrow,
                    checked = currentConfig.animationsEnabled,
                    onCheckedChange = { onConfigChanged(currentConfig.copy(animationsEnabled = it)) },
                    testTag = "toggle_animations"
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Hardware Diagnostics Card
        BatteryDiagnosticsCard(batteryInfo = batteryInfo)

        Spacer(modifier = Modifier.height(36.dp))
    }

    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = {
                Text("Add to Home Screen", fontWeight = FontWeight.Bold, color = Color.White)
            },
            text = {
                Text(
                    text = pinDialogMessage,
                    color = Color(0xFFCBD5E1),
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showPinDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
                ) {
                    Text("Got it", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF0F172A),
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun StyleSelectorTabs(
    selectedStyle: WidgetStyle,
    onStyleSelected: (WidgetStyle) -> Unit
) {
    val styles = listOf(
        Triple(WidgetStyle.LIQUID_GLASS, "Liquid Glass", Icons.Default.Layers),
        Triple(WidgetStyle.WATER_BUBBLE, "Water Bubble", Icons.Default.WaterDrop),
        Triple(WidgetStyle.MINIMAL, "Minimal", Icons.Default.Info)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0x22475569), RoundedCornerShape(16.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        styles.forEach { (style, name, icon) ->
            val isSelected = selectedStyle == style
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) CyanAccent else Color.Transparent)
                    .clickable { onStyleSelected(style) }
                    .padding(vertical = 10.dp)
                    .testTag("style_tab_${style.name.lowercase()}"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) Color.Black else Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = name,
                        color = if (isSelected) Color.Black else Color(0xFF94A3B8),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (checked) CyanAccent else Color(0xFF64748B),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = subtitle,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = CyanAccent,
                uncheckedThumbColor = Color(0xFF94A3B8),
                uncheckedTrackColor = Color(0xFF1E293B)
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}
