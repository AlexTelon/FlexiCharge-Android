package com.flexicharge.bolt.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.flexicharge.bolt.R


class ProfileMenuLoggedOutActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_menu_logged_out)
    }

    fun loginClick(view: android.view.View) {}
    fun aboutClick(view: android.view.View) {
        startActivity(Intent(this, AboutActivity::class.java))
    }
}