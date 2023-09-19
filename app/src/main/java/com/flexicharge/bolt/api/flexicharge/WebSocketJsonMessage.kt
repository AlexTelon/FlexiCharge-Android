package com.flexicharge.bolt.api.flexicharge

import org.json.JSONArray

class WebSocketJsonMessage {
    data class ChargingStat(val value: Number, val unit: String)
    data class LiveMetricsMessage(
        val chargingPercent: ChargingStat,
        val chargingPower: ChargingStat,
        val chargedSoFar: ChargingStat)



    companion object {
        private const val ROOT_INDEX_METER_VALUES = 3
        private const val METER_VALUES_FIELD_NAME_VALUES = "values"
        private const val VALUES_FIELD_NAME_CHARGING_PERCENT = "chargingPercent"
        private const val VALUES_FIELD_NAME_CHARGING_POWER = "chargingPower"
        private const val VALUES_FIELD_NAME_CHARGED_SO_FAR = "chargedSoFar"

        private const val CHARGING_STAT_FIELD_NAME_VALUE = "value"
        private const val CHARGING_STAT_FIELD_NAME_UNIT = "unit"

        fun parseLiveMetrics(webSocketMessage: String): LiveMetricsMessage {
            val jsonRoot = JSONArray(webSocketMessage)
            val jsonMeterValues = jsonRoot.getJSONObject(ROOT_INDEX_METER_VALUES)
            val jsonValues = jsonMeterValues.getJSONObject(METER_VALUES_FIELD_NAME_VALUES)

            val jsonChargingPercent = jsonValues.getJSONObject(VALUES_FIELD_NAME_CHARGING_PERCENT)
            val jsonChargingPower = jsonValues.getJSONObject(VALUES_FIELD_NAME_CHARGING_POWER)
            val jsonChargedSoFar = jsonValues.getJSONObject(VALUES_FIELD_NAME_CHARGED_SO_FAR)

            val chargingPercent = ChargingStat(
                jsonChargingPercent.getDouble(CHARGING_STAT_FIELD_NAME_VALUE),
                jsonChargingPercent.getString(CHARGING_STAT_FIELD_NAME_UNIT)
            )

            val chargingPower = ChargingStat(
                jsonChargingPower.getDouble(CHARGING_STAT_FIELD_NAME_VALUE),
                jsonChargingPower.getString(CHARGING_STAT_FIELD_NAME_UNIT)
            )

            val chargedSoFar = ChargingStat(
                jsonChargedSoFar.getDouble(CHARGING_STAT_FIELD_NAME_VALUE),
                jsonChargedSoFar.getString(CHARGING_STAT_FIELD_NAME_UNIT)
            )

            return LiveMetricsMessage(chargingPercent, chargingPower, chargedSoFar)
        }
    }
}