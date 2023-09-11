package com.flexicharge.bolt.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.flexicharge.bolt.R
import com.flexicharge.bolt.activities.businessLogic.EntryManager
import com.flexicharge.bolt.helpers.StatusCode
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import com.flexicharge.bolt.api.flexicharge.UserFullDetails
import com.flexicharge.bolt.api.flexicharge.VerificationDetails
import com.flexicharge.bolt.databinding.ActivityRegisterBinding
import com.flexicharge.bolt.databinding.ActivityVerifyEmailBinding
import com.flexicharge.bolt.helpers.LoginChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        verificationEmail = binding.verifyActivityEditTextEmail
        val userPass = intent.getStringExtra("userPass")
        val userEmail = intent.getStringExtra("userEmail")
        val userFirstName = intent.getStringExtra("userFirstName")
        val userLastName = intent.getStringExtra("userLastName")

        confirmVerification(userEmail = userEmail!!,userPass = userPass!!, userFirstName!!, userLastName!!)
    }

    private lateinit var verificationCode : EditText
    private lateinit var verificationEmail : EditText

    private fun confirmVerification(userEmail: String, userPass : String, userFirstName : String, userLastName : String){
        val verifyBtn = findViewById<Button>(R.id.buttonVerify)

        verifyBtn.setOnClickListener {
            verifyUser(verificationEmail.text.toString(), verificationCode.text.toString(), userEmail, userPass, userFirstName, userLastName)
        }
    }

    // send verification code form backend
    private fun verifyUser (userEmail: String, userCode : String, userEmail2: String,userPass : String, userFirstName : String, userLastName : String) {
        lifecycleScope.launch(Dispatchers.IO) {
            // handle request to backend.
            try {
                val requestBody = VerificationDetails(userEmail, userCode)
                val response = RetrofitInstance.flexiChargeApi.verifyEmail(requestBody)
                if (response.code() == StatusCode.ok) {
                    lifecycleScope.launch( Dispatchers.Main) {

                        val userInfo: Map<String, String> = mapOf(
                            "email" to userEmail2,
                            "pass" to userPass,
                            "firstName" to userFirstName,
                            "lastName" to userLastName
                        )

                        SignIn(userInfo)
                        val intent = Intent(this@VerifyActivity, LoginActivity::class.java)
                        startActivity(intent)
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


    private fun SignIn(userData : Map<String, String>){
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
                    SendToMain(collectedData)
                }
                else {
                    lifecycleScope.launch (Dispatchers.Main) {

                    }
                }
            }
        }
    }

    private fun SendToMain(userData : Map<String, String>){
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
           firstName =  userData["firstname"],
            lastName = userData["lastName"]
        )
        lifecycleScope.launch(Dispatchers.IO) {
            val updating = RetrofitInstance.flexiChargeApi.updateUserInfo(userData["accessToken"]!!, userInfo)
            if(updating.isSuccessful){

            }

        }
        startActivity(Intent(this, MainActivity::class.java))
        finish()

    }




}
























