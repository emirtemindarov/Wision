package app.emirtemindarov.p1.components

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
//import app.emirtemindarov.p1.assistant.data.Edge
import app.emirtemindarov.p1.assistant.data.GraphModel
import app.emirtemindarov.p1.render.data.RenderGraph
import app.emirtemindarov.p1.render.data.RenderNode
import app.emirtemindarov.p1.utils.colorForType
//import app.emirtemindarov.p1.assistant.data.Node
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/*// заглушка
@Composable
fun GraphRenderPreview(
    scale: Float,
    offset: Offset,
    modifier: Modifier = Modifier
) {
    val fakeGraph = GraphModel(
        nodes = listOf(
            Node(id = "A", label = "Alpha", type = "type1"),
            Node(id = "B", label = "Beta", type = "type1"),
            Node(id = "C", label = "Gamma", type = "type2"),
            Node(id = "D", label = "Delta", type = "type2"),
        ),
        edges = listOf(
            Edge(from = "A", to = "B", type = "link"),
            Edge(from = "A", to = "C", type = "link"),
            Edge(from = "B", to = "D", type = "link"),
            Edge(from = "C", to = "D", type = "link"),
        )
    )

    GraphRenderV1M3(
        graph = fakeGraph,
        scale = scale,
        offset = offset,
        modifier = modifier
    )
}*/

@Composable
fun GraphRenderV1M3(
    graph: GraphModel,
    scale: Float,
    offset: Offset,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
    ) {

        Canvas(modifier = Modifier.matchParentSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // === Фон всего полотна ===
            drawRect(
                color = Color(0xFFE3F2FD), // голубой
                size = size,
                topLeft = Offset.Zero
            )
            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    "Полотно",
                    40f,
                    60f,
                    android.graphics.Paint().apply {
                        textSize = 40f
                        color = android.graphics.Color.BLACK
                    }
                )
            }

            // === Центр полотна ===
            val center = Offset(canvasWidth / 2, canvasHeight / 2)
            drawCircle(
                color = Color(0xFFFFCDD2), // розовый
                radius = 150f,
                center = center
            )
            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    "Центр",
                    center.x,
                    center.y,
                    android.graphics.Paint().apply {
                        textSize = 40f
                        color = android.graphics.Color.BLACK
                    }
                )
            }

            // === Область графа ===
            val radius = min(canvasWidth, canvasHeight) / 3f
            drawCircle(
                color = Color(0xFFC8E6C9), // зелёный
                radius = radius,
                center = center,
                alpha = 0.5f
            )
            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    "Размер графа",
                    center.x - radius,
                    center.y - radius,
                    android.graphics.Paint().apply {
                        textSize = 40f
                        color = android.graphics.Color.DKGRAY
                    }
                )
            }

            // --- Расчёт позиций узлов ---
            val angleStep = (2 * Math.PI / graph.nodes.size).toFloat()
            val nodePositions = mutableMapOf<String, Offset>()
            graph.nodes.forEachIndexed { i, node ->
                val angle = i * angleStep
                val x = (canvasWidth / 2 + radius * cos(angle)).toFloat()
                val y = (canvasHeight / 2 + radius * sin(angle)).toFloat()
                nodePositions[node.id] = Offset(x, y)
            }

            // --- Рисуем связи ---
            graph.edges.forEach { edge ->
                val from = nodePositions[edge.from]
                val to = nodePositions[edge.to]
                if (from != null && to != null) {
                    drawLine(
                        color = Color.Gray,
                        start = from * scale + offset,
                        end = to * scale + offset,
                        strokeWidth = 3f
                    )
                }
            }

            // --- Рисуем узлы ---
            graph.nodes.forEach { node ->
                val pos = nodePositions[node.id] ?: return@forEach
                val finalPos = pos * scale + offset
                drawCircle(
                    color = Color.Blue,
                    radius = 30f * scale,
                    center = finalPos
                )
                // Подпись
                drawIntoCanvas {
                    it.nativeCanvas.drawText(
                        node.label,
                        finalPos.x + 40f,
                        finalPos.y,
                        android.graphics.Paint().apply {
                            textSize = 32f * scale
                            color = android.graphics.Color.BLACK
                        }
                    )
                }
            }
        }
    }

}

@Composable
fun GraphRenderV2(
    renderGraph: RenderGraph,
    scale: MutableFloatState,
    offset: MutableState<Offset>,
    mainElementName: String,
    modifier: Modifier = Modifier
) {
    //var scale by remember { mutableFloatStateOf(1f) }
    //var offset by remember { mutableStateOf(Offset.Zero) }
    var selectedNode by remember { mutableStateOf<RenderNode?>(null) }

    val textColor = MaterialTheme.colorScheme.onSurface

    // FIXME Масштабирование работает некорректно когда граф не в центральной области
    // Поддержка мультитач и перетаскивания
    val gestureModifier = Modifier.pointerInput(Unit) {
        detectTransformGestures { centroid, pan, zoom, _ ->
            scale.floatValue = (scale.floatValue * zoom).coerceIn(0.3f, 4f)
            offset.value += pan
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .then(gestureModifier)
    ) {

        val center = Offset(size.width / 2, size.height / 2)

        // --- Рисуем узлы ---
        renderGraph.nodes.forEach { node ->

            val pos = center + node.position * scale.floatValue + offset.value

            // FIXME Контейнер (если expanded)
            if (node.expanded && node.children.isNotEmpty()) {
                val width = node.defaultRadius * 4 * scale.floatValue
                val height = node.defaultRadius * 4 * scale.floatValue

                drawRoundRect(
                    color = Color(0xFFEEEEEE),
                    topLeft = pos - Offset(width/2, height/2),
                    size = Size(width, height),
                    cornerRadius = CornerRadius(12f * scale.floatValue)
                )
            }

            // Сам узел
            drawCircle(
                color = colorForType(node.data.type),
                radius =
                    when (node.data.type) {
                        "file" -> 45f
                        "folder" -> 60f
                        "package" -> 80f
                        "class" -> 40f
                        "interface" -> 40f
                        "function" -> 35f
                        "method" -> 35f
                        "variable" -> 20f
                        "block" -> 40f
                        "enum" -> 20f
                        "constructor" -> 35f
                        "object" -> 40f
                        "extension" -> 35f
                        else -> { node.defaultRadius}
                    }
                            *
                    scale.floatValue
                            +
                    if (node.data.label == mainElementName) 10 else 0,
                center = pos
            )

            drawContext.canvas.nativeCanvas.drawText(
                node.data.label,
                pos.x + node.defaultRadius * scale.floatValue + 10f,
                pos.y,
                android.graphics.Paint().apply {
                    color = textColor.toArgb()
                    textSize = 32f * scale.floatValue
                }
            )
        }

        // --- Рисуем связи ---
        renderGraph.edges.forEach { edge ->

            val start = center + edge.from.position * scale.floatValue + offset.value
            val end = center + edge.to.position * scale.floatValue + offset.value

            drawLine(
                color = Color.Gray.copy(alpha = 0.5f),
                start = start,
                end = end,
                strokeWidth = 2f * scale.floatValue
            )
        }


    }

    // Клик по узлу (упрощённая логика)
    LaunchedEffect(selectedNode) {
        selectedNode?.let { node ->
            // центрируем сцену на выбранном узле
            offset.value = Offset.Zero - node.position * scale.floatValue + Offset(500f, 1000f)
        }
    }
}
