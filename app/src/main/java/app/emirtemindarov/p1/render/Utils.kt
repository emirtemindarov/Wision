package app.emirtemindarov.p1.render

import app.emirtemindarov.p1.Environment.ALL_NODES_ROOT

fun buildNodeTree(
    nodes: List<NodeModel>,
    rootId: String,
    expandedState: Map<String, Boolean>,
    visited: Set<String> = emptySet(),
    excludeNodeId: String? = null
): Node {

    val nodesMap = nodes.associateBy { it.id }

    if (rootId == ALL_NODES_ROOT) {
        val childIds = nodes
            .flatMap { it.childrenIds }
            .toSet()

        val rootNodes = nodes
            .filter { it.id !in childIds }

        return Node(
            id = ALL_NODES_ROOT,
            name = "All",
            type = NodeType.FOLDER,
            expanded = true,
            children = rootNodes.map {
                buildNodeTree(nodes, it.id, expandedState)
            }
        )
    }

    if (rootId in visited) {
        return Node(rootId, "...", NodeType.CLASS)
    }

    val model = nodesMap[rootId]
        ?: return Node(rootId, "?", NodeType.CLASS)

    return Node(
        id = model.id,
        name = model.name,
        type = model.type,
        expanded = expandedState[model.id] ?: true,
        children = model.childrenIds
            .filter { it != excludeNodeId }
            .map {
                buildNodeTree(
                    nodes,
                    it,
                    expandedState,
                    visited + rootId,
                    excludeNodeId
                )
            }
    )
}

fun buildLinkedNodes(
    nodes: List<NodeModel>,
    edges: List<EdgeModel>,
    rootId: String,
    expandedState: Map<String, Boolean>,
    edgeType: EdgeType,
    direction: Direction,
): List<Node> {

    if (rootId == ALL_NODES_ROOT) return emptyList()

    val nodesMap = nodes.associateBy { it.id }

    val targetIds = when (edgeType) {

        EdgeType.USES -> {
            when (direction) {
                Direction.OUTGOING ->
                    edges.filter { it.type == EdgeType.USES && it.from == rootId }
                        .map { it.to }

                Direction.INCOMING ->
                    edges.filter { it.type == EdgeType.USES && it.to == rootId }
                        .map { it.from }
            }
        }

        EdgeType.IMPLEMENTS,
        EdgeType.INHERITS -> {
            when (direction) {
                Direction.OUTGOING ->
                    edges.filter { it.type == edgeType && it.from == rootId }
                        .map { it.to }

                Direction.INCOMING ->
                    edges.filter { it.type == edgeType && it.to == rootId }
                        .map { it.from }
            }
        }
    }.distinct()

    return targetIds
        .filter { nodesMap.containsKey(it) }
        .map { buildNodeTree(nodes, it, expandedState, excludeNodeId = rootId) }
        .distinctBy { it.id }
}