package com.flexicharge.bolt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Toast

class RegisterSplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_splash)
    }

    fun confirmRegistration(view: View) {
        // Validate user input
        // Check TOS Agreement
        // Communicate with backend
        // Handle Backend Reply
        // Proceed to MainActivity upon confirmation
        val text = "Hello toast!"
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
    }
    fun goToSignIn(view: View) {
        //Go to sign in activity
    }
    fun continueAsGuest(view: View) {
        //Continue to MainActivity
        startActivity(Intent(this, MainActivity::class.java))
    }
}