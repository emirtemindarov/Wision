package app.emirtemindarov.p1.assistant.data

import app.emirtemindarov.p1.render.EdgeModel
import app.emirtemindarov.p1.render.NodeModel
import app.emirtemindarov.p1.render.NodeType
import kotlinx.serialization.Serializable

@Serializable
data class GraphModel(
    val nodes: List<NodeModel>,
    val edges: List<EdgeModel>,
)