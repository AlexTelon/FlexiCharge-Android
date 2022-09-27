package com.flexicharge.bolt.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
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

        val emailEditText = binding.loginActivityEditTextEmail
        val passwordEditText = binding.loginActivityEditTextPassword

        validateUserInput(emailEditText, "isEmail")
        validateUserInput(passwordEditText, "isPass")

        binding.loginActivityButtonLogout.setOnClickListener {
            username = emailEditText.text.toString()
            password = passwordEditText.text.toString()
            entryManager.singIn(username, password) { loginBody, message, isOK ->
                if (isOK) {
                    navigateToMain(loginBody.accessToken, loginBody.userID, loginBody.username, loginBody.email)
                }
                else {
                    Log.d("sharedoPre", message)
                }
            }

        }


    }

    private fun validateUserInput(field: EditText, isWhat: String): Boolean {
        val loginButton = binding.loginActivityButtonLogout
        var valid = false
        field.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if(s.isEmpty()) {
                        field.error = "Can not be empty"
                    }
                    when (isWhat) {
                        "isEmail" ->
                            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(field.text).matches()) {
                                loginButton.isEnabled = false
                                field.error = "Invalid email."
                            } else {
                                loginButton.isEnabled = true
                                valid = true
                            }

                        "isPass" ->
                            if (
                                s.length < 8
                                || s.firstOrNull { it.isDigit() } == null
                                || s.filter { it.isLetter() }.firstOrNull { it.isUpperCase() } == null
                                || s.filter { it.isLetter() }.firstOrNull { it.isLowerCase() } == null
                                || s.firstOrNull { !it.isLetterOrDigit() } == null
                            ) {
                                loginButton.isEnabled = false
                                field.error = "Password must have 8 chars containing upper- and lower case characters, digits and symbols"
                            } else {
                                loginButton.isEnabled = true
                                valid = true
                            }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {   }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {  }

        })

        return valid
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