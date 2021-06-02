package com.shimnssso.headonenglish.ui.lecture

sealed class CardMode(
    val value: Int
) {

    // 0 -> 1 -> 2 -> 3 -> 0
    object HideText : CardMode(0)
    object Default : CardMode(1)
    object HideDescription : CardMode(2)
    object DefaultAgain : CardMode(3)

    fun next(showKeyword: Boolean): CardMode {
        return if (showKeyword) {
            when (this) {
                HideDescription -> Default
                else -> HideDescription
            }
        } else {when (this) {
                HideText -> Default
                Default -> HideDescription
                HideDescription -> DefaultAgain
                DefaultAgain -> HideText
            }
        }
    }

    fun refresh(showKeyword: Boolean): CardMode {
        return if (showKeyword) {
            when (this) {
                HideText -> Default
                else -> this
            }
        } else {
            this
        }
    }
}