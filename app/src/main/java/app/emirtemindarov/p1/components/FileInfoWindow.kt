package app.emirtemindarov.p1.components

import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import app.emirtemindarov.p1.mvvm.data.FileInfo
import app.emirtemindarov.p1.utils.FileUtils.formatDate

// окно информации о файле
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileInfoWindow(
    file: FileInfo,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {

                // TODO небольшая информация о файле (учесть щрифт и цвет)

                Text("$file")

                if (file.isDirectory) {
                    // Если папка
                    Text(text = "Папка: ${file.name}", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Путь: ${shortText(file.uri)}", style = MaterialTheme.typography.bodyMedium)
                } else {
                    // Если файл
                    Text(text = "Размер: ${file.size} байт", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "MIME тип: ${file.mimeType}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Последнее изменение: ${formatDate(file.lastModified)}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}