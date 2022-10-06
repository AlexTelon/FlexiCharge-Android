package com.flexicharge.bolt.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.flexicharge.bolt.R
import com.flexicharge.bolt.api.flexicharge.ResetRequestBody
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import com.flexicharge.bolt.databinding.ActivityConfirmEmailBinding
import com.flexicharge.bolt.helpers.TextInputType
import com.flexicharge.bolt.helpers.Validator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ConfirmEmailActivity : AppCompatActivity() {
    private lateinit var binding:ActivityConfirmEmailBinding
    private val validator = Validator()
    private var emailAddress_ = " "
    private var newPassword_ = " "
    private var confirmCode_ = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_email)
        binding = ActivityConfirmEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val getEmailAddress = intent.getStringExtra("emailAddress")
        val emailAdd= binding.textViewEmailRecover
        val newPassword = binding.newPassword
        emailAdd.text = getEmailAddress
        val confirmCode = binding.confirmCode
        val error = binding.confirmEmailActivityErrorMessage
        val sendAgainEmail= binding.sendAgainEmailMessage
        validator.validateUserInput(newPassword, TextInputType.isPassword)
        validator.validateUserInput(confirmCode, TextInputType.isConfirmationCode)
        emailAddress_ = emailAdd.text.toString()
        binding.buttonConfirm.setOnClickListener {
            newPassword_ = newPassword.text.toString()
            confirmCode_ = confirmCode.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val body = ResetRequestBody(emailAddress_, newPassword_, confirmCode_)
                    val response = RetrofitInstance.flexiChargeApi.confReset(body)
                    if (response.code() == 200) {
                        navigateToLogIn()
                    } else{
                        lifecycleScope.launch(Dispatchers.Main){
                            if (response.message() == "Bad Request" ) {
                                error.text = "Incorrect newPassword or conformation code"
                            }else {
                                error.text = response.message()
                            }
                        }
                    }
                } catch (e: HttpException) {
                    error.text ="Internal Server Error"
                } catch (e: IOException) {
                    error.text ="Internal Server Error"
                }
            }
        }
        binding.textViewSendAgain.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                try {
                    val response = RetrofitInstance.flexiChargeApi.resetPass(emailAddress_)
                    if (response.code() == 200) {
                        sendAgainEmail.text = "A message with a verification code has been sent"
                        delay(2000)
                        sendAgainEmail.text = " "
                    } else if (response.code() == 400) {
                        error.text = "Please try later."

                    }
                } catch (e: HttpException) {
                    error.text ="Internal Server Error"
                } catch (e: IOException) {
                    error.text ="Internal Server Error"
                }
            }
        }
    }
    private fun navigateToLogIn() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
    }
