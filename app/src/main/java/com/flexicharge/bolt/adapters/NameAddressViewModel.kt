package com.flexicharge.bolt.adapters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import com.flexicharge.bolt.api.flexicharge.UserFullDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NameAddressViewModel : ViewModel() {
    private val _hintText = MutableLiveData<String>()
    private val _updated = MutableLiveData(false)

    val hintText : LiveData<String>
    get() = _hintText

    val updated : LiveData<Boolean>
        get() = _updated

    private suspend fun updateUserAPI(token : String, userDetails : UserFullDetails){

        val update = RetrofitInstance.flexiChargeApi.updateUserInfo("Bearer $token",userDetails)
        if (update.isSuccessful){
            withContext(Dispatchers.Main){
                _updated.value = true
            }

        }
    }

    suspend fun updateUser(token: String, firstName : String, address :String, postcode : String, town : String){
        val firstNameChecked    = validateInput(firstName)
        val addressChecked      = validateInput(address)
        val postcodeChecked     = validateInput(postcode)
        val townChecked         = validateInput(town)

        val userInfo = UserFullDetails(
            firstNameChecked,
            "TESTARSSON",
            "",
            addressChecked,
            postcodeChecked,
            townChecked,
            ""
        )
        updateUserAPI(token, userInfo)

    }

    private fun validateInput(name : String?) : String{
        return name ?: ""
    }
}