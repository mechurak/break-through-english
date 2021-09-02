package com.shimnssso.headonenglish.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shimnssso.headonenglish.ui.theme.HeadOnEnglishTheme

@Composable
fun MultiLineRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val rowStartIdxList = mutableListOf(0)
        var rowHeight = 0
        var tempWidthSum = 0

        // Don't constrain child views further, measure them with given constraints  
        // List of measured children
        val placeables = measurables.mapIndexed { index, measurable ->
            // Measure each child
            val placeable = measurable.measure(constraints)

            if ((tempWidthSum + placeable.width) > constraints.maxWidth) {
                rowStartIdxList.add(index)
                tempWidthSum = placeable.width
            } else {
                tempWidthSum += placeable.width
                rowHeight = rowHeight.coerceAtLeast(placeable.height)
            }
            placeable
        }

        // Y of each row, based on the height accumulation of previous rows
        val rowY = IntArray(rowStartIdxList.size) { 0 }
        val ROW_BETWEEN = 8
        val overallHeight =
            rowHeight * rowStartIdxList.size + ROW_BETWEEN * (rowStartIdxList.size - 1)
        for (i in 1 until rowStartIdxList.size) {
            rowY[i] = rowY[i - 1] + rowHeight + ROW_BETWEEN
        }

        // Set the size of the parent layout
        layout(constraints.maxWidth, overallHeight) {
            // x cord we have placed up to, per row
            val rowX = IntArray(rowStartIdxList.size) { 0 }

            val rowMetaIter = rowStartIdxList.iterator()
            var rowMeta = rowMetaIter.next()  // 0
            if (rowMetaIter.hasNext()) {
                rowMeta = rowMetaIter.next()
            }
            var curRow = 0

            placeables.forEachIndexed { index, placeable ->
                if (index != 0 && index == rowMeta) {
                    curRow += 1
                    if (rowMetaIter.hasNext()) {
                        rowMeta = rowMetaIter.next()
                    }
                }
                val heightOffset = (rowHeight - placeable.height) / 2
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