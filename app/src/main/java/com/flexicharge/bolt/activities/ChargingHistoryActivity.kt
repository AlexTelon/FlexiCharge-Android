package com.flexicharge.bolt.activities

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flexicharge.bolt.R
import com.flexicharge.bolt.adapters.ChargePointListAdapter
import com.flexicharge.bolt.adapters.InvoiceAdapter
import com.flexicharge.bolt.databinding.ActivityAccountSettingsBinding

import com.flexicharge.bolt.databinding.ActivityChargingHistoryBinding

class ChargingHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChargingHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChargingHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView : RecyclerView = findViewById(R.id.chargingHistory_recyclerView)
        val data = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7", "Item 8", "Item 9", "Item 10", "Item 11", "Item 12")

        val adapter = InvoiceAdapter(data)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}