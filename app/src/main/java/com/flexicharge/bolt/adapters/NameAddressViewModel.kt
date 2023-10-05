package com.flexicharge.bolt.adapters
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import com.flexicharge.bolt.api.flexicharge.UserFullDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NameAddressViewModel : ViewModel() {
    private val _currentUserInfo    = MutableLiveData<UserFullDetails>()
    private val _updated            = MutableLiveData(false)
    private val _infoDone           = MutableLiveData(false)
    private val _updateFailed       = MutableLiveData(false)

    val currentUserInfo : LiveData<UserFullDetails>
        get() = _currentUserInfo

    val updated : LiveData<Boolean>
        get() = _updated

    val infoDone : LiveData<Boolean>
        get() = _infoDone

    val updateFailed : LiveData<Boolean>
        get() = _updateFailed

    private suspend fun updateUserAPI(token : String, userDetails : UserFullDetails){
        val update = RetrofitInstance.flexiChargeApi.updateUserInfo("Bearer $token",userDetails)
        if (update.isSuccessful){
            withContext(Dispatchers.Main){
                _updated.value = true
            }
        }else{
            withContext(Dispatchers.Main){
                _updateFailed.value = true
            }
        }
    }

    suspend fun getUserData(token: String){
        val download = RetrofitInstance.flexiChargeApi.getUserInfo("Bearer $token")
        if (download.isSuccessful){
            withContext(Dispatchers.Main){
                _currentUserInfo.value = download.body()
                toggleInfoDone()
            }

        }else{
            withContext(Dispatchers.Main){

            }
        }
    }

    fun toggleInfoDone(){
        _infoDone.value = !_infoDone.value!!
    }

    suspend fun updateUser(token: String, firstName : String, lastName : String,phoneNumber: String ,  address :String, postcode : String, town : String){
        val firstNameChecked        = validateInput(firstName)
        val lastNameChecked         = validateInput(lastName)
        val lastPhoneNumberChecked  = validateInput(phoneNumber)
        val addressChecked          = validateInput(address)
        val postcodeChecked         = validateInput(postcode)
        val townChecked             = validateInput(town)

        val userInfo = UserFullDetails(
            firstNameChecked,
            lastNameChecked,
            lastPhoneNumberChecked,
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