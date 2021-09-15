package com.flexicharge.bolt.AccountActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.flexicharge.bolt.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun confirmLogin(view: View) {
        Toast.makeText(applicationContext, "TODO: Login!", Toast.LENGTH_SHORT).show()
    }
    fun forgotPassword(view: View) {
        startActivity(Intent(this, ForgotPasswordActivity::class.java))
    }
}