package com.example.util

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Utility for Persian (Jalali / Shamsi) calendar conversion, formatting,
 * day of week calculation, and week navigation.
 */
data class JalaliDate(
    val year: Int,
    val month: Int,
    val day: Int
) : Comparable<JalaliDate> {
    val monthName: String
        get() = PersianDateHelper.monthNames.getOrElse(month - 1) { "" }

    fun format(includeMonthName: Boolean = false): String {
        return if (includeMonthName) {
            "${day.toPersianDigits()} $monthName ${year.toPersianDigits()}"
        } else {
            String.format("%04d/%02d/%02d", year, month, day).toPersianDigits()
        }
    }

    fun toStandardString(): String {
        return String.format("%04d/%02d/%02d", year, month, day)
    }

    fun toKey(): String {
        return String.format("%04d-%02d-%02d", year, month, day)
    }

    override fun compareTo(other: JalaliDate): Int {
        if (year != other.year) return year.compareTo(other.year)
        if (month != other.month) return month.compareTo(other.month)
        return day.compareTo(other.day)
    }
}

object PersianDateHelper {
    val monthNames = listOf(
        "فروردین", "اردیبهشت", "خرداد",
        "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر",
        "دی", "بهمن", "اسفند"
    )

    // Persian week days: Saturday to Friday
    val weekDayNames = listOf(
        "شنبه",
        "یکشنبه",
        "دوشنبه",
        "سه‌شنبه",
        "چهارشنبه",
        "پنج‌شنبه",
        "جمعه"
    )

    /**
     * Gets today's date directly from the user's device/system clock.
     */
    fun getEffectiveToday(): LocalDate {
        return LocalDate.now()
    }

    /**
     * Standard, astronomically accurate Gregorian to Jalali conversion algorithm.
     * Accurately converts 2026-10-02 to 1405-07-10 (۱۰ مهر ۱۴۰۵).
     */
    fun gregorianToJalali(localDate: LocalDate): JalaliDate {
        val gy = localDate.year
        val gm = localDate.monthValue
        val gd = localDate.dayOfMonth

        val gDaysInMonth = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
        val gy2 = if (gm > 2) (gy + 1) else gy
        var days = 355666 + (365 * gy) + ((gy2 + 3) / 4) - ((gy2 + 99) / 100) + ((gy2 + 399) / 400) + gd + gDaysInMonth[gm - 1]
        var jy = -1595 + (33 * (days / 12053))
        days %= 12053
        jy += 4 * (days / 1461)
        days %= 1461
        if (days > 365) {
            jy += (days - 1) / 365
            days = (days - 1) % 365
        }
        val jm = if (days < 186) 1 + (days / 31) else 7 + ((days - 186) / 30)
        val jd = 1 + (if (days < 186) (days % 31) else ((days - 186) % 30))
        return JalaliDate(jy, jm, jd)
    }

    /**
     * Standard Jalali to Gregorian conversion algorithm.
     */
    fun jalaliToGregorian(jYear: Int, jMonth: Int, jDay: Int): LocalDate {
        val jy = jYear + 1595
        var days = -355668 + (365 * jy) + ((jy / 33) * 8) + (((jy % 33) + 3) / 4) + jDay +
                (if (jMonth < 7) ((jMonth - 1) * 31) else (((jMonth - 7) * 30) + 186))
        var gy = 400 * (days / 146097)
        days %= 146097
        if (days > 36524) {
            gy += 100 * ((--days) / 36524)
            days %= 36524
            if (days >= 365) days++
        }
        gy += 4 * (days / 1461)
        days %= 1461
        if (days > 365) {
            gy += (days - 1) / 365
            days = (days - 1) % 365
        }
        val gdInMonths = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        var gm = 0
        val isLeap = (gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0)
        if (isLeap) gdInMonths[1] = 29
        while (gm < 12 && days >= gdInMonths[gm]) {
            days -= gdInMonths[gm]
            gm++
        }
        return LocalDate.of(gy, gm + 1, days + 1)
    }

    /**
     * Index of day of week: Saturday = 0, Sunday = 1, ..., Friday = 6
     */
    fun getPersianDayOfWeekIndex(localDate: LocalDate): Int {
        return when (localDate.dayOfWeek) {
            java.time.DayOfWeek.SATURDAY -> 0
            java.time.DayOfWeek.SUNDAY -> 1
            java.time.DayOfWeek.MONDAY -> 2
            java.time.DayOfWeek.TUESDAY -> 3
            java.time.DayOfWeek.WEDNESDAY -> 4
            java.time.DayOfWeek.THURSDAY -> 5
            java.time.DayOfWeek.FRIDAY -> 6
        }
    }

    fun getDayOfWeekName(localDate: LocalDate): String {
        return weekDayNames[getPersianDayOfWeekIndex(localDate)]
    }

    /**
     * Returns the 7 LocalDates representing the Persian week (Saturday to Friday)
     * containing the given reference date.
     */
    fun getWeekDates(referenceDate: LocalDate): List<LocalDate> {
        val currentDayIndex = getPersianDayOfWeekIndex(referenceDate)
        val saturday = referenceDate.minusDays(currentDayIndex.toLong())
        return (0..6).map { saturday.plusDays(it.toLong()) }
    }

    /**
     * Calculates days until next Konkur exam (Tir).
     */
    fun getDaysUntilKonkur(currentDate: LocalDate, targetExamDate: LocalDate? = null): Long {
        val target = targetExamDate ?: run {
            val jToday = gregorianToJalali(currentDate)
            val targetYear = if (jToday.month > 4 || (jToday.month == 4 && jToday.day > 10)) {
                jToday.year + 1
            } else {
                jToday.year
            }
            jalaliToGregorian(targetYear, 4, 10)
        }
        val diff = ChronoUnit.DAYS.between(currentDate, target)
        return if (diff < 0) 0 else diff
    }

    /**
     * Parses standard string "YYYY/MM/DD" or "YYYY-MM-DD" into JalaliDate.
     */
    fun parseJalaliDate(input: String): JalaliDate? {
        val normalized = input.toLatinDigits().replace("-", "/").trim()
        val parts = normalized.split("/")
        if (parts.size == 3) {
            val y = parts[0].toIntOrNull() ?: return null
            val m = parts[1].toIntOrNull() ?: return null
            val d = parts[2].toIntOrNull() ?: return null
            if (m in 1..12 && d in 1..31) {
                return JalaliDate(y, m, d)
            }
        }
        return null
    }
}

/**
 * Extension function to convert Latin digits to Persian digits.
 */
fun String.toPersianDigits(): String {
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    val sb = StringBuilder()
    for (ch in this) {
        if (ch in '0'..'9') {
            sb.append(persianDigits[ch - '0'])
        } else {
            sb.append(ch)
        }
    }
    return sb.toString()
}

/**
 * Extension function to convert Persian/Arabic digits to Latin digits.
 */
fun String.toLatinDigits(): String {
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    val sb = StringBuilder()
    for (ch in this) {
        val pIdx = persianDigits.indexOf(ch)
        val aIdx = arabicDigits.indexOf(ch)
        if (pIdx != -1) {
            sb.append(pIdx)
        } else if (aIdx != -1) {
            sb.append(aIdx)
        } else {
            sb.append(ch)
        }
    }
    return sb.toString()
}

fun Int.toPersianDigits(): String = this.toString().toPersianDigits()
fun Long.toPersianDigits(): String = this.toString().toPersianDigits()
