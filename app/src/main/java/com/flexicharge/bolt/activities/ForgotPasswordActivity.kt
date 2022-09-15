package com.flexicharge.bolt.activities

import ForgetPassFragment.FragmentRecoverPass
import ForgetPassFragment.FragmentRevoverEmailSent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.flexicharge.bolt.Communicator_
import com.flexicharge.bolt.R


class ForgotPasswordActivity : AppCompatActivity(), Communicator_ {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        val fragmentRecoverPass = FragmentRecoverPass()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragmentRecoverPass).commit()
    }
    override fun passDataCom(editTextInput: String) {
        val bundle = Bundle()
        // key name EmailAddress
        bundle.putString("EmailAddress", editTextInput)

        val transaction= this.supportFragmentManager.beginTransaction()
        val fragmentingEmailSent = FragmentRevoverEmailSent()
        fragmentingEmailSent.arguments = bundle
        transaction.replace(R.id.fragment_container, fragmentingEmailSent)
        transaction.commit()
    }

}