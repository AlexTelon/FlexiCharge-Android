package com.flexicharge.bolt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.flexicharge.bolt.AccountActivities.RegisterActivity

class ProfileMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_menu)
    }
    fun chargingHistoryClick(view: View) {}
    fun invoiceClick(view: View) {}
    fun accountClick(view: View) {}
    fun nameAddressClick(view: View) {}
    fun aboutClick(view: View) {}
    fun loginClick(view: View) {
        startActivity(Intent(this, RegisterActivity::class.java));
    }
}