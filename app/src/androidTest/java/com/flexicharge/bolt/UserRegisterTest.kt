package com.flexicharge.bolt

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flexicharge.bolt.activities.RegisterActivity
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestRegisterActivity {

    @Test
    fun testFirstNameAndLastNameIncluded() {
        val activityScenario = ActivityScenario.launch(RegisterActivity::class.java)
        activityScenario.onActivity { activity ->
           activity.registerUserFirstName.setText("Bob")
           activity.registerUserLastName.setText("TheBuilder")

            assert(activity.registerUserFirstName.text.toString() == "Bob")
            assert(activity.registerUserLastName.text.toString() == "TheBuilder")
        }
    }

    @Test
    fun testFirstNameAndLastNameNull() {
        val activityScenario = ActivityScenario.launch(RegisterActivity::class.java)

        activityScenario.onActivity { activity ->
            assert(activity.registerUserFirstName.text.toString().isEmpty())
            assert(activity.registerUserLastName.text.toString().isEmpty())
        }
    }
}
