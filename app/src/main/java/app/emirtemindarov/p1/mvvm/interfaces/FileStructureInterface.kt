package app.emirtemindarov.p1.mvvm.interfaces

import app.emirtemindarov.p1.mvvm.data.FileInfo

/**
 * Адаптирует originalRootViewModel или singleFileViewModel под единый интерфейс.
 */
interface FileStructureInterface {
    fun getCurrentlyViewedFile(): FileInfo?
    fun setCurrentlyViewedFile(file: FileInfo)
}
