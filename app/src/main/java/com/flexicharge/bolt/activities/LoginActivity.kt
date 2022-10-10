package com.flexicharge.bolt.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.flexicharge.bolt.R
import com.flexicharge.bolt.activities.businessLogic.EntryManager
import com.flexicharge.bolt.databinding.ActivityLoginBinding
import com.flexicharge.bolt.helpers.TextInputType
import com.flexicharge.bolt.helpers.Validator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val validator = Validator()
    private val entryManager = EntryManager()
    private lateinit var binding: ActivityLoginBinding
    private var username = " "
    private var password = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val emailEditText = binding.loginActivityEditTextEmail
        val passwordEditText = binding.loginActivityEditTextPassword
        val error = binding.loginActivityErrorMessage

        validator.validateUserInput(emailEditText, TextInputType.isEmail)
        validator.validateUserInput(passwordEditText, TextInputType.isPassword)

        binding.loginActivityButtonLogout.setOnClickListener {
            username = emailEditText.text.toString()
            password = passwordEditText.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                entryManager.singIn(username, password) { loginBody, message, isOK ->
                    if (isOK) {
                        navigateToMain(loginBody.accessToken, loginBody.user_id, loginBody.username, loginBody.email)
                    }
                    else {
                        lifecycleScope.launch (Dispatchers.Main) {
                            if (message == "Bad Request"){
                                error.text = "Incorrect username or password."
                            } else {
                                error.text = message
                            }
                        }
                    }
                }
            }
        }

        binding.guest.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }

    private fun navigateToMain(accessToken: String, userId: String, username: String, email:String) {
        val sharedPreferences = getSharedPreferences("loginPreference", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {
            putString("accessToken", accessToken)
            putString("userId", userId)
            putString("userName", username)
            putString("email", email)
            putInt("isLoggedIn", 1 )
//            putBoolean("loggedIn", true)
            putString("loggedIn", "true")
        }.apply()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}