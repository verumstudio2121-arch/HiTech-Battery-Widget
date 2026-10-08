package com.example.data

import android.os.BatteryManager

enum class BatteryStatus {
    CHARGING,
    DISCHARGING,
    FULL,
    NOT_CHARGING,
    UNKNOWN;

    val displayText: String
        get() = when (this) {
            CHARGING -> "Charging"
            DISCHARGING -> "Discharging"
            FULL -> "Fully Charged"
            NOT_CHARGING -> "Not Charging"
            UNKNOWN -> "Unknown"
        }
}

enum class PluggedState {
    AC,
    USB,
    WIRELESS,
    UNPLUGGED;

    val displayText: String
        get() = when (this) {
            AC -> "AC Charger"
            USB -> "USB Cable"
            WIRELESS -> "Wireless"
            UNPLUGGED -> "On Battery"
        }
}

enum class BatteryHealth {
    GOOD,
    OVERHEAT,
    DEAD,
    OVER_VOLTAGE,
    UNSPECIFIED_FAILURE,
    COLD,
    UNKNOWN;

    val displayText: String
        get() = when (this) {
            GOOD -> "Good"
            OVERHEAT -> "Overheat"
            DEAD -> "Dead"
            OVER_VOLTAGE -> "Over Voltage"
            UNSPECIFIED_FAILURE -> "Failure"
            COLD -> "Cold"
            UNKNOWN -> "Normal"
        }
}

data class BatteryInfo(
    val percentage: Int = 100,
    val status: BatteryStatus = BatteryStatus.NOT_CHARGING,
    val plugged: PluggedState = PluggedState.UNPLUGGED,
    val temperatureCelsius: Float = 25.0f,
    val voltageVolts: Float = 4.0f,
    val health: BatteryHealth = BatteryHealth.GOOD,
    val technology: String = "Li-ion",
    val isPresent: Boolean = true
) {
    val isCharging: Boolean
        get() = status == BatteryStatus.CHARGING

    val isFullyCharged: Boolean
        get() = status == BatteryStatus.FULL || (percentage >= 100 && isCharging)

    val isLow: Boolean
        get() = percentage <= 20

    val isCritical: Boolean
        get() = percentage <= 10

    val isExtreme: Boolean
        get() = percentage <= 5

    val statusSummary: String
        get() = when {
            isFullyCharged -> "Fully Charged"
            isCharging -> "Charging"
            isExtreme -> "Critical Battery!"
            isCritical -> "Very Low Battery"
            isLow -> "Low Battery"
            else -> "Discharging"
        }
}
