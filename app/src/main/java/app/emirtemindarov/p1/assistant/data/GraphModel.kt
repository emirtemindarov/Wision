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
    val signature: Signature,
    val visibility: String? = null,  // public, private, protected...
    val typeName: String? = null,
    val children_ids: List<String>? = null
)

@Serializable
data class Signature(
    val params: List<Param>? = null,
    val generics: List<String>? = null,
    val return_type: String? = null,
    val nullable_return_type: Boolean? = null,
    val modifiers: List<String>? = null,
    val exceptions: List<String>? = null,
    val namespace: String? = null,
    val annotations: List<String>? = null
)

@Serializable
data class Param(
    val label: String,
    val type: String,
    val nullable_type: Boolean,
    val default_value: String? = null,
    val modifiers: List<String>? = null
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

