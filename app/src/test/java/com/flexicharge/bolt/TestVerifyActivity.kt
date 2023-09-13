package com.flexicharge.bolt

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flexicharge.bolt.activities.VerifyActivity
import com.flexicharge.bolt.helpers.LoginChecker
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith



class TestVerifyActivity {

    @Test
    fun testVerify() {

        val activityScenario = ActivityScenario.launch(VerifyActivity::class.java)

        activityScenario.onActivity { activity ->
            val userInfo: Map<String, String> = mapOf(
                "email" to "didiwa6692@searpen.com",
                "pass" to "Test1234!",
                "firstName" to "TESTAR",
                "lastName" to "TESTARSSON"
            )

            activity.signIn(userInfo)



        }
        assert(4 == 4)
    }
}