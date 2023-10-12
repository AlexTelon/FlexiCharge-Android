package com.flexicharge.bolt.activities

import android.os.Bundle
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.flexicharge.bolt.R

class KlarnaOrderCompletedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_klarna_order_completed)
        val message = intent.getStringExtra("message")
        val textView = findViewById<TextView>(R.id.klarnaOrderCompletedActivity_textView_message)
        textView.text = message

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }
}