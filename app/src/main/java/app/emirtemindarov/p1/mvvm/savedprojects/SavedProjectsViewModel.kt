package app.emirtemindarov.p1.mvvm.savedprojects

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.emirtemindarov.p1.room.GraphDao
import app.emirtemindarov.p1.room.GraphEntity
import app.emirtemindarov.p1.utils.LogUtils.logLong
import app.emirtemindarov.p1.utils.LogUtils.logShort
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SavedProjectsViewModel(
    graphDao: GraphDao
) : ViewModel() {

    /*val graphs: StateFlow<List<GraphEntity>> =
        graphDao.getAll()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )*/

    private val _state =
        MutableStateFlow(SavedProjectsState())

    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {

            // задержка для проверки, на случай долгого ответа базы данных
            // TODO сделать DELAY константу по типу DEBUG
            delay(20_000)

            graphDao.getAll().collect { graphsList ->
                _state.update { it.copy(
                    stage = when {
                        graphsList.isEmpty() -> SavedProjectsStage.Empty
                        else -> SavedProjectsStage.Success(graphsList)
                    }
                ) }
            }
        }
    }

    fun debug() {
        logShort("savedProjectsViewModel", "${state.value.stage}")

    }
}
