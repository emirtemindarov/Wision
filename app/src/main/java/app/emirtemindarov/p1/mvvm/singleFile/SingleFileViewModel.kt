package app.emirtemindarov.p1.mvvm.singleFile

import android.content.Context
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.emirtemindarov.p1.mvvm.data.FileInfo
import app.emirtemindarov.p1.mvvm.interfaces.FileStructureInterface
import app.emirtemindarov.p1.utils.FileUtils.buildSingleFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SingleFileViewModel : ViewModel(), FileStructureInterface {
    // Приватный мутабельный стейт
    private val _state = MutableStateFlow(SingleFileState())
    // Публичный неизменяемый поток
    val state = _state.asStateFlow()

    fun loadAndSetOriginalRoot(context: Context, file: DocumentFile) {
        viewModelScope.launch {
            clear()
            val singleFile = buildSingleFile(
                context = context,
                document = file
            )
            setSingleFile(singleFile)
        }
    }

    fun setSingleFile(newSingleFile: FileInfo) {
        Log.i("setSingleFile", "$newSingleFile")

        _state.update { it.copy(
            singleFile = newSingleFile
        ) }
    }

    override fun setCurrentlyViewedFile(file: FileInfo) {
        _state.update { it.copy(
            currentlyViewedFile = file
        ) }
    }

    fun getSingleFile(): FileInfo? {
        return _state.value.singleFile
    }

    override fun getCurrentlyViewedFile(): FileInfo? {
        return _state.value.currentlyViewedFile
    }

    fun clear() {
        _state.update { it.copy(
            singleFile = null
        ) }
    }

    fun debug() {
        Log.i("SingleFile", "${state.value.singleFile}")
        Log.i("CurrentlyViewedFile", "${state.value.currentlyViewedFile}")

    }
}