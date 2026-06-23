package app.emirtemindarov.p1.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.emirtemindarov.p1.R
import app.emirtemindarov.p1.mvvm.data.FileHierarchy
import app.emirtemindarov.p1.mvvm.data.FileInfo
import app.emirtemindarov.p1.utils.FileUtils.formatDate

@Composable
fun AnalysisFileInfoDisplay(
    fileInfo: FileInfo,
    children: List<FileHierarchy>,
    onSelect: (FileInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val sortedChildren = remember(children) {
        val dirs = children
            .filter { it.fileInfo.isDirectory }
            .sortedBy { it.fileInfo.name.lowercase() }

        val files = children
            .filter { !it.fileInfo.isDirectory }
            .sortedBy { it.fileInfo.name.lowercase() }

        dirs + files
    }

    val uriExpanded = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
        /*.verticalScroll(rememberScrollState())*/
    ) {
        Text(
            text = fileInfo.name,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        /*Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Путь", style = MaterialTheme.typography.titleMedium)

            TextButton(onClick = { uriExpanded.value = !uriExpanded.value }) {
                Text(if (uriExpanded.value) "Свернуть" else "Развернуть")
            }
        }

        Text(
            text = fileInfo.uri,
            maxLines = if (uriExpanded.value) 25 else 1,
            overflow = TextOverflow.MiddleEllipsis,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Изменен: ${formatDate(fileInfo.lastModified)}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(8.dp))*/

        if (fileInfo.isDirectory) {
            /*Text(
                text = "Тип: Папка",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Элементов: ${sortedChildren.size}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(16.dp))*/

            Text(
                text = "Содержимое папки:",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(12.dp))

            val listState = rememberLazyListState()

            LazyColumnFadeContainer(
                listState = listState,
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        sortedChildren,
                        key = { it.fileInfo.uri }
                    ) { child ->
                        val isDirectory = child.fileInfo.isDirectory
                        val backgroundColor = if (isDirectory) {
                            Color(0x1AFFD2AA)
                        } else {
                            Color(0x0D73AAEF)
                        }

                        Surface(
                            color = backgroundColor,
                            tonalElevation = 0.dp,
                            shadowElevation = 0.dp,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    onSelect(child.fileInfo)
                                }
                        ) {
                            Column {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = child.fileInfo.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                                    Row {
                                        Text(
                                            text = "Дата изменения: ",
                                            fontWeight = FontWeight.Light,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = formatDate(child.fileInfo.lastModified),
                                            fontWeight = FontWeight.Medium,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }

                                    if (isDirectory) {
                                        Row {
                                            Text(
                                                text = "Количество элементов: ",
                                                fontWeight = FontWeight.Light,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                            Text(
                                                text = "${child.children.size}",
                                                fontWeight = FontWeight.Medium,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    } else {
                                        Row {
                                            Text(
                                                text = "Размер: ",
                                                fontWeight = FontWeight.Light,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                            Text(
                                                text = "${child.fileInfo.size} байт",
                                                fontWeight = FontWeight.Medium,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }

                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth()
                                ) {
                                    Icon(
                                        painter = painterResource(
                                            if (isDirectory) R.drawable.folder_24px
                                            else R.drawable.docs_24px
                                        ),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            /*Text(
                text = "Тип: Файл",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Размер: ${fileInfo.size} байт",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "MIME: ${fileInfo.mimeType}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(16.dp))*/

            Text(
                text = "Содержимое файла:",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(12.dp))

            LazyColumnWithItem(
                modifier = Modifier.fillMaxWidth()
            ) {
                CodeTextWithLineNumbers(text = fileInfo.fileContent.orEmpty())
            }
        }
    }
}