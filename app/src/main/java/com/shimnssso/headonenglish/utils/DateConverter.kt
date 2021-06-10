package com.shimnssso.headonenglish.utils

import java.text.SimpleDateFormat
import java.util.Locale

object DateConverter {

    // Date and Time Patterns (https://developer.android.com/reference/java/text/SimpleDateFormat#date-and-time-patterns)
    private val formatter = SimpleDateFormat("yyyy-MM-dd")
    private val formatter2 = SimpleDateFormat("yyyy-MM-dd (EEE)")
    private val formatter3 = SimpleDateFormat("w")  // Week in year
    private val formatter4 = SimpleDateFormat("EEE", Locale.US)

    fun withDayName(withoutDay: String): String {
        val tempDate = formatter.parse(withoutDay)
        return formatter2.format(tempDate!!)
    }

    fun weekInYear(withoutDay: String): Int {
        val tempDate = formatter.parse(withoutDay)
        return formatter3.format(tempDate!!).toInt()
    }

    fun isMonday(withoutDay: String): Boolean {
        val tempDate = formatter.parse(withoutDay)
        val day = formatter4.format(tempDate!!)
        return "Mon" == day
    }

    private val dateRegex = Regex("^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$")
    fun isDateBase(inputStr: String): Boolean {
        return dateRegex.containsMatchIn(inputStr)
    }
}