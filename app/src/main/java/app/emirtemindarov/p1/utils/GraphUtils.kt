package app.emirtemindarov.p1.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import app.emirtemindarov.p1.assistant.data.GraphModel
import app.emirtemindarov.p1.render.data.RenderEdge
import app.emirtemindarov.p1.render.data.RenderGraph
import app.emirtemindarov.p1.render.data.RenderNode
import kotlin.math.cos
import kotlin.math.sin

// Работает - не трожь!

fun GraphModel.toRenderGraph(layout: Int = 1): RenderGraph {

    // 1) Создаём RenderNode для каждого узла
    val renderNodes = nodes.associate { node ->
        node.id to RenderNode(data = node)
    }.toMutableMap()

    // 2) Связываем узлы по children_ids (вложенность)
    renderNodes.values.forEach { renderNode ->
        val children = renderNode.data.properties?.children_ids ?: return@forEach
        children.forEach { childId ->
            val childNode = renderNodes[childId]
            if (childNode != null) {
                renderNode.children += childNode
            }
        }
    }

    // 3) Создаём связи RenderEdge
    val renderEdges = edges.mapNotNull { edge ->
        val from = renderNodes[edge.from]
        val to = renderNodes[edge.to]

        if (from != null && to != null) {
            RenderEdge(edge, from, to)
        } else null
    }

    // 4) Раскладка узлов
    when (layout) {
        1 -> applyInitialCircleLayout(renderNodes.values.toList())
        2 -> applyRadialTreeLayout(renderNodes.values.toList())
        3 -> applyHorizontalTreeLayout(renderNodes.values.toList())
        4 -> applyVerticalTreeLayout(renderNodes.values.toList())
    }

    return RenderGraph(
        nodes = renderNodes.values.toList(),
        edges = renderEdges
    )
}

fun applyInitialCircleLayout(nodes: List<RenderNode>) {
    if (nodes.isEmpty()) return

    val center = Offset(0f, 0f)
    val radius = 1500f
    val angleStep = (Math.PI * 2 / nodes.size).toFloat()

    nodes.forEachIndexed { index, node ->
        val angle = index * angleStep
        node.position = Offset(
            x = center.x + radius * cos(angle),
            y = center.y + radius * sin(angle)
        )
    }
}

fun applyVerticalTreeLayout(
    nodes: List<RenderNode>,
    levelGap: Float = 300f,
    siblingGap: Float = 900f
) {
    if (nodes.isEmpty()) return

    // --- 1) Ищем корни
    val allChildren = nodes.flatMap { it.children }
    val roots = nodes.filter { it !in allChildren }

    // Если нет корней — рисуем просто вертикальный список
    if (roots.isEmpty()) {
        nodes.forEachIndexed { index, node ->
            node.position = Offset(0f, index * levelGap)
        }
        return
    }

    // --- 2) Позиционируем корни по горизонтали
    roots.forEachIndexed { i, root ->
        root.position = Offset(i * siblingGap, 0f)
    }

    // --- 3) Рекурсивный layout детей
    fun layout(node: RenderNode, depth: Int) {
        val children = node.children
        if (children.isEmpty()) return

        val dynamicGap = siblingGap + node.children.size * 40f
        val totalWidth = (children.size - 1) * dynamicGap
        val startX = node.position.x - totalWidth / 2

        children.forEachIndexed { index, child ->
            child.position = Offset(
                x = startX + index * siblingGap,
                y = depth * levelGap
            )
            layout(child, depth + 1)
        }
    }

    // --- Запуск для всех корней
    roots.forEach { layout(it, 1) }
}

fun applyHorizontalTreeLayout(
    nodes: List<RenderNode>,
    levelGap: Float = 900f,
    siblingGap: Float = 300f
) {
    if (nodes.isEmpty()) return

    // --- 1) Ищем корни
    val allChildren = nodes.flatMap { it.children }
    val roots = nodes.filter { it !in allChildren }

    if (roots.isEmpty()) {
        nodes.forEachIndexed { index, node ->
            node.position = Offset(index * levelGap, 0f)
        }
        return
    }

    // --- 2) Размещаем корни вертикально
    roots.forEachIndexed { i, root ->
        root.position = Offset(0f, i * siblingGap)
    }

    // --- 3) Дети справа
    fun layout(node: RenderNode, depth: Int) {
        val children = node.children
        if (children.isEmpty()) return

        val totalHeight = (children.size - 1) * siblingGap
        val startY = node.position.y - totalHeight / 2

        children.forEachIndexed { index, child ->
            child.position = Offset(
                x = depth * levelGap,
                y = startY + index * siblingGap
            )
            layout(child, depth + 1)
        }
    }

    roots.forEach { layout(it, 1) }
}

fun applyRadialTreeLayout(
    nodes: List<RenderNode>,
    levelRadius: Float = 400f
) {
    if (nodes.isEmpty()) return

    // --- 1) Ищем корневые узлы (те, кто ни у кого не в children)
    val allChildren = nodes.flatMap { it.children }
    val roots = nodes.filter { it !in allChildren }

    // Если корней нет — fallback на простую окружность
    if (roots.isEmpty()) {
        applyInitialCircleLayout(nodes)
        return
    }

    // --- 2) Распределяем корни по кругу
    val rootAngleStep = (Math.PI * 2 / roots.size).toFloat()

    roots.forEachIndexed { index, root ->
        val angle = index * rootAngleStep
        root.position = Offset(
            x = (0f + levelRadius * cos(angle)).toFloat(),
            y = (0f + levelRadius * sin(angle)).toFloat()
        )
    }

    // --- 3) Рекурсивно раскладываем детей
    fun layoutSubtree(node: RenderNode, depth: Int, startAngle: Float, endAngle: Float) {
        val children = node.children
        if (children.isEmpty()) return

        val step = (endAngle - startAngle) / children.size
        val nextRadius = (depth + 1) * levelRadius

        children.forEachIndexed { index, child ->
            val a1 = startAngle + index * step
            val a2 = a1 + step
            val angle = (a1 + a2) / 2f

            child.position = Offset(
                x = (nextRadius * cos(angle)).toFloat(),
                y = (nextRadius * sin(angle)).toFloat()
            )

            layoutSubtree(child, depth + 1, a1, a2)
        }
    }

    // --- 4) Запускаем для всех корней
    roots.forEachIndexed { i, root ->
        val start = i * rootAngleStep
        val end = start + rootAngleStep
        layoutSubtree(root, 1, start, end)
    }
}

fun colorForType(type: String): Color = when (type) {
    "file" -> Color(0xFF4CAF50)
    "class" -> Color(0xFF2196F3)
    "function" -> Color(0xFFFF9800)
    "variable" -> Color(0xFF9C27B0)
    "block" -> Color(0xFF795548)
    else -> Color(0xFF90A4AE)
}

/*
fun buildRenderGraph(model: GraphModel): RenderGraph {
    val renderNodes = mutableMapOf<String, RenderNode>()

    // 1. Создаём визуальные узлы (RenderNode)
    model.nodes.forEach { node ->
        renderNodes[node.id] = RenderNode(data = node)
    }

    // 2. Формируем связи
    val renderEdges = model.edges.mapNotNull { e ->
        val from = renderNodes[e.from]
        val to = renderNodes[e.to]
        if (from != null && to != null) {
            RenderEdge(from, to, e.type)
        } else null
    }

    // 3. Формируем иерархию (контейнеры)
    model.edges.forEach { edge ->
        if (edge.type == "contained_in") {
            val child = renderNodes[edge.from]
            val parent = renderNodes[edge.to]
            if (child != null && parent != null) {
                parent.children += child
            }
        }
    }

    return RenderGraph(renderNodes, renderEdges)
}
*/
