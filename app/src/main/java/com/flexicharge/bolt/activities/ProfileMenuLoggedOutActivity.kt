package com.flexicharge.bolt.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.flexicharge.bolt.R
import com.flexicharge.bolt.databinding.ActivityProfileMenuLoggedOutBinding


class ProfileMenuLoggedOutActivity : AppCompatActivity(){
    lateinit var binding: ActivityProfileMenuLoggedOutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileMenuLoggedOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginActivityButtonLogout.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

//        binding.l.setOnClickListener {
//            startActivity(Intent(this, LoginActivity::class.java))
//        }

    }

    fun loginClick(view: android.view.View) {}
    fun aboutClick(view: android.view.View) {
        startActivity(Intent(this, AboutActivity::class.java))
    }
}