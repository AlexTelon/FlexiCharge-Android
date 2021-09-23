package com.flexicharge.bolt.AccountActivities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.flexicharge.bolt.MainActivity
import com.flexicharge.bolt.R

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        askPermissions()
    }

    private fun askPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MainActivity.PERMISSION_CODE
            )
            return
        }
    }

    fun confirmRegistration(view: View) {
        // Validate user input
        // Check TOS Agreement
        // Communicate with backend
        // Handle Backend Reply
        // Proceed to MainActivity upon confirmation
        Toast.makeText(applicationContext, "TODO: Register Account!", Toast.LENGTH_SHORT).show()
    }
    fun goToSignIn(view: View) {
        //Go to sign in activity
        startActivity(Intent(this, LoginActivity::class.java))
    }
    fun continueAsGuest(view: View) {
        //Continue to MainActivity
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply { putBoolean("isGuest", true) }.apply()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}