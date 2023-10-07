package com.flexicharge.bolt

import androidx.test.core.app.ActivityScenario.launch
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flexicharge.bolt.activities.MainActivity
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestMainActivity {
    @Test fun testValidateChargerIdIsCorrect() {
        val activityScenario = launch(MainActivity::class.java)

        fun getIdString(id: Int): String {
            val ones = id % 10
            val tens = (id / 10) % 10
            val hundreds = (id / 100) % 10
            val thousands = (id / 1000) % 10
            val tenThousands = (id / 10000) % 10
            val hundredThousands = (id / 100000) % 10

            return "%d%d%d%d%d%d".format(
                hundredThousands,
                tenThousands,
                thousands,
                hundreds,
                tens,
                ones
            )
        }
        activityScenario.onActivity { activity ->
            for (i in 0..999999) {
                val idString = getIdString(i)
                assert(activity.validateChargerId(idString))
            }

            assert(!activity.validateChargerId("-00000"))
            assert(!activity.validateChargerId("00000000"))
            assert(!activity.validateChargerId("0000000"))
            assert(!activity.validateChargerId("00000"))
            assert(!activity.validateChargerId("0000"))
            assert(!activity.validateChargerId("000"))
            assert(!activity.validateChargerId("00"))
            assert(!activity.validateChargerId("0"))
        }
    }
}