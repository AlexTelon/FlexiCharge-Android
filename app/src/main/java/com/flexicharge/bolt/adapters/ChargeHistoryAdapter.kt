package com.flexicharge.bolt.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flexicharge.bolt.R
import com.flexicharge.bolt.api.flexicharge.ChargingHistoryObject

class ChargeHistoryAdapter(private val data: List<ChargingHistoryObject>) : RecyclerView.Adapter<ChargeHistoryAdapter.ViewHolder>() {

    private val isItemExpanded = BooleanArray(data.size) {false}

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textView            : TextView       = itemView.findViewById(R.id.charger_location_text)
        val expandImage         : ImageView      = itemView.findViewById(R.id.expand_image)
        val expandedInfoLayout  : LinearLayout   = itemView.findViewById(R.id.expanded_info_layout)
        val priceTextCollapsed  : TextView       = itemView.findViewById(R.id.charging_ammount_text)
        val totalSum            : TextView       = itemView.findViewById(R.id.history_receipt_text)
        val startTime           : TextView       = itemView.findViewById(R.id.history_date_text)
        val chargeTime          : TextView       = itemView.findViewById(R.id.history_charge_time_text)
        val transferredKwh       : TextView       = itemView.findViewById(R.id.history_kWh_text)
        val priceKwh            : TextView       = itemView.findViewById(R.id.history_kWhPrice_text)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_charging_history_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val text = createTextStrings(holder,position)
        holder.priceTextCollapsed.text  = text["priceCollapsed"]
        holder.textView.text            = text["location"]
        holder.chargeTime.text          = text["chargeTime"]
        holder.priceKwh.text            = text["priceKwh"]
        holder.totalSum.text            = text["totalSum"]
        holder.startTime.text           = text["startTime"]
        holder.transferredKwh.text      = text["transferredKwh"]

        holder.expandImage.setOnClickListener {
            isItemExpanded[position] = !isItemExpanded[position]

            updateExpandedView(holder, isItemExpanded[position])
        }
    }

    private fun createTextStrings(holder: ViewHolder, position: Int): Map<String, String> {
        val item = data[position]
        return mapOf(
            "priceCollapsed"    to "total: ${item.totalSum} kr ",
            "location"          to item.location,
            "chargeTime"        to item.chargeTime,
            "priceKwh"          to "${item.priceKwh} kr/kWh",
            "totalSum"          to "${item.totalSum} kr",
            "startTime"         to item.startTime,
            "transferredKwh"    to "${item.transferedKwh} kWh"
        )
    }

    private fun updateExpandedView(holder: ViewHolder, visible : Boolean) {
        val context = holder.expandImage.context
        if(visible){
            holder.expandImage.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate_reverse))
            holder.priceTextCollapsed.visibility = TextView.GONE
            holder.expandedInfoLayout.visibility = View.VISIBLE

        }
        else{
            holder.expandImage.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate))
            holder.priceTextCollapsed.visibility = TextView.VISIBLE
            holder.expandedInfoLayout.visibility = View.GONE
        }
    }


    override fun getItemCount(): Int {
        return data.size
    }

}