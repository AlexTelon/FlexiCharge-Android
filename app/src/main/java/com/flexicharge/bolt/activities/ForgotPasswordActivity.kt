package com.flexicharge.bolt.activities


import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.flexicharge.bolt.R
import com.flexicharge.bolt.activities.businessLogic.EntryManager
import com.flexicharge.bolt.databinding.ActivityForgotPasswordBinding
import com.flexicharge.bolt.helpers.TextInputType
import com.flexicharge.bolt.helpers.Validator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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
            lifecycleScope.launch(Dispatchers.IO) {
                EntryManager().resetPassword(emailAddress) { message, isOK ->
                    if (isOK) {
                        navigateToConfirmEmail(emailAddress)
                    } else {
                        lifecycleScope.launch (Dispatchers.Main) {
                            AlertDialog.Builder(this@ForgotPasswordActivity)
                                .setTitle("Oops!")
                                .setMessage(message)
                                .setNegativeButton("Ok") { _, _ ->

                                }.show()
                        }
                    }
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