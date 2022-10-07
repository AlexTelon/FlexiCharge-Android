package com.flexicharge.bolt.activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.flexicharge.bolt.R
import com.flexicharge.bolt.helpers.StatusCode
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import com.flexicharge.bolt.api.flexicharge.VerificationDetails
import com.flexicharge.bolt.databinding.ActivityRegisterBinding
import com.flexicharge.bolt.databinding.ActivityVerifyEmailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


class VerifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerifyEmailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_email)
        binding = ActivityVerifyEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        verificationCode = binding.verifyActivityEditTextCode
        verificationEmail = binding.verifyActivityEditTextEmail


        confirmVerification()
    }

    private lateinit var verificationCode : EditText
    private lateinit var verificationEmail : EditText

    private fun confirmVerification(){
        val verifyBtn = findViewById<Button>(R.id.buttonVerify)

        verifyBtn.setOnClickListener {
            verifyUser(verificationEmail.text.toString(), verificationCode.text.toString())
        }
    }

    // send verification code form backend
    private fun verifyUser (userEmail: String, userCode : String) {
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


}
























