package com.flexicharge.bolt.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flexicharge.bolt.R
import com.flexicharge.bolt.adapters.ChargeHistoryAdapter
import com.flexicharge.bolt.api.flexicharge.ChargingHistoryObject
import com.flexicharge.bolt.databinding.ActivityChargingHistoryBinding

class ChargingHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChargingHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChargingHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView : RecyclerView = findViewById(R.id.chargingHistory_recyclerView)

        val data = createMockData()
        val adapter = ChargeHistoryAdapter(data)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun createMockData() : List<ChargingHistoryObject>{

        val charge1 = ChargingHistoryObject(
            location        = "Per-brahe parkeringshus, Jönköping",
            totalSum        = "27.74",
            startTime       = "15 June, kl 12:04",
            chargeTime      = "3 hrs 54 min",
            transferedKwh   = "9.24",
            priceKwh        = "3.00"
        )

        val charge2 = ChargingHistoryObject(
            location        = "Per-brahe parkeringshus, Jönköping",
            totalSum        = "27.74",
            startTime       = "15 June, kl 12:04",
            chargeTime      = "3 hrs 54 min",
            transferedKwh   = "9.24",
            priceKwh        = "3.00"
        )

        val charge3 = ChargingHistoryObject(
            location        = "Per-brahe parkeringshus, Jönköping",
            totalSum        = "27.74",
            startTime       = "18 June, kl 14:04",
            chargeTime      = "4 hrs 23 min",
            transferedKwh   = "10.58",
            priceKwh        = "2.48"
        )

        val charge4 = ChargingHistoryObject(
            location        = "Per-brahe parkeringshus, Jönköping",
            totalSum        = "27.74",
            startTime       = "15 June, kl 12:04",
            chargeTime      = "3 hrs 54 min",
            transferedKwh   = "9.24",
            priceKwh        = "3.00"
        )

        val charge5 = ChargingHistoryObject(
            location        = "Per-brahe parkeringshus, Jönköping",
            totalSum        = "27.74",
            startTime       = "15 June, kl 12:04",
            chargeTime      = "3 hrs 54 min",
            transferedKwh   = "9.24",
            priceKwh        = "3.00"
        )

        val charge6 = ChargingHistoryObject(
            location        = "Per-brahe parkeringshus, Jönköping",
            totalSum        = "27.74",
            startTime       = "15 June, kl 12:04",
            chargeTime      = "3 hrs 54 min",
            transferedKwh   = "9.24",
            priceKwh        = "3.00"
        )

        return  listOf(charge1,charge2,charge3,charge4,charge5,charge6)

    }
}