package app.emirtemindarov.p1.render

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

// Содержит информацию о всех нодах
@Serializable
data class NodeModel(
    val id: String,
    val name: String,
    val type: NodeType,
    val childrenIds: List<String> = emptyList()
)

// Структура пересобирается при изменении root/center/focus по focusNodeId
// Используется при композиции, данные копируются из модели и компонуются в единую структуру
data class Node(
    val id: String,
    val name: String,
    val type: NodeType,
    val expanded: Boolean = true,
    val children: List<Node> = emptyList()
)

enum class ExpandState {
    EXPANDED,    // нода развернута, при наличии детей
    COLLAPSED,   // нода свернута, при наличии детей
    NONE         // нода не имеет детей поэтому не может быть развернута/свернута (иконка отсутствует)
}

@Serializable
enum class NodeType {
    FILE,
    FOLDER,
    CLASS,
    INTERFACE,
    FUNCTION,
    VARIABLE,
    BLOCK,
    OBJECT,   // вместо else
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