package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testPersianDateConversion() {
    val date = java.time.LocalDate.of(2026, 10, 2)
    val jDate = com.example.util.PersianDateHelper.gregorianToJalali(date)
    assertEquals(1405, jDate.year)
    assertEquals(7, jDate.month)
    assertEquals(10, jDate.day)
  }
}
