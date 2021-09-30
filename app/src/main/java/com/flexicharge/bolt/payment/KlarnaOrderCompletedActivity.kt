package com.flexicharge.bolt.payment

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.flexicharge.bolt.MainActivity
import com.flexicharge.bolt.R

class KlarnaOrderCompletedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_klarna_order_completed)
        var message = intent.getStringExtra("message")
        var textView = findViewById<TextView>(R.id.messageTextView)
        textView.text = message
    }
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
    }
}