package com.flexicharge.bolt.helpers

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class Validator {

    fun validateUserInput(field: EditText, isWhat: String) {
        var valid = false
        field.addTextChangedListener(object : TextWatcher {
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
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {   }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {  }

        })
    }
}