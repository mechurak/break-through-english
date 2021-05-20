package com.shimnssso.headonenglish.utils

import org.junit.Assert
import org.junit.Test

class DateConverterTest {
    @Test
    fun withDayNameTest() {
        val withoutDay = "2021-05-20"
        val ret = DateConverter.withDayName(withoutDay)
        println(ret)
        Assert.assertEquals("2021-05-20 (목)", ret)
    }

    @Test
    fun weekInYearTest() {
        val withoutDay = "2021-05-20"
        val ret = DateConverter.weekInYear(withoutDay)
        println(ret)
        Assert.assertEquals(21, ret)
    }

    @Test
    fun isMonDayTest() {
        val withoutDay = "2021-05-20"
        val ret = DateConverter.isMonday(withoutDay)
        println(ret)
        Assert.assertEquals(false, ret)
    }
}