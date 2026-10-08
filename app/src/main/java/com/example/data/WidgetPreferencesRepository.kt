package com.example.data

import android.content.Context
import android.content.SharedPreferences

object WidgetPreferencesRepository {

    private const val PREFS_NAME = "hitech_battery_widget_prefs"
    private const val KEY_DEFAULT_STYLE = "default_style"
    private const val KEY_DEFAULT_COLOR = "default_color"
    private const val KEY_DEFAULT_CUSTOM_HEX = "default_custom_hex"
    private const val KEY_DEFAULT_SHOW_PERCENT = "default_show_percent"
    private const val KEY_DEFAULT_SHOW_CHARGING_ICON = "default_show_charging_icon"
    private const val KEY_DEFAULT_SHOW_STATUS_TEXT = "default_show_status_text"
    private const val KEY_DEFAULT_AUTO_COLOR = "default_auto_color"
    private const val KEY_DEFAULT_ANIMATIONS = "default_animations"
    private const val KEY_ACTIVE_WIDGET_IDS = "active_widget_ids"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun getWidgetPrefs(context: Context, appWidgetId: Int): SharedPreferences {
        return context.getSharedPreferences("hitech_widget_$appWidgetId", Context.MODE_PRIVATE)
    }

    fun loadDefaultConfig(context: Context): WidgetConfig {
        val prefs = getPrefs(context)
        val styleName = prefs.getString(KEY_DEFAULT_STYLE, WidgetStyle.LIQUID_GLASS.name)
        val colorName = prefs.getString(KEY_DEFAULT_COLOR, WidgetColorPreset.CYAN.name)
        val customHex = prefs.getInt(KEY_DEFAULT_CUSTOM_HEX, 0xFF00E5FF.toInt())
        val showPercent = prefs.getBoolean(KEY_DEFAULT_SHOW_PERCENT, true)
        val showCharging = prefs.getBoolean(KEY_DEFAULT_SHOW_CHARGING_ICON, true)
        val showStatus = prefs.getBoolean(KEY_DEFAULT_SHOW_STATUS_TEXT, true)
        val autoColor = prefs.getBoolean(KEY_DEFAULT_AUTO_COLOR, false)
        val animations = prefs.getBoolean(KEY_DEFAULT_ANIMATIONS, true)

        return WidgetConfig(
            style = runCatching { WidgetStyle.valueOf(styleName ?: "") }.getOrDefault(WidgetStyle.LIQUID_GLASS),
            colorPreset = runCatching { WidgetColorPreset.valueOf(colorName ?: "") }.getOrDefault(WidgetColorPreset.CYAN),
            customColorHex = customHex,
            showPercentage = showPercent,
            showChargingIcon = showCharging,
            showStatusText = showStatus,
            autoColor = autoColor,
            animationsEnabled = animations
        )
    }

    fun saveDefaultConfig(context: Context, config: WidgetConfig) {
        getPrefs(context).edit().apply {
            putString(KEY_DEFAULT_STYLE, config.style.name)
            putString(KEY_DEFAULT_COLOR, config.colorPreset.name)
            putInt(KEY_DEFAULT_CUSTOM_HEX, config.customColorHex)
            putBoolean(KEY_DEFAULT_SHOW_PERCENT, config.showPercentage)
            putBoolean(KEY_DEFAULT_SHOW_CHARGING_ICON, config.showChargingIcon)
            putBoolean(KEY_DEFAULT_SHOW_STATUS_TEXT, config.showStatusText)
            putBoolean(KEY_DEFAULT_AUTO_COLOR, config.autoColor)
            putBoolean(KEY_DEFAULT_ANIMATIONS, config.animationsEnabled)
            apply()
        }
    }

    fun loadWidgetConfig(context: Context, appWidgetId: Int): WidgetConfig {
        val prefs = getWidgetPrefs(context, appWidgetId)
        if (!prefs.contains("configured")) {
            // Fall back to default
            return loadDefaultConfig(context)
        }

        val styleName = prefs.getString("style", WidgetStyle.LIQUID_GLASS.name)
        val colorName = prefs.getString("color", WidgetColorPreset.CYAN.name)
        val customHex = prefs.getInt("custom_hex", 0xFF00E5FF.toInt())
        val showPercent = prefs.getBoolean("show_percent", true)
        val showCharging = prefs.getBoolean("show_charging", true)
        val showStatus = prefs.getBoolean("show_status", true)
        val autoColor = prefs.getBoolean("auto_color", false)
        val animations = prefs.getBoolean("animations", true)

        return WidgetConfig(
            style = runCatching { WidgetStyle.valueOf(styleName ?: "") }.getOrDefault(WidgetStyle.LIQUID_GLASS),
            colorPreset = runCatching { WidgetColorPreset.valueOf(colorName ?: "") }.getOrDefault(WidgetColorPreset.CYAN),
            customColorHex = customHex,
            showPercentage = showPercent,
            showChargingIcon = showCharging,
            showStatusText = showStatus,
            autoColor = autoColor,
            animationsEnabled = animations
        )
    }

    fun saveWidgetConfig(context: Context, appWidgetId: Int, config: WidgetConfig) {
        getWidgetPrefs(context, appWidgetId).edit().apply {
            putBoolean("configured", true)
            putString("style", config.style.name)
            putString("color", config.colorPreset.name)
            putInt("custom_hex", config.customColorHex)
            putBoolean("show_percent", config.showPercentage)
            putBoolean("show_charging", config.showChargingIcon)
            putBoolean("show_status", config.showStatusText)
            putBoolean("auto_color", config.autoColor)
            putBoolean("animations", config.animationsEnabled)
            apply()
        }

        // Track active widget ID
        val activeIds = getActiveWidgetIds(context).toMutableSet()
        activeIds.add(appWidgetId.toString())
        getPrefs(context).edit().putStringSet(KEY_ACTIVE_WIDGET_IDS, activeIds).apply()
    }

    fun deleteWidgetConfig(context: Context, appWidgetId: Int) {
        getWidgetPrefs(context, appWidgetId).edit().clear().apply()
        val activeIds = getActiveWidgetIds(context).toMutableSet()
        activeIds.remove(appWidgetId.toString())
        getPrefs(context).edit().putStringSet(KEY_ACTIVE_WIDGET_IDS, activeIds).apply()
    }

    fun getActiveWidgetIds(context: Context): Set<String> {
        return getPrefs(context).getStringSet(KEY_ACTIVE_WIDGET_IDS, emptySet()) ?: emptySet()
    }
}
