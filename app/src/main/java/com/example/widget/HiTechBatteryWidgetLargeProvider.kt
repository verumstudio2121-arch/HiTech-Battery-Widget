package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.BatteryMonitor
import com.example.data.WidgetPreferencesRepository
import com.example.data.WidgetSize

class HiTechBatteryWidgetLargeProvider : HiTechBatteryWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val batteryInfo = BatteryMonitor.getCurrentBatteryInfo(context)
        for (appWidgetId in appWidgetIds) {
            val config = WidgetPreferencesRepository.loadWidgetConfig(context, appWidgetId)
            val bitmap = WidgetBitmapRenderer.renderWidgetBitmap(
                context = context,
                config = config,
                batteryInfo = batteryInfo,
                size = WidgetSize.LARGE
            )
            val views = RemoteViews(context.packageName, R.layout.widget_hitech_battery)
            views.setImageViewBitmap(R.id.widget_image, bitmap)

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
