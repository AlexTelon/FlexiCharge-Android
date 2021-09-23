package com.flexicharge.bolt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class ProfileMenuLoggedOutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_menu_logged_out)
    }
    fun aboutClick(view: View) {}
    fun loginClick(view: View) {}
}