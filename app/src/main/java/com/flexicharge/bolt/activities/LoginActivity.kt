package com.flexicharge.bolt.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.flexicharge.bolt.R
import com.flexicharge.bolt.activities.businessLogic.EntryManager
import com.flexicharge.bolt.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private val entryManager = EntryManager()
    private lateinit var binding: ActivityLoginBinding
    private var username = " "
    private var password = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginActivityButtonLogout.setOnClickListener {
            username = binding.loginActivityEditTextEmail.text.toString()
            password = binding.loginActivityEditTextPassword.text.toString()
            entryManager.singUp(username, password) { loginBody, message, isOK ->
                if (isOK) {
                    navigateToMain(loginBody.accessToken, loginBody.userID, loginBody.username, loginBody.email)
                }
                else {
                    Log.d("sharedoPre", message)
                }
            }

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
        }.apply()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}