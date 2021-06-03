package com.shimnssso.headonenglish.ui.lecture

import org.junit.Assert
import org.junit.Test

class CardModeTest {
    @Test
    fun nextTest() {
        val curMode = CardMode.HideText
        var showKeyword = false
        val nextMode = curMode.next()
        println("curMode: ${curMode.value}, showKeyword: $showKeyword")
        println("nextMode: ${nextMode.value}")
        Assert.assertEquals(nextMode, CardMode.Default)
    }
}