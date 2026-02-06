package app.emirtemindarov.p1.mvvm.savedprojects

import app.emirtemindarov.p1.room.GraphEntity

sealed class SavedProjectsStage {

    object Loading : SavedProjectsStage()

    data class Success(
        val graphs: List<GraphEntity>
    ) : SavedProjectsStage()

    object Empty : SavedProjectsStage()

    data class Error(
        val message: String
    ) : SavedProjectsStage()
}
