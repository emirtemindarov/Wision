package app.emirtemindarov.p1.components

import android.R.attr.maxWidth
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.remember
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import app.emirtemindarov.p1.mvvm.data.FileInfo

// TODO нужна легкая версия
@Composable
fun FileHierarchyDisplay(
    fileHierarchy: FileHierarchy,
    originalRootViewModel: OriginalRootViewModel,
    navController: NavHostController,
    isRoot: Boolean = false,
    depth: Int = 0
) {
    val folderUri = fileHierarchy.fileInfo.uri.toUri()
    val folded = originalRootViewModel.isFolderFolded(folderUri)

    var details by remember { mutableStateOf<FileInfo?>(null) }

    details?.let { fileInfo ->
        FileInfoWindow(fileInfo) { details = null }
    }

    val config = remember { LayoutConfig() }

    val contentStartPadding = config.indentPerLevel * depth

    Column(modifier = Modifier.padding(vertical = 2.dp)) {

        var isPressed by remember { mutableStateOf(false) }

        Row(modifier = Modifier.fillMaxWidth()) {

            Box(
                modifier = Modifier
                    .fillMaxHeight()            // Box занимает всю высоту строки
                    .align(Alignment.CenterVertically) // сам Box центрируется в Row
            ) {
                Row(
                    modifier = Modifier
                        .sizeIn(minWidth = 348.dp)
                        .align(Alignment.CenterStart)  // строка центрируется внутри Box
                        .padding(start = contentStartPadding)
                        .background(
                            if (isPressed)
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
                            else
                                MaterialTheme.colorScheme.surface
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    isPressed = true
                                    tryAwaitRelease()
                                    isPressed = false
                                },
                                onTap = {
                                    if (fileHierarchy.fileInfo.isDirectory) {
                                        navController.navigate(
                                            Screen.FolderDetailsScreen(
                                                id = fileHierarchy.fileInfo.uri
                                            )
                                        )
                                    } else {
                                        navController.navigate(
                                            Screen.FileDetailsScreen(
                                                id = fileHierarchy.fileInfo.uri
                                            )
                                        )
                                    }
                                },
                                onDoubleTap = {
                                    if (fileHierarchy.fileInfo.isDirectory &&
                                        fileHierarchy.children.isNotEmpty()
                                    ) {
                                        originalRootViewModel.toggleFolder(folderUri)
                                    }
                                },
                                onLongPress = {
                                    details = fileHierarchy.fileInfo
                                }
                            )
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(resolveIcon(fileHierarchy, isRoot)),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.width(config.iconTextSpacing))

                    Text(
                        text = fileHierarchy.fileInfo.name,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Стрелка с расширенной областью нажатия без изменения визуальной компоновки
                if (fileHierarchy.fileInfo.isDirectory &&
                    fileHierarchy.children.isNotEmpty()
                ) {
                    val touchPadding = 8.dp
                    val touchSize = config.arrowSize + touchPadding * 2

                    val interactionSource = remember { MutableInteractionSource() }

                    // Внешний Box это зона касания; внутри него иконка центрирована.
                    Box(
                        modifier = Modifier
                            .background(Color.Transparent)
                            .size(touchSize) // увеличенная зона касания
                            .offset(x = contentStartPadding + config.arrowOffset - touchPadding)
                            .align(Alignment.CenterStart)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                originalRootViewModel.toggleFolder(folderUri)
                            }
                    ) {
                        Icon(
                            painter = painterResource(
                                if (folded)
                                    R.drawable.keyboard_arrow_right_24px
                                else
                                    R.drawable.keyboard_arrow_down_24px
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .size(config.arrowSize)
                                .align(Alignment.Center),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Дети
        if (fileHierarchy.fileInfo.isDirectory && !folded) {
            fileHierarchy.children.forEach { child ->
                Spacer(modifier = Modifier.height(config.verticalSpacing))

                FileHierarchyDisplay(
                    fileHierarchy = child,
                    originalRootViewModel = originalRootViewModel,
                    navController = navController,
                    depth = depth + 1
                )
            }
        }
    }
}



private fun resolveIcon(
    fileHierarchy: FileHierarchy,
    isRoot: Boolean
): Int {
    return if (fileHierarchy.fileInfo.isDirectory) {
        when {
            isRoot -> R.drawable.home_24px
            fileHierarchy.children.isEmpty() -> R.drawable.empty_folder_24px
            else -> R.drawable.folder_24px
        }
    } else {
        when {
            fileHierarchy.fileInfo.fileContent.isNullOrBlank() ->
                R.drawable.draft_24px

            fileHierarchy.fileInfo.mimeType in archiveMimeTypes ->
                R.drawable.folder_zip_24px

            else -> R.drawable.docs_24px
        }
    }
}

private val archiveMimeTypes = listOf(
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
)

private class LayoutConfig {
    val indentPerLevel = 20.dp
    val arrowSize = 16.dp
    val arrowOffset = (-18).dp
    val iconTextSpacing = 4.dp
    val verticalSpacing = 4.dp
}