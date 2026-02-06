package app.emirtemindarov.p1.assistant

import androidx.compose.runtime.Stable
import app.emirtemindarov.p1.assistant.data.GraphModel
import app.emirtemindarov.p1.room.GraphLoadMode
import kotlinx.serialization.Serializable


@Stable
sealed class AssistantStage {

    object Idle : AssistantStage()

    data class Loading(
        val mode: GraphLoadMode
    ) : AssistantStage()

    data class Success(
        val graph: GraphModel,
        val graphId: String
    ) : AssistantStage()

    data class Error(
        val message: String
    ) : AssistantStage()
}


