package com.shimnssso.headonenglish.ui.lecture

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shimnssso.headonenglish.R
import com.shimnssso.headonenglish.model.DomainCard
import com.shimnssso.headonenglish.network.Cell
import com.shimnssso.headonenglish.utils.CellConverter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RowCard(
    index: Int,
    card: DomainCard,
    defaultMode: CardMode,
    defaultShowKeyword: Boolean,
    isFocused: Boolean = false,
    changeFocus: (Int, Float) -> Unit = { _, _ -> }
) {
    val isFirst = card.order % 10 == 1

    if (isFirst && card.order != 1) {
        Spacer(Modifier.height(48.dp))
    }
    val topPadding = if (isFirst && !isFocused) 16.dp else 0.dp

    var mode by remember { mutableStateOf(defaultMode) }
    var showKeyword by remember { mutableStateOf(defaultShowKeyword) }

    val interactionSource = remember { MutableInteractionSource() }

    val spellingCell: Cell = CellConverter.fromJson(card.text)
    val hasHint = !card.hint.isNullOrEmpty()
    val hasNote = !card.note.isNullOrEmpty()
    val hasMemo = !card.memo.isNullOrEmpty()

    val surfaceColor = if (isFocused) MaterialTheme.colors.primary.copy(alpha = 0.1f) else MaterialTheme.colors.surface

    var positionY by remember { mutableStateOf(0f) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(defaultMode, defaultShowKeyword) {
        mode = defaultMode
        showKeyword = defaultShowKeyword
        if (isFocused) {
            if (mode == CardMode.HideDescription && !hasHint && !hasNote && !hasMemo) {
                mode = CardMode.Default
            }
            scope.launch {
                delay(200)
                changeFocus(index, positionY)
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned {
                positionY = it.positionInParent().y
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .background(surfaceColor)
                .clickable(
                    interactionSource = interactionSource,
                    indication = LocalIndication.current,
                    enabled = !isFocused
                ) {
                    changeFocus(index, positionY)
                    if (mode == CardMode.HideDescription && !hasHint && !hasNote && !hasMemo) {
                        mode = CardMode.Default
                    }
                }
                .padding(bottom = 16.dp, top = topPadding)
        ) {
            if (isFocused) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_curtain_closed),
                        contentDescription = "curtain closed icon",
                        modifier = Modifier
                            .padding(4.dp)
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (!showKeyword) MaterialTheme.colors.primary else Color.LightGray)
                            .clickable(
                                onClick = {
                                    // Trigger ripple effect
                                    val press = PressInteraction.Press(Offset.Zero)
                                    interactionSource.tryEmit(press)
                                    interactionSource.tryEmit(PressInteraction.Release(press))
                                    showKeyword = false
                                })
                            .padding(8.dp)
                    )

                    Image(
                        painter = painterResource(R.drawable.ic_curtain_opened),
                        contentDescription = "curtain opened icon",
                        modifier = Modifier
                            .padding(4.dp)
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (showKeyword) MaterialTheme.colors.primary else Color.LightGray)
                            .clickable(
                                onClick = {
                                    // Trigger ripple effect
                                    val press = PressInteraction.Press(Offset.Zero)
                                    interactionSource.tryEmit(press)
                                    interactionSource.tryEmit(PressInteraction.Release(press))
                                    showKeyword = true
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
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(hideTextBackground)
                            .clickable(
                                onClick = {
                                    // Trigger ripple effect
                                    val press = PressInteraction.Press(Offset.Zero)
                                    interactionSource.tryEmit(press)
                                    interactionSource.tryEmit(PressInteraction.Release(press))
                                    mode = CardMode.HideText
                                    showKeyword = false
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
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(defaultModeBackground)
                            .clickable(
                                onClick = {
                                    // Trigger ripple effect
                                    val press = PressInteraction.Press(Offset.Zero)
                                    interactionSource.tryEmit(press)
                                    interactionSource.tryEmit(PressInteraction.Release(press))
                                    mode = CardMode.Default
                                })
                            .padding(8.dp)
                    )

                    val hasDescription = hasHint || hasNote || hasMemo
                    val hideDescriptionBackground = if (hasDescription) {
                        if (mode == CardMode.HideDescription) MaterialTheme.colors.primary else Color.LightGray
                    } else surfaceColor
                    Image(
                        painter = painterResource(R.drawable.ic_book_closed),
                        contentDescription = "hide memo mode",
                        colorFilter = if (hasDescription) null else ColorFilter.tint(color = hideDescriptionBackground),
                        modifier = Modifier
                            .padding(4.dp)
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(hideDescriptionBackground)
                            .clickable(
                                enabled = hasDescription,
                                onClick = {
                                    // Trigger ripple effect
                                    val press = PressInteraction.Press(Offset.Zero)
                                    interactionSource.tryEmit(press)
                                    interactionSource.tryEmit(PressInteraction.Release(press))
                                    mode = CardMode.HideDescription
                                })
                            .padding(8.dp)
                    )
                }
            }

            FormattedText(
                cell = spellingCell,
                mode = mode,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp),
                showKeyword = showKeyword
            ) {
                if (!isFocused) {
                    // Trigger ripple effect
                    val press = PressInteraction.Press(Offset.Zero)
                    interactionSource.tryEmit(press)
                    interactionSource.tryEmit(PressInteraction.Release(press))

                    changeFocus(index, positionY)
                    if (mode == CardMode.HideDescription && !hasHint && !hasNote && !hasMemo) {
                        mode = CardMode.Default
                    }
                }
            }

            if (mode != CardMode.HideDescription) {
                if (hasHint) {
                    Text(
                        text = card.hint!!,
                        style = MaterialTheme.typography.caption,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                if (hasNote) {
                    Text(
                        text = card.note!!,
                        style = MaterialTheme.typography.overline,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                if (hasMemo) {
                    Text(
                        text = card.memo!!,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }

        if (mode == CardMode.HideDescription && (hasHint || hasNote || hasMemo)) {
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = "expend icon",
                modifier = Modifier
                    .size(8.dp)
                    .align(Alignment.BottomEnd)
            )
        } else if (mode != CardMode.HideDescription && (hasHint || hasNote || hasMemo)) {
            Icon(
                Icons.Filled.KeyboardArrowUp,
                contentDescription = "shrink icon",
                modifier = Modifier
                    .size(10.dp)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}

@Preview("RowCardPreview", widthDp = 360, heightDp = 120, showBackground = true)
@Composable
fun RowCardPreview() {
    val card = DomainCard(
        date = "2021-06-07",
        order = 1,
        text = "{\"formattedValue\":\"The [ROI] could be better if you invest in ETFs.\",\"textFormatRuns\":[{\"format\":{}},{\"startIndex\":5,\"format\":{\"underline\":true}},{\"startIndex\":8,\"format\":{}},{\"startIndex\":19,\"format\":{\"underline\":true}},{\"startIndex\":22,\"format\":{}},{\"startIndex\":35,\"format\":{\"underline\":true}},{\"startIndex\":39,\"format\":{}},{\"startIndex\":43,\"format\":{\"underline\":true}},{\"startIndex\":47,\"format\":{}}]}",
        hint = "ETF에 투자하는 것이 투자수익률이 더 좋을 수 있어.",
        note = "ROI return on investment ",
        memo = "test memo"
    )
    RowCard(index = 1, card = card, defaultMode = CardMode.Default, defaultShowKeyword = true)
}