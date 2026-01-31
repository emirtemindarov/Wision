package app.emirtemindarov.p1

import app.emirtemindarov.p1.room.GraphLoadMode
import kotlinx.serialization.Serializable

sealed class Screen {

    // Высокоуровневый экран (Вкладка с выбором файлов и переходом к анализу) / Начальная вкладка / Является вкладкой даже при отсутствии привычного отображения элементов Scaffold
    @Serializable
    data object AnalysisRoot : Screen()

    // Высокоуровневый экран (Вкладка с сохраненными графами)
    @Serializable
    data object SavedRoot : Screen()

    // Начальный экран AnalysisRoot / Первый экран видимый пользователю
    @Serializable
    data object HomeScreen : Screen()

    // Единственный экран на вкладке с сохраненными графами
    @Serializable
    data object SavedScreen : Screen()

    @Serializable
    data object FileSelectionScreen : Screen()

    @Serializable
    data object FolderSelectionScreen : Screen()

    // Только для отображения информации о файле при нажатии из иерархии файлов / без возможности выбора нового файла / основной элемент - кнопка перехода на FileAnalysisScreen / один из элементов - FileInfoDisplay
    @Serializable
    data class FileDetailsScreen(
        val id: String
    )

    // Только для отображения информации о папке при нажатии из иерархии файлов / без возможности выбора нового файла / основной элемент - кнопка перехода на FolderAnalysisScreen / один из элементов - FileInfoDisplay
    @Serializable
    data class FolderDetailsScreen(
        val id: String
    )

    // Разбор элементов и связей в файле / Важный экран / может быть вызван из любой вкладки
    @Serializable
    data class FileAnalysisScreen(
        val graphId: String? = null,   // если не null → грузим из БД
        val fileInfo: String? = null    // если не null → новый анализ
    ) : Screen()

    // Разбор элементов и связей в папке / Важный экран / может быть вызван из любой вкладки
    @Serializable
    data class FolderAnalysisScreen(
        val graphId: String? = null,   // если не null → грузим из БД
        val fileHierarchyInfo: String? = null    // если не null → новый анализ
    ) : Screen()
}