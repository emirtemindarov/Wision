package app.emirtemindarov.p1.mvvm.singleFile

import androidx.compose.runtime.Stable
import app.emirtemindarov.p1.mvvm.data.FileInfo

@Stable
data class SingleFileState(
    // при выборе только файла / идентичен currentlyViewedFile ?
    val singleFile: FileInfo? = null,

    // текущий просматриваемый файл / идентичен singleFile ?
    //  / добавлен для соответствия интерфейсу FileStructureInterface
    val currentlyViewedFile: FileInfo? = null
)