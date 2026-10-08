package com.example.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object BatteryMonitor {

    fun getCurrentBatteryInfo(context: Context): BatteryInfo {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatusIntent = context.registerReceiver(null, filter)
        return parseBatteryIntent(batteryStatusIntent)
    }

    fun batteryFlow(context: Context): Flow<BatteryInfo> = callbackFlow {
        // Emit current state immediately
        trySend(getCurrentBatteryInfo(context))

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                if (intent != null) {
                    val info = parseBatteryIntent(intent)
                    trySend(info)
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_BATTERY_OKAY)
        }

        context.registerReceiver(receiver, filter)

        awaitClose {
            try {
                context.unregisterReceiver(receiver)
            } catch (e: Exception) {
                // Ignore if already unregistered
            }
        }
    }

    private fun parseBatteryIntent(intent: Intent?): BatteryInfo {
        if (intent == null) {
            return BatteryInfo()
        }

        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val percentage = if (level >= 0 && scale > 0) {
            ((level.toFloat() / scale.toFloat()) * 100f).toInt().coerceIn(0, 100)
        } else {
            100
        }

        val rawStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val status = when (rawStatus) {
            BatteryManager.BATTERY_STATUS_CHARGING -> BatteryStatus.CHARGING
            BatteryManager.BATTERY_STATUS_DISCHARGING -> BatteryStatus.DISCHARGING
            BatteryManager.BATTERY_STATUS_FULL -> BatteryStatus.FULL
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> BatteryStatus.NOT_CHARGING
            else -> BatteryStatus.UNKNOWN
        }

        val rawPlugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val plugged = when (rawPlugged) {
            BatteryManager.BATTERY_PLUGGED_AC -> PluggedState.AC
            BatteryManager.BATTERY_PLUGGED_USB -> PluggedState.USB
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> PluggedState.WIRELESS
            else -> PluggedState.UNPLUGGED
        }

        val tempTenths = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 250)
        val tempCelsius = tempTenths / 10.0f

        val rawVoltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 4000)
        val voltageVolts = rawVoltage / 1000.0f

        val rawHealth = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
        val health = when (rawHealth) {
            BatteryManager.BATTERY_HEALTH_GOOD -> BatteryHealth.GOOD
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> BatteryHealth.OVERHEAT
            BatteryManager.BATTERY_HEALTH_DEAD -> BatteryHealth.DEAD
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> BatteryHealth.OVER_VOLTAGE
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> BatteryHealth.UNSPECIFIED_FAILURE
            BatteryManager.BATTERY_HEALTH_COLD -> BatteryHealth.COLD
            else -> BatteryHealth.UNKNOWN
        }

        val technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Li-ion"
        val present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, true)

        return BatteryInfo(
            percentage = percentage,
            status = status,
            plugged = plugged,
            temperatureCelsius = tempCelsius,
            voltageVolts = voltageVolts,
            health = health,
            technology = technology,
            isPresent = present
        )
    }
}
