package app.emirtemindarov.p1.mvvm.data

import android.net.Uri
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * Представляет информацию о файле или папке. Не хранит иерархию.
 */
@Serializable
data class FileInfo(
    val name: String,
    val uri: String,
    val size: Long,
    val mimeType: String?,
    val lastModified: Long,
    val isDirectory: Boolean,
    val fileContent: String?
)
