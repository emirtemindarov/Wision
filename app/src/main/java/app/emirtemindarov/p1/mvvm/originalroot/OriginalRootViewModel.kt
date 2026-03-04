package app.emirtemindarov.p1.mvvm.originalroot

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.emirtemindarov.p1.mvvm.data.FileHierarchy
import app.emirtemindarov.p1.mvvm.data.FileInfo
import app.emirtemindarov.p1.mvvm.interfaces.FileStructureInterface
import app.emirtemindarov.p1.utils.FileUtils.buildFileHierarchy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OriginalRootViewModel : ViewModel(), FileStructureInterface {
    // Приватный мутабельный стейт
    private val _state = MutableStateFlow(OriginalRootState())
    // Публичный неизменяемый поток
    val state = _state.asStateFlow()

    // Приватный мутабельный стейт
    private val _foldedMap = mutableStateMapOf<Uri, Boolean>()
    // Публичный неизменяемый поток
    val foldedMap: Map<Uri, Boolean> get() = _foldedMap

    fun toggleFolder(uri: Uri) {
        val current = _foldedMap[uri] ?: false
        _foldedMap[uri] = !current
    }

    fun isFolderFolded(uri: Uri): Boolean {
        return _foldedMap[uri] ?: false
    }


    // TODO заменить DocumentFile на android.provider.DocumentsContract
    fun loadAndSetOriginalRoot(context: Context, folder: DocumentFile, backStackEntryId: String?) {
        viewModelScope.launch {
            clear()
            val originalHierarchy = buildFileHierarchy(
                context = context,
                document = folder,
                originalUri = folder.uri
            )
            setOriginalRoot(originalHierarchy)
            setOriginalRootBackStackEntryId(backStackEntryId)
        }
    }

    fun setOriginalRoot(newOriginalRoot: FileHierarchy) {
        Log.i("setOriginalRoot", "$newOriginalRoot")

        _state.update { it.copy(
            originalRoot = newOriginalRoot
        ) }
    }

    override fun setCurrentlyViewedFile(file: FileInfo) {
        _state.update { it.copy(
            currentlyViewedFile = file
        ) }
    }

    fun setOriginalRootBackStackEntryId(backStackEntryId: String?) {
        _state.update { it.copy(
            originalRootBackStackEntryId = backStackEntryId
        ) }
    }

    // Вызывается при гарантированном существовании папки
    fun getOriginalRootUri(): Uri? {
        return _state.value.originalRoot?.fileInfo?.uri?.toUri()
    }

    fun getOriginalRoot(): FileHierarchy? {
        Log.i("state.value.originalRoot", "${state.value.originalRoot}")
        return state.value.originalRoot
    }

    override fun getCurrentlyViewedFile(): FileInfo? {
        return _state.value.currentlyViewedFile
    }

    fun getOriginalRootBackStackEntryId(): String? {
        Log.i("state.value.originalRootBackStackEntryId", "${state.value.originalRootBackStackEntryId}")
        return state.value.originalRootBackStackEntryId
    }

    fun clear() {
        _state.update { it.copy(
            originalRoot = null
        ) }
    }

    fun debug() {
        Log.i("OriginalRoot", "${state.value.originalRoot}")
        Log.i("CurrentlyViewedFile", "${state.value.currentlyViewedFile}")

    }
}