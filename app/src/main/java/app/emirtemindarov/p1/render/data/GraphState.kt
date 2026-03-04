package app.emirtemindarov.p1.render.data

import androidx.compose.ui.geometry.Offset
import app.emirtemindarov.p1.assistant.data.GraphModel
import app.emirtemindarov.p1.assistant.data.GraphNode
import app.emirtemindarov.p1.assistant.data.GraphEdge

data class RenderGraph(
    val nodes: List<RenderNode>,
    val edges: List<RenderEdge>
)

data class RenderNode(
    val data: GraphNode,
    var position: Offset = Offset.Zero,
    var defaultRadius: Float = 60f,
    var expanded: Boolean = false,
    val children: MutableList<RenderNode> = mutableListOf()
)

data class RenderEdge(
    val data: GraphEdge,
    val from: RenderNode,
    val to: RenderNode
)
