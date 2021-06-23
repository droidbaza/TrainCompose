package com.droidbaza.traincompose.components.splash

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.min

@Composable
fun IntelliJSplashScreen(rows: Int =8, columns: Int=13, modifier: Modifier = Modifier.fillMaxSize()) {
    Canvas(modifier = modifier) {
        val cellSize =
            min(this.size.width / columns, this.size.height / rows)
        for (row in 0 until rows) {
            for (column in 0 until columns) {
                translate(
                    left = column * cellSize,
                    top = row * cellSize
                ) {
                    drawIntelliJCell(cellSize)
                }
            }
        }
    }
}

sealed class IntelliJCell {
    object Circle : IntelliJCell()

    sealed class Quadrant(val startAngle: Float, val topLeftOffset: Offset) :
        IntelliJCell() {
        object TopLeft : Quadrant(180f, Offset.Zero)
        object TopRight : Quadrant(270f, -Offset(1f, 0f))
        object BottomLeft : Quadrant(90f, -Offset(0f, 1f))
        object BottomRight : Quadrant(0f, -Offset(1f, 1f))
    }
}

val cellTypes = listOf(
    IntelliJCell.Circle,
    IntelliJCell.Quadrant.TopLeft,
    IntelliJCell.Quadrant.TopRight,
    IntelliJCell.Quadrant.BottomLeft,
    IntelliJCell.Quadrant.BottomRight
)

val cellColors = listOf(
    Color(0xFFFF7000),
    Color(0xFF007EFF),
    Color(0xFFFF0058)
)

fun DrawScope.drawIntelliJCell(cellSize: Float) {
    val cell = cellTypes.random()
    val color = cellColors.random()
    when (cell) {
        is IntelliJCell.Circle -> {
            drawCircle(
                color = color,
                radius = cellSize / 2,
                center = Offset(cellSize, cellSize).div(2f)
            )
        }
        is IntelliJCell.Quadrant -> {
            drawArc(
                color = color,
                startAngle = cell.startAngle,
                sweepAngle = 90f,
                useCenter = true,
                topLeft = cell.topLeftOffset.times(cellSize),
                size = Size(cellSize, cellSize).times(2f)
            )
        }
    }
}

@Preview(widthDp = 1280, heightDp = 800)
@Composable
fun Preview() {
    IntelliJSplashScreen(rows = 8, columns = 13, modifier = Modifier.fillMaxSize())
}