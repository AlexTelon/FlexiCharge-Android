package com.flexicharge.bolt.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.flexicharge.bolt.R
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import com.flexicharge.bolt.api.flexicharge.UserDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


class RegisterActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


    }

    val emailIsValid : Boolean = true;
    val passwordIsValid : Boolean = true;
    val phoneNrIsValid : Boolean = true;

    // first take the input from user
    var registerUserEmail            = findViewById<EditText>(R.id.loginActivity_editText_email)
    var registerUserPhoneNr          = findViewById<EditText>(R.id.editTextPhone)
    var registerUserPass             = findViewById<EditText>(R.id.loginActivity_editText_password)
    var registerUserRepeatPass       = findViewById<EditText>(R.id.editTextPasswordRepeat);
    var RegisterErrorHelper          = findViewById<TextView>(R.id.loginActivity_textView_helper)

    //make them into strings
    var RegisterErrorHelperString    = RegisterErrorHelper.text.toString()
    var registerUserPhoneNrString    = registerUserPhoneNr.text.toString()
    var registerUserEmailString      = registerUserEmail.text.toString()
    var registerUserPassString       = registerUserPass.text.toString()
    var registerUserRepeatPassString = registerUserRepeatPass.text.toString()

    fun confirmRegistration(view: View) {
        // Validate user Email


        // Send Post Request To Backend With new user details
        val registerBtn = findViewById<Button>(R.id.buttonRegisterConfirm)
        val agreeCheckBox = findViewById<CheckBox>(R.id.checkBoxTosAgreement)

        registerBtn.setOnClickListener {
            if (agreeCheckBox.isChecked()) {
                sendUserData(registerUserEmailString, registerUserPassString,registerUserPhoneNrString )

            }
        }
    }

    //check if Email is valid
    private fun emailFocusedListener() {
        registerUserEmail.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                RegisterErrorHelperString = validEmail()
            }
        }
    }

    private fun validEmail(): String {
        if(!Patterns.EMAIL_ADDRESS.matcher(registerUserEmailString).matches())
            return "Invalid Email Address"

        return ""
    }

    //check if Pass is valid
    private fun PasswordFocusedListener() {
        registerUserPass.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                RegisterErrorHelperString = validPassword()
            }
        }
    }

    //check if Email is valid
    private fun validPassword(): String {
        if(registerUserPassString.length < 8) {
            return "Minimum 8 Characters Password!"
        }
        return ""
    }

    //check if Phone is valid
    private fun phoneFocusedListener() {
        registerUserPhoneNr.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                RegisterErrorHelperString = validPhone()
            }
        }
    }

    //check if phonenr is valid
    private fun validPhone() : String {
        if(!registerUserPhoneNrString.matches(".*[0-9]".toRegex())){
            return "Must Be all Digits"
        }
        if(registerUserPhoneNrString.length != 10) {
            return "Must Be 10 Digits!"
        }
        return ""
    }


    //function to send users' data to backend

        private fun sendUserData (userEmail : String, userPass : String, userPhoneNr : String  ){
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val requestBody = UserDetails(userEmail, userPass, userPhoneNr)
                val response = RetrofitInstance.flexiChargeApi.registerNewUser(requestBody)
                if (response.isSuccessful) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    lifecycleScope.launch(Dispatchers.Main) {

                    }
                }
            } catch (e: HttpException) {
                lifecycleScope.launch(Dispatchers.Main) {

                }
            } catch (e: IOException) {

            }
        }
        }
        // Handle Backend Reply


        // Proceed to MainActivity upon confirmation




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