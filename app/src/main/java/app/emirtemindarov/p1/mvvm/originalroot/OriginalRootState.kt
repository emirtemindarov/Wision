package app.emirtemindarov.p1.mvvm.originalroot

import androidx.compose.runtime.Stable
import app.emirtemindarov.p1.mvvm.data.FileHierarchy
import app.emirtemindarov.p1.mvvm.data.FileInfo

@Stable
data class OriginalRootState(
    // начало начального дерева, моя адаптация структуры данных
    val originalRoot: FileHierarchy? = null,

    // текущий просматриваемый файл или папка
    val currentlyViewedFile: FileInfo? = null
)