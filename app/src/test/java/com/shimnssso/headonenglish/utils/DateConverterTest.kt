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


    @Test
    fun isDateBaseTest() {
        val withoutDay = "2021-05-20"
        val ret = DateConverter.isDateBase(withoutDay)
        println(ret)
        Assert.assertEquals(true, ret)

        val normalText = "123"
        val ret2 = DateConverter.isDateBase(normalText)
        println(ret2)
        Assert.assertEquals(false, ret2)
    }

    @Test
    fun getDateStrFromLongTest() {
        val systemTime = 1649297470436L  // 2022-04-07
        val ret2 = DateConverter.getDateStrFromLong(systemTime)
        println(ret2)
        Assert.assertEquals("2022-04-07", ret2)
    }
}