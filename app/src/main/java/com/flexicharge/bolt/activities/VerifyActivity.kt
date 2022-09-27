package com.flexicharge.bolt.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.flexicharge.bolt.R
import com.flexicharge.bolt.StatusCode
import com.flexicharge.bolt.activities.businessLogic.EntryManager
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import com.flexicharge.bolt.api.flexicharge.UserDetails
import com.flexicharge.bolt.api.flexicharge.VerificationDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import retrofit2.HttpException
import java.io.IOException


class VerifyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_email)

        verificationCode = findViewById<EditText>(R.id.verifyActivity_editText_code)
        verificationEmail = findViewById<EditText>(R.id.verifyActivity_editText_email)

        confirmVerification()
    }

    private lateinit var verificationCode : EditText
    private lateinit var verificationEmail : EditText

    fun confirmVerification(){
        var verifyBtn = findViewById<Button>(R.id.buttonVerify)

        verifyBtn.setOnClickListener {
            verifyUser(verificationEmail.text.toString(), verificationCode.text.toString())
        }
    }

    // send verification code form backend
    fun verifyUser (userEmail: String, userCode : String) {
        lifecycleScope.launch(Dispatchers.IO) {
            // handle request to backend.
            try {
                val requestBody = VerificationDetails(userEmail, userCode)
                val response = RetrofitInstance.flexiChargeApi.verifyEmail(requestBody)
                if (response.code() == StatusCode.ok) {
                    lifecycleScope.launch( Dispatchers.Main) {
                        val intent = Intent(this@VerifyActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
                else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        Toast.makeText(this@VerifyActivity, "Code Invalid, Try Again", Toast.LENGTH_LONG)
                    }
                }
            } catch (e: HttpException) {
                lifecycleScope.launch(Dispatchers.Main) {
                    Toast.makeText(this@VerifyActivity, "you are in HTTP Exception", Toast.LENGTH_LONG)
                }
            } catch (e: IOException) {lifecycleScope.launch(Dispatchers.Main) {
                Toast.makeText(this@VerifyActivity, e.message, Toast.LENGTH_LONG)
            }
            }
        }
    }


}
























