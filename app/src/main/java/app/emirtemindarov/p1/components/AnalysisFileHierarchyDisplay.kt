package app.emirtemindarov.p1.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import app.emirtemindarov.p1.R
import app.emirtemindarov.p1.mvvm.data.FileHierarchy
import app.emirtemindarov.p1.mvvm.data.FileInfo
import kotlin.collections.forEach

@Composable
fun AnalysisFileHierarchyDisplay(
    fileHierarchy: FileHierarchy,
    modifier: Modifier = Modifier,
    onFileClick: (FileInfo) -> Unit = {}
) {
    val foldedMap = remember { mutableStateMapOf<String, Boolean>() }

    fun expandAll(root: FileHierarchy) {
        val allFolders = mutableListOf<Uri>()
        collectFolders(root, allFolders)

        allFolders.forEach { uri ->
            foldedMap[uri.toString()] = false
        }
    }

    fun collapseAll(root: FileHierarchy) {
        val allFolders = mutableListOf<Uri>()
        collectFolders(root, allFolders)

        allFolders.forEach { uri ->
            foldedMap[uri.toString()] = true
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        // === ВЕРХ (НЕЗАВИСИМЫЙ, НЕ РАСТЯГИВАЕТСЯ) ===
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Иерархия папки:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = { expandAll(fileHierarchy) }) {
                    Icon(
                        painter = painterResource(R.drawable.expand_all_24px),
                        contentDescription = null
                    )
                }

                IconButton(onClick = { collapseAll(fileHierarchy) }) {
                    Icon(
                        painter = painterResource(R.drawable.collapse_all_24px),
                        contentDescription = null
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // === НИЗ (НЕЗАВИСИМЫЙ, СВОЙ СКРОЛЛ / РАЗМЕР) ===
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            val horizontalScrollState = rememberScrollState()

            Box(modifier = Modifier.fillMaxWidth()) {
                LazyColumnWithItem(
                    modifier = Modifier.fillMaxSize(),
                    horizontalScrollState = horizontalScrollState
                ) {
                    AnalysisFileHierarchyNode(
                        fileHierarchy = fileHierarchy,
                        foldedMap = foldedMap,
                        depth = 0,
                        isRoot = true,
                        onFileClick = onFileClick
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalysisFileHierarchyNode(
    fileHierarchy: FileHierarchy,
    foldedMap: MutableMap<String, Boolean>,
    depth: Int,
    isRoot: Boolean,
    onFileClick: (FileInfo) -> Unit
) {
    val fileInfo = fileHierarchy.fileInfo
    val uri = fileInfo.uri
    val hasChildren = fileInfo.isDirectory && fileHierarchy.children.isNotEmpty()
    val folded = foldedMap[uri] ?: false

    val indentPerLevel = 20.dp
    val arrowSize = 16.dp
    val arrowOffset = (-18).dp
    val arrowSpace = 0.dp
    val iconTextSpacing = 4.dp
    val verticalSpacing = 4.dp

    val contentStartPadding = indentPerLevel * depth
    var isPressed by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(vertical = verticalSpacing)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .sizeIn(minWidth = 348.dp)
                        .align(Alignment.CenterStart)
                        .padding(start = contentStartPadding)
                        .background(
                            if (isPressed) {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
                            } else {
                                Color.Transparent
                            }
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    isPressed = true
                                    tryAwaitRelease()
                                    isPressed = false
                                },
                                onTap = {
                                    onFileClick(fileInfo)
                                },
                                onDoubleTap = {
                                    if (fileInfo.isDirectory && hasChildren) {
                                        foldedMap[uri] = !(foldedMap[uri] ?: false)
                                    }
                                }
                            )
                        }
                        /*.padding(horizontal = 10.dp, vertical = 8.dp)*/,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(arrowSpace))

                    Icon(
                        painter = painterResource(resolveAnalysisIcon(fileHierarchy, isRoot)),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.width(iconTextSpacing))

                    Text(
                        text = fileInfo.name,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (hasChildren) {
                    val touchPadding = 8.dp
                    val touchSize = arrowSize + touchPadding * 2
                    val interactionSource = remember { MutableInteractionSource() }

                    Box(
                        modifier = Modifier
                            .background(Color.Transparent)
                            .size(touchSize)
                            .offset(x = contentStartPadding + arrowOffset - touchPadding)
                            .align(Alignment.CenterStart)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                foldedMap[uri] = !(foldedMap[uri] ?: false)
                            }
                    ) {
                        Icon(
                            painter = painterResource(
                                if (folded) R.drawable.keyboard_arrow_right_24px
                                else R.drawable.keyboard_arrow_down_24px
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .size(arrowSize)
                                .align(Alignment.Center),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        if (fileInfo.isDirectory && !folded) {
            fileHierarchy.children.forEach { child ->
                AnalysisFileHierarchyNode(
                    fileHierarchy = child,
                    foldedMap = foldedMap,
                    depth = depth + 1,
                    isRoot = false,
                    onFileClick = onFileClick
                )
            }
        }
    }
}

private fun resolveAnalysisIcon(
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
            fileHierarchy.fileInfo.fileContent.isNullOrBlank() -> R.drawable.draft_24px
            fileHierarchy.fileInfo.mimeType in archiveMimeTypes -> R.drawable.folder_zip_24px
            else -> R.drawable.docs_24px
        }
    }
}

fun collectFolders(
    node: FileHierarchy,
    result: MutableList<Uri>
) {
    if (node.fileInfo.isDirectory) {
        result.add(node.fileInfo.uri.toUri())

        node.children.forEach {
            collectFolders(it, result)
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