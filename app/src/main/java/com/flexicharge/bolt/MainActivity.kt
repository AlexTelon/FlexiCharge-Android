package com.flexicharge.bolt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

        //validateConnectionToFakeAPI()
        validateConnectionToMockDataApi()

    }

    private fun validateConnectionToMockDataApi() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {

                val response = RetrofitInstance.api.getMockApiData()
                Log.d("asdf", response.isSuccessful.toString())
                if (response.isSuccessful) {
                    val chargerId = response.body() as FakeJsonResponse
                    Log.d("validateConnection", "Connected to charger " + chargerId.id)
                    lifecycleScope.launch(Dispatchers.Main)  {
                        binding.mainActivityConnectedStatus.text = "Connected to charger " + chargerId.id
                    }
                } else {
                    Log.d("validateConnection", "Could not connect to charger Kapp")
                    lifecycleScope.launch(Dispatchers.Main)  {
                        binding.mainActivityConnectedStatus.text = "Not connected to charger Kapp"
                    }
                }
            } catch (e: HttpException) {
                Log.d("validateConnection", "Crashed with Exception")
            } catch (e: IOException) {
                Log.d("validateConnection", "You might not have internet connection")
                lifecycleScope.launch(Dispatchers.Main)  {
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
                    lifecycleScope.launch(Dispatchers.Main)  {
                        binding.mainActivityConnectedStatus.text = "Connected to charger Kapp"
                    }
                } else {
                    Log.d("validateConnection", "Could not connect to charger Kapp")
                    lifecycleScope.launch(Dispatchers.Main)  {
                        binding.mainActivityConnectedStatus.text = "Not connected to charger Kapp"
                    }
                }
            } catch (e: HttpException) {
                Log.d("validateConnection", "Crashed with Exception")
            } catch (e: IOException) {
                Log.d("validateConnection", "You might not have internet connection")
                lifecycleScope.launch(Dispatchers.Main)  {
                    binding.mainActivityConnectedStatus.text = "Not connected to charger Kapp"
                }
            }
        }
    }
}