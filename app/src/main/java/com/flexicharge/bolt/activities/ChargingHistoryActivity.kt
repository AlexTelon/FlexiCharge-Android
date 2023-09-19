package com.flexicharge.bolt.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flexicharge.bolt.databinding.ActivityAccountSettingsBinding

import com.flexicharge.bolt.databinding.ActivityChargingHistoryBinding

class ChargingHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChargingHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChargingHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}