package com.flexicharge.bolt.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.flexicharge.bolt.R

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
    }

    fun recoverPassword(view: View) {
        Toast.makeText(applicationContext, "TODO: Recover Password!", Toast.LENGTH_SHORT).show()
    }
}