package com.flexicharge.bolt.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.flexicharge.bolt.R
import com.flexicharge.bolt.adapters.NameAddressViewModel
import com.flexicharge.bolt.databinding.ActivityAccountSettingsBinding
import com.flexicharge.bolt.databinding.ActivityNameAndAddressBinding
import com.flexicharge.bolt.helpers.LoginChecker
import com.flexicharge.bolt.helpers.TextInputType
import com.flexicharge.bolt.helpers.Validator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountSettingsActivity : AppCompatActivity() {
    lateinit var binding: ActivityAccountSettingsBinding
    private val validateHelper = Validator()
    private lateinit var viewModel: NameAddressViewModel
    private lateinit var bindingAccount: ActivityNameAndAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        bindingAccount = ActivityNameAndAddressBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[NameAddressViewModel::class.java]
        val firstname = bindingAccount.nameAndAddressEditFirstName
        val lastName = bindingAccount.nameAndAddressEditLastName
        val phoneNumber = binding.accountSettingsEditPhoneNumber
        val address = bindingAccount.nameAndAddressEditAddress
        val postCode = bindingAccount.nameAndAddressEditPostcode
        val town = bindingAccount.nameAndAddressEditTown
        val sharedPreferences = getSharedPreferences("loginPreference", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("accessToken", "")

        validateHelper.validateUserInput(phoneNumber, TextInputType.isPhoneNumber)

        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getUserData(token!!)
        }

        viewModel.infoDone.observe(this) {
            if (it) {
                updateTextFields(firstname, lastName, phoneNumber, address, postCode, town)
            }
        }

        viewModel.updateFailed.observe(this) {
            if (it) {
                Toast.makeText(this, "Update phone number failed", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.updated.observe(this) {
            if (it) {
                finish()
            }
        }

        setContentView(binding.root)

        binding.buttonUpdateUserInfo.setOnClickListener {
            if (phoneNumber.error == null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.updateUser(
                        token!!,
                        firstname.text.toString(),
                        lastName.text.toString(),
                        phoneNumber.text.toString(),
                        address.text.toString(),
                        postCode.text.toString(),
                        town.text.toString()
                    )
                }
            }
        }

        binding.accountSettingsChangePassword.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }
        binding.loginActivityDeleteAccount.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun updateTextFields(
        firstName: EditText,
        lastName: EditText,
        phoneNumber: EditText,
        address: EditText,
        postCode: EditText,
        town: EditText
    ) {
        val userInfo = viewModel.currentUserInfo.value
        firstName.text = Editable.Factory.getInstance().newEditable(userInfo?.firstName)
        lastName.text = Editable.Factory.getInstance().newEditable(userInfo?.lastName)
        phoneNumber.text = Editable.Factory.getInstance().newEditable(userInfo?.phoneNumber)
        address.text = Editable.Factory.getInstance().newEditable(userInfo?.streetAddress)
        postCode.text = Editable.Factory.getInstance().newEditable(userInfo?.zipCode)
        town.text = Editable.Factory.getInstance().newEditable(userInfo?.city)
        viewModel.toggleInfoDone()
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