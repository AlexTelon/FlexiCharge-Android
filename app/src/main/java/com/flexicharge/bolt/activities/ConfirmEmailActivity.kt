package com.flexicharge.bolt.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.flexicharge.bolt.R
import com.flexicharge.bolt.activities.businessLogic.EntryManager
import com.flexicharge.bolt.databinding.ActivityConfirmEmailBinding
import com.flexicharge.bolt.helpers.TextInputType
import com.flexicharge.bolt.helpers.Validator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConfirmEmailActivity : AppCompatActivity() {
    private lateinit var binding:ActivityConfirmEmailBinding
    private val validator = Validator()
    private var emailAddress_ = " "
    private var newPassword_ = " "
    private var confirmCode_ = ""
    private var confirmPassword_ = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_email)
        binding = ActivityConfirmEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val getEmailAddress = intent.getStringExtra("emailAddress")
        val emailAdd= binding.textViewEmailRecover
        val newPassword = binding.newPassword
        emailAdd.text = getEmailAddress
        val confirmCode = binding.confirmCode
        val error = binding.confirmEmailActivityErrorMessage
        val sendAgainEmail= binding.sendAgainEmailMessage
        val confirmPassword = binding.confirmNewPassword
        validator.validateUserInput(newPassword, TextInputType.isPassword)
        validator.validateUserInput(confirmCode, TextInputType.isConfirmationCode)
        emailAddress_ = emailAdd.text.toString()

        checkRepeatPass()

        binding.buttonConfirm.setOnClickListener {
            newPassword_ = newPassword.text.toString()
            confirmCode_ = confirmCode.text.toString()
            confirmPassword_ = confirmPassword.text.toString()
            lifecycleScope.launch(Dispatchers.Main) {
                EntryManager().confirmResetPass(emailAddress_, newPassword_, confirmCode_){ message, isOk ->
                    if (isOk) {
                        navigateToLogIn()
                    } else {
                        lifecycleScope.launch(Dispatchers.Main) {
                            buildAlertDialog(message)
                        }
                    }
                }
            }
        }
        binding.textViewSendAgain.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                EntryManager().resetPassword(emailAddress_) { message, isOK ->
                    if (isOK) {
                        Toast.makeText(
                            this@ConfirmEmailActivity,
                            "New confirmation code is sent.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        lifecycleScope.launch (Dispatchers.Main) {
                            buildAlertDialog(message)
                        }
                    }
                }
            }
        }
    }

    fun checkRepeatPass() {
        val confirmPassEditText = binding.confirmNewPassword
        val newPasswordEditText = binding.newPassword
        confirmPassEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (confirmPassEditText.text.toString() != newPasswordEditText.text.toString()) {
                        confirmPassEditText.error = "does not match"
                    } else if (confirmPassEditText.text.toString() != newPasswordEditText.text.toString()) {
                        confirmPassEditText.error = null
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun buildAlertDialog(message: String) {
        AlertDialog.Builder(this@ConfirmEmailActivity)
            .setTitle("Oops!")
            .setMessage(message)
            .setNegativeButton("Ok") { _, _ ->

            }.show()
    }

    private fun navigateToLogIn() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
