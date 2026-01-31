package app.emirtemindarov.p1.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import app.emirtemindarov.p1.mvvm.data.FileHierarchy
import app.emirtemindarov.p1.mvvm.data.FileInfo
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Работает - не трожь!
 */
object FileUtils {

    /**
     * Создает иерархию файлов, рекурсивно перебирая все файлы и папки внутри указанной папки. Используется при работе с DocumentFile API.
     */
    suspend fun buildFileHierarchy(
        context: Context,
        document: DocumentFile,
        originalUri: Uri? = null
    ): FileHierarchy {
        return withContext(Dispatchers.IO) {

            val fileInfo = FileInfo(
                name = document.name ?: "Неизвестная папка",
                uri = document.uri.toString(),
                size = document.length(),
                mimeType = document.type,
                lastModified = document.lastModified(),
                isDirectory = document.isDirectory,
                fileContent = readFileContent(
                    context = context,
                    uri = document.uri
                )
            )

            val children = document.listFiles().map { file ->
                Log.i("1file","${file.name} | parent = ${file.parentFile?.name}")
                buildFileHierarchy(
                    context = context,
                    document = file,
                    originalUri = originalUri
                )
            }

            FileHierarchy(
                fileInfo = fileInfo,
                children = children
            )
        }
    }

    /**
     * Создает единичный файл при выборе указанного файла.
     */
    suspend fun buildSingleFile(
        context: Context,
        document: DocumentFile
    ): FileInfo {
        return withContext(Dispatchers.IO) {

            if (document.isDirectory) {
                Log.w("singleFileIsDirectory!", "${document.name}")
            }

            FileInfo(
                name = document.name ?: "Неизвестный файл",
                uri = document.uri.toString(),
                size = document.length(),
                mimeType = document.type,
                lastModified = document.lastModified(),
                isDirectory = document.isDirectory,
                fileContent = readFileContent(
                    context = context,
                    uri = document.uri
                )
            )
        }
    }

    fun findInFileHierarchyByUri(
        node: FileHierarchy,
        targetUri: Uri
    ): FileHierarchy? {

        Log.i("node targetUri", "<node: $node | targetUri: $targetUri>")
        Log.i("check1", node.fileInfo.uri)
        Log.i("node children[]", "${node.children}")

        if (node.fileInfo.uri == targetUri.toString()) {
            return node
        }
        for (child in node.children) {
            val result = findInFileHierarchyByUri(child, targetUri)
            if (result != null) return result
        }
        return null
    }

    // FIXME не использовать!  на данный момент вызывает сам себя, не применяется больше нигде
    fun findInDocumentFileByUri(
        node: DocumentFile,
        targetUri: Uri
    ): DocumentFile? {
        Log.i("node targetUri", "<node: ${node.uri} | targetUri: $targetUri>")
        Log.i("check1", "${node.uri}")

        if (node.uri == targetUri) {
            return node
        }

        if (node.isDirectory) {
            val children = node.listFiles()
            Log.i("node children[]", children.joinToString { it.uri.toString() })
            for (child in children) {
                val result = findInDocumentFileByUri(child, targetUri)
                if (result != null) return result
            }
        }

        return null
    }



    /**
     * Проверяет, является ли MIME-тип файла текстовым.
     */
    private fun isTextFile(mimeType: String?): Boolean {
        return mimeType?.startsWith("text/") == true || mimeType == "application/json" || mimeType == "application/xml"
    }

    /**
     * Считывает текстовое содержимое файла.
     */
    private fun readFileContent(context: Context, uri: Uri): String? {
        return try {
            context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Форматирует метку времени в удобочитаемый формат даты и времени.
     */
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
