package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.widget.HiTechBatteryWidgetProvider

class PowerConnectionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action
        if (action == Intent.ACTION_POWER_CONNECTED ||
            action == Intent.ACTION_POWER_DISCONNECTED ||
            action == Intent.ACTION_BATTERY_LOW ||
            action == Intent.ACTION_BATTERY_OKAY
        ) {
            HiTechBatteryWidgetProvider.updateAllWidgets(context)
        }
    }
}
