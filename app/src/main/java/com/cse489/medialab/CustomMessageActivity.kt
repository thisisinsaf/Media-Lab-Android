package com.cse489.medialab

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/** Activity 2 in the custom-broadcast path: collects plain text from the user. */
class CustomMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_message)

        findViewById<MaterialToolbar>(R.id.customToolbar).setNavigationOnClickListener { finish() }
        val input = findViewById<TextInputEditText>(R.id.messageInput)
        val inputLayout = findViewById<TextInputLayout>(R.id.messageInputLayout)

        findViewById<MaterialButton>(R.id.sendMessageButton).setOnClickListener {
            val text = input.text?.toString()?.trim().orEmpty()
            if (text.isBlank()) {
                inputLayout.error = "Enter a message to send."
                input.requestFocus()
                return@setOnClickListener
            }
            inputLayout.error = null
            startActivity(
                Intent(this, BroadcastResultActivity::class.java)
                    .putExtra(BroadcastResultActivity.EXTRA_MESSAGE, text)
            )
        }
    }
}
