package app.emirtemindarov.p1.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GraphEntity(
    @PrimaryKey val graphId: String,
    val createdAt: Long,
    val graphJson: String,     // правила построения графа
    val graphSource: String,   // иерархия-источник
    val graphName: String,
    val lastModified: Long,
)
