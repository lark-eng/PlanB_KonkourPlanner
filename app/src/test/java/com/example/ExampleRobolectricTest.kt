package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.PersianDateHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Plan B", appName)
    }

    @Test
    fun `persian date conversion is accurate`() {
        // Test date: 2024-03-20 is 1403-01-01 (Nowruz)
        val testDate = LocalDate.of(2024, 3, 20)
        val jDate = PersianDateHelper.gregorianToJalali(testDate)
        assertEquals(1403, jDate.year)
        assertEquals(1, jDate.month)
        assertEquals(1, jDate.day)
    }

    @Test
    fun `persian week dates contain 7 days starting from Saturday`() {
        val today = PersianDateHelper.getEffectiveToday()
        val weekDates = PersianDateHelper.getWeekDates(today)
        assertEquals(7, weekDates.size)
        val sat = weekDates.first()
        assertEquals(0, PersianDateHelper.getPersianDayOfWeekIndex(sat))
    }

    @Test
    fun `effective today is valid Persian year`() {
        val eff = PersianDateHelper.getEffectiveToday()
        val jToday = PersianDateHelper.gregorianToJalali(eff)
        assertTrue(jToday.year in 1403..1410)
    }
}
