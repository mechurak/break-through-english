package com.shimnssso.headonenglish.ui.lecture

sealed class CardMode(
    val value: Int
) {
    // 0 -> 1 -> 2 -> 3 -> 0
    object HideText : CardMode(0)
    object Default : CardMode(1)
    object HideDescription : CardMode(2)
    object DefaultAgain : CardMode(3)

    fun next(): CardMode {
        return when (this) {
            HideText -> Default
            Default -> HideDescription
            HideDescription -> DefaultAgain
            DefaultAgain -> HideText
            else -> Default
        }
    }
}