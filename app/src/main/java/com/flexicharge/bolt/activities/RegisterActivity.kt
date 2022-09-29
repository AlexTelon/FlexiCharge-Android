package com.flexicharge.bolt.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.flexicharge.bolt.R
import com.flexicharge.bolt.activities.businessLogic.EntryManager
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import com.flexicharge.bolt.api.flexicharge.UserDetails
import com.flexicharge.bolt.helpers.TextInputType
import com.flexicharge.bolt.helpers.Validator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import retrofit2.HttpException
import java.io.IOException


class RegisterActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)



        registerUserEmail            = findViewById<EditText>(R.id.loginActivity_editText_email)
        registerUserPass             = findViewById<EditText>(R.id.loginActivity_editText_password)
        registerUserRepeatPass       = findViewById<EditText>(R.id.editTextPasswordRepeat)


        confirmRegistration(  )
    }

    var passwordRepeatIsValid : Boolean = true;

    // first take the input from user
    lateinit var registerUserEmail          : EditText
    lateinit var registerUserPass           : EditText
    lateinit var registerUserRepeatPass     : EditText


    private fun confirmRegistration() {
        var registerBtn = findViewById<Button>(R.id.buttonRegisterConfirm)
        val agreeCheckBox = findViewById<CheckBox>(R.id.checkBoxTosAgreement)

        val validateHelper = Validator()
        validateHelper.validateUserInput(registerUserEmail, TextInputType.isEmail)
        validateHelper.validateUserInput(registerUserPass, TextInputType.isPassword)
        // when register btn is clicked, check if email, pass and phone are valid, then check agreement box, then send request to backend.
        registerBtn.setOnClickListener {
            validRepeatPassword()
            if (agreeCheckBox.isChecked()) {
                if(passwordRepeatIsValid == true) {
                if(registerUserEmail.error == null) {
                    if(registerUserPass.error == null){
                        sendUserData(registerUserEmail.text.toString(), registerUserPass.text.toString())
                    }
                }
            }
            }
        }
    }


    private fun validRepeatPassword(){
        if(registerUserPass.text.toString() != registerUserRepeatPass.text.toString()){
            passwordRepeatIsValid = false
            registerUserRepeatPass.setError("Doesn't match!")
        }
        else {
            passwordRepeatIsValid = true
        }
    }
    //function to send users' data to backend

        private fun sendUserData (userEmail : String, userPass : String){
        lifecycleScope.launch(Dispatchers.IO) {
            // handle request to backend.
            try {
                val requestBody = UserDetails(userEmail, userPass)
                val response = RetrofitInstance.flexiChargeApi.registerNewUser(requestBody)
                if (response.isSuccessful) {
                    lifecycleScope.launch( Dispatchers.Main) {
                        val intent = Intent(this@RegisterActivity, VerifyActivity::class.java)
                        startActivity(intent)
                   }
                }
                else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        Toast.makeText(this@RegisterActivity, "you are in else", Toast.LENGTH_LONG)
                    }
                }
            } catch (e: HttpException) {
                lifecycleScope.launch(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "you are in HTTP Exception", Toast.LENGTH_LONG)
                }
            } catch (e: IOException) {lifecycleScope.launch(Dispatchers.Main) {
                Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_LONG)
                }
            }
        }
        }
    fun goToSignIn(view: View) {
        //Go to sign in activity
        startActivity(Intent(this, LoginActivity::class.java))
    }

    fun continueAsGuest(view: View) {
        //Continue to MainActivity
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply { putBoolean("isGuest", true) }.apply()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}