package com.shimnssso.headonenglish.ui.lecture

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.SignalCellular0Bar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.shimnssso.headonenglish.model.DomainCard
import com.shimnssso.headonenglish.network.Cell
import com.shimnssso.headonenglish.utils.CellConverter

@Composable
fun RowCard(
    card: DomainCard,
    defaultShowDescription: Boolean,
    defaultShowKeyword: Boolean,
) {
    val isFirst = card.order % 10 == 1

    if (isFirst && card.order != 1) {
        Spacer(Modifier.height(24.dp))
    }
    val topPadding = if (isFirst) 16.dp else 0.dp

    var showDescription by remember { mutableStateOf(defaultShowDescription) }

    LaunchedEffect(defaultShowDescription) {
        showDescription = defaultShowDescription
    }

    val interactionSource = remember { MutableInteractionSource() }

    val spellingCell: Cell = CellConverter.fromJson(card.text)
    val hasNote = !card.note.isNullOrEmpty()
    val hasMemo = !card.memo.isNullOrEmpty()

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .clickable(
                    interactionSource = interactionSource,
                    indication = LocalIndication.current
                ) { showDescription = !showDescription }
                .padding(bottom = 16.dp, top = topPadding)
        ) {
            FormattedText(
                cell = spellingCell,
                modifier = Modifier.padding(horizontal = 8.dp),
                defaultShowKeyword = defaultShowKeyword
            ) {
                // Trigger ripple effect
                val press = PressInteraction.Press(Offset.Zero)
                interactionSource.tryEmit(press)
                interactionSource.tryEmit(PressInteraction.Release(press))
                showDescription = !showDescription
            }

            if (showDescription) {
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

        if (!showDescription && (hasNote || hasMemo)) {
            Icon(
                Icons.Filled.SignalCellular0Bar,
                contentDescription = "temp description",
                modifier = Modifier
                    .size(8.dp)
                    .align(Alignment.BottomEnd)
            )
        } else if (showDescription && (hasNote || hasMemo)) {
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