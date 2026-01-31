package app.emirtemindarov.p1.assistant.data

import kotlinx.serialization.Serializable

@Serializable
data class GraphModel(
    val nodes: List<GraphNode>,
    val edges: List<GraphEdge>
)

// === NODE ===

@Serializable
data class GraphNode(
    val id: String,
    val label: String,
    val type: String,
    val subtype: String? = null,
    val color: String? = null,
    val description: String? = null,
    val inferred: Boolean? = null,
    val properties: NodeProperties? = null
)

// Поле "properties" (строгое)
@Serializable
data class NodeProperties(
    val path: String? = null,
    val lines: Int? = null,
    val signature: String? = null,
    val visibility: String? = null,
    val typeName: String? = null,
    val children_ids: List<String>? = null
)

// === EDGE ===

@Serializable
data class GraphEdge(
    val from: String,
    val to: String,
    val type: String,
    val properties: EdgeProperties? = null
)

@Serializable
data class EdgeProperties(
    val line: Int? = null,
    val context: String? = null,
    val inferred: Boolean? = null
)

