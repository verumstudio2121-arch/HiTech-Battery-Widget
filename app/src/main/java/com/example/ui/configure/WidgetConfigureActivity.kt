package com.example.ui.configure

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BatteryInfo
import com.example.data.BatteryMonitor
import com.example.data.WidgetColorPreset
import com.example.data.WidgetConfig
import com.example.data.WidgetPreferencesRepository
import com.example.data.WidgetStyle
import com.example.ui.components.ColorSelectorRow
import com.example.ui.components.WidgetInteractivePreview
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.HiTechTheme
import com.example.widget.HiTechBatteryWidgetProvider

class WidgetConfigureActivity : ComponentActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Set the result to CANCELED. This will immediately cancel the widget addition
        // if the user backs out of the activity without clicking Save.
        setResult(RESULT_CANCELED)

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        // If this activity was started without a widget ID, finish with error
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            // If opened directly from app, check if we have any active widget or allow configuring default
            appWidgetId = 0
        }

        val initialConfig = WidgetPreferencesRepository.loadWidgetConfig(this, appWidgetId)

        setContent {
            HiTechTheme {
                val batteryInfo by BatteryMonitor.batteryFlow(this)
                    .collectAsState(initial = BatteryMonitor.getCurrentBatteryInfo(this))

                WidgetConfigureScreen(
                    appWidgetId = appWidgetId,
                    initialConfig = initialConfig,
                    batteryInfo = batteryInfo,
                    onSave = { updatedConfig ->
                        // Save per-widget configuration
                        WidgetPreferencesRepository.saveWidgetConfig(this, appWidgetId, updatedConfig)

                        // Push widget update
                        val appWidgetManager = AppWidgetManager.getInstance(this)
                        HiTechBatteryWidgetProvider.updateWidget(
                            context = this,
                            appWidgetManager = appWidgetManager,
                            appWidgetId = appWidgetId,
                            batteryInfo = batteryInfo
                        )

                        // Return RESULT_OK to launcher
                        val resultValue = Intent().apply {
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                        }
                        setResult(RESULT_OK, resultValue)
                        finish()
                    },
                    onCancel = {
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun WidgetConfigureScreen(
    appWidgetId: Int,
    initialConfig: WidgetConfig,
    batteryInfo: BatteryInfo,
    onSave: (WidgetConfig) -> Unit,
    onCancel: () -> Unit
) {
    var config by remember { mutableStateOf(initialConfig) }
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = Color(0xFF080C16)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Configure Widget",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (appWidgetId > 0) "Widget ID #$appWidgetId" else "Default Settings",
                        fontSize = 13.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                IconButton(onClick = onCancel) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Live Preview of chosen configuration
            WidgetInteractivePreview(
                config = config,
                batteryInfo = batteryInfo,
                modifier = Modifier.testTag("configure_live_preview")
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Style Selector
            Text(
                text = "Style",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            ConfigureStyleSelector(
                selectedStyle = config.style,
                onSelect = { config = config.copy(style = it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Color Selector
            ColorSelectorRow(
                selectedPreset = config.colorPreset,
                customHex = config.customColorHex,
                onSelectPreset = { config = config.copy(colorPreset = it) },
                onCustomColorChanged = { hex ->
                    config = config.copy(customColorHex = hex, colorPreset = WidgetColorPreset.CUSTOM)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Options
            Text(
                text = "Options",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ConfigureToggle(
                        title = "Auto Color Mode",
                        subtitle = "Adaptive color based on battery level & charging",
                        icon = Icons.Default.AutoAwesome,
                        checked = config.autoColor,
                        onCheckedChange = { config = config.copy(autoColor = it) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ConfigureToggle(
                        title = "Show Battery %",
                        subtitle = "Numeric battery level",
                        icon = Icons.Default.Percent,
                        checked = config.showPercentage,
                        onCheckedChange = { config = config.copy(showPercentage = it) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ConfigureToggle(
                        title = "Show Charging ⚡ Icon",
                        subtitle = "Lightning bolt when plugged in",
                        icon = Icons.Default.Bolt,
                        checked = config.showChargingIcon,
                        onCheckedChange = { config = config.copy(showChargingIcon = it) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ConfigureToggle(
                        title = "Show Status Text",
                        subtitle = "'Charging', 'Fully Charged', etc.",
                        icon = Icons.Default.TextFields,
                        checked = config.showStatusText,
                        onCheckedChange = { config = config.copy(showStatusText = it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Save / Apply Button
            Button(
                onClick = { onSave(config) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("apply_widget_config_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color(0xFF030712))
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Apply to Widget", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ConfigureStyleSelector(
    selectedStyle: WidgetStyle,
    onSelect: (WidgetStyle) -> Unit
) {
    val styles = listOf(
        Pair(WidgetStyle.LIQUID_GLASS, "Liquid Glass"),
        Pair(WidgetStyle.WATER_BUBBLE, "Water Bubble"),
        Pair(WidgetStyle.MINIMAL, "Minimal")
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0x22475569), RoundedCornerShape(14.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        styles.forEach { (style, name) ->
            val isSelected = selectedStyle == style
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) CyanAccent else Color.Transparent)
                    .clickable { onSelect(style) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
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

@Composable
private fun ConfigureToggle(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
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
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (checked) CyanAccent else Color(0xFF64748B),
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text(text = subtitle, color = Color(0xFF94A3B8), fontSize = 11.sp)
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
            )
        )
    }
}
