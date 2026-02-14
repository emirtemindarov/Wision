package app.emirtemindarov.p1.components

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import app.emirtemindarov.p1.Environment
import app.emirtemindarov.p1.R
import app.emirtemindarov.p1.Screen
import app.emirtemindarov.p1.components.buttons.OpenDialogButton
import app.emirtemindarov.p1.mvvm.data.FileHierarchy
import app.emirtemindarov.p1.mvvm.data.FileInfo
import app.emirtemindarov.p1.mvvm.interfaces.FileStructureInterface
import app.emirtemindarov.p1.utils.FileUtils.formatDate

// Может работать и с папкой, и с файлом
@Composable
fun FileInfoDisplay(
    fileInfo: FileInfo,
    children: List<FileHierarchy>? = null,      // public final val children: List<FileHierarchy> = emptyList()
    fileStructure: FileStructureInterface,      // originalRootViewModel или singleFileViewModel
    navController: NavHostController,
) {

    //fileStructure.setCurrentlyViewedFile(fileInfo)

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {

        Log.i("1fileInfo", "$fileInfo")
        if (Environment.DEBUG) {
            Text("FileInfoDisplay")
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (fileInfo.isDirectory) {
            // Если папка
            Text(text = "Папка: ${fileInfo.name}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Путь: ${shortText(fileInfo.uri)}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier
                .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // TODO !!! Кнопка "Открыть анализ" если текущий документ еще не поменялся

                OpenDialogButton(
                    enabled = true,
                    dialogTitle = "Подтвердите действие",
                    dialogText = "Вы действительно хотите запустить анализ папки?",
                    onConfirm = {

                        // TODO проверить логику на пустой папке
                        val fileHierarchy = children?.let {
                            FileHierarchy(
                                fileInfo = fileInfo,
                                children = it
                            )
                        }

                        navController.navigate(
                            Screen.FolderAnalysisScreen(
                                fileHierarchyInfo = fileHierarchy.toString()
                            )
                        )
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.flowchart_24px),
                            contentDescription = "Провести анализ",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Провести анализ")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Log.i("children", "$children")



            // цикличный вывод дочерних элементов через LazyColumn с красивым оформлением
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(children ?: emptyList()) { child ->

                    val backgroundColor = if (child.fileInfo.isDirectory)
                        Color(0x0CFFD2AA)
                        else Color(0x0D73AAEF)

                    Surface(
                        color = backgroundColor,
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp, 0.dp, 8.dp, 8.dp)
                            .border(1.dp,
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(10.dp)
                            )
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    //onPress = { /* Called when the gesture starts */ },
                                    onTap = {
                                        // Called on Single Tap

                                        if (child.fileInfo.isDirectory) {
                                            Log.i("child is directory", "$child")

                                            navController.navigate(
                                                Screen.FolderDetailsScreen(
                                                    id = child.fileInfo.uri
                                                )
                                            )
                                        } else {
                                            // Для файлов
                                            Log.i("child is singleFile", "$child")

                                            navController.navigate(
                                                Screen.FileDetailsScreen(
                                                    id = child.fileInfo.uri
                                                )
                                            )
                                        }

                                    },

                                    //onDoubleTap = { /* Called on Double Tap */ },

                                    /*onLongPress = { offset ->
                                    // Called on Hold

                                    // isNavigating.value = true

                                    // TODO контекстное меню

                                    if (fileHierarchy.fileInfo.isDirectory) {
                                        // Для папок
                                        navController.navigate(
                                            Screen.FolderDetailsScreen(
                                                id = fileHierarchy.fileInfo.uri
                                            )
                                        )
                                    } else {
                                        // Для файлов
                                        navController.navigate(
                                            Screen.FileDetailsScreen(
                                                id = fileHierarchy.fileInfo.uri
                                            )
                                        )
                                    }

                                },*/
                                )
                            }

                    ) {
                        Column {
                            Column(modifier = Modifier.padding(start = 12.dp, top = 12.dp, end = 12.dp)) {
                                Text(
                                    text = child.fileInfo.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp)) {
                                Text(
                                    text = "Путь: ${shortText(child.fileInfo.uri)}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                if (!child.fileInfo.isDirectory) {
                                    Text(
                                        text = "Размер: ${child.fileInfo.size} байт",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.padding(start = 12.dp, bottom = 12.dp, end = 12.dp).fillMaxWidth()
                            ) {
                                Column {
                                    if (child.fileInfo.isDirectory) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.empty_folder_24px),
                                            contentDescription = "Папка",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource(id = R.drawable.docs_24px),
                                            contentDescription = "Файл",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }



        } else {
            // Если файл
            Text(text = "Размер: ${fileInfo.size} байт", style = MaterialTheme.typography.bodyMedium)
            Text(text = "MIME тип: ${fileInfo.mimeType}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Последнее изменение: ${formatDate(fileInfo.lastModified)}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier
                .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                OpenDialogButton(
                    enabled = true,
                    dialogTitle = "Подтвердите действие",
                    dialogText = "Вы действительно хотите запустить анализ файла?",
                    onConfirm = {
                        // если запускаем новый анализ (перевод в FileHierarchy для системности).
                        //  создаю FileHierarchy напрямую, так как не работаю с DocumentFile API
                        navController.navigate(
                            Screen.FileAnalysisScreen(
                                fileInfo = FileHierarchy(
                                    fileInfo = fileInfo
                                ).toString()
                            )
                        )
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.flowchart_24px),
                            contentDescription = "Провести анализ",
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text("Провести анализ")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Файл: ${fileInfo.name}",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Text(text = "${fileInfo.fileContent}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

fun shortText(text: String, head: Int = 48, tail: Int = 48): String {
    if (text.length <= head + tail + 3) return text
    val start = text.take(head)
    val end = text.takeLast(tail)
    return "$start...$end"
}
