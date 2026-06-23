package app.emirtemindarov.p1.render

import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
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
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.toSize
import app.emirtemindarov.p1.Environment.ALL_NODES_ROOT
//import app.emirtemindarov.p1.assistant.data.Edge
import app.emirtemindarov.p1.assistant.data.GraphModel
import app.emirtemindarov.p1.components.FileInfoWindow
import app.emirtemindarov.p1.mvvm.data.FileHierarchy
//import app.emirtemindarov.p1.render.data.contains
//import app.emirtemindarov.p1.assistant.data.Node
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

// FIXME иногда в историю записывается повторное нажатие на focusNodeId
@Composable
fun GraphRenderV3(
    graph: GraphModel,
    graphSource: FileHierarchy,
    camera: CameraState,
    icons: Map<String, Drawable?>,
    focusNodeId: String,
    onNodeFocusCallback: (String) -> Unit,
    onViewportChanged: (Size) -> Unit,
    onDetailsClick: (String) -> Unit
) {

    //Log.i("graphSource_GraphRenderV3", "$graphSource")

    LaunchedEffect(focusNodeId) {
        Log.i("focusNodeId", focusNodeId)
        camera.scale = 1f
        camera.offset = Offset.Zero
    }

    fun onNodeFocus(id: String) {
        onNodeFocusCallback(id)
    }

    val expandedState = remember {
        mutableStateMapOf<String, Boolean>().apply {

            this[ALL_NODES_ROOT] = true

            graph.nodes.forEach { (id, node) ->
                this[id] = true
            }
        }
    }

    // список областей взаимодействия
    val hitBoxes = remember { mutableStateListOf<Pair<Rect, String>>() }

    val nodeRects = remember { mutableStateMapOf<String, Rect>() }

    // перестраивает отображение при изменении ключевых значений
    val buildedRoot = remember(graph.nodes, focusNodeId, expandedState.toMap()) {
        buildNodeTree(graph.nodes, focusNodeId, expandedState)
    }

    val isFocusExpanded = expandedState[focusNodeId] == true

    val parentOfFocusIds = remember(focusNodeId) {
        graph.nodes
            .filter { focusNodeId in it.childrenIds }
            .map { it.id }
            .toSet()
    }

    // список внутренних элементов центральной ноды
    val focusDescendantsIds = remember(focusNodeId, graph.nodes) {
        val map = graph.nodes.associateBy { it.id }

        val result = mutableSetOf<String>()

        fun dfs(id: String) {
            val node = map[id] ?: return
            node.childrenIds.forEach { childId ->
                if (result.add(childId)) {
                    dfs(childId)
                }
            }
        }

        dfs(focusNodeId)
        result
    }

    fun buildGroup(
        edgeTypes: List<EdgeType>,
        direction: Direction
    ): List<Node> {
        return edgeTypes
            .flatMap { type ->
                buildLinkedNodes(
                    graph.nodes,
                    graph.edges,
                    focusNodeId,
                    expandedState,
                    type,
                    direction
                )
            }
            .filter { node ->
                val hideChildren = isFocusExpanded && node.id in focusDescendantsIds
                !hideChildren
            }
            .distinctBy { it.id }
    }

    fun isBidirectional(a: String, b: String, edges: List<EdgeModel>): Boolean {
        val ab = edges.any { it.type == EdgeType.USES && it.from == a && it.to == b }
        val ba = edges.any { it.type == EdgeType.USES && it.from == b && it.to == a }
        return ab && ba
    }

    val rightIds = graph.edges
        .filter { it.type == EdgeType.USES && it.from == focusNodeId }
        .map { it.to }
        .toSet()

    val left = graph.edges
        .filter { it.type == EdgeType.USES && it.to == focusNodeId }
        .map { it.from }
        .distinct()
        .filter { id ->
            !(id in rightIds && isBidirectional(focusNodeId, id, graph.edges))
        }
        .map { id ->
            graph.nodes.firstOrNull { it.id == focusNodeId }.let {
                buildNodeTree(graph.nodes, id, expandedState, excludeNodeId = focusNodeId)
            }
        }

    val right = remember(focusNodeId, expandedState.toMap()) {
        buildGroup(
            edgeTypes = listOf(EdgeType.USES),
            direction = Direction.OUTGOING
        )
    }

    val top = remember(focusNodeId, expandedState.toMap()) {
        buildGroup(
            edgeTypes = listOf(EdgeType.IMPLEMENTS, EdgeType.INHERITS),
            direction = Direction.OUTGOING
        )
    }

    val bottom = remember(focusNodeId, expandedState.toMap()) {
        buildGroup(
            edgeTypes = listOf(EdgeType.IMPLEMENTS, EdgeType.INHERITS),
            direction = Direction.INCOMING
        )
    }

    Box(
        Modifier
            .fillMaxSize()
            .cameraGestures(camera)
            .pointerInput(Unit) {
                detectTapGestures(

                    onTap = { tap ->
                        val hit = hitBoxes.toList().lastOrNull { it.first.contains(tap) }

                        hit?.let { (_, action) ->
                            if (action.startsWith("header:")) {
                                val id = action.removePrefix("header:")
                                onNodeFocus(id)
                            } else if (action.startsWith("expand:")) {
                                val id = action.removePrefix("expand:")
                                expandedState[id] = !(expandedState[id] ?: true)
                            } else if (action.startsWith("details:")) {
                                val id = action.removePrefix("details:")
                                onDetailsClick(id)
                            }
                        }
                    },

                    onDoubleTap = { tap ->
                        val hit = hitBoxes.toList().lastOrNull { it.first.contains(tap) }

                        hit?.let { (_, action) ->
                            if (action.startsWith("header:")) {
                                val id = action.removePrefix("header:")
                                expandedState[id] = !(expandedState[id] ?: true)
                            }
                        }
                    },

                    onLongPress = { tap ->
                        val hit = hitBoxes.toList().lastOrNull { it.first.contains(tap) }

                        hit?.let { (_, action) ->
                            if (action.startsWith("header:")) {
                                val id = action.removePrefix("header:")
                                onDetailsClick(id)
                            }
                        }
                    }
                )
            }
    ) {

        Canvas(Modifier
            .fillMaxSize()
            .onSizeChanged {
                onViewportChanged(it.toSize())
            }
        ) {

            hitBoxes.clear()

            nodeRects.clear()

            fun applyCamera(o: Offset) = o * camera.scale + camera.offset
            fun applyCamera(r: Rect) = Rect(applyCamera(r.topLeft), applyCamera(r.bottomRight))

            val canvasNative = drawContext.canvas.nativeCanvas

            drawRect(Color(0xFFF5F5F5), size = size)

            // центр canvas
            val center = Offset(size.width / 2, size.height / 2)

            // CONFIG
            val padding = 24f
            val iconSize = 40f
            val bottomIconSize = 30f

            val bodyPadding = 44f
            val verticalSpacing = 42f
            val horizontalPadding = padding * 2
            val columnSpacing = 24f

            val iconSpacing = 12f
            val bottomSpacing = 24f

            val headerMeasurePaint = TextPaint().apply {
                textSize = 28f
                isAntiAlias = true
                isSubpixelText = true
                isLinearText = true
            }

            val headerDrawPaint = TextPaint(headerMeasurePaint).apply {
                // Размер не меняем через textSize — масштабируем сам canvas.
                // Так текст не пересчитывается ступеньками при зуме.
            }



            // =========================
            // NODE
            // =========================

            fun drawNode(
                node: Node,
                topLeft: Offset,
                isRoot: Boolean = false,
                measureOnly: Boolean = false,
                maxWidth: Float
            ): Size {

                val hasChildren = node.children.isNotEmpty()

                val expandState = when {
                    !hasChildren -> ExpandState.NONE
                    node.expanded -> ExpandState.EXPANDED
                    else -> ExpandState.COLLAPSED
                }

                val children = if (expandState == ExpandState.EXPANDED) node.children else emptyList()

                val headerHeight =
                    if (expandState == ExpandState.EXPANDED) 80f else 60f

                // ===== HEADER WIDTH
                val headerWidth =
                    headerMeasurePaint.measureText(node.name)

                // ===== AVAILABLE CONTENT WIDTH
                val contentMaxWidth =
                    (maxWidth - bodyPadding * 2).coerceAtLeast(0f)

                val columns = if (children.size <= 1) 1 else 2

                val availableWidthPerColumn =
                    if (columns == 1) contentMaxWidth
                    else (contentMaxWidth - columnSpacing) / 2f

                // ===== MEASURE CHILDREN WITH CONSTRAINTS
                val childSizes = children.map {
                    drawNode(
                        it,
                        Offset.Zero,
                        false,
                        true,
                        maxWidth = availableWidthPerColumn
                    )
                }

                val columnHeights = MutableList(columns) { 0f }
                val columnWidths = MutableList(columns) { 0f }
                val columnItems = MutableList(columns) { mutableListOf<Int>() }

                children.forEachIndexed { i, _ ->
                    val size = childSizes[i]

                    val col = if (columns == 1) {
                        0
                    } else {
                        columnHeights.indices.minBy { columnHeights[it] }
                    }

                    columnItems[col].add(i)

                    columnHeights[col] += size.height + verticalSpacing
                    columnWidths[col] = maxOf(columnWidths[col], size.width)
                }

                columnHeights.replaceAll {
                    if (it > 0) it - verticalSpacing else 0f
                }

                val extraBottomSpace =
                    if (node.expanded && hasChildren) bottomSpacing else 0f

                val bodyHeight =
                    if (children.isNotEmpty())
                        columnHeights.max() + bodyPadding * 2 + extraBottomSpace
                    else
                        0f

                val bodyWidth =
                    columnWidths.sum() +
                            if (columns == 2) columnSpacing else 0f

                // ===== FINAL WIDTH (CONSTRAINED)
                val rawWidth =
                    maxOf(headerWidth, bodyWidth) +
                            horizontalPadding +
                            if (expandState != ExpandState.NONE) iconSize + iconSpacing else 0f

                val finalWidth =
                    min(maxWidth, rawWidth).coerceAtLeast(20f)

                val finalHeight =
                    headerHeight + bodyHeight

                if (measureOnly) {
                    return Size(finalWidth, finalHeight)
                }

                val rect = Rect(topLeft, Size(finalWidth, finalHeight))
                val rectScreen = applyCamera(rect)

                // Общие данные BG и FG
                val radius = 16f * camera.scale

                val topLeftRadius = CornerRadius(0f, 0f)

                val topRightRadius =
                    if (!hasChildren || !node.expanded)
                        CornerRadius(0f, 0f)
                    else
                        CornerRadius(radius, radius)

                val bottomRightRadius = CornerRadius(radius, radius)
                val bottomLeftRadius = CornerRadius(radius, radius)

                // BACKGROUND
                val shadowExpand = 1f

                val shadowRect = Rect(
                    rect.left - shadowExpand,
                    rect.top + 6f,
                    rect.right + shadowExpand,
                    rect.bottom + 6f
                )

                val shadowRectScreen = applyCamera(shadowRect)

                val shadowRr = RoundRect(
                    shadowRectScreen,
                    topLeft = topLeftRadius,
                    topRight = topRightRadius,
                    bottomRight = bottomRightRadius,
                    bottomLeft = bottomLeftRadius
                )

                val shadowPath = Path().apply { addRoundRect(shadowRr) }

                drawPath(
                    path = shadowPath,
                    color = Color.Black.copy(0.15f)
                )

                // FOREGROUND
                val rr = RoundRect(
                    rectScreen,
                    topLeft = topLeftRadius,
                    topRight = topRightRadius,
                    bottomRight = bottomRightRadius,
                    bottomLeft = bottomLeftRadius
                )

                val path = Path().apply { addRoundRect(rr) }

                drawPath(
                    path = path,
                    color = Color.White
                )

                // STRIPE (OVERLAY)
                val stripeHeight = if (hasChildren) 6f else 2f

                val stripeRight =
                    if (hasChildren && node.expanded)
                        rect.right - iconSize * 2
                    else
                        rect.right

                val stripeRect = Rect(
                    rect.left,
                    rect.top,
                    stripeRight,
                    rect.top + stripeHeight
                )

                val stripeScreen = applyCamera(stripeRect)

                drawRect(
                    colorForType(node.type),
                    stripeScreen.topLeft,
                    stripeScreen.size
                )

                // TITLE
                val headerRect = Rect(rect.topLeft, Size(rect.width, headerHeight))
                val headerScreen = applyCamera(headerRect)

                // немного увеличим область (как для иконок)
                val hitPadding = 12f * camera.scale

                val hitRect = Rect(
                    Offset(
                        headerScreen.left - hitPadding,
                        headerScreen.top - hitPadding
                    ),
                    Size(
                        headerScreen.width + hitPadding * 2,
                        headerScreen.height + hitPadding * 2
                    )
                )

                hitBoxes.add(hitRect to "header:${node.id}")

                val maxTextWidth =
                    (rect.width - horizontalPadding - if (expandState != ExpandState.NONE) iconSize + iconSpacing else 0f)
                        .coerceAtLeast(0f)

                val text = android.text.TextUtils.ellipsize(
                    node.name,
                    headerMeasurePaint,
                    maxTextWidth,
                    android.text.TextUtils.TruncateAt.END
                )

                val textX = rect.left + padding
                val textY = rect.top + headerHeight / 2f -
                        (headerDrawPaint.descent() + headerDrawPaint.ascent()) / 2f

                canvasNative.save()
                canvasNative.translate(camera.offset.x, camera.offset.y)
                canvasNative.scale(camera.scale, camera.scale)
                canvasNative.drawText(
                    text.toString(),
                    textX,
                    textY,
                    headerDrawPaint
                )
                canvasNative.restore()

                // ICON
                val iconX = rect.right - iconSize

                if (expandState != ExpandState.NONE) {

                    val iconKey =
                        if (expandState == ExpandState.EXPANDED) "collapse"
                        else "expand"

                    val c = applyCamera(Offset(iconX, rect.top + headerHeight / 2))

                    val s = iconSize * camera.scale

                    // ===== DRAW ICON
                    icons[iconKey]?.let {
                        it.setBounds(
                            (c.x - s / 2).toInt(),
                            (c.y - s / 2).toInt(),
                            (c.x + s / 2).toInt(),
                            (c.y + s / 2).toInt()
                        )
                        it.draw(canvasNative)
                    }

                    // ===== HITBOX (увеличенный)
                    val hitPadding = 20f * camera.scale

                    val hitRect = Rect(
                        Offset(
                            c.x - s / 2 - hitPadding,
                            c.y - s / 2 - hitPadding
                        ),
                        Size(
                            s + hitPadding * 2,
                            s + hitPadding * 2
                        )
                    )

                    hitBoxes.add(hitRect to "expand:${node.id}")
                }

                // ===== DRAW CHILDREN
                val bodyLeft = rect.left + bodyPadding
                val bodyTop = rect.top + headerHeight + bodyPadding

                var currentX = bodyLeft

                for (col in 0 until columns) {

                    var currentY = bodyTop

                    columnItems[col].forEach { i ->
                        val child = children[i]
                        val sizeChild = childSizes[i]

                        drawNode(
                            child,
                            Offset(currentX, currentY),
                            false,
                            false,
                            maxWidth = availableWidthPerColumn
                        )

                        currentY += sizeChild.height + verticalSpacing
                    }

                    currentX += columnWidths[col] + columnSpacing
                }

                // BOTTOM ICON
                if (isRoot && hasChildren && expandState == ExpandState.EXPANDED) {

                    val c = applyCamera(
                        Offset(
                            rect.right - iconSpacing,
                            rect.bottom - iconSpacing
                        )
                    )

                    val s = bottomIconSize * camera.scale

                    icons["details"]?.let {
                        it.setBounds(
                            (c.x - s).toInt(),
                            (c.y - s).toInt(),
                            c.x.toInt(),
                            c.y.toInt()
                        )
                        it.draw(canvasNative)
                    }

                    // ===== HITBOX (увеличенный)
                    val hitPadding = 20f * camera.scale

                    val hitRect = Rect(
                        Offset(c.x - s - hitPadding, c.y - s - hitPadding),
                        Size(s + hitPadding * 2, s + hitPadding * 2)
                    )

                    hitBoxes.add(hitRect to "details:${node.id}")
                }

                nodeRects[node.id] = rect

                return rect.size
            }



            // ROOT
            val maxWidth = 10800f   // чем больше тем лучше

            val rootSize = drawNode(
                buildedRoot,
                Offset.Zero,
                true,
                true,
                maxWidth = maxWidth
            )

            val rootTopLeft = Offset(
                center.x - rootSize.width / 2,
                center.y - rootSize.height / 2
            )

            val rootCenter = Offset(
                rootTopLeft.x + rootSize.width / 2,
                rootTopLeft.y + rootSize.height / 2
            )

            val areaSpacing = 120f

            val leftSizes = left.map {
                drawNode(it, Offset.Zero, false, true, maxWidth = maxWidth)
            }

            val rightSizes = right.map {
                drawNode(it, Offset.Zero, false, true, maxWidth = maxWidth)
            }

            val topSizes = top.map {
                drawNode(it, Offset.Zero, false, true, maxWidth = maxWidth)
            }

            val bottomSizes = bottom.map {
                drawNode(it, Offset.Zero, false, true, maxWidth = maxWidth)
            }

            fun drawVerticalStack(
                nodes: List<Node>,
                sizes: List<Size>,
                startX: Float,
                startY: Float,
                alignRight: Boolean = false
            ) {
                var y = startY

                nodes.forEachIndexed { i, node ->
                    val size = sizes[i]

                    val x = if (alignRight) startX - size.width else startX

                    drawNode(
                        node,
                        Offset(x, y),
                        true,
                        false,
                        maxWidth = maxWidth
                    )

                    y += size.height + verticalSpacing
                }
            }

            fun drawHorizontalStack(
                nodes: List<Node>,
                sizes: List<Size>,
                startX: Float,
                startY: Float,
                alignBottom: Boolean = false
            ) {
                var x = startX

                nodes.forEachIndexed { i, node ->
                    val size = sizes[i]

                    val y = if (alignBottom) startY - size.height else startY

                    drawNode(
                        node,
                        Offset(x, y),
                        true,
                        false,
                        maxWidth = maxWidth
                    )

                    x += size.width + columnSpacing
                }
            }

            val maxLeftWidth = leftSizes.maxOfOrNull { it.width } ?: 0f
            val maxRightWidth = rightSizes.maxOfOrNull { it.width } ?: 0f

            val safeHorizontalSpacing =
                maxOf(maxLeftWidth, maxRightWidth) + areaSpacing

            // LEFT
            val leftStartX = rootTopLeft.x - safeHorizontalSpacing

            val leftHeight = leftSizes.fold(0f) { acc, it -> acc + it.height } +
                    verticalSpacing * (left.size - 1)

            val leftStartY = rootCenter.y - leftHeight / 2

            drawVerticalStack(
                left,
                leftSizes,
                startX = leftStartX,
                startY = leftStartY,
                alignRight = true
            )

            // RIGHT
            val rightStartX = rootTopLeft.x + rootSize.width + safeHorizontalSpacing

            val rightHeight = rightSizes.fold(0f) { acc, it -> acc + it.height } +
                    verticalSpacing * (right.size - 1)

            val rightStartY = rootCenter.y - rightHeight / 2

            drawVerticalStack(
                right,
                rightSizes,
                startX = rightStartX,
                startY = rightStartY
            )


            val verticalGroupHalf =
                maxOf(leftHeight, rightHeight, rootSize.height) / 2

            val safeVerticalSpacing =
                verticalGroupHalf + areaSpacing

            // TOP
            val topHeight = topSizes.maxOfOrNull { it.height } ?: 0f
            val topWidth = topSizes.fold(0f) { acc, it -> acc + it.width } +
                    columnSpacing * (top.size - 1)

            val topStartY = rootTopLeft.y - safeVerticalSpacing - topHeight
            val topStartX = rootCenter.x - topWidth / 2

            drawHorizontalStack(
                top,
                topSizes,
                startX = topStartX,
                startY = topStartY
            )

            // BOTTOM
            val bottomHeight = bottomSizes.maxOfOrNull { it.height } ?: 0f
            val bottomWidth = bottomSizes.fold(0f) { acc, it -> acc + it.width } +
                    columnSpacing * (bottom.size - 1)

            val bottomStartY = rootTopLeft.y + rootSize.height + safeVerticalSpacing
            val bottomStartX = rootCenter.x - bottomWidth / 2

            drawHorizontalStack(
                bottom,
                bottomSizes,
                startX = bottomStartX,
                startY = bottomStartY
            )

            // ROOT/CENTER/FOCUS
            drawNode(
                buildedRoot,
                Offset(
                    center.x - rootSize.width / 2,
                    center.y - rootSize.height / 2
                ),
                true,
                false,
                maxWidth = maxWidth
            )

            // с этого места ноды, их размеры и координаты уже отрисованы




            fun Offset.normalize(): Offset {
                val length = getDistance()
                return if (length == 0f) Offset.Zero else this / length
            }

            fun drawArrowOrthogonal(
                from: Offset,
                to: Offset,
                isHorizontal: Boolean,   // LEFT / RIGHT = true, TOP / BOTTOM = false
                bendFactor: Float = 0.5f, // где находятся изломы (0..1)
                dashed: Boolean = false,
                reverseArrow: Boolean = false
            ) {
                val start = applyCamera(from)
                val end = applyCamera(to)

                val mid1: Offset
                val mid2: Offset

                if (isHorizontal) {
                    // расстояние по X
                    val dx = to.x - from.x
                    val bendX = from.x + dx * bendFactor

                    mid1 = applyCamera(Offset(bendX, from.y))
                    mid2 = applyCamera(Offset(bendX, to.y))

                } else {
                    // расстояние по Y
                    val dy = to.y - from.y
                    val bendY = from.y + dy * bendFactor

                    mid1 = applyCamera(Offset(from.x, bendY))
                    mid2 = applyCamera(Offset(to.x, bendY))
                }

                val path = Path().apply {
                    moveTo(start.x, start.y)
                    lineTo(mid1.x, mid1.y)
                    lineTo(mid2.x, mid2.y)
                    lineTo(end.x, end.y)
                }

                drawPath(
                    path = path,
                    color = Color.Black,
                    style = Stroke(
                        width = 3f,
                        pathEffect = if (dashed)
                            PathEffect.dashPathEffect(floatArrayOf(20f, 10f))
                        else null
                    )
                )

                // наконечник
                val arrowSize = 20f/* * camera.scale*/

                val directionVector = if (reverseArrow)
                    (start - mid1)
                else
                    (end - mid2)

                val dir = directionVector.normalize()

                val arrowPoint = if (reverseArrow) start else end

                val left = Offset(
                    arrowPoint.x - dir.x * arrowSize - dir.y * arrowSize / 2,
                    arrowPoint.y - dir.y * arrowSize + dir.x * arrowSize / 2
                )

                val right = Offset(
                    arrowPoint.x - dir.x * arrowSize + dir.y * arrowSize / 2,
                    arrowPoint.y - dir.y * arrowSize - dir.x * arrowSize / 2
                )

                drawPath(
                    Path().apply {
                        moveTo(arrowPoint.x, arrowPoint.y)
                        lineTo(left.x, left.y)
                        lineTo(right.x, right.y)
                        close()
                    },
                    Color.Black
                )
            }

            val rootRect = nodeRects[focusNodeId] ?: return@Canvas
            Log.i("rootRect", "$rootRect      | focusNodeId = $focusNodeId")

            fun Rect.leftCenter() = Offset(this.left, this.center.y)
            fun Rect.rightCenter() = Offset(this.right, this.center.y)
            fun Rect.topCenter() = Offset(this.center.x, this.top)
            fun Rect.bottomCenter() = Offset(this.center.x, this.bottom)

            val isExternalChild: (String) -> Boolean = { nodeId ->
                nodeId in focusDescendantsIds && !isFocusExpanded
            }

            left.forEach { node ->
                val r = nodeRects[node.id] ?: return@forEach

                val external = node.id in parentOfFocusIds

                drawArrowOrthogonal(
                    from = r.rightCenter(),
                    to = rootRect.leftCenter(),
                    isHorizontal = true,
                    dashed = external,
                    reverseArrow = external
                )
            }

            right.forEach { node ->
                val r = nodeRects[node.id] ?: return@forEach

                val external = isExternalChild(node.id)

                drawArrowOrthogonal(
                    from = rootRect.rightCenter(),
                    to = r.leftCenter(),
                    isHorizontal = true,
                    dashed = external,
                    reverseArrow = external
                )
            }

            top.forEach { node ->
                val r = nodeRects[node.id] ?: return@forEach

                drawArrowOrthogonal(
                    from = rootRect.topCenter(),
                    to = r.bottomCenter(),
                    isHorizontal = false
                )
            }

            bottom.forEach { node ->
                val r = nodeRects[node.id] ?: return@forEach

                drawArrowOrthogonal(
                    from = r.topCenter(),
                    to = rootRect.bottomCenter(),
                    isHorizontal = false
                )
            }
        }
    }
}