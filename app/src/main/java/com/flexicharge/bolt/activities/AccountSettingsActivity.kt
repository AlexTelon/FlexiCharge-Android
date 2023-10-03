package com.flexicharge.bolt.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.flexicharge.bolt.R
import com.flexicharge.bolt.R.*
import com.flexicharge.bolt.databinding.ActivityAccountSettingsBinding
import com.flexicharge.bolt.helpers.LoginChecker

class AccountSettingsActivity : AppCompatActivity() {
    lateinit var binding: ActivityAccountSettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.accountSettingsChangePassword.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }
        binding.loginActivityDeleteAccount.setOnClickListener {
            showDeleteConfirmationDialog()
        }

    }
    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_dialog_buttons, null)
        builder.setView(dialogView)
        builder.setTitle("Delete Account")
        builder.setMessage("Are you sure you want to delete your Flexicharge user account?")
        val positiveButton = dialogView.findViewById<Button>(R.id.btnDelete)
        val negativeButton = dialogView.findViewById<Button>(R.id.btnCancel)
        val alertDialog = builder.create()
        alertDialog.show()

        positiveButton.setOnClickListener {
            LoginChecker.LOGGED_IN = false
            getSharedPreferences("loginPreference", Context.MODE_PRIVATE).edit().apply {
                clear()
            }.apply()

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        negativeButton.setOnClickListener {
            alertDialog.dismiss()
        }

    }




}