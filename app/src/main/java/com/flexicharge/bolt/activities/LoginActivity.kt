package com.flexicharge.bolt.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.flexicharge.bolt.R
import androidx.lifecycle.lifecycleScope
import com.flexicharge.bolt.StatusCode
import com.flexicharge.bolt.api.flexicharge.Credentials
import com.flexicharge.bolt.api.flexicharge.LoginBody
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import com.flexicharge.bolt.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    var username = " "
    var password = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginActivityButtonLogout.setOnClickListener {
            username = binding.loginActivityEditTextEmail.text.toString()
            password = binding.loginActivityEditTextPassword.text.toString()
            singUp(username, password) { loginBody, isOK ->
                Log.d("responseBody", isOK.toString())
//                if (isOK) {
                    Log.d("responseBody", loginBody.toString())
//                }
//                else {
//                    Log.d("responseBody", "loginBody is empty, SHIT gone WRONG")
//                }
            }

        }

    }

    private fun singUp(
        username: String,
        password: String,
        callback: (LoginBody, isOK: Boolean) -> Unit
    ) {

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val body = Credentials(username, password)
                val response = RetrofitInstance.flexiChargeApi.signIn(body)

//                if (response.code() == StatusCode.ok) {
                    response.body()?.let {
                        callback.invoke(it, true)
                    }
//                }
//                if (response.code() == StatusCode.badRequest) {
//                    response.body()?.let {
//                        Log.d("responseBody", "in in in ")
//                        callback.invoke(it, false)
//                    }
//                }
            } catch (e: HttpException) {
                Log.d("loginChecking", "Http Error")
            } catch (e: IOException) {
                Log.d("loginChecking", "No Internet Error - ChargerList will not be initialized")
            }
        }
    }
}