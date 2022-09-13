package com.flexicharge.bolt

import androidx.test.core.app.ActivityScenario.launch
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flexicharge.bolt.activities.MainActivity
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestMainActivity {
    @Test fun testUnixToDateTimeIsCorrect() {
        val activityScenario = launch(MainActivity::class.java)
        activityScenario.onActivity { activity ->
            val unixTime = 0
            val dateTime = activity.unixToDateTime(unixTime.toString())
            println("Unix time: %s, dateTime: %s".format(unixTime.toString(), dateTime))
            assert(dateTime == "01/01/01:00")
        }
    }


}