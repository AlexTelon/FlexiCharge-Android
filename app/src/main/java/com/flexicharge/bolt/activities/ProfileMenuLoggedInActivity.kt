package com.flexicharge.bolt.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
        }

        binding.chargingHistoryTextView.setOnClickListener{
            val intent = Intent(this, ChargingHistoryActivity::class.java)
            startActivity(intent)
        }

        binding.invoicesTextView.setOnClickListener{
            val intent = Intent(this, InvoiceActivity::class.java)
            startActivity(intent)
        }

        binding.nameAddressTextView.setOnClickListener{
            val intent = Intent(this, NameAddressActivity::class.java)
            startActivity(intent)
        }

        binding.aboutTextView.setOnClickListener{
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
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