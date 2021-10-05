package com.flexicharge.bolt.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.flexicharge.bolt.Chargers
import com.flexicharge.bolt.R

class ChargersListAdapter(private var chargers: Chargers): RecyclerView.Adapter<ChargersListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.charger_list_item, parent, false)
        return ViewHolder(v)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chargerId: TextView = itemView.findViewById(R.id.chargerId)
        val chargerStatus: TextView = itemView.findViewById(R.id.chargerStatus)
        val chargerCost: TextView = itemView.findViewById(R.id.chargerCost)
        val chargerAC : TextView = itemView.findViewById(R.id.chargerAC)

        init {
            itemView.setOnClickListener { v: View ->

            }
        }
    }


    override fun getItemCount(): Int {
        return chargers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.chargerId.text = chargers[position].chargePointID.toString()
        holder.chargerStatus.text = chargers[position].status
    }
}