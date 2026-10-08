package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.BatteryInfo
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CrimsonAccent
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent

@Composable
fun BatteryDiagnosticsCard(
    batteryInfo: BatteryInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0x3338BDF8)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "System Battery Status",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                // Live status pill
                val (pillColor, pillText, pillIcon) = when {
                    batteryInfo.isFullyCharged -> Triple(EmeraldAccent, "Fully Charged", Icons.Default.CheckCircle)
                    batteryInfo.isCharging -> Triple(CyanAccent, "Charging", Icons.Default.Bolt)
                    batteryInfo.isExtreme -> Triple(CrimsonAccent, "Critical (<=5%)", Icons.Default.Warning)
                    batteryInfo.isCritical -> Triple(CrimsonAccent, "Low (<=10%)", Icons.Default.Warning)
                    batteryInfo.isLow -> Triple(AmberAccent, "Low (<=20%)", Icons.Default.Warning)
                    else -> Triple(Color(0xFF94A3B8), "Discharging", Icons.Default.Info)
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(pillColor.copy(alpha = 0.15f))
                        .border(1.dp, pillColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = pillIcon,
                        contentDescription = null,
                        tint = pillColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = pillText,
                        color = pillColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Percentage progress bar
            LinearProgressIndicator(
                progress = { batteryInfo.percentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape),
                color = when {
                    batteryInfo.isCharging -> CyanAccent
                    batteryInfo.isCritical -> CrimsonAccent
                    batteryInfo.isLow -> AmberAccent
                    else -> EmeraldAccent
                },
                trackColor = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 2x2 Telemetry Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TelemetryItem(
                    label = "Power Source",
                    value = batteryInfo.plugged.displayText,
                    icon = Icons.Default.Bolt,
                    tint = CyanAccent,
                    modifier = Modifier.weight(1f)
                )
                TelemetryItem(
                    label = "Temperature",
                    value = "${batteryInfo.temperatureCelsius}°C / ${(batteryInfo.temperatureCelsius * 1.8f + 32).toInt()}°F",
                    icon = Icons.Default.Thermostat,
                    tint = AmberAccent,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TelemetryItem(
                    label = "Voltage",
                    value = "${String.format("%.2f", batteryInfo.voltageVolts)} V",
                    icon = Icons.Default.WifiTethering,
                    tint = EmeraldAccent,
                    modifier = Modifier.weight(1f)
                )
                TelemetryItem(
                    label = "Battery Health",
                    value = "${batteryInfo.health.displayText} (${batteryInfo.technology})",
                    icon = Icons.Default.Info,
                    tint = Color(0xFFA855F7),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TelemetryItem(
    label: String,
    value: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF162032))
            .border(1.dp, Color(0x22475569), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
