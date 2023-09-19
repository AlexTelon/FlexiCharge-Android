package com.flexicharge.bolt.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
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
    lateinit var binding: ActivityNameAndAddressBinding
    private lateinit var viewModel : NameAddressViewModel
    private val validateHelper = Validator()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNameAndAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("loginPreference", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("accessToken", "")
        val name = binding.nameAndAddressEditName
        val address = binding.nameAndAddressEditAddress
        val postCode = binding.nameAndAddressEditPostcode
        val town = binding.nameAndAddressEditTown
        viewModel = ViewModelProvider(this).get(NameAddressViewModel::class.java)


        viewModel.updated.observe(this) {
            if (it == true) {
                startActivity(Intent(this, ProfileMenuLoggedInActivity::class.java))
                finish()
            }
        }


        binding.nameAndAddressUpdate.setOnClickListener {

            val userInfo = UserFullDetails(
                name.text.toString(),
                "TESTARSSON",
                "",
                "",
                "",
                "",
                ""
            )
            lifecycleScope.launch(Dispatchers.IO){
                viewModel.updateUser(token!!,
                                    name.text.toString(),
                                    address.text.toString(),
                                    postCode.text.toString(),
                                    town.text.toString())
            }


            }



            GlobalScope.launch(Dispatchers.IO) {
                val userInfo = RetrofitInstance.flexiChargeApi.getUserInfo("Bearer $token")
                withContext(Dispatchers.Main){
                    if(userInfo.isSuccessful){
                        validateHelper.validateUserInput(name,TextInputType.isName)
                        val firstName = userInfo.body()?.firstName
                        val firstNameText = Editable.Factory.getInstance().newEditable(firstName)
                        name.text = firstNameText

                        val addressString = userInfo.body()?.streetAddress
                        val addressText = Editable.Factory.getInstance().newEditable(addressString)
                        address.text = addressText

                        val postCodeString = userInfo.body()?.zipCode
                        val postCodeText = Editable.Factory.getInstance().newEditable(postCodeString)
                        postCode.text = postCodeText

                        val townString = userInfo.body()?.city
                        val townText = Editable.Factory.getInstance().newEditable(townString)
                        town.text = townText


                    }
                }
            }








    }
}