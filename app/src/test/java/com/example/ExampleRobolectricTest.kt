package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.BatteryHealth
import com.example.data.BatteryInfo
import com.example.data.BatteryStatus
import com.example.data.PluggedState
import com.example.data.WidgetColorPreset
import com.example.data.WidgetConfig
import com.example.data.WidgetPreferencesRepository
import com.example.data.WidgetSize
import com.example.data.WidgetStyle
import com.example.widget.WidgetBitmapRenderer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("HiTech Battery Widget", appName)
    }

    @Test
    fun `verify battery info states and thresholds`() {
        val chargingInfo = BatteryInfo(
            percentage = 87,
            status = BatteryStatus.CHARGING,
            plugged = PluggedState.AC
        )
        assertTrue(chargingInfo.isCharging)
        assertFalse(chargingInfo.isLow)
        assertEquals("Charging", chargingInfo.statusSummary)

        val fullInfo = BatteryInfo(
            percentage = 100,
            status = BatteryStatus.FULL
        )
        assertTrue(fullInfo.isFullyCharged)
        assertEquals("Fully Charged", fullInfo.statusSummary)

        val lowInfo = BatteryInfo(
            percentage = 18,
            status = BatteryStatus.DISCHARGING
        )
        assertTrue(lowInfo.isLow)
        assertFalse(lowInfo.isCritical)
        assertEquals("Low Battery", lowInfo.statusSummary)

        val criticalInfo = BatteryInfo(
            percentage = 8,
            status = BatteryStatus.DISCHARGING
        )
        assertTrue(criticalInfo.isCritical)
        assertFalse(criticalInfo.isExtreme)

        val extremeInfo = BatteryInfo(
            percentage = 4,
            status = BatteryStatus.DISCHARGING
        )
        assertTrue(extremeInfo.isExtreme)
    }

    @Test
    fun `verify widget preferences persistence`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val widgetId = 101

        val customConfig = WidgetConfig(
            style = WidgetStyle.WATER_BUBBLE,
            colorPreset = WidgetColorPreset.PURPLE,
            showPercentage = true,
            autoColor = true
        )

        WidgetPreferencesRepository.saveWidgetConfig(context, widgetId, customConfig)
        val loadedConfig = WidgetPreferencesRepository.loadWidgetConfig(context, widgetId)

        assertEquals(WidgetStyle.WATER_BUBBLE, loadedConfig.style)
        assertEquals(WidgetColorPreset.PURPLE, loadedConfig.colorPreset)
        assertTrue(loadedConfig.showPercentage)
        assertTrue(loadedConfig.autoColor)
    }

    @Test
    fun `verify widget bitmap renderer creates valid bitmaps`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val batteryInfo = BatteryInfo(percentage = 75, status = BatteryStatus.CHARGING)

        // Liquid Glass
        val glassConfig = WidgetConfig(style = WidgetStyle.LIQUID_GLASS)
        val glassBitmap = WidgetBitmapRenderer.renderWidgetBitmap(context, glassConfig, batteryInfo, WidgetSize.SMALL)
        assertNotNull(glassBitmap)
        assertEquals(400, glassBitmap.width)
        assertEquals(400, glassBitmap.height)

        // Water Bubble
        val bubbleConfig = WidgetConfig(style = WidgetStyle.WATER_BUBBLE)
        val bubbleBitmap = WidgetBitmapRenderer.renderWidgetBitmap(context, bubbleConfig, batteryInfo, WidgetSize.MEDIUM)
        assertNotNull(bubbleBitmap)
        assertEquals(800, bubbleBitmap.width)
        assertEquals(400, bubbleBitmap.height)

        // Minimal
        val minimalConfig = WidgetConfig(style = WidgetStyle.MINIMAL)
        val minimalBitmap = WidgetBitmapRenderer.renderWidgetBitmap(context, minimalConfig, batteryInfo, WidgetSize.LARGE)
        assertNotNull(minimalBitmap)
        assertEquals(800, minimalBitmap.width)
        assertEquals(750, minimalBitmap.height)
    }
}
