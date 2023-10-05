package com.flexicharge.bolt.activities

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.flexicharge.bolt.R
import com.flexicharge.bolt.helpers.LoginChecker

class SplashscreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LOGGIN", "ON Create")
        setContentView(R.layout.activity_splashscreen)


        Handler(Looper.getMainLooper()).postDelayed({
            Log.d("LOGGIN", "requestPermissionLoop ")
            requestPermission()
        }, 1000)
    }


    private val permissionRequestCode = 521
    private fun checkP(p: String): Boolean {
        return ContextCompat.checkSelfPermission(this, p) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        var ps: Array<String> = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val extraPermission = Manifest.permission.POST_NOTIFICATIONS
            ps += extraPermission
        }



        if (!checkP(Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, ps, permissionRequestCode)
            return
        } else {
            Log.d("LOGGIN", "Else check permission")
            checkPermission()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        checkPermission()
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun checkPermission() {
        if (!isLocationEnabled()) {
            Log.d("LOGGIN", "is located Enabled not")
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(intent, REQUEST_LOCATION)
        } else {
            val loginSharedPref = getSharedPreferences("loginPreference", Context.MODE_PRIVATE)
            val isLoggedIn = loginSharedPref.getString("loggedIn", Context.MODE_PRIVATE.toString())
            if (isLoggedIn == "true") {
                LoginChecker.LOGGED_IN = true
                Log.d("LOGGIN", "Send to main activity")
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, RegisterActivity::class.java))
            }

            this.finish()
        }
    }


    private fun isLocationEnabled(): Boolean {
        val lm = getSystemService(LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false

        try {
            gps_enabled = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } catch (ex: Exception) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }
        return (gps_enabled && network_enabled)
    }

    private val REQUEST_LOCATION = 223
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        checkPermission()
    }
}