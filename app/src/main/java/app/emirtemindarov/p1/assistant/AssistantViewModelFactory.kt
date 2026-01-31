package app.emirtemindarov.p1.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.emirtemindarov.p1.room.GraphDao

class AssistantViewModelFactory(
    private val graphDao: GraphDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AssistantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AssistantViewModel(graphDao) as T
        }
        error("Unknown ViewModel class")
    }
}