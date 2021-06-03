package com.shimnssso.headonenglish.ui.lecture

import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.SignalCellular0Bar
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.SubtitlesOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.dp
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
        Spacer(Modifier.height(24.dp))
    }
    val topPadding = if (isFirst && !isFocused) 16.dp else 0.dp

    var mode by remember { mutableStateOf(defaultMode) }
    var showKeyword by remember { mutableStateOf(defaultShowKeyword) }

    val interactionSource = remember { MutableInteractionSource() }

    val spellingCell: Cell = CellConverter.fromJson(card.text)
    val hasNote = !card.note.isNullOrEmpty()
    val hasMemo = !card.memo.isNullOrEmpty()

    val surfaceColor = if (isFocused) MaterialTheme.colors.primary.copy(alpha = 0.1f) else MaterialTheme.colors.surface

    var positionY by remember { mutableStateOf(0f) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(defaultMode, defaultShowKeyword) {
        mode = defaultMode
        showKeyword = defaultShowKeyword
        if (isFocused) {
            if (mode == CardMode.HideDescription && !hasNote && !hasMemo) {
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
                    if (mode == CardMode.HideDescription && !hasNote && !hasMemo) {
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
                    IconButton(
                        onClick = {
                            // Trigger ripple effect
                            val press = PressInteraction.Press(Offset.Zero)
                            interactionSource.tryEmit(press)
                            interactionSource.tryEmit(PressInteraction.Release(press))
                            mode = CardMode.HideText
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lightbulb,
                            contentDescription = "temp settings",
                            tint = if (mode == CardMode.HideText) MaterialTheme.colors.primary else Color.LightGray
                        )
                    }
                    IconButton(
                        onClick = {
                            // Trigger ripple effect
                            val press = PressInteraction.Press(Offset.Zero)
                            interactionSource.tryEmit(press)
                            interactionSource.tryEmit(PressInteraction.Release(press))
                            mode = CardMode.Default
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Subtitles,
                            contentDescription = "temp settings",
                            tint = if (mode == CardMode.Default || mode == CardMode.DefaultAgain) MaterialTheme.colors.primary else Color.LightGray
                        )
                    }
                    val hasDescription = hasNote || hasMemo
                    val tint = if (hasDescription) {
                        if (mode == CardMode.HideDescription) MaterialTheme.colors.primary else Color.LightGray
                    } else surfaceColor
                    IconButton(
                        enabled = hasDescription,
                        onClick = {
                            // Trigger ripple effect
                            val press = PressInteraction.Press(Offset.Zero)
                            interactionSource.tryEmit(press)
                            interactionSource.tryEmit(PressInteraction.Release(press))
                            mode = CardMode.HideDescription
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SubtitlesOff,
                            contentDescription = "temp settings",
                            tint = tint
                        )
                    }
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
                    if (mode == CardMode.HideDescription && !hasNote && !hasMemo) {
                        mode = CardMode.Default
                    }
                }
            }

            if (mode != CardMode.HideDescription) {
                if (hasNote) {
                    Text(
                        text = card.note!!,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                if (hasMemo) {
                    Text(
                        text = card.memo!!,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }

        if (mode == CardMode.HideDescription && (hasNote || hasMemo)) {
            Icon(
                Icons.Filled.SignalCellular0Bar,
                contentDescription = "temp description",
                modifier = Modifier
                    .size(8.dp)
                    .align(Alignment.BottomEnd)
            )
        } else if (mode != CardMode.HideDescription && (hasNote || hasMemo)) {
            Icon(
                Icons.Filled.KeyboardArrowUp,
                contentDescription = "temp description",
                modifier = Modifier
                    .size(10.dp)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}