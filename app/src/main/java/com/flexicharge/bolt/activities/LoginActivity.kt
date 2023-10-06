package com.flexicharge.bolt.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.flexicharge.bolt.R
import com.flexicharge.bolt.activities.businessLogic.EntryManager
import com.flexicharge.bolt.databinding.ActivityLoginBinding
import com.flexicharge.bolt.helpers.LoginChecker
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

        validator.validateUserInput(emailEditText, TextInputType.isEmail)
        validator.validateUserInput(passwordEditText, TextInputType.isPassword)

        binding.loginActivityButtonLogin.setOnClickListener {
            username = emailEditText.text.toString()
            password = passwordEditText.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                entryManager.singIn(username, password) { responseBody, message, isOK ->
                    if (isOK) {
                        LoginChecker.LOGGED_IN = true
                        navigateToMain(
                            responseBody.accessToken,
                            responseBody.user_id,
                            responseBody.username,
                            responseBody.email
                        )
                    } else {
                        lifecycleScope.launch(Dispatchers.Main) {
                            buildAlertDialog(message)
                        }
                    }
                }
            }
        }

        binding.loginActivityTextViewForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun buildAlertDialog(message: String) {
        AlertDialog.Builder(this@LoginActivity)
            .setTitle("Oops!")
            .setMessage(message)
            .setNegativeButton("Ok") { _, _ ->
            }.show()
    }
    private fun navigateToMain(accessToken: String, userId: String, username: String, email: String) {
        val sharedPreferences = getSharedPreferences("loginPreference", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {
            putString("accessToken", accessToken)
            putString("userId", userId)
            putString("userName", username)
            putString("email", email)
            putString("loggedIn", "true")
        }.apply()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}