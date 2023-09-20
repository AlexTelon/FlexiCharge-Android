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
        val firstname           = binding.nameAndAddressEditFirstName
        val lastName            = binding.nameAndAddressEditLastName
        val address             = binding.nameAndAddressEditAddress
        val postCode            = binding.nameAndAddressEditPostcode
        val town                = binding.nameAndAddressEditTown
        val sharedPreferences   = getSharedPreferences("loginPreference", Context.MODE_PRIVATE)
        val token               = sharedPreferences.getString("accessToken", "")

        validateHelper.validateUserInput(firstname, TextInputType.isName)
        validateHelper.validateUserInput(lastName, TextInputType.isName)
        validateHelper.validateUserInput(postCode,TextInputType.isPostCode)
        validateHelper.validateUserInput(town,TextInputType.isTown)
        validateHelper.validateUserInput(address,TextInputType.isAddress)

        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getUserData(token!!)
        }

        viewModel.infoDone.observe(this) {
            if(it)
                updateTextFields(firstname,lastName,address,postCode,town)
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
            if(firstname.error == null && lastName.error == null && postCode.error == null && town.error == null && address.error == null){
                lifecycleScope.launch(Dispatchers.IO){
                    viewModel.updateUser(token!!,
                        firstname.text.toString(),
                        lastName.text.toString(),
                        address.text.toString(),
                        postCode.text.toString(),
                        town.text.toString()
                    )
                }
            }
        }
    }

    private fun updateTextFields(firstName : EditText, lastName : EditText, address : EditText, postCode : EditText, town : EditText){
        val userInfo    = viewModel.currentUserInfo.value

        firstName.text  = Editable.Factory.getInstance().newEditable(userInfo?.firstName)
        lastName.text   = Editable.Factory.getInstance().newEditable(userInfo?.lastName)
        address.text    = Editable.Factory.getInstance().newEditable(userInfo?.streetAddress)
        postCode.text   = Editable.Factory.getInstance().newEditable(userInfo?.zipCode)
        town.text       = Editable.Factory.getInstance().newEditable(userInfo?.city)
        viewModel.toggleInfoDone()
    }



}

