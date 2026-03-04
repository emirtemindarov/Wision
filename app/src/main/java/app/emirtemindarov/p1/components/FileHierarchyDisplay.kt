package app.emirtemindarov.p1.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.remember
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import app.emirtemindarov.p1.Environment
import app.emirtemindarov.p1.R
import app.emirtemindarov.p1.Screen
import app.emirtemindarov.p1.mvvm.originalroot.OriginalRootViewModel
import app.emirtemindarov.p1.mvvm.data.FileHierarchy

@Composable
fun FileHierarchyDisplay(
    fileHierarchy: FileHierarchy,
    originalRootViewModel: OriginalRootViewModel,
    navController: NavHostController,
    isRoot: Boolean = false
) {

    Log.i("checkpoint FHDisplay", "FHD")
    Log.i("fileHierarchy.fileInfo.uri", fileHierarchy.fileInfo.uri)

    val folderUri = fileHierarchy.fileInfo.uri.toUri()
    val folded = originalRootViewModel.isFolderFolded(folderUri)

    val parentLeftPadding = 18    // keyboard_arrow + spacer

    // блок содержащий строку
    Column(
        modifier = Modifier.padding(start = 26.dp)/*.background(Color.Yellow)*/,
    ) {

        // Тестовая метка уровня
        if (Environment.DEBUG) { Text("FileHierarchyDisplay") }

        var isPressed by remember { mutableStateOf(false) }

        // область касания
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isPressed)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
                    else
                        MaterialTheme.colorScheme.surface
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            // Called when the gesture starts
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        },
                        onTap = {
                            // Called on Single Tap
                            if (fileHierarchy.fileInfo.isDirectory) {
                                Log.i("hierarchy root", fileHierarchy.fileInfo.uri)

                                if (fileHierarchy.children.isNotEmpty()) {
                                    originalRootViewModel.toggleFolder(folderUri)
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
                },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // строка с индикатором сворачиваемости, иконкой элемента и его названием
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (fileHierarchy.fileInfo.isDirectory) {

                    val hasContent = fileHierarchy.children.isNotEmpty()

                    if (hasContent) {
                        Icon(
                            painter = painterResource(
                                if (folded)
                                    R.drawable.keyboard_arrow_right_24px
                                else
                                    R.drawable.keyboard_arrow_down_24px
                            ),
                            contentDescription = null,
                            modifier = Modifier.size((parentLeftPadding - 4).dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        // Иконка не пустой папки
                        Icon(
                            painter = painterResource(
                                if (isRoot)
                                    R.drawable.home_24px
                                else
                                    R.drawable.folder_24px
                            ),
                            contentDescription = "Папка",
                            tint = MaterialTheme.colorScheme.onSurface
                        )

                    } else {

                        Spacer(modifier = Modifier.width(parentLeftPadding.dp))   // размер несуществующей иконки свернутости 24 + растояние 4

                        // Иконка пустой папки
                        Icon(
                            painter = painterResource(
                                if (isRoot)
                                    R.drawable.home_24px
                                else
                                    R.drawable.empty_folder_24px
                            ),
                            contentDescription = "Папка",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                } else { // Иконка файла

                    Spacer(modifier = Modifier.width(parentLeftPadding.dp))

                    val iconRes = when {
                        fileHierarchy.fileInfo.fileContent.isNullOrBlank() ->
                            R.drawable.draft_24px

                        fileHierarchy.fileInfo.mimeType in listOf(
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
                        ) -> R.drawable.folder_zip_24px

                        else -> R.drawable.docs_24px
                    }

                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = fileHierarchy.fileInfo.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        // Отображение потомков (только для папок) если развернута
        if (fileHierarchy.fileInfo.isDirectory && !folded) {

            Log.i("fileHierarchy.children", "${fileHierarchy.children}")
            fileHierarchy.children.forEach { child ->

                Spacer(modifier = Modifier.height(8.dp))

                FileHierarchyDisplay(
                    fileHierarchy = child,
                    originalRootViewModel = originalRootViewModel,
                    navController = navController,
                )
            }
        }
    }
}

