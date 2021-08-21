package com.shimnssso.headonenglish.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shimnssso.headonenglish.ui.theme.HeadOnEnglishTheme
import timber.log.Timber

@Composable
fun MultiLineRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    data class RowMeta(
        val startItemIndex: Int,
        val maxHeight: Int
    )

    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val rowMetas = mutableListOf<RowMeta>()
        var tempStartItemIndex = 0
        var tempWidthSum = 0
        var tempRowHeight = 0

        Timber.e("constraints.maxWidth: ${constraints.maxWidth}, constraints.maxHeight: ${constraints.maxHeight}")

        // Don't constrain child views further, measure them with given constraints  
        // List of measured children
        val placeables = measurables.mapIndexed { index, measurable ->
            Timber.e("=== index: $index")

            // Measure each child
            val placeable = measurable.measure(constraints)

            if ((tempWidthSum + placeable.width) > constraints.maxWidth) {
                Timber.e("add($tempStartItemIndex, $tempRowHeight)")
                rowMetas.add(RowMeta(tempStartItemIndex, tempRowHeight))
                tempWidthSum = placeable.width
                tempRowHeight = placeable.height
                Timber.e("+${placeable.width}, tempWidthSum: $tempWidthSum")
                Timber.e("placeable.height: ${placeable.height}, tempRowHeight: $tempRowHeight")
                tempStartItemIndex = index
            } else {
                tempWidthSum += placeable.width
                tempRowHeight = tempRowHeight.coerceAtLeast(placeable.height)
                Timber.e("+${placeable.width}, tempWidthSum: $tempWidthSum")
                Timber.e("placeable.height: ${placeable.height}, tempRowHeight: $tempRowHeight")
            }

            placeable
        }
        Timber.e("add($tempStartItemIndex, $tempRowHeight) for last row")
        rowMetas.add(RowMeta(tempStartItemIndex, tempRowHeight))

        // Y of each row, based on the height accumulation of previous rows
        val rowY = IntArray(rowMetas.size) { 0 }
        for (i in 1 until rowMetas.size) {
            rowY[i] = rowY[i - 1] + rowMetas[i - 1].maxHeight
        }

        // Set the size of the parent layout
        layout(constraints.maxWidth, constraints.maxHeight) {
            // x cord we have placed up to, per row
            val rowX = IntArray(rowMetas.size) { 0 }

            val rowMetaIter = rowMetas.iterator()
            var rowMeta = rowMetaIter.next()
            if (rowMetaIter.hasNext()) {
                rowMeta = rowMetaIter.next()
            }
            var curRow = 0

            placeables.forEachIndexed { index, placeable ->
                if (rowMeta.startItemIndex == index) {
                    curRow += 1
                    if (rowMetaIter.hasNext()) {
                        rowMeta = rowMetaIter.next()
                    }
                }
                val heightOffset = (rowMeta.maxHeight - placeable.height) / 2
                placeable.placeRelative(
                    x = rowX[curRow],
                    y = rowY[curRow] + heightOffset
                )
                rowX[curRow] += placeable.width
            }
        }
    }
}

@Preview
@Composable
fun MultiLineRowPreview() {
    HeadOnEnglishTheme {
        val prevTexts = "This is a test sentence. I would like".split(" ")
        val nextTexts = "locate a TextField among Texts.".split(" ")
        MultiLineRow {
            for (text in prevTexts) {
                Text(text, modifier = Modifier.padding(end = 6.dp))
            }
            AnswerTextField(expectedText = "to", Modifier.padding(end = 6.dp))
            for (text in nextTexts) {
                Text(text, modifier = Modifier.padding(end = 6.dp))
            }
        }
    }
}