package com.shimnssso.headonenglish.ui.lecture

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.shimnssso.headonenglish.R

@Composable
fun FocusedHeader(
    showKeyword: Boolean,
    mode: CardMode,
    hasDescription: Boolean,
    surfaceColor: Color,
    onValueChanged: (Boolean, CardMode) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_curtain_closed),
            contentDescription = "curtain closed icon",
            modifier = Modifier
                .padding(4.dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(if (!showKeyword) MaterialTheme.colors.primary else Color.LightGray)
                .clickable(
                    onClick = {
                        onValueChanged(false, mode)
                    })
                .padding(8.dp)
        )

        Image(
            painter = painterResource(R.drawable.ic_curtain_opened),
            contentDescription = "curtain opened icon",
            modifier = Modifier
                .padding(4.dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(if (showKeyword) MaterialTheme.colors.primary else Color.LightGray)
                .clickable(
                    onClick = {
                        onValueChanged(true, mode)
                    })
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        val hideTextBackground =
            if (mode == CardMode.HideText) MaterialTheme.colors.primary else Color.LightGray
        Image(
            painter = painterResource(R.drawable.ic_lightbulb),
            contentDescription = "hide text mode",
            // colorFilter = ColorFilter.tint(color = hidTextTint),
            modifier = Modifier
                .padding(4.dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(hideTextBackground)
                .clickable(
                    onClick = {
                        onValueChanged(false, CardMode.HideText)
                    })
                .padding(8.dp)
        )

        val defaultModeBackground =
            if (mode == CardMode.Default || mode == CardMode.DefaultAgain) MaterialTheme.colors.primary else Color.LightGray
        Image(
            painter = painterResource(R.drawable.ic_book_opened),
            contentDescription = "default mode",
            // colorFilter = ColorFilter.tint(color = defaultModeTint),
            modifier = Modifier
                .padding(4.dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(defaultModeBackground)
                .clickable(
                    onClick = {
                        onValueChanged(showKeyword, CardMode.Default)
                    })
                .padding(8.dp)
        )

        val hideDescriptionBackground = if (hasDescription) {
            if (mode == CardMode.HideDescription) MaterialTheme.colors.primary else Color.LightGray
        } else surfaceColor
        Image(
            painter = painterResource(R.drawable.ic_book_closed),
            contentDescription = "hide memo mode",
            colorFilter = if (hasDescription) null else ColorFilter.tint(color = hideDescriptionBackground),
            modifier = Modifier
                .padding(4.dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(hideDescriptionBackground)
                .clickable(
                    enabled = hasDescription,
                    onClick = {
                        onValueChanged(showKeyword, CardMode.HideDescription)
                    })
                .padding(8.dp)
        )
    }
}