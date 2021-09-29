package com.flexicharge.bolt.payment

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flexicharge.bolt.R

class KlarnaOrderCompletedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_klarna_order_completed)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, KlarnaActivity::class.java))
    }

}