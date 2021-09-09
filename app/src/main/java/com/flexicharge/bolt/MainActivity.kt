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
import com.flexicharge.bolt.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        setupChargerInputFocus(bottomSheetView)
        setupChargerIdValidator(bottomSheetView)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
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

    private fun setupChargerIdValidator(view: View) {
        val editTextInput1 = view.findViewById<EditText>(R.id.charger_input_edit_text_1)
        val editTextInput2 = view.findViewById<EditText>(R.id.charger_input_edit_text_2)
        val editTextInput3 = view.findViewById<EditText>(R.id.charger_input_edit_text_3)
        val editTextInput4 = view.findViewById<EditText>(R.id.charger_input_edit_text_4)
        val editTextInput5 = view.findViewById<EditText>(R.id.charger_input_edit_text_5)
        val editTextInput6 = view.findViewById<EditText>(R.id.charger_input_edit_text_6)
        val chargerInputStatus = view.findViewById<TextView>(R.id.charger_input_status)
        editTextInput6.doOnTextChanged { _, _, _, count ->
            //if (count == 1) editTextInput6.requestFocus()
            var chargerId = (editTextInput1.text.toString() +
                    editTextInput2.text.toString() +
                    editTextInput3.text.toString() +
                    editTextInput4.text.toString() +
                    editTextInput5.text.toString() +
                    editTextInput6.text.toString())
            chargerInputStatus.text = "Connected to " + chargerId
            chargerInputStatus.setBackgroundResource(R.color.green)
            //chargerInputStatus.setTextColor(resources.getColor(R.color.green))
            validateConnectionToMockDataApi(chargerId)
            Log.d("editTextInput6",chargerId)
        }
    }



/*    fun validateChargerIdInput(
        chargerId: String
    ): Boolean {
        if(chargerId.length != 6) {
            return false
        }
        if(chargerId.count { it.isDigit() } != 6) {
            return false
        }
        if(chargerId in occupiedChargerIds) {
            return false
        }
        if(chargerId !in availableChargerIds) {
            return false
        }

        return true
    }*/



    private fun validateConnectionToMockDataApi(chargerId: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {

                val response = RetrofitInstance.api.getMockApiData(chargerId)
                Log.d("asdf", response.isSuccessful.toString())
                if (response.isSuccessful) {
                    val chargerId = response.body() as FakeJsonResponse
                    Log.d("validateConnection", "Connected to charger " + chargerId.id)
                    lifecycleScope.launch(Dispatchers.Main) {
                        binding.mainActivityConnectedStatus.text =
                            "Connected to charger " + chargerId.id
                    }
                } else {
                    Log.d("validateConnection", "Could not connect to charger Kapp")
                    lifecycleScope.launch(Dispatchers.Main) {
                        binding.mainActivityConnectedStatus.text =
                            "Not connected to charger Kapp"
                    }
                }
            } catch (e: HttpException) {
                Log.d("validateConnection", "Crashed with Exception")
            } catch (e: IOException) {
                Log.d("validateConnection", "You might not have internet connection")
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.mainActivityConnectedStatus.text = "Not connected to charger Kapp"
                }
            }
        }
    }


    private fun validateConnectionToFakeAPI() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getFakeApiData()

                if (response.isSuccessful) {
                    Log.d("validateConnection", "Connected to charger Kapp")
                    lifecycleScope.launch(Dispatchers.Main) {
                        binding.mainActivityConnectedStatus.text = "Connected to charger Kapp"
                    }
                } else {
                    Log.d("validateConnection", "Could not connect to charger Kapp")
                    lifecycleScope.launch(Dispatchers.Main) {
                        binding.mainActivityConnectedStatus.text =
                            "Not connected to charger Kapp"
                    }
                }
            } catch (e: HttpException) {
                Log.d("validateConnection", "Crashed with Exception")
            } catch (e: IOException) {
                Log.d("validateConnection", "You might not have internet connection")
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.mainActivityConnectedStatus.text = "Not connected to charger Kapp"
                }
            }
        }
    }
}