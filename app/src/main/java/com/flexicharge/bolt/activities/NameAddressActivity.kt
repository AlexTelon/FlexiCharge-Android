package com.flexicharge.bolt.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flexicharge.bolt.databinding.ActivityNameAndAddressBinding

class NameAddressActivity : AppCompatActivity() {
    lateinit var binding: ActivityNameAndAddressBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNameAndAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}