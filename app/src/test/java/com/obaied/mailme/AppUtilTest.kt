package com.obaied.mailme

import com.obaied.mailme.util.AppUtil
import org.junit.Test
import kotlin.test.assert

/**
 * Created by ab on 11.11.17.
 */
class AppUtilTest {
    @Test
    fun testHumanReadableString() {
        val millisForOneSecond = 1000L
        assert(AppUtil.getHumanReadableDuration(millisForOneSecond).equals("00:00:01"))

        val millisForOneMinute = 60000L
        assert(AppUtil.getHumanReadableDuration(millisForOneMinute).equals("00:01:00"))

        val millisForOneHour = 3600000L
        assert(AppUtil.getHumanReadableDuration(millisForOneHour).equals("01:00:00"))

        val millisForCustom = 65000L
        assert(AppUtil.getHumanReadableDuration(millisForCustom).equals("00:01:05"))
    }
}