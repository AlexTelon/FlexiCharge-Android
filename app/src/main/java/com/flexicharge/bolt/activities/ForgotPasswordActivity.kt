package com.flexicharge.bolt.activities


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import com.flexicharge.bolt.R
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import com.flexicharge.bolt.databinding.ActivityForgotPasswordBinding
import com.flexicharge.bolt.helpers.TextInputType
import com.flexicharge.bolt.helpers.Validator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private val validator = Validator()
    private var emailAddress = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val emailEdittext :EditText = binding.resetActivityEditTextEmail
        val error = binding.resetPassActivityErrorMessage
        validator.validateUserInput(emailEdittext, TextInputType.isEmail)
        binding.buttonConfirmRecoverPassword.setOnClickListener {
            emailAddress = emailEdittext.text.toString()
            lifecycleScope.launch(Dispatchers.Main) {
                try {
                    val response = RetrofitInstance.flexiChargeApi.resetPass(emailAddress)
                    if (response.code() == 200) {
                        if (emailAddress.isEmpty()){
                            error.text = "Con not be empty"
                        }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()){
                            error.text = "Invalid email"
                        }
                        else{
                            navigateToConfirmEmail(emailAddress)
                        }
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
    private fun navigateToConfirmEmail(emailAdd : String){
        val intent = Intent(this, ConfirmEmailActivity::class.java)
        intent.putExtra("emailAddress", emailAdd)
        startActivity(intent)
    }
}