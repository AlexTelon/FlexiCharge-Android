package com.flexicharge.bolt.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flexicharge.bolt.databinding.ActivityProfileMenuLoggedOutBinding

class ProfileMenuLoggedOutActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileMenuLoggedOutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileMenuLoggedOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginActivityButtonLogout.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.aboutTextView.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }
}