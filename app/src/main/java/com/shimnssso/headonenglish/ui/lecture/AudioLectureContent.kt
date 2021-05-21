package com.shimnssso.headonenglish.ui.lecture

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import com.shimnssso.headonenglish.room.DatabaseCard
import com.shimnssso.headonenglish.room.DatabaseLecture
import com.shimnssso.headonenglish.utils.CellConverter
import timber.log.Timber

@Composable
fun AudioLectureContent(
    lecture: DatabaseLecture?,
    cards: List<DatabaseCard>,
    modifier: Modifier = Modifier
) {
    // This is the official way to access current context from Composable functions
    val context = LocalContext.current

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    // We only want to react to changes in sourceUrl.
    // This effect will be executed at each commit phase if
    // [sourceUrl] has changed.
    LaunchedEffect(lecture) {
        Timber.i("LaunchedEffect. lecture: %s", lecture)
        Timber.i("LaunchedEffect. cards: %s", cards)
    }

    DisposableEffect(Unit) {
        Timber.d("DisposableEffect setup")
        onDispose {
            Timber.i("onDispose!!")
        }
    }

    Column {
        LazyColumn(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            items(cards) { card ->
                if (card.id % 10 == 1 && card.id != 1) {
                    Divider(
                        thickness = 2.dp,
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = MaterialTheme.colors.surface
                    )
                }
                val spellingCell = CellConverter.fromJson(card.spelling!!)
                FormattedText(cell = spellingCell)
                Text(
                    text = card.meaning ?: "", style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            item {
                Spacer(Modifier.height(48.dp))
            }
        }
    }
}
