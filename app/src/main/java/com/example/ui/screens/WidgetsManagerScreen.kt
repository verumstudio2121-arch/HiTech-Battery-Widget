package com.example.ui.screens

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BatteryInfo
import com.example.data.WidgetConfig
import com.example.data.WidgetPreferencesRepository
import com.example.ui.configure.WidgetConfigureActivity
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.widget.HiTechBatteryWidgetLargeProvider
import com.example.widget.HiTechBatteryWidgetMediumProvider
import com.example.widget.HiTechBatteryWidgetProvider
import com.example.widget.HiTechBatteryWidgetSmallProvider

@Composable
fun WidgetsManagerScreen(
    batteryInfo: BatteryInfo,
    onConfigureWidget: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var refreshTrigger by remember { mutableStateOf(0) }

    // Fetch active widgets
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val providers = listOf(
        HiTechBatteryWidgetProvider::class.java,
        HiTechBatteryWidgetSmallProvider::class.java,
        HiTechBatteryWidgetMediumProvider::class.java,
        HiTechBatteryWidgetLargeProvider::class.java
    )
    val placedWidgetIds = remember(refreshTrigger) {
        val list = mutableListOf<Int>()
        for (p in providers) {
            val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, p))
            list.addAll(ids.toList())
        }
        list.distinct()
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "My Widgets",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Individual Widget Management",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            // Sync all widgets button
            IconButton(
                onClick = {
                    HiTechBatteryWidgetProvider.updateAllWidgets(context)
                    refreshTrigger++
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFF0F172A))
                    .border(1.dp, Color(0x33475569), CircleShape)
                    .testTag("refresh_all_widgets_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh all widgets",
                    tint = CyanAccent
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Multi-Widget Explainer Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0x3338BDF8)))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyanAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Layers, contentDescription = null, tint = CyanAccent)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "Independent Widget Configurations",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Each widget on your home screen can have its own unique style, color, and size preset.",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Active Home Screen Widgets (${placedWidgetIds.size})",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (placedWidgetIds.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, Color(0x22475569), RoundedCornerShape(20.dp))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Widgets,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Widgets Placed Yet",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Long press your home screen, tap 'Widgets', and select HiTech Battery to add one.",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                placedWidgetIds.forEachIndexed { index, widgetId ->
                    val config = WidgetPreferencesRepository.loadWidgetConfig(context, widgetId)
                    WidgetInstanceCard(
                        index = index + 1,
                        widgetId = widgetId,
                        config = config,
                        onEdit = {
                            val intent = Intent(context, WidgetConfigureActivity::class.java).apply {
                                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                            }
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Widget Styles Reference Cards
        Text(
            text = "Available Widget Sizes & Presets",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        PresetSizeCard(
            title = "HiTech Battery (Small)",
            dimension = "2x2 or 1x1 cells",
            description = "Compact spherical or circular badge. Perfect for tight grids and minimal screens."
        )
        Spacer(modifier = Modifier.height(10.dp))
        PresetSizeCard(
            title = "HiTech Battery (Medium)",
            dimension = "4x2 horizontal banner",
            description = "Wide banner displaying large percentage, status, and telemetry highlights."
        )
        Spacer(modifier = Modifier.height(10.dp))
        PresetSizeCard(
            title = "HiTech Battery (Large)",
            dimension = "4x3 or 4x4 dashboard",
            description = "Full hardware dashboard including temperature, voltage, health, and battery level."
        )

        Spacer(modifier = Modifier.height(36.dp))
    }
}

@Composable
private fun WidgetInstanceCard(
    index: Int,
    widgetId: Int,
    config: WidgetConfig,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0x33475569)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(config.colorPreset.primaryHex)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$index",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "Widget #$widgetId",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "${config.style.displayName} • ${config.colorPreset.displayName}",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }
            }

            Button(
                onClick = onEdit,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Edit", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun PresetSizeCard(
    title: String,
    dimension: String,
    description: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0x22475569), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyanAccent.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = dimension, color = CyanAccent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = description, color = Color(0xFF94A3B8), fontSize = 12.sp)
        }
    }
}
