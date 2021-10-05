package com.flexicharge.bolt.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
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
        val chargerType: TextView = itemView.findViewById(R.id.chargerType)
        //val chargerIcon: AppCompatImageView = itemView.findViewById(R.id.chargerCableIcon)

        init {
            if(chargerStatus.toString() == "Available"){
                itemView.setOnClickListener { v: View ->
                    //TODO: Selected charger
                }
            }

        }
    }


    override fun getItemCount(): Int {
        return chargers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.chargerId.text = chargers[position].chargerID.toString()
        holder.chargerStatus.text = chargers[position].status

        if(holder.chargerStatus.text != "Available"){
            holder.chargerId.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.light_grey))
            holder.chargerStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
            holder.chargerAC.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.light_grey))
            holder.chargerType.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.light_grey))
            //holder.chargerIcon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.light_grey))
            holder.chargerCost.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.light_grey))
        }
    }
}