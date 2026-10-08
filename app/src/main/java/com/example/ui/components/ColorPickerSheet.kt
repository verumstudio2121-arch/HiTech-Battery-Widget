package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WidgetColorPreset

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorSelectorRow(
    selectedPreset: WidgetColorPreset,
    customHex: Int,
    onSelectPreset: (WidgetColorPreset) -> Unit,
    onCustomColorChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCustomDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Accent Color",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            WidgetColorPreset.values().forEach { preset ->
                val isSelected = selectedPreset == preset
                val color = if (preset == WidgetColorPreset.CUSTOM) {
                    Color(customHex)
                } else {
                    Color(preset.primaryHex)
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) Color.White else Color(0x44FFFFFF),
                            shape = CircleShape
                        )
                        .clickable {
                            if (preset == WidgetColorPreset.CUSTOM) {
                                onSelectPreset(preset)
                                showCustomDialog = true
                            } else {
                                onSelectPreset(preset)
                            }
                        }
                        .testTag("color_preset_${preset.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    if (preset == WidgetColorPreset.CUSTOM && !isSelected) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Custom color",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = if (preset == WidgetColorPreset.WHITE) Color.Black else Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }

    if (showCustomDialog) {
        CustomColorPickerDialog(
            initialColor = Color(customHex),
            onDismiss = { showCustomDialog = false },
            onColorSelected = { newColor ->
                onCustomColorChanged(newColor)
                onSelectPreset(WidgetColorPreset.CUSTOM)
                showCustomDialog = false
            }
        )
    }
}

@Composable
fun CustomColorPickerDialog(
    initialColor: Color,
    onDismiss: () -> Unit,
    onColorSelected: (Int) -> Unit
) {
    var red by remember { mutableFloatStateOf(initialColor.red) }
    var green by remember { mutableFloatStateOf(initialColor.green) }
    var blue by remember { mutableFloatStateOf(initialColor.blue) }

    val currentColor = Color(red, green, blue)
    val hexCode = String.format("#%02X%02X%02X", (red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt())

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Custom Color Picker",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                // Color preview box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(currentColor)
                        .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = Color(0x88000000),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = hexCode,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Red Slider
                Text("Red: ${(red * 255).toInt()}", color = Color(0xFFEF4444), fontSize = 13.sp)
                Slider(
                    value = red,
                    onValueChange = { red = it },
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFEF4444),
                        activeTrackColor = Color(0xFFEF4444)
                    )
                )

                // Green Slider
                Text("Green: ${(green * 255).toInt()}", color = Color(0xFF22C55E), fontSize = 13.sp)
                Slider(
                    value = green,
                    onValueChange = { green = it },
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF22C55E),
                        activeTrackColor = Color(0xFF22C55E)
                    )
                )

                // Blue Slider
                Text("Blue: ${(blue * 255).toInt()}", color = Color(0xFF38BDF8), fontSize = 13.sp)
                Slider(
                    value = blue,
                    onValueChange = { blue = it },
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF38BDF8),
                        activeTrackColor = Color(0xFF38BDF8)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val argb = android.graphics.Color.rgb(
                        (red * 255).toInt(),
                        (green * 255).toInt(),
                        (blue * 255).toInt()
                    )
                    onColorSelected(argb)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("confirm_custom_color_button")
            ) {
                Text("Select Color", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        containerColor = Color(0xFF0F172A),
        shape = RoundedCornerShape(24.dp)
    )
}
