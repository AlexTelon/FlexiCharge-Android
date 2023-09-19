package com.flexicharge.bolt.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.flexicharge.bolt.adapters.NameAddressViewModel
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import com.flexicharge.bolt.api.flexicharge.UserFullDetails
import com.flexicharge.bolt.databinding.ActivityNameAndAddressBinding
import com.flexicharge.bolt.helpers.TextInputType
import com.flexicharge.bolt.helpers.Validator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NameAddressActivity : AppCompatActivity() {
    lateinit var binding            : ActivityNameAndAddressBinding
    private lateinit var viewModel  : NameAddressViewModel
    private val validateHelper      = Validator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding                 = ActivityNameAndAddressBinding.inflate(layoutInflater)
        viewModel               = ViewModelProvider(this).get(NameAddressViewModel::class.java)
         val name               = binding.nameAndAddressEditName
         val address            = binding.nameAndAddressEditAddress
         val postCode           = binding.nameAndAddressEditPostcode
         val town               = binding.nameAndAddressEditTown
        val sharedPreferences   = getSharedPreferences("loginPreference", Context.MODE_PRIVATE)
         val token              = sharedPreferences.getString("accessToken", "")

        val firstNameError = validateHelper.validateUserInput(name, TextInputType.isName)

        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getUserData(token!!)
        }

        viewModel.infoDone.observe(this) {
            if(it)
                updateTextFields(name,address,postCode,town)
        }

        viewModel.updateFailed.observe(this){
            if (it)
                Toast.makeText(this, "Update profile failed", Toast.LENGTH_LONG).show()
        }

        viewModel.updated.observe(this) {
            if (it) {
                startActivity(Intent(this, ProfileMenuLoggedInActivity::class.java))
                finish()
            }
        }

        setContentView(binding.root)


        binding.nameAndAddressUpdate.setOnClickListener {
            if(name.error == null){
                lifecycleScope.launch(Dispatchers.IO){
                    viewModel.updateUser(token!!,
                        name.text.toString(),
                        address.text.toString(),
                        postCode.text.toString(),
                        town.text.toString()
                    )
                }
            }
        }
    }

    private fun updateTextFields(name : EditText, address : EditText, postCode : EditText, town : EditText){
        val userInfo    = viewModel.currentUserInfo.value

        name.text       = Editable.Factory.getInstance().newEditable(userInfo?.firstName)
        address.text    = Editable.Factory.getInstance().newEditable(userInfo?.streetAddress)
        postCode.text   = Editable.Factory.getInstance().newEditable(userInfo?.zipCode)
        town.text       = Editable.Factory.getInstance().newEditable(userInfo?.city)
        viewModel.toggleInfoDone()
    }

}

