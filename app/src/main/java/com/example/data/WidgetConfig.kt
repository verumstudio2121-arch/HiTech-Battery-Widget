package com.example.data

import android.graphics.Color

enum class WidgetStyle(val displayName: String, val description: String) {
    LIQUID_GLASS("Liquid Glass", "Translucent glass panel with smooth liquid level and specular highlights"),
    WATER_BUBBLE("Water Bubble", "Spherical glass bubble with dynamic liquid wave and floating micro-bubbles"),
    MINIMAL("Minimal", "Clean, high-readability typography with sleek progress indicator")
}

enum class WidgetColorPreset(val displayName: String, val primaryHex: Int, val secondaryHex: Int, val glowHex: Int) {
    BLUE("Blue", 0xFF0284C7.toInt(), 0xFF38BDF8.toInt(), 0x6638BDF8),
    PURPLE("Purple", 0xFF7C3AED.toInt(), 0xFFA855F7.toInt(), 0x66A855F7),
    GREEN("Green", 0xFF16A34A.toInt(), 0xFF22C55E.toInt(), 0x6622C55E),
    CYAN("Cyan", 0xFF0891B2.toInt(), 0xFF06B6D4.toInt(), 0x6606B6D4),
    PINK("Pink", 0xFFDB2777.toInt(), 0xFFEC4899.toInt(), 0x66EC4899),
    ORANGE("Orange", 0xFFEA580C.toInt(), 0xFFF97316.toInt(), 0x66F97316),
    RED("Red", 0xFFDC2626.toInt(), 0xFFEF4444.toInt(), 0x66EF4444),
    WHITE("White", 0xFF94A3B8.toInt(), 0xFFF8FAFC.toInt(), 0x66F8FAFC),
    CUSTOM("Custom", 0xFF00E5FF.toInt(), 0xFF80D8FF.toInt(), 0x6600E5FF);
}

enum class WidgetSize {
    SMALL,   // ~1x1 or 2x2
    MEDIUM,  // ~4x2 (horizontal)
    LARGE    // ~4x3 or 4x4 (dashboard)
}

data class ResolvedColors(
    val primary: Int,
    val secondary: Int,
    val glow: Int,
    val isWarning: Boolean = false
)

data class WidgetConfig(
    val style: WidgetStyle = WidgetStyle.LIQUID_GLASS,
    val colorPreset: WidgetColorPreset = WidgetColorPreset.CYAN,
    val customColorHex: Int = 0xFF00E5FF.toInt(),
    val showPercentage: Boolean = true,
    val showChargingIcon: Boolean = true,
    val showStatusText: Boolean = true,
    val autoColor: Boolean = false,
    val animationsEnabled: Boolean = true,
    val highDetailGlass: Boolean = true
) {
    fun resolveColors(batteryInfo: BatteryInfo): ResolvedColors {
        if (autoColor) {
            return when {
                batteryInfo.isCharging -> {
                    // Electric Emerald / Bright Green for charging
                    ResolvedColors(
                        primary = 0xFF059669.toInt(),
                        secondary = 0xFF10B981.toInt(),
                        glow = 0x8810B981.toInt()
                    )
                }
                batteryInfo.isExtreme -> {
                    // Critical Red < 5%
                    ResolvedColors(
                        primary = 0xFF991B1B.toInt(),
                        secondary = 0xFFDC2626.toInt(),
                        glow = 0xAAEF4444.toInt(),
                        isWarning = true
                    )
                }
                batteryInfo.isCritical -> {
                    // Critical Red <= 10%
                    ResolvedColors(
                        primary = 0xFFB91C1C.toInt(),
                        secondary = 0xFFEF4444.toInt(),
                        glow = 0x88EF4444.toInt(),
                        isWarning = true
                    )
                }
                batteryInfo.isLow -> {
                    // Amber/Red Warning <= 20%
                    ResolvedColors(
                        primary = 0xFFC2410C.toInt(),
                        secondary = 0xFFF97316.toInt(),
                        glow = 0x77F97316.toInt(),
                        isWarning = true
                    )
                }
                batteryInfo.percentage <= 50 -> {
                    // Warm amber/gold 20-50%
                    ResolvedColors(
                        primary = 0xFFD97706.toInt(),
                        secondary = 0xFFFBBF24.toInt(),
                        glow = 0x66FBBF24.toInt()
                    )
                }
                else -> {
                    // Normal > 50%
                    getPresetColors()
                }
            }
        }

        // Even in manual mode, extreme low battery (<10%) gives a slight warning badge if desired
        return getPresetColors()
    }

    private fun getPresetColors(): ResolvedColors {
        return if (colorPreset == WidgetColorPreset.CUSTOM) {
            val primary = customColorHex
            // Calculate a lighter secondary shade
            val red = Color.red(primary)
            val green = Color.green(primary)
            val blue = Color.blue(primary)
            val secondary = Color.rgb(
                (red + 40).coerceAtMost(255),
                (green + 40).coerceAtMost(255),
                (blue + 40).coerceAtMost(255)
            )
            val glow = Color.argb(100, red, green, blue)
            ResolvedColors(primary, secondary, glow)
        } else {
            ResolvedColors(colorPreset.primaryHex, colorPreset.secondaryHex, colorPreset.glowHex)
        }
    }
}
