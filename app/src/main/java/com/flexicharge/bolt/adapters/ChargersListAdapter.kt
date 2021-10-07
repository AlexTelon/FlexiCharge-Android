package com.flexicharge.bolt.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.flexicharge.bolt.ChargePoint
import com.flexicharge.bolt.Charger
import com.flexicharge.bolt.R

class ChargersListAdapter(private val chargers: List<Charger>, private val enteredChargerId: String, private val chargePoint: ChargePoint, private val act: ChangeInputInterface): RecyclerView.Adapter<ChargersListAdapter.ViewHolder>() {

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
        val chargerCableIcon: AppCompatImageView = itemView.findViewById(R.id.chargerCableIcon)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.chargerId.text = chargers[position].chargerID.toString()
        holder.chargerStatus.text = chargers[position].status
        holder.chargerCost.text = chargePoint.price + "kr/kWh"

        if(holder.chargerId.text == enteredChargerId){
            holder.itemView.setBackgroundResource(R.drawable.rounded_background_selected)
        }

        if(chargers[position].status == "Available"){
            holder.itemView.setOnClickListener{
                act.changeInput(holder.chargerId.text.toString())
            }

        } else {
            holder.itemView.isClickable = false
            holder.chargerId.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.light_grey))
            holder.chargerStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
            holder.chargerAC.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.light_grey))
            holder.chargerType.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.light_grey))
            holder.chargerCableIcon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.light_grey))
            holder.chargerCost.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.light_grey))
        }
    }

    interface ChangeInputInterface {
        fun changeInput(newInput: String){}
    }

    override fun getItemCount(): Int {
        return chargers.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}