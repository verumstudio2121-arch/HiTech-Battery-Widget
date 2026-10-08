package com.example.ui.screens

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
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WidgetConfig
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CrimsonAccent
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent

@Composable
fun SettingsScreen(
    currentConfig: WidgetConfig,
    onConfigChanged: (WidgetConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Text(
            text = "Performance, Battery & About",
            fontSize = 13.sp,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Low Battery Thresholds Guide
        Text(
            text = "Low Battery Indicator Stages",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0x22475569)))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                ThresholdRow(
                    level = "20%",
                    title = "Low Battery State",
                    description = "Widget switches accent to warm amber and signals gentle battery attention.",
                    tint = AmberAccent,
                    icon = Icons.Default.BatteryAlert
                )
                ThresholdRow(
                    level = "10%",
                    title = "Critical Battery Warning",
                    description = "Widget turns crimson red with prominent warning glow.",
                    tint = CrimsonAccent,
                    icon = Icons.Default.Warning
                )
                ThresholdRow(
                    level = "5%",
                    title = "Extreme Emergency State",
                    description = "Intensified warning styling indicating imminent shutdown.",
                    tint = Color(0xFF991B1B),
                    icon = Icons.Default.Warning
                )
                ThresholdRow(
                    level = "100%",
                    title = "Fully Charged Completion",
                    description = "Signals '✓ Fully Charged' with emerald green completion glow.",
                    tint = EmeraldAccent,
                    icon = Icons.Default.CheckCircle
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Section: Battery Conservation & Architecture
        Text(
            text = "Battery Efficiency & Performance",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0x22475569)))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                FeatureRow(
                    icon = Icons.Default.Eco,
                    tint = EmeraldAccent,
                    title = "0% Background Drain",
                    description = "Does not run wake-locks or high-FPS polling services. Operates 100% on system events."
                )
                FeatureRow(
                    icon = Icons.Default.Speed,
                    tint = CyanAccent,
                    title = "Instant Hardware Broadcasts",
                    description = "Updates instantaneously on charger plug/unplug and battery percentage shifts."
                )
                FeatureRow(
                    icon = Icons.Default.Security,
                    tint = Color(0xFFA855F7),
                    title = "Complete Privacy & Offline",
                    description = "Zero internet permissions requested. All computations stay entirely on your device."
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Section: About
        Text(
            text = "About HiTech Battery",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0x22475569)))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "HiTech Battery Widget",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Version 1.0.0 • Made by HiTech",
                    fontSize = 13.sp,
                    color = CyanAccent,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "A native Android home-screen widget crafted with custom Canvas graphics, liquid glass aesthetics, and real hardware battery telemetry.",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun ThresholdRow(
    level: String,
    title: String,
    description: String,
    tint: Color,
    icon: ImageVector
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(tint.copy(alpha = 0.15f))
                .border(1.dp, tint.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = level,
                color = tint,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = description, color = Color(0xFF94A3B8), fontSize = 12.sp)
        }
    }
}

@Composable
private fun FeatureRow(
    icon: ImageVector,
    tint: Color,
    title: String,
    description: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(tint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = description, color = Color(0xFF94A3B8), fontSize = 12.sp)
        }
    }
}
