package app.emirtemindarov.p1.mvvm.savedprojects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.emirtemindarov.p1.room.GraphDao
import app.emirtemindarov.p1.room.GraphEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SavedProjectsViewModel(
    graphDao: GraphDao
) : ViewModel() {

    val graphs: StateFlow<List<GraphEntity>> =
        graphDao.getAll()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
}