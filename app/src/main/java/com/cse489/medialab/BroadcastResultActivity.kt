package com.cse489.medialab

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

/**
 * Activity 3: registers a real receiver first, then sends an app-scoped broadcast.
 * Text is displayed ONLY from onReceive(), not from the launching Intent.
 */
class BroadcastResultActivity : AppCompatActivity() {
    private lateinit var receivedText: TextView
    private lateinit var stateText: TextView
    private var registered = false

    private val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != ACTION_CUSTOM_MESSAGE) return
            val text = intent.getStringExtra(EXTRA_MESSAGE).orEmpty()
            receivedText.text = text
            stateText.text = "✓ Broadcast received successfully by the dynamic receiver."
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_broadcast_result)
        findViewById<MaterialToolbar>(R.id.resultToolbar).setNavigationOnClickListener { finish() }
        receivedText = findViewById(R.id.receivedMessageText)
        stateText = findViewById(R.id.receiverState)

        findViewById<MaterialButton>(R.id.rebroadcastButton).setOnClickListener { sendToReceiver() }
        findViewById<MaterialButton>(R.id.editMessageButton).setOnClickListener { finish() }
    }

    override fun onStart() {
        super.onStart()
        ContextCompat.registerReceiver(
            this,
            messageReceiver,
            IntentFilter(ACTION_CUSTOM_MESSAGE),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        registered = true
        stateText.text = "Receiver registered. Dispatching custom broadcast…"
        receivedText.post { if (registered) sendToReceiver() }
    }

    private fun sendToReceiver() {
        if (!registered) return
        val text = intent.getStringExtra(EXTRA_MESSAGE).orEmpty()
        // setPackage restricts delivery to this application; no manifest-exported receiver.
        sendBroadcast(
            Intent(ACTION_CUSTOM_MESSAGE)
                .setPackage(packageName)
                .putExtra(EXTRA_MESSAGE, text)
        )
    }

    override fun onStop() {
        if (registered) {
            unregisterReceiver(messageReceiver)
            registered = false
        }
        super.onStop()
    }

    companion object {
        const val ACTION_CUSTOM_MESSAGE = "com.cse489.medialab.ACTION_CUSTOM_MESSAGE"
        const val EXTRA_MESSAGE = "com.cse489.medialab.EXTRA_MESSAGE"
    }
}
