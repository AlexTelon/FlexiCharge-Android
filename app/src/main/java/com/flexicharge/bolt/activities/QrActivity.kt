package com.flexicharge.bolt.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.flexicharge.bolt.R
import java.io.Serializable


class QrActivity : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 123)
        } else {
            scanQR()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 123) {
            if (!mutableListOf(grantResults).contains<Serializable>(PackageManager.PERMISSION_DENIED)) {
                scanQR()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun scanQR() {

        val scannerView: CodeScannerView = findViewById(R.id.qrActivity_codeScannerView)
        codeScanner = CodeScanner(this, scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.startPreview()
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false
        scannerView.setOnClickListener {
            codeScanner.startPreview()

            codeScanner.decodeCallback = DecodeCallback {
                runOnUiThread {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it.text))
                    startActivity(browserIntent)
                }
            }
            codeScanner.errorCallback = ErrorCallback {
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Camera initialization error : ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }

}