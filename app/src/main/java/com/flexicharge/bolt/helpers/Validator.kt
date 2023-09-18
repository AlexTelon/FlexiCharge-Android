package com.flexicharge.bolt.helpers

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.core.text.isDigitsOnly

class Validator {

    fun validateUserInput(field: EditText, isWhat: String) {

        field.addTextChangedListener(object : TextWatcher {
            val letters = Regex("^[a-zA-ZåÅäÄöÖ]+$")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if(s.isEmpty()) {
                        field.error = "Can not be empty"
                    }
                    when (isWhat) {
                        TextInputType.isEmail ->
                            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(field.text).matches()) {

                                field.error = "Invalid email."
                            }
                            else{
                                field.error = null

                            }

                        TextInputType.isPassword ->
                            if (
                                s.length < 8
                                || s.firstOrNull { it.isDigit() } == null
                                || s.filter { it.isLetter() }.firstOrNull { it.isUpperCase() } == null
                                || s.filter { it.isLetter() }.firstOrNull { it.isLowerCase() } == null
                                || s.firstOrNull { !it.isLetterOrDigit() } == null
                            ) {
                                field.error = "Password must have 8 chars containing upper- and lower case characters, digits and symbols"
                            }
                            else {
                                field.error = null

                            }
                        TextInputType.isConfirmationCode ->
                            if (s.length != 6
                                || !s.isDigitsOnly()
                            ) {
                                field.error = "Conformation code must have 6 digits."
                            }
                            else{
                                field.error = null
                            }
                        TextInputType.isTooLong ->
                            if (s.length > 15) {
                                field.error = "Name can't be longer than 15 characters"
                            }
                            else {
                                field.error = null
                            }
                        TextInputType.isName ->
                            if (s.length > 15) {
                                field.error = "Name can't be longer than 15 characters"
                            }
                            else if(!letters.matches(s)){
                                field.error = "Name can only contain letters"
                            }
                            else {
                                field.error = null
                            }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {   }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {  }

        })
    }
}