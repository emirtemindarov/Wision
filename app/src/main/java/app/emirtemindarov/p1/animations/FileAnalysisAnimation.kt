package app.emirtemindarov.p1.animations

import android.graphics.Color.alpha
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.delay
import kotlin.random.Random

// Анимация во время загрузки графа (для файла или папки)
@Composable
fun FileAnalysisAnimation() {
    //val scope = rememberCoroutineScope()

    // Стадии анимации
    var stage by remember { mutableIntStateOf(0) }

    // 10 строк
    val lines = remember {
        List(10) { i ->
            LineData(
                id = i,
                y = 80f + i * 35f,
                highlight = mutableStateOf(false)
            )
        }
    }

    // Узлы
    val nodes = remember { mutableStateListOf<NodeData>() }

    var globalAlpha by remember { mutableIntStateOf(1) }

    // Старт последовательности
    LaunchedEffect(Unit) {
        while (true) {

            // СБРОС СОСТОЯНИЯ
            stage = 0
            globalAlpha = 1
            nodes.clear()
            lines.forEach { it.highlight.value = false }

            delay(400)

            stage = 1
            delay(1200)

            stage = 2
            lines.forEach {
                it.highlight.value = true
                delay(120)
            }
            delay(800)

            stage = 3
            lines.forEach { line ->
                nodes += NodeData(
                    id = line.id,
                    x = 200f,
                    y = line.y
                )
            }
            delay(600)

            stage = 4
            nodes.forEach { node ->
                node.x += (-100..200).random()
                node.y += (-100..100).random()
            }

            delay(400)

            stage = 5

            delay(2000)

        }
    }

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {

        val scale = 1.8f

        val offsetX = (size.width - 0f) / 2f     // примерная ширина сцены
        val offsetY = (size.height + 800f) / 2f    // примерная высота

        withTransform({
            translate(offsetX, offsetY)
            scale(scale, scale)
            alpha(globalAlpha)
        }) {

            // 1–2: рисуем строки
            if (stage <= 2) {
                lines.forEach { line ->
                    drawRoundRect(
                        color = Color.LightGray.copy(alpha = 0.4f),
                        topLeft = Offset(40f, line.y),
                        size = Size(size.width - 680f, 24f),
                        cornerRadius = CornerRadius(6f, 6f)
                    )

                    if (line.highlight.value) {
                        val highlightX = 40f + (0..200).random()
                        val highlightW = (60..120).random()

                        drawRoundRect(
                            color = randomColor(),
                            topLeft = Offset(highlightX.toFloat(), line.y),
                            size = Size(highlightW.toFloat(), 24f),
                            cornerRadius = CornerRadius(6f, 6f)
                        )
                    }
                }

                // Горизонтальное "сканирование"
                if (stage == 1) {
                    val time = (System.currentTimeMillis() % 1500).toFloat() / 1500f
                    val scanY = lerp(80f, 80f + 9 * 35f, time)

                    drawRect(
                        color = Color.White.copy(alpha = 0.2f),
                        topLeft = Offset(0f, scanY),
                        size = Size(size.width, 30f)
                    )
                }
            }

            // 3–5: узлы
            if (stage >= 3) {
                nodes.forEach { node ->
                    drawCircle(
                        color = randomColor(),
                        radius = 16f,
                        center = Offset(node.x, node.y)
                    )
                }
            }

            // 5: связи
            if (stage == 5) {
                nodes.shuffled().zipWithNext().forEach { (a, b) ->
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.4f),
                        start = Offset(a.x, a.y),
                        end = Offset(b.x, b.y),
                        strokeWidth = 2f
                    )
                }
            }

        }
    }
}

data class LineData(
    val id: Int,
    val y: Float,
    val highlight: MutableState<Boolean>
)

data class NodeData(
    val id: Int,
    var x: Float,
    var y: Float
)

fun randomColor(): Color = Color(
    Random.nextFloat() * 0.6f + 0.4f,
    Random.nextFloat() * 0.6f + 0.4f,
    Random.nextFloat() * 0.6f + 0.4f
)
