package com.flexicharge.bolt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flexicharge.bolt.adapters.ChargerListAdapter
import com.flexicharge.bolt.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var chargerAddressList = mutableListOf<String>()
    private var chargerDistanceList = mutableListOf<Int>()
    private var numberOfChargers = mutableListOf<Int>()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Fill lists with trash data for now
        addToList()

        binding.button.setOnClickListener {
            setupChargerInput()
        }
    }

    private fun setupChargerInput() {

        val bottomSheetDialog = BottomSheetDialog(
            this@MainActivity, R.style.BottomSheetDialogTheme
        )

        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.layout_charger_input,
            findViewById<ConstraintLayout>(R.id.chargerInputLayout)
        )

        setupChargerRecyclerView(bottomSheetView)
        setupChargerInputFocus(bottomSheetView)
        setupChargerInputCompletion(bottomSheetView)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    //Delete later.
    private fun addToList() {
        for (i in 1..5) {
            chargerAddressList.add("Kungsgatan 5")
            chargerDistanceList.add(i*100)
            numberOfChargers.add(i)
        }
    }

    private fun setupChargerRecyclerView(view: View) {
        val listOfChargersRecyclerView = view.findViewById<RecyclerView>(R.id.charger_input_list_recyclerview)
        listOfChargersRecyclerView.layoutManager = LinearLayoutManager(this)
        listOfChargersRecyclerView.adapter = ChargerListAdapter(chargerAddressList, chargerDistanceList, numberOfChargers)
    }

    private fun setupChargerInputFocus(view: View) {
        val editTextInput1 = view.findViewById<EditText>(R.id.charger_input_edit_text_1)
        val editTextInput2 = view.findViewById<EditText>(R.id.charger_input_edit_text_2)
        val editTextInput3 = view.findViewById<EditText>(R.id.charger_input_edit_text_3)
        val editTextInput4 = view.findViewById<EditText>(R.id.charger_input_edit_text_4)
        val editTextInput5 = view.findViewById<EditText>(R.id.charger_input_edit_text_5)
        val editTextInput6 = view.findViewById<EditText>(R.id.charger_input_edit_text_6)

        editTextInput1.doOnTextChanged { _, _, _, count ->
            if (count == 1) editTextInput2.requestFocus()
        }
        editTextInput2.doOnTextChanged { _, _, _, count ->
            if (count == 1) editTextInput3.requestFocus()
        }
        editTextInput3.doOnTextChanged { _, _, _, count ->
            if (count == 1) editTextInput4.requestFocus()
        }
        editTextInput4.doOnTextChanged { _, _, _, count ->
            if (count == 1) editTextInput5.requestFocus()
        }
        editTextInput5.doOnTextChanged { _, _, _, count ->
            if (count == 1) editTextInput6.requestFocus()
        }
    }

    private fun setupChargerInputCompletion(view: View) {
        val editTextInput1 = view.findViewById<EditText>(R.id.charger_input_edit_text_1)
        val editTextInput2 = view.findViewById<EditText>(R.id.charger_input_edit_text_2)
        val editTextInput3 = view.findViewById<EditText>(R.id.charger_input_edit_text_3)
        val editTextInput4 = view.findViewById<EditText>(R.id.charger_input_edit_text_4)
        val editTextInput5 = view.findViewById<EditText>(R.id.charger_input_edit_text_5)
        val editTextInput6 = view.findViewById<EditText>(R.id.charger_input_edit_text_6)
        val chargerInputStatus = view.findViewById<TextView>(R.id.charger_input_status)
        editTextInput6.doOnTextChanged { _, _, _, _ ->
            val chargerId = (editTextInput1.text.toString() +
                    editTextInput2.text.toString() +
                    editTextInput3.text.toString() +
                    editTextInput4.text.toString() +
                    editTextInput5.text.toString() +
                    editTextInput6.text.toString())
            if (validateChargerId(chargerId)) validateConnectionToMockDataApi(chargerId, chargerInputStatus)
            else {
                chargerInputStatus.text = "ChargerId has to consist of 6 digits"
                chargerInputStatus.setBackgroundResource(R.color.red)
            }
        }
    }

    private fun validateChargerId(chargerId: String): Boolean {
        if(chargerId.length != 6) {
            return false
        }
        if(chargerId.count { it.isDigit() } != 6) {
            return false
        }
        return true
    }

    private fun validateConnectionToMockDataApi(chargerId: String, chargerInputStatus: TextView) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getMockApiData(chargerId)
                if (response.isSuccessful) {
                    val charger = response.body() as FakeJsonResponse
                    Log.d("validateConnection", "Connected to charger " + charger.id)
                    lifecycleScope.launch(Dispatchers.Main) {
                        if (charger.status == 1) {
                            chargerInputStatus.text = "Connected to charger " + charger.id + "\n located at Long:" + charger.location.longitude + " Lat:" + charger.location.latitude
                            chargerInputStatus.setBackgroundResource(R.color.green)
                        }
                        else {
                            chargerInputStatus.text = "Charger " + charger.id + " is busy"
                            chargerInputStatus.setBackgroundResource(R.color.red)
                        }
                    }
                } else {
                    Log.d("validateConnection", "Could not connect to charger" + chargerId)
                    lifecycleScope.launch(Dispatchers.Main) {
                        chargerInputStatus.text = "Charger " + chargerId + " does not exist"
                        chargerInputStatus.setBackgroundResource(R.color.red)
                    }
                }
            } catch (e: HttpException) {
                Log.d("validateConnection", "Crashed with Exception")
            } catch (e: IOException) {
                Log.d("validateConnection", "You might not have internet connection")
                lifecycleScope.launch(Dispatchers.Main) {
                    chargerInputStatus.text = "Unable to establish connection"
                    chargerInputStatus.setBackgroundResource(R.color.red)
                }
            }
        }
    }
}