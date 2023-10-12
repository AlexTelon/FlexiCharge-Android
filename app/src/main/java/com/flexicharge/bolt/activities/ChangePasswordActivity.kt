package com.flexicharge.bolt.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flexicharge.bolt.databinding.ActivityChangePasswordBinding
import com.flexicharge.bolt.helpers.TextInputType
import com.flexicharge.bolt.helpers.Validator

class ChangePasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityChangePasswordBinding
    private val validateHelper = Validator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newPassword = binding.changePasswordEditNew

        validateHelper.validateUserInput(newPassword, TextInputType.isPassword)

        binding.buttonUpdatePassword.setOnClickListener {
        }
        binding.buttonForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}