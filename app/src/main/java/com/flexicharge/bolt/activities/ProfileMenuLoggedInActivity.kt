package com.flexicharge.bolt.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flexicharge.bolt.activities.businessLogic.AccountSettingsActivity
import com.flexicharge.bolt.databinding.ActivityProfileMenuLoggedInBinding
import com.flexicharge.bolt.helpers.LoginChecker

class ProfileMenuLoggedInActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileMenuLoggedInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileMenuLoggedInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.accountSettingsTextView.setOnClickListener{
            val intent = Intent(this, AccountSettingsActivity::class.java)
            startActivity(intent)
            finish()
        }


        binding.loginActivityButtonLogout.setOnClickListener {
            LoginChecker.LOGGED_IN = false
            getSharedPreferences("loginPreference", Context.MODE_PRIVATE).edit().apply {
                clear()
            }.apply()

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}