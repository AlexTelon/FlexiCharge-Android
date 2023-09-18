package com.flexicharge.bolt.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.flexicharge.bolt.R

class KlarnaOrderCompletedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_klarna_order_completed)
        var message = intent.getStringExtra("message")
        var textView = findViewById<TextView>(R.id.klarnaOrderCompletedActivity_textView_message)
        textView.text = message
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}