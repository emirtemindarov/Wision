package app.emirtemindarov.p1.mvvm.data

import kotlinx.serialization.Serializable

/**
 * Представляет иерархию файлов и папок.
 */
@Serializable
data class FileHierarchy(
    val fileInfo: FileInfo,
    val children: List<FileHierarchy> = emptyList(),
)
