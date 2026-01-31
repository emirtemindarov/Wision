package app.emirtemindarov.p1.room

sealed interface GraphLoadMode {

    data class NewAnalysis(
        val payload: String
    ) : GraphLoadMode

    data class FromDatabase(
        val graphId: String
    ) : GraphLoadMode
}
