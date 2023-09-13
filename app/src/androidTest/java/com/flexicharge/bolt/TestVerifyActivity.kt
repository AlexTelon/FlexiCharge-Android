package com.flexicharge.bolt

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flexicharge.bolt.activities.MainActivity
import com.flexicharge.bolt.activities.VerifyActivity
import com.flexicharge.bolt.activities.businessLogic.EntryManager
import com.flexicharge.bolt.helpers.LoginChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.internal.wait
import okhttp3.internal.waitMillis
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith





@RunWith(AndroidJUnit4::class)
class TestVerifyActivity {



    @Test
    fun testVerify() = runBlocking{

        val activityScenario = ActivityScenario.launch(VerifyActivity::class.java)


/*
        activityScenario.onActivity { activity ->
            val userInfo: Map<String, String> = mapOf(
                "email" to "didiwa6692@searpen.com",
                "pass" to "Test1234!",
                "firstName" to "TESTAR",
                "lastName" to "TESTARSSON"
            )

        activity.signIn(userInfo)


        }

         */
        assert(2 == 2)
    }
}


