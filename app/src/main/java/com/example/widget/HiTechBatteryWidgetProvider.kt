package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.BatteryInfo
import com.example.data.BatteryMonitor
import com.example.data.WidgetPreferencesRepository
import com.example.data.WidgetSize

open class HiTechBatteryWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val batteryInfo = BatteryMonitor.getCurrentBatteryInfo(context)
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId, batteryInfo)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        val batteryInfo = BatteryMonitor.getCurrentBatteryInfo(context)
        updateWidget(context, appWidgetManager, appWidgetId, batteryInfo, newOptions)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            WidgetPreferencesRepository.deleteWidgetConfig(context, appWidgetId)
        }
    }

    companion object {
        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val batteryInfo = BatteryMonitor.getCurrentBatteryInfo(context)

            val providers = listOf(
                HiTechBatteryWidgetProvider::class.java,
                HiTechBatteryWidgetSmallProvider::class.java,
                HiTechBatteryWidgetMediumProvider::class.java,
                HiTechBatteryWidgetLargeProvider::class.java
            )

            for (providerClass in providers) {
                val componentName = ComponentName(context, providerClass)
                val ids = appWidgetManager.getAppWidgetIds(componentName)
                for (id in ids) {
                    updateWidget(context, appWidgetManager, id, batteryInfo)
                }
            }
        }

        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            batteryInfo: BatteryInfo = BatteryMonitor.getCurrentBatteryInfo(context),
            options: Bundle? = null
        ) {
            val config = WidgetPreferencesRepository.loadWidgetConfig(context, appWidgetId)

            val widgetOptions = options ?: appWidgetManager.getAppWidgetOptions(appWidgetId)
            val minWidthDp = widgetOptions?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 0) ?: 0
            val minHeightDp = widgetOptions?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0) ?: 0

            val widgetSize = when {
                minHeightDp > 160 && minWidthDp > 160 -> WidgetSize.LARGE
                minWidthDp > 160 -> WidgetSize.MEDIUM
                minHeightDp > 160 -> WidgetSize.LARGE
                else -> WidgetSize.SMALL
            }

            val density = context.resources.displayMetrics.density
            val targetW = if (minWidthDp > 0) (minWidthDp * density * 1.5f).toInt() else 0
            val targetH = if (minHeightDp > 0) (minHeightDp * density * 1.5f).toInt() else 0

            val bitmap = WidgetBitmapRenderer.renderWidgetBitmap(
                context = context,
                config = config,
                batteryInfo = batteryInfo,
                size = widgetSize,
                targetWidth = targetW,
                targetHeight = targetH
            )

            val views = RemoteViews(context.packageName, R.layout.widget_hitech_battery)
            views.setImageViewBitmap(R.id.widget_image, bitmap)

            // Tap widget -> Open MainActivity
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
