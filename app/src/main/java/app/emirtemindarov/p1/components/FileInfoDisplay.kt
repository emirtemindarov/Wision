package app.emirtemindarov.p1.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import app.emirtemindarov.p1.Environment
import app.emirtemindarov.p1.R
import app.emirtemindarov.p1.Screen
import app.emirtemindarov.p1.components.buttons.OpenDialogButton
import app.emirtemindarov.p1.components.dividers.HorizontalDivider
import app.emirtemindarov.p1.mvvm.data.FileHierarchy
import app.emirtemindarov.p1.mvvm.data.FileInfo
import app.emirtemindarov.p1.mvvm.interfaces.FileStructureInterface
import app.emirtemindarov.p1.ui.theme.CustomTextStyles
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
        /*.padding(16.dp)*/) {

        Log.i("1fileInfo", "$fileInfo")

        if (Environment.DEBUG) {
            Text("FileInfoDisplay")
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (fileInfo.isDirectory) {
            // Если папка

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

                        val fileHierarchy = children?.let {
                            FileHierarchy(
                                fileInfo = fileInfo,
                                children = it
                            )
                        }

                        navController.navigate(
                            Screen.AnalysisScreen(
                                // FIXME отправлять json !!!!!!!!!!!!!!!!!!!!!!
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

                Text(
                    text = "Содержимое папки:",
                    style = MaterialTheme.typography.titleMedium
                )

                //HorizontalDivider(thickness = (0.3).dp)
            }

            Log.i("children", "$children")




            val sortedChildren = remember(children) {
                val dirs = children.orEmpty()
                    .filter { it.fileInfo.isDirectory }
                    .sortedBy { it.fileInfo.name.lowercase() }

                val files = children.orEmpty()
                    .filter { !it.fileInfo.isDirectory }
                    .sortedBy { it.fileInfo.name.lowercase() }

                dirs + files
            }

            //Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {


                val listState = rememberLazyListState()

                LazyColumnFadeContainer(
                    listState = listState,
                    modifier = Modifier.fillMaxSize()
                ) {

                    // цикличный вывод дочерних элементов через LazyColumn с красивым оформлением
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            sortedChildren,
                            key = { it.fileInfo.uri }
                        ) { child ->

                            val isDirectory = child.fileInfo.isDirectory

                            val backgroundColor = if (isDirectory)
                                Color(0x1AFFD2AA)
                            else Color(0x0D73AAEF)

                            Surface(
                                color = backgroundColor,
                                tonalElevation = 0.dp,
                                shadowElevation = 0.dp,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp, 0.dp, 8.dp, 8.dp)
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.primary,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            //onPress = { /* Called when the gesture starts */ },
                                            onTap = {
                                                // Called on Single Tap

                                                if (isDirectory) {
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
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = child.fileInfo.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            maxLines = 2,
                                            lineHeight = 16.sp,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp)) {
                                        Row {
                                            Text(
                                                text = "Дата изменения: ",
                                                fontFamily = CustomTextStyles.Standard,
                                                fontWeight = FontWeight.Light,
                                                fontSize = 12.sp,
                                                lineHeight = 4.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = formatDate(child.fileInfo.lastModified),
                                                fontFamily = CustomTextStyles.Standard,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 12.sp,
                                                lineHeight = 4.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        if (isDirectory) {
                                            Row {
                                                Text(
                                                    text = "Количество элементов: ",
                                                    fontFamily = CustomTextStyles.Standard,
                                                    fontWeight = FontWeight.Light,
                                                    fontSize = 12.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "${child.children.count()}",
                                                    fontFamily = CustomTextStyles.Standard,
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 12.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        } else {
                                            Row {
                                                Text(
                                                    text = "Размер: ",
                                                    fontFamily = CustomTextStyles.Standard,
                                                    fontWeight = FontWeight.Light,
                                                    fontSize = 12.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "${child.fileInfo.size} байт",
                                                    fontFamily = CustomTextStyles.Standard,
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 12.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                    Row(
                                        horizontalArrangement = Arrangement.End,
                                        modifier = Modifier
                                            .padding(
                                                start = 12.dp,
                                                bottom = 12.dp,
                                                end = 12.dp
                                            ).fillMaxWidth()
                                    ) {
                                        Column {
                                            if (isDirectory) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.folder_24px),
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
                }
            }



        } else {
            // Если файл

            Spacer(modifier = Modifier.height(8.dp))

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
                            Screen.AnalysisScreen(
                                fileHierarchyInfo = FileHierarchy(
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
                    text = "Содержимое файла:",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Можно вызвать отдельно (нужен fileInfo)
            LazyColumnWithItem(
                modifier = Modifier.fillMaxSize()
            ) {
                CodeTextWithLineNumbers(text = fileInfo.fileContent ?: "")
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
