package com.flexicharge.bolt.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.flexicharge.bolt.R
import com.flexicharge.bolt.activities.businessLogic.EntryManager
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import com.flexicharge.bolt.api.flexicharge.UserFullDetails
import com.flexicharge.bolt.api.flexicharge.VerificationDetails
import com.flexicharge.bolt.databinding.ActivityVerifyEmailBinding
import com.flexicharge.bolt.helpers.LoginChecker
import com.flexicharge.bolt.helpers.StatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException


class VerifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerifyEmailBinding
    private val entryManager = EntryManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_email)
        binding = ActivityVerifyEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        verificationCode = binding.verifyActivityEditTextCode
        val userPass = intent.getStringExtra("userPass")
        val userEmail = intent.getStringExtra("userEmail")
        var userFirstName = intent.getStringExtra("userFirstName")
        var userLastName = intent.getStringExtra("userLastName")

        if(userFirstName == null)
            userFirstName =""
        if(userLastName == null)
            userLastName = ""

        confirmVerification(userEmail = userEmail!!, userPass = userPass!!, userFirstName, userLastName)
    }

    private lateinit var verificationCode : EditText


    private fun confirmVerification(userEmail: String, userPass : String, userFirstName : String, userLastName : String){
        val verifyBtn = findViewById<Button>(R.id.buttonVerify)

        verifyBtn.setOnClickListener {
            verifyUser( verificationCode.text.toString(), userEmail, userPass, userFirstName, userLastName)
        }
    }


    private fun verifyUser ( userCode : String, userEmail: String,userPass : String, userFirstName : String, userLastName : String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val requestBody = VerificationDetails(userEmail, userCode)
                val response = RetrofitInstance.flexiChargeApi.verifyEmail(requestBody)
                if (response.code() == StatusCode.ok) {
                    lifecycleScope.launch( Dispatchers.Main) {
                        val userInfo: Map<String, String> = mapOf(
                            "email" to userEmail,
                            "pass" to userPass,
                            "firstName" to userFirstName,
                            "lastName" to userLastName
                        )
                        signIn(userInfo)
                    }
                }
                else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        Toast.makeText(this@VerifyActivity, "Code Invalid, Try Again", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: HttpException) {
                lifecycleScope.launch(Dispatchers.Main) {
                    Toast.makeText(this@VerifyActivity, e.message, Toast.LENGTH_LONG).show()
                }
            } catch (e: IOException) {lifecycleScope.launch(Dispatchers.Main) {
                Toast.makeText(this@VerifyActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    fun signIn(userData : Map<String, String>){
        lifecycleScope.launch(Dispatchers.IO) {
            entryManager.singIn(userData["email"]!!, userData["pass"]!!) { responseBody, message, isOK ->
                if (isOK) {
                    LoginChecker.LOGGED_IN = true
                    val loggedInData = mapOf(
                        "accessToken" to responseBody.accessToken,
                        "userId" to responseBody.user_id,
                        "username" to responseBody.username
                    )
                    val collectedData = loggedInData + userData
                    sendToMain(collectedData)
                }
                else {
                    lifecycleScope.launch (Dispatchers.Main) {
                    }
                }
            }
        }
    }

    private fun sendToMain(userData : Map<String,String>){
        val sharedPreferences = getSharedPreferences("loginPreference", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {
            putString("accessToken", userData["accessToken"])
            putString("userId", userData["userId"])
            putString("userName", userData["userId"])
            putString("email", userData["email"])
            putString("loggedIn", "true")
        }.apply()
        val userInfo = UserFullDetails(
            userData["firstName"],
            userData["lastName"],
            "",
            "",
            "",
            "",
            "",
        )
        lifecycleScope.launch(Dispatchers.IO) {
            val token = userData["accessToken"]
           val test = RetrofitInstance.flexiChargeApi.updateUserInfo("Bearer $token", userInfo)
            if(test.isSuccessful){
                withContext(Dispatchers.Main){
                    startActivity(Intent(this@VerifyActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

}
























