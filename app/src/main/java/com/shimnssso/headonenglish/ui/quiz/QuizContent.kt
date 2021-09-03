package com.shimnssso.headonenglish.ui.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shimnssso.headonenglish.model.DomainCard
import com.shimnssso.headonenglish.network.Cell
import com.shimnssso.headonenglish.ui.components.AnswerTextField
import com.shimnssso.headonenglish.ui.components.MultiLineRow
import com.shimnssso.headonenglish.utils.CellConverter
import timber.log.Timber

@Composable
fun QuizContent(
    card: DomainCard,
    success: () -> Unit,
    fail: () -> Unit,
) {
    val spellingCell: Cell = CellConverter.fromJson(card.text)
    val hasHint = !card.hint.isNullOrEmpty()
    val hasNote = !card.note.isNullOrEmpty()
    val hasMemo = !card.memo.isNullOrEmpty()

    var showMemo by remember { mutableStateOf(false) }
    val focusRequests = remember { mutableStateListOf<FocusRequester>() }
    val values = remember { mutableStateListOf<String>() }
    val expects = remember { mutableStateListOf<String>() }
    val quizAnswerPairs = remember { mutableStateListOf<Pair<Boolean, String>>() }
    var wordsSize by remember { mutableStateOf(0) }

    LaunchedEffect(card) {
        showMemo = false

        quizAnswerPairs.clear()
        val tempQuizAnswerPairs = CellConverter.getQuizAnswerPair(spellingCell)
        Timber.d("tempQuizAnswerPairs: $tempQuizAnswerPairs")
        quizAnswerPairs.addAll(tempQuizAnswerPairs)

        expects.clear()
        val tempExpectList = quizAnswerPairs.filter { it.first }.map {
            it.second
        }
        expects.addAll(tempExpectList)
        Timber.d("expects: $expects")

        wordsSize = tempExpectList.size
        Timber.d("wordsSize: $wordsSize")

        values.clear()
        focusRequests.clear()
        for (i in 0 until wordsSize) {
            values.add("")
            focusRequests.add(FocusRequester())
        }
    }

    Box(
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (hasHint) {
                Text(
                    text = card.hint!!,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else {
                Text(
                    text = "no description"
                )
            }

            Spacer(Modifier.height(20.dp))

            if (hasNote || hasMemo) {
                if (showMemo) {
                    Button(onClick = { showMemo = false }) {
                        Text(text = "Hide Memo")
                    }
                    Spacer(Modifier.height(10.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
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
                } else {
                    Button(onClick = { showMemo = true }) {
                        Text(text = "Show Memo")
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                MultiLineRow {
                    var curIdx = -1
                    quizAnswerPairs.map {
                        if (it.first) {
                            curIdx += 1
                            AnswerTextField(
                                modifier = Modifier.padding(end = 6.dp),
                                idx = curIdx,
                                expectedText = it.second,
                                value = values[curIdx],
                                onValueChanged = { wordIdx, newStr ->
                                    values[wordIdx] = newStr
                                },
                                focusRequester = focusRequests[curIdx],
                                onNext = { wordIdx ->
                                    Timber.d("onNext($wordIdx) from \"${it.second}\"")
                                    var nextIdx = wordIdx + 1
                                    if (nextIdx == wordsSize) {
                                        success()
                                    } else {
                                        focusRequests[nextIdx].requestFocus()
                                    }
                                }
                            )
                        } else {
                            Text(text = it.second, modifier = Modifier.padding(end = 6.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(50.dp))
        }

        var tempSuccess = true
        expects.forEachIndexed { idx, expect ->
            tempSuccess = tempSuccess && (values[idx] == expect)
        }
        Timber.e("tempSuccess: $tempSuccess")
        if (tempSuccess) {
            showMemo = true
            Box(
                modifier = Modifier
                    .padding(top = 100.dp)
                    .size(200.dp)
                    .border(20.dp, Color.Red.copy(alpha = 0.3f), RoundedCornerShape(50))
            )
        }
    }
}