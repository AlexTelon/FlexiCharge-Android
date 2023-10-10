package com.flexicharge.bolt.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flexicharge.bolt.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
