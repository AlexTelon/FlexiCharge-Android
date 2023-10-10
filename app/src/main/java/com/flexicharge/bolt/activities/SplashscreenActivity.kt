package com.flexicharge.bolt.activities

import android.Manifest
import android.app.Activity
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.flexicharge.bolt.R
import com.flexicharge.bolt.helpers.LoginChecker

class SplashscreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        Handler(Looper.getMainLooper()).postDelayed({
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
        } else if (!checkP(Manifest.permission.POST_NOTIFICATIONS)) {
            ActivityCompat.requestPermissions(this, ps, permissionRequestCode)
            return
        } else {
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

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                checkPermission()
            }
        }

    private fun checkPermission() {
        if (!isLocationEnabled()) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            // startActivityForResult(intent, requestLocation)
            resultLauncher.launch(intent)
        } else {
            val loginSharedPref = getSharedPreferences("loginPreference", Context.MODE_PRIVATE)
            val isLoggedIn = loginSharedPref.getString("loggedIn", Context.MODE_PRIVATE.toString())
            if (isLoggedIn == "true") {
                LoginChecker.LOGGED_IN = true
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, RegisterActivity::class.java))
            }

            this.finish()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val lm = getSystemService(LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        var networkEnabled = false

        try {
            gpsEnabled = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } catch (ex: Exception) {
            Log.d("splashScreenError", "try gps enabled error")
        }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
            Log.d("splashScreenError", "try network enabled error")
        }
        return (gpsEnabled && networkEnabled)
    }
}