package com.flexicharge.bolt.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.flexicharge.bolt.R
import com.flexicharge.bolt.api.flexicharge.ChargePoints

class ChargePointListAdapter(
    private var chargePoints: ChargePoints,
    private var act: ShowChargePointInterface,
    private var distance: MutableList<String>,
    private var chargerCount: MutableList<Int>
) :
    RecyclerView.Adapter<ChargePointListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemAddress: TextView = itemView.findViewById(R.id.chargePointListItem_textView_address)
        val itemDistance: TextView = itemView.findViewById(
            R.id.chargePointListItem_textView_distance
        )
        val itemImage: ImageView = itemView.findViewById(
            R.id.chargePointListItem_imageView_chargerIcon
        )
        val itemNumberOfChargers: TextView = itemView.findViewById(
            R.id.chargePointListItem_textView_no_of_chargers
        )

        init {
            itemView.setOnClickListener { _: View ->
                val position: Int = adapterPosition
                act.showChargePoint(
                    chargePoints[position].location[0],
                    chargePoints[position].location[1],
                    chargePoints[position].chargePointID
                )
            }
        }
    }

    interface ShowChargePointInterface {
        fun showChargePoint(
            latitude: Double,
            longitude: Double,
            chargePointID: Int
        ) {}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.chargepoint_list_item,
            parent,
            false
        )
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val distanceText = "${distance[position]} km"
        val numberOfChargersText = "${chargerCount[position]} chargers"

        holder.itemDistance.text = distanceText
        holder.itemAddress.text = chargePoints[position].name
        if (chargerCount[position] > 0) {
            holder.itemNumberOfChargers.text = numberOfChargersText
        } else {
            holder.itemNumberOfChargers.text = holder.itemView.context.getString(
                R.string.no_chargers_available
            )
            holder.itemNumberOfChargers.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.red)
            )
            holder.itemImage.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return chargePoints.size
    }
}
