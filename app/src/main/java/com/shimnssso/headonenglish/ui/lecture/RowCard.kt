package com.shimnssso.headonenglish.ui.lecture

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimnssso.headonenglish.room.DatabaseCard
import com.shimnssso.headonenglish.utils.CellConverter

@Composable
fun RowCard(
    card: DatabaseCard
) {
    if (card.order % 10 == 1 && card.order != 1) {
        Divider(
            thickness = 2.dp,
            modifier = Modifier.padding(vertical = 12.dp),
            color = MaterialTheme.colors.background
        )
    }
    var showDescription by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
            .clickable { showDescription = !showDescription }
            .padding(bottom = 16.dp)
    ) {
        val spellingCell = CellConverter.fromJson(card.text!!)
        val hasNote = !card.note.isNullOrEmpty()
        val hasMemo = !card.memo.isNullOrEmpty()
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            FormattedText(cell = spellingCell, modifier = Modifier.padding(horizontal = 8.dp))
            if (!showDescription && (hasNote || hasMemo)) {
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    contentDescription = "temp description",
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.TopEnd)
                )
            } else if (showDescription && (hasNote || hasMemo)) {
                Icon(
                    Icons.Filled.KeyboardArrowUp,
                    contentDescription = "temp description",
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.TopEnd)
                )
            }
        }
        if (showDescription) {
            if (hasNote) {
                Text(text = card.note!!, style = MaterialTheme.typography.body2, modifier = Modifier.padding(horizontal = 16.dp))
            }
            if (hasMemo) {
                Text(text = card.memo!!, style = MaterialTheme.typography.body2, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}