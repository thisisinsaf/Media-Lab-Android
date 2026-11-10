package com.cse489.medialab

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

/** Receives Android's sticky ACTION_BATTERY_CHANGED system broadcast. */
class BatteryStatusActivity : AppCompatActivity() {
    private lateinit var percentageText: TextView
    private lateinit var progress: ProgressBar
    private lateinit var chargingText: TextView
    private lateinit var healthText: TextView
    private var registered = false

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_BATTERY_CHANGED) renderBattery(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battery_status)
        findViewById<MaterialToolbar>(R.id.batteryToolbar).setNavigationOnClickListener { finish() }
        percentageText = findViewById(R.id.batteryPercentage)
        progress = findViewById(R.id.batteryProgress)
        chargingText = findViewById(R.id.batteryCharging)
        healthText = findViewById(R.id.batteryHealth)
        findViewById<MaterialButton>(R.id.batteryRefreshButton).setOnClickListener { refreshSnapshot() }
    }

    override fun onStart() {
        super.onStart()
        // Exclusively a system broadcast: no RECEIVER_EXPORTED flag is required.
        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        registered = true
    }

    private fun refreshSnapshot() {
        // Battery changed is sticky; asking for its last snapshot causes no new broadcast.
        registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))?.let(::renderBattery)
    }

    private fun renderBattery(battery: Intent) {
        val level = battery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = battery.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val percentage = if (level >= 0 && scale > 0) (level * 100 / scale) else -1
        percentageText.text = if (percentage >= 0) "$percentage%" else "--%"
        progress.progress = percentage.coerceAtLeast(0)

        val status = battery.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        chargingText.text = when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "⚡ Charging"
            BatteryManager.BATTERY_STATUS_FULL -> "✓ Fully charged"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "Running on battery"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Connected · Not charging"
            else -> "Battery status unknown"
        }
        val plugged = battery.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
        val source = when (plugged) {
            BatteryManager.BATTERY_PLUGGED_AC -> "AC charger"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB power"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless charger"
            else -> "Battery power"
        }
        healthText.text = "Power source: $source"
    }

    override fun onStop() {
        if (registered) {
            unregisterReceiver(batteryReceiver)
            registered = false
        }
        super.onStop()
    }
}
