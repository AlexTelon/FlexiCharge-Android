package com.flexicharge.bolt.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.flexicharge.bolt.Chargers
import com.flexicharge.bolt.MainActivity
import com.flexicharge.bolt.R

class ChargerListAdapter(private var mockChargers: Chargers, private var act: addAndPanToMarkerInterface) :
RecyclerView.Adapter<ChargerListAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemAddress: TextView = itemView.findViewById(R.id.charger_address)
        val itemDistance: TextView = itemView.findViewById(R.id.charger_distance)
        val itemImage : ImageView = itemView.findViewById(R.id.charger_icon)
        val itemNumberOfChargers: TextView = itemView.findViewById(R.id.charger_number_of_available)


        init {
            itemView.setOnClickListener { v: View ->
                val position: Int = adapterPosition
                Toast.makeText(itemView.context, "You clicked on charger at ${mockChargers[position].chargePointID}", Toast.LENGTH_SHORT).show()
                act.addAndPanToMarker(mockChargers[position].location[0], mockChargers[position].location[1], mockChargers[position].chargerID.toString())
            }
        }

    }

    interface addAndPanToMarkerInterface {
        fun addAndPanToMarker (latitude: Double, longitude: Double, title: String) {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.charger_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemAddress.text = mockChargers[position].chargerID.toString()
        holder.itemDistance.text = ""
        if(mockChargers.size > 0 ){
            holder.itemNumberOfChargers.text = mockChargers.size.toString()
        } else {
            holder.itemNumberOfChargers.text =holder.itemView.context.getString(R.string.no_chargers_available)
            holder.itemNumberOfChargers.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
            holder.itemImage.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return mockChargers.size
    }

}