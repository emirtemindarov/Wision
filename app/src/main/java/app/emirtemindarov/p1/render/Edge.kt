package app.emirtemindarov.p1.render

import kotlinx.serialization.Serializable

@Serializable
enum class EdgeType {
    USES,       // формирует колонку справа
    //USED_BY,    // формирует колонку слева (формируется обратно USES)
    IMPLEMENTS, // формирует колонку сверху или снизу
    INHERITS    // формирует колонку сверху или снизу
}

@Serializable
data class EdgeModel(
    val from: String,   // от какой ноды, по ее id
    val to: String,     // до какой ноды, по ее id
    val type: EdgeType  // тип связи
)

enum class Direction {
    OUTGOING, // from → to
    INCOMING  // to → from
}