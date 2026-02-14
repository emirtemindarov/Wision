package app.emirtemindarov.p1.render

import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
//import app.emirtemindarov.p1.assistant.data.Edge
import app.emirtemindarov.p1.assistant.data.GraphModel
import app.emirtemindarov.p1.render.data.RenderGraph
import app.emirtemindarov.p1.render.data.RenderNode
//import app.emirtemindarov.p1.render.data.contains
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
                    Paint().apply {
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
                    Paint().apply {
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
                    Paint().apply {
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
                        Paint().apply {
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

            Log.i("node", "$node")

            val pos = center + node.position * scale.floatValue + offset.value

            // FIXME Контейнер (если expanded)
            if (node.expanded && node.children.isNotEmpty()) {
                val width = node.defaultRadius * 4 * scale.floatValue
                val height = node.defaultRadius * 4 * scale.floatValue

                Log.i("node-children", "${node.children}")

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
                Paint().apply {
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

    // FIXME не работает
    // Клик по узлу (упрощённая логика)
    LaunchedEffect(selectedNode) {
        selectedNode?.let { node ->
            // центрируем сцену на выбранном узле
            offset.value = Offset.Zero - node.position * scale.floatValue + Offset(500f, 1000f)
        }
    }
}

/*@Composable
fun GraphRenderV2M2(
    renderGraph: RenderGraph,
    scale: MutableFloatState,
    offset: MutableState<Offset>,
    mainElementName: String,
    modifier: Modifier = Modifier
) {

    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    val center = remember(canvasSize) {
        Offset(canvasSize.width / 2f, canvasSize.height / 2f)
    }

    //var scale by remember { mutableFloatStateOf(1f) }
    //var offset by remember { mutableStateOf(Offset.Zero) }

    var selectedNode by remember { mutableStateOf<RenderNode?>(null) }

    val textColor = MaterialTheme.colorScheme.onSurface

    // FIXME Масштабирование работает некорректно когда граф не в центральной области
    // Поддержка мультитач и перетаскивания
    val gestureModifier = Modifier.pointerInput(Unit) {
        detectTransformGestures { centroid, pan, zoom, _ ->

            val oldScale = scale.floatValue
            val newScale = (oldScale * zoom).coerceIn(0.3f, 4f)

            val scaleFactor = newScale / oldScale

            val worldCentroid = (centroid - center) / oldScale

            offset.value =
                offset.value - worldCentroid * (scaleFactor - 1f) + pan / newScale

            scale.floatValue = newScale
        }
    }

    val tapModifier = Modifier.pointerInput(renderGraph) {
        detectTapGestures { tap ->

            val hitNode = renderGraph.nodes.firstOrNull { node ->
                node.contains(
                    tap = tap,
                    center = center,
                    scale = scale.floatValue,
                    offset = offset.value
                )
            }

            hitNode?.let { node ->
                node.expanded = !node.expanded
                selectedNode = node
            }
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .onSizeChanged { canvasSize = it }
            .then(gestureModifier)    // TODO полезно
            .then(tapModifier)
    ) {

        //val center = Offset(size.width / 2, size.height / 2)

        // --- Рисуем узлы ---
        renderGraph.nodes.forEach { node ->

            Log.i("node", "$node")

            val pos = center + node.position * scale.floatValue + offset.value

            // FIXME не видел контейнеров (всегда ноды)
            // Контейнер (если expanded)
            if (node.expanded && node.children.isNotEmpty()) {
                val width = node.defaultRadius * 4 * scale.floatValue
                val height = node.defaultRadius * 4 * scale.floatValue

                Log.i("node-children", "${node.children}")

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
                Paint().apply {
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

    // FIXME не работает
    // Клик по узлу (упрощённая логика)
    LaunchedEffect(selectedNode) {
        selectedNode?.let { node ->
            offset.value = -node.position * scale.floatValue
        }
    }
}*/

@Composable
fun GraphRenderV3(
    renderGraph: RenderGraph,
    modifier: Modifier = Modifier
) {
    val scale = remember { mutableFloatStateOf(1f) }
    val offset = remember { mutableStateOf(Offset.Zero) }

    val gestureModifier = Modifier.pointerInput(Unit) {
        detectTransformGestures { centroid, pan, zoom, _ ->

            val oldScale = scale.floatValue
            val newScale = (oldScale * zoom).coerceIn(0.3f, 4f)

            // 1. мировая точка под пальцами ДО зума
            val worldPoint = (centroid - offset.value) / oldScale

            // 2. обновляем scale
            scale.floatValue = newScale

            // 3. компенсируем offset, чтобы фокус остался под пальцами
            offset.value = centroid - worldPoint * newScale + pan
        }
    }


    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .then(gestureModifier)
    ) {

        renderGraph.nodes.forEach { node ->

            val pos = node.position * scale.floatValue + offset.value

            drawCircle(
                color = Color.Blue,
                radius = node.defaultRadius * scale.floatValue,
                center = pos
            )
        }

        renderGraph.edges.forEach { edge ->
            drawLine(
                color = Color.Gray,
                start = edge.from.position * scale.floatValue + offset.value,
                end = edge.to.position * scale.floatValue + offset.value,
                strokeWidth = 2f * scale.floatValue
            )
        }
    }
}

@Stable
class CameraState(
    scale: Float = 1f,
    offset: Offset = Offset.Zero
) {
    var scale by mutableFloatStateOf(scale)
    var offset by mutableStateOf(offset)

    fun worldToScreen(world: Offset): Offset =
        world * scale + offset

    fun screenToWorld(screen: Offset): Offset =
        (screen - offset) / scale
}

fun Modifier.cameraGestures(
    camera: CameraState,
    minScale: Float = 0.3f,
    maxScale: Float = 4f
): Modifier = pointerInput(Unit) {

    detectTransformGestures { centroid, pan, zoom, _ ->

        val oldScale = camera.scale
        val newScale = (oldScale * zoom).coerceIn(minScale, maxScale)

        // мировая точка под пальцами ДО зума
        val worldFocus = camera.screenToWorld(centroid)

        camera.scale = newScale

        // сдвиг так, чтобы worldFocus осталась под centroid
        camera.offset =
            centroid - worldFocus * newScale + pan
    }
}

fun RenderNode.contains(
    tap: Offset,
    camera: CameraState
): Boolean {
    val worldTap = camera.screenToWorld(tap)
    return (worldTap - position).getDistance() <= defaultRadius
}

// Самая удачная версия
@Composable
fun GraphRenderV3M2(
    renderGraph: RenderGraph,
    camera: CameraState,
    mainElementName: String,
    modifier: Modifier = Modifier
) {
    val textColor = MaterialTheme.colorScheme.onSurface

    // обновление UI
    var redrawTrigger by remember { mutableIntStateOf(0) }
    Log.i("redrawTriggerValue", "$redrawTrigger")

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .cameraGestures(camera)
            .pointerInput(renderGraph, redrawTrigger) {
                detectTapGestures { tap ->
                    renderGraph.nodes
                        .firstOrNull { it.contains(tap, camera) }
                        ?.let { node ->
                            node.expanded = !node.expanded
                            redrawTrigger++
                        }
                }
            }
    ) {
        // установил для обновления UI при изменении
        redrawTrigger

        // --- edges ---
        renderGraph.edges.forEach { edge ->
            drawLine(
                color = Color.Gray.copy(alpha = 0.5f),
                start = camera.worldToScreen(edge.from.position),
                end = camera.worldToScreen(edge.to.position),
                strokeWidth = 2f * camera.scale
            )
        }

        // --- nodes ---
        renderGraph.nodes.forEach { node ->

            val pos = camera.worldToScreen(node.position)

            // контейнер
            if (node.expanded && node.children.isNotEmpty()) {
                val size = node.defaultRadius * 4f
                val topLeft =
                    camera.worldToScreen(
                        node.position - Offset(size / 2, size / 2)
                    )

                drawRoundRect(
                    color = Color(0xFFEEEEEE),
                    topLeft = topLeft,
                    size = Size(size, size) * camera.scale,
                    cornerRadius = CornerRadius(12f * camera.scale)
                )
            }

            // радиус узла
            val radius =
                when (node.data.type) {
                    "file" -> 45f
                    "folder" -> 60f
                    "package" -> 80f
                    "class", "interface" -> 40f
                    "function", "method", "constructor", "extension" -> 35f
                    "variable", "enum" -> 20f
                    "block", "object" -> 40f
                    else -> node.defaultRadius
                } * camera.scale +
                        if (node.data.label == mainElementName) 10f else 0f

            // узел
            drawCircle(
                color = colorForType(node.data.type),
                radius = radius,
                center = pos
            )

            // подпись
            drawContext.canvas.nativeCanvas.drawText(
                node.data.label,
                pos.x + radius + 10f,
                pos.y,
                Paint().apply {
                    color = textColor.toArgb()
                    textSize = 32f * camera.scale.coerceAtMost(1.5f)
                    isAntiAlias = true
                }
            )
        }
    }
}

fun RenderNode.childrenBounds(): Rect {
    if (children.isEmpty()) {
        return Rect(position, Size.Zero)
    }

    val minX = children.minOf { it.position.x - it.defaultRadius }
    val maxX = children.maxOf { it.position.x + it.defaultRadius }
    val minY = children.minOf { it.position.y - it.defaultRadius }
    val maxY = children.maxOf { it.position.y + it.defaultRadius }

    return Rect(
        offset = Offset(minX, minY),
        size = Size(maxX - minX, maxY - minY)
    )
}

@Composable
fun GraphRenderV3M3(
    renderGraph: RenderGraph,
    camera: CameraState,
    mainElementName: String,
    modifier: Modifier = Modifier
) {
    val textColor = MaterialTheme.colorScheme.onSurface

    // обновление UI
    var redrawTrigger by remember { mutableIntStateOf(0) }
    Log.i("redrawTriggerValue", "$redrawTrigger")

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .cameraGestures(camera)
            .pointerInput(renderGraph, redrawTrigger) {
                detectTapGestures { tap ->
                    renderGraph.nodes
                        .firstOrNull { it.contains(tap, camera) }
                        ?.let { node ->
                            node.expanded = !node.expanded
                            redrawTrigger++
                        }
                }
            }
    ) {
        // установил для обновления UI при изменении
        redrawTrigger

        // --- edges ---
        renderGraph.edges.forEach { edge ->
            drawLine(
                color = Color.Gray.copy(alpha = 0.5f),
                start = camera.worldToScreen(edge.from.position),
                end = camera.worldToScreen(edge.to.position),
                strokeWidth = 2f * camera.scale
            )
        }

        // --- nodes ---
        renderGraph.nodes.forEach { node ->

            val parentCollapsed = renderGraph.nodes.any { parent ->
                parent.children.contains(node) && !parent.expanded
            }

            if (!parentCollapsed) {

                val pos = camera.worldToScreen(node.position)

                // контейнер
                if (node.expanded && node.children.isNotEmpty()) {

                    val bounds = node.childrenBounds()
                    val topLeft = camera.worldToScreen(bounds.topLeft)
                    val size = bounds.size * camera.scale

                    drawRoundRect(
                        color = Color(0xFFEEEEEE),
                        topLeft = topLeft,
                        size = size,
                        cornerRadius = CornerRadius(16f * camera.scale)
                    )
                }

                // радиус узла
                val radius =
                    when (node.data.type) {
                        "file" -> 45f
                        "folder" -> 60f
                        "package" -> 80f
                        "class", "interface" -> 40f
                        "function", "method", "constructor", "extension" -> 35f
                        "variable", "enum" -> 20f
                        "block", "object" -> 40f
                        else -> node.defaultRadius
                    } * camera.scale +
                            if (node.data.label == mainElementName) 10f else 0f

                // узел
                drawCircle(
                    color = colorForType(node.data.type),
                    radius = radius,
                    center = pos
                )

                // подпись
                drawContext.canvas.nativeCanvas.drawText(
                    node.data.label,
                    pos.x + radius + 10f,
                    pos.y,
                    Paint().apply {
                        color = textColor.toArgb()
                        textSize = 32f * camera.scale.coerceAtMost(1.5f)
                        isAntiAlias = true
                    }
                )
            }
        }
    }
}
