package app.emirtemindarov.p1.render.data

import androidx.compose.ui.geometry.Offset
import app.emirtemindarov.p1.assistant.data.GraphModel
import app.emirtemindarov.p1.assistant.data.GraphNode
import app.emirtemindarov.p1.assistant.data.GraphEdge

data class RenderGraph(
    val nodes: Map<String, RenderNode>,
    val edges: List<RenderEdge>,
    var focusNodeId: String
)

data class RenderNode(
    val id: String,
    val data: GraphNode,
    var position: Offset = Offset.Zero,
)

data class RenderEdge(
    val data: GraphEdge,
)
