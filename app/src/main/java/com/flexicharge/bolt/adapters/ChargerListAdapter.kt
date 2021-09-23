package com.flexicharge.bolt.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.flexicharge.bolt.Chargers
import com.flexicharge.bolt.R

class ChargerListAdapter(private var chargers: Chargers, private var act: panToMarkerInterface, private var distance : MutableList<String>) :
RecyclerView.Adapter<ChargerListAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemAddress: TextView = itemView.findViewById(R.id.charger_address)
        val itemDistance: TextView = itemView.findViewById(R.id.charger_distance)
        val itemImage : ImageView = itemView.findViewById(R.id.charger_icon)
        val itemNumberOfChargers: TextView = itemView.findViewById(R.id.charger_number_of_available)


        init {
            itemView.setOnClickListener { v: View ->
                val position: Int = adapterPosition
                Toast.makeText(itemView.context, "You clicked on charger at ${chargers[position].chargePointID}", Toast.LENGTH_SHORT).show()
                act.panToMarker(
                    chargers[position].location[0],
                    chargers[position].location[1],
                    chargers[position].chargerID.toString(),
                    chargers[position].status
                )
            }
        }

    }

    interface panToMarkerInterface {
        fun panToMarker (latitude: Double, longitude: Double, title: String, status: Int) {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.charger_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemDistance.text = distance[position] + "km"
        holder.itemAddress.text = "Charger " + chargers[position].chargerID.toString()
        if(chargers.size > 0 ){
            holder.itemNumberOfChargers.text = "1 charger"
        } else {
            holder.itemNumberOfChargers.text =holder.itemView.context.getString(R.string.no_chargers_available)
            holder.itemNumberOfChargers.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
            holder.itemImage.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return chargers.size
    }

}
