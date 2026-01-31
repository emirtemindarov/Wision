package app.emirtemindarov.p1.assistant

import androidx.compose.runtime.Stable

@Stable
data class AssistantState(
    val stage: AssistantStage = AssistantStage.Loading,
)

