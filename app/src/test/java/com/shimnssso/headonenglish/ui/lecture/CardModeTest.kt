package com.shimnssso.headonenglish.ui.lecture

import org.junit.Assert
import org.junit.Test

class CardModeTest {
    @Test
    fun nextTest() {
        val curMode = CardMode.HideText
        var showKeyword = false
        val nextMode = curMode.next(showKeyword)
        println("curMode: ${curMode.value}, showKeyword: $showKeyword")
        println("nextMode: ${nextMode.value}")
        Assert.assertEquals(nextMode, CardMode.Default)

        showKeyword = true
        val nextMode2 = curMode.next(showKeyword)
        println("curMode: ${curMode.value}, showKeyword: $showKeyword")
        println("nextMode: ${nextMode2.value}")
        Assert.assertEquals(nextMode2, CardMode.HideDescription)
    }

    @Test
    fun refreshTest() {
        val curMode = CardMode.HideText
        var showKeyword = false
        val nextMode = curMode.refresh(showKeyword)
        println("curMode: ${curMode.value}, showKeyword: $showKeyword")
        println("nextMode: ${nextMode.value}")
        Assert.assertEquals(nextMode, CardMode.HideText)

        showKeyword = true
        val nextMode2 = curMode.refresh(showKeyword)
        println("curMode: ${curMode.value}, showKeyword: $showKeyword")
        println("nextMode: ${nextMode2.value}")
        Assert.assertEquals(nextMode2, CardMode.Default)

    }
}