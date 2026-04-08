package app.emirtemindarov.p1.utils

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import app.emirtemindarov.p1.assistant.data.EdgeType
import app.emirtemindarov.p1.assistant.data.GraphModel
import app.emirtemindarov.p1.assistant.data.NodeType
import app.emirtemindarov.p1.render.data.RenderEdge
import app.emirtemindarov.p1.render.data.RenderGraph
import app.emirtemindarov.p1.render.data.RenderNode
import app.emirtemindarov.p1.utils.LogUtils.logLong
import kotlin.math.cos
import kotlin.math.sin

fun GraphModel.toRenderGraph(): RenderGraph {

    val nodes = mutableMapOf<String, RenderNode>()

    // this = graph
    this.nodes.forEach { node ->
        nodes[node.id] = RenderNode(
            id = node.id,
            data = node,
            position = Offset.Zero   // ?
        )
    }

    val edges = this.edges.map { edge ->
        RenderEdge(edge)
    }

    val renderGraph = RenderGraph(
        nodes,
        edges,
        focusNodeId = this.nodes.first().id
    )
    Log.i("focusNodeId_GraphUtils", renderGraph.focusNodeId)

    logLong("renderGraph", "$renderGraph")

    return renderGraph
}

fun layout(graph: RenderGraph, focusId: String) {
    val center = Offset(0f, 0f)
    graph.nodes[focusId]?.position = center

    val outgoing = graph.edges.filter { it.data.from == focusId }
    val incoming = graph.edges.filter { it.data.to == focusId }

    val contains = outgoing.filter { it.data.type == EdgeType.CONTAINS }
    val uses = outgoing.filter { it.data.type == EdgeType.USES }

    val parents = incoming.filter { it.data.type == EdgeType.INHERITS }
    val usedBy = incoming.filter { it.data.type == EdgeType.CALLS || it.data.type == EdgeType.IMPLEMENTS }

    val horizontalSpacing = 900f   // между группами (лево/право)
    val verticalSpacing = 600f     // между группами (верх/низ)

    placeColumn(graph, contains, -horizontalSpacing, 0f, focusId)     // слева (внутренности)
    placeColumn(graph, uses, horizontalSpacing, 0f, focusId)          // справа (использует)

    placeColumn(graph, parents, 0f, -verticalSpacing, focusId)      // сверху (кто содержит)
    placeColumn(graph, usedBy, 0f, verticalSpacing, focusId)        // снизу (кто использует)
}

fun placeColumn(
    graph: RenderGraph,
    edges: List<RenderEdge>,
    baseX: Float,
    baseY: Float,
    focusId: String
) {
    edges.forEachIndexed { index, edge ->
        val nodeId = if (edge.data.from == focusId) {
            edge.data.to
        } else {
            edge.data.from
        }

        val node = graph.nodes[nodeId] ?: return@forEachIndexed

        val nodeSpacingY = 250f   // вертикальное расстояние внутри группы
        val nodeSpacingX = 120f   // небольшой “разброс” по X

        node.position = Offset(
            baseX + (index % 2) * nodeSpacingX,
            baseY + index * nodeSpacingY
        )
    }
}

data class GraphColumns(
    val center: RenderNode?,
    val left: List<RenderNode>,
    val right: List<RenderNode>,
    val top: List<RenderNode>,
    val bottom: List<RenderNode>,
)

fun buildColumns(graph: RenderGraph): GraphColumns {
    val focusId = graph.focusNodeId
    val center = graph.nodes[focusId]

    val outgoing = graph.edges.filter { it.data.from == focusId }
    val incoming = graph.edges.filter { it.data.to == focusId }

    val left = outgoing
        .filter { it.data.type == EdgeType.CONTAINS }
        .mapNotNull { graph.nodes[it.data.to] }

    val right = outgoing
        .filter { it.data.type != EdgeType.CONTAINS }
        .mapNotNull { graph.nodes[it.data.to] }

    val top = incoming
        .filter { it.data.type == EdgeType.CONTAINS }
        .mapNotNull { graph.nodes[it.data.from] }

    val bottom = incoming
        .filter { it.data.type != EdgeType.CONTAINS }
        .mapNotNull { graph.nodes[it.data.from] }

    return GraphColumns(center, left, right, top, bottom)
}

// TODO использовать приятные для глаза цвета
fun colorForType(type: NodeType): Color = when (type) {
    NodeType.FILE -> Color(0xFF2196F3)
    NodeType.FOLDER -> Color(0xFFFF9800)
    NodeType.CLASS -> Color(0xFF4CAF50)
    NodeType.INTERFACE -> Color(0xFFDDEA1E)
    NodeType.FUNCTION -> Color(0xFF2C47D5)
    NodeType.VARIABLE -> Color(0xFFBA68C8)
    NodeType.BLOCK -> Color(0xFF795548)
    NodeType.OBJECT -> Color(0xFF90A4AE)
}

fun edgeColor(type: EdgeType): Color = when(type) {
    EdgeType.CONTAINS -> Color.Gray
    EdgeType.USES -> Color.Blue
    EdgeType.CALLS -> Color.Green
    EdgeType.IMPLEMENTS -> Color.Magenta
    EdgeType.INHERITS -> Color.Red
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
