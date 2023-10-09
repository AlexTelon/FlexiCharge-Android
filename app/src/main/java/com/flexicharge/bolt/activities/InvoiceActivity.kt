package com.flexicharge.bolt.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flexicharge.bolt.databinding.ActivityInvoicesBinding

class InvoiceActivity : AppCompatActivity() {
    lateinit var binding: ActivityInvoicesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoicesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}