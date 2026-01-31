package app.emirtemindarov.p1.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import app.emirtemindarov.p1.Environment
import app.emirtemindarov.p1.R
import app.emirtemindarov.p1.Screen
import app.emirtemindarov.p1.mvvm.originalroot.OriginalRootViewModel
import app.emirtemindarov.p1.mvvm.data.FileHierarchy

// TODO проверить отображение при большом названии файла или папки !!!

@Composable
fun FileHierarchyDisplay(
    fileHierarchy: FileHierarchy,
    originalRootViewModel: OriginalRootViewModel,
    navController: NavHostController,
) {

    Log.i("checkpoint FHDisplay", "FHD")
    Log.i("fileHierarchy.fileInfo.uri", fileHierarchy.fileInfo.uri.toString())

    val folderUri = fileHierarchy.fileInfo.uri.toUri()
    val folded = originalRootViewModel.isFolderFolded(folderUri)
    val parentLeftGap = 22    // keyboard_arrow + spacer

    Column(modifier = Modifier.padding(start = 24.dp, top = 4.dp, bottom = 4.dp)) {

        // Тестовая метка уровня
        if (Environment.DEBUG) { Text("FileHierarchyDisplay") }

        Row(
            modifier = Modifier
                .fillMaxWidth()

                .pointerInput(Unit) {
                    detectTapGestures(
                        //onPress = { /* Called when the gesture starts */ },
                        onTap = {
                            // Called on Single Tap
                            if (fileHierarchy.fileInfo.isDirectory) {
                                Log.i("hierarchy root", fileHierarchy.fileInfo.uri)

                                originalRootViewModel.toggleFolder(folderUri)

                                if (fileHierarchy.children.isNotEmpty()) {
                                    Log.i(
                                        "first children",
                                        fileHierarchy.children.first().fileInfo.uri
                                    )
                                }

                            } else {
                                // Для файлов
                                navController.navigate(
                                    Screen.FileDetailsScreen(
                                        id = fileHierarchy.fileInfo.uri
                                    )
                                )
                            }

                        },

                        //onDoubleTap = { /* Called on Double Tap */ },

                        onLongPress = { offset ->
                            // Called on Hold
                            // TODO контекстное меню?

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

                        },
                    )
                }
                /*.clickable {

                }*/,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (fileHierarchy.fileInfo.isDirectory) { // Иконка папки
                    if (!folded) {
                        Icon(
                            painter = painterResource(id = R.drawable.keyboard_arrow_down_24px),
                            contentDescription = "Папка развернута",
                            modifier = Modifier.size((parentLeftGap - 4).dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.keyboard_arrow_right_24px),
                            contentDescription = "Папка свернута",
                            modifier = Modifier.size((parentLeftGap - 4).dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // TODO пустая папка?

                    Icon(
                        painter = painterResource(id = R.drawable.folder_24px),
                        contentDescription = "Папка",
                        tint = MaterialTheme.colorScheme.onSurface
                    )

                } else { // Иконка файла

                    Spacer(modifier = Modifier.width(parentLeftGap.dp))

                    if (fileHierarchy.fileInfo.fileContent.isNullOrBlank()) { // если файл пустой или нет информации о его содержимом
                        Icon(
                            painter = painterResource(id = R.drawable.draft_24px),
                            contentDescription = "Пустой файл",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    } else { // zip папка считается файлом!
                        if (fileHierarchy.fileInfo.mimeType in listOf(
                                "application/zip",
                                "application/x-zip",
                                "application/x-zip-compressed",
                                "application/rar",
                                "application/x-rar",
                                "application/x-rar-compressed",
                                "application/vnd.rar",
                                "application/x-7z-compressed",
                                "application/x-7zip",
                                "application/7z",
                                "application/x-tar",
                                "application/x-gtar",
                                "application/tar",
                                "application/gzip",
                                "application/x-gzip",
                                "application/x-bzip2",
                                "application/bzip2"
                        )) {
                            Icon(
                                painter = painterResource(id = R.drawable.folder_zip_24px),
                                contentDescription = "Zip папка",
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

                Spacer(modifier = Modifier.width(2.dp))

                Text(
                    text = fileHierarchy.fileInfo.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,   // TODO полезная вещь!!!
                )
            }
            
            /*// FIXME Не нужен (Показывает первые 50 символов файла)
            if (!fileHierarchy.fileInfo.isDirectory && fileHierarchy.fileInfo.fileContent != null) {
                Text(
                    text = "Текст: ${fileHierarchy.fileInfo.fileContent.take(50)}...",
                    style = MaterialTheme.typography.bodySmall
                )
            }*/
        }

        // Отображение потомков (только для папок) если развернута
        if (fileHierarchy.fileInfo.isDirectory && !folded) {
            Log.i("fileHierarchy.children", "${fileHierarchy.children}")

            fileHierarchy.children.forEach { child ->

                FileHierarchyDisplay(
                    fileHierarchy = child,
                    originalRootViewModel = originalRootViewModel,
                    navController = navController,
                )
            }
        }
    }
}

