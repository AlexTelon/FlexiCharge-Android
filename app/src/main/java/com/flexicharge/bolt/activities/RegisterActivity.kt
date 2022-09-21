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
        registerEmailErrorHelper     = findViewById<EditText>(R.id.register_activity_valid_email_helper)
        registerPasswordErrorHelper  = findViewById<EditText>(R.id.register_activity_valid_password_helper)

        // check email valid
        registerUserEmail.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                registerEmailErrorHelper.text = validEmail()
            }
        }
        // check pass valid
        registerUserPass.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                registerPasswordErrorHelper.text = validPassword()
            }
        }

        confirmRegistration(  )
    }

    var emailIsValid : Boolean = true;
    var passwordIsValid : Boolean = true;
    var phoneNrIsValid : Boolean = true;

    // first take the input from user
    lateinit var registerUserEmail          : EditText
    lateinit var registerUserPass           : EditText
    lateinit var registerUserRepeatPass     : EditText
    // these two help with showing if email and password are valid
    lateinit var registerEmailErrorHelper   : TextView
    lateinit var registerPasswordErrorHelper   : TextView



    private fun confirmRegistration() {
        var registerBtn = findViewById<Button>(R.id.buttonRegisterConfirm)
        val agreeCheckBox = findViewById<CheckBox>(R.id.checkBoxTosAgreement)
        // when register btn is clicked, check if email, pass and phone are valid, then check agreement box, then send request to backend.
        registerBtn.setOnClickListener {
            validEmail()
            validPassword()

            if (agreeCheckBox.isChecked()) {
                if(emailIsValid && passwordIsValid  && phoneNrIsValid ) {
                    sendUserData(registerUserEmail.toString(), registerUserPass.toString())
                }

            }
        }
    }


    // check if email is valid
    private fun validEmail(): String {
        if(!Patterns.EMAIL_ADDRESS.matcher(registerUserEmail.toString()).matches()) {
            emailIsValid = false;
            return "Invalid Email Address"
        }
        else
            emailIsValid = true;
        return ""
    }

    //check if Password is valid

    private fun validPassword(): String {
        if(registerUserPass.toString().length < 8) {
            passwordIsValid = false;
            return "Minimum 8 Characters Password!"

        }
        else {
            passwordIsValid = true;
        }
        return ""
    }

    //function to send users' data to backend

        private fun sendUserData (userEmail : String, userPass : String){
        lifecycleScope.launch(Dispatchers.IO) {
            // handle request to backend.
            try {
                val requestBody = UserDetails(userEmail, userPass)
                val response = RetrofitInstance.flexiChargeApi.registerNewUser(requestBody)
                if (response.isSuccessful) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        // Proceed to MainActivity upon confirmation
                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        Toast.makeText(this@RegisterActivity, "you are in else", Toast.LENGTH_LONG)

                    }
                }
            } catch (e: HttpException) {
                lifecycleScope.launch(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "you are in HTTP Exception", Toast.LENGTH_LONG)
                }
            } catch (e: IOException) {
                Toast.makeText(this@RegisterActivity, "you are in IO Exception", Toast.LENGTH_LONG)
            }
        }
        }
        // Handle Backend Reply







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