package app.emirtemindarov.p1.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.emirtemindarov.p1.mvvm.data.FileInfo
import app.emirtemindarov.p1.utils.FileUtils.formatDate
import androidx.compose.runtime.*
import app.emirtemindarov.p1.assistant.data.GraphModel
import app.emirtemindarov.p1.mvvm.data.FileHierarchy
import kotlinx.coroutines.launch
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.net.toUri
import app.emirtemindarov.p1.utils.FileUtils.findInFileHierarchyByUri
import app.emirtemindarov.p1.R
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.ui.draw.shadow
import app.emirtemindarov.p1.mvvm.fileinfowindow.FileInfoWindowViewModel
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import app.emirtemindarov.p1.components.buttons.ComplexButton
import app.emirtemindarov.p1.ui.theme.CustomTextStyles
import kotlin.math.roundToInt

// окно информации о файле
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileInfoWindow(
    file: FileInfo,
    displayFileContent: Boolean = true,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var uriExpanded by remember { mutableStateOf(false) }
    var contentExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Text(
                text = file.name,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(12.dp))

            // URI
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Путь", style = MaterialTheme.typography.titleMedium)

                TextButton(onClick = { uriExpanded = !uriExpanded }) {
                    Text(if (uriExpanded) "Свернуть" else "Развернуть")
                }
            }

            Text(
                text = file.uri,
                maxLines = if (uriExpanded) 25 else 1,
                overflow = TextOverflow.MiddleEllipsis,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(12.dp))

            if (!file.isDirectory) {

                Text(
                    text = "Размер: ${file.size} байт",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "MIME: ${file.mimeType}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Изменен: ${formatDate(file.lastModified)}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(12.dp))

                // Код в окне
                if (displayFileContent) {
                    Text("Содержимое", style = MaterialTheme.typography.titleMedium)

                    Spacer(Modifier.height(12.dp))

                    file.fileContent?.let { fileContent ->
                        // Можно вызвать отдельно (нужен fileInfo)
                        LazyColumnWithItem(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            CodeTextWithLineNumbers(text = fileContent)
                        }
                    }
                }
            }
        }
    }
}


// окно информации о файле (тестовое)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileInfoWindow(
    id: String,
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

                Text(id)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileInfoWindowLocked(
    graph: GraphModel,
    graphSource: FileHierarchy,
    isPortrait: Boolean,
    initialInspectedNodeId: String? = null,
    onDismiss: () -> Unit,
    onFocusNodeSelected: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val windowVm: FileInfoWindowViewModel = viewModel()

    LaunchedEffect(graphSource.fileInfo.uri) {
        windowVm.syncGraphRoot(graphSource.fileInfo.uri)
    }

    LaunchedEffect(initialInspectedNodeId) {
        if (initialInspectedNodeId != null) {
            windowVm.selectTab(2)
            windowVm.inspectEdges(initialInspectedNodeId)
        }
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { target -> target != SheetValue.Hidden }
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val sheetHeightPx = with(density) { maxHeight.toPx() }
        val dismissThresholdPx = sheetHeightPx * 0.33f

        val dragOffset = remember { Animatable(0f) }
        var dragDirection by remember { mutableIntStateOf(0) } // -1 вверх, 1 вниз, 0 нет движения

        fun dismissAnimated(afterMove: (() -> Unit)? = null) {
            scope.launch {
                dragDirection = 1
                dragOffset.animateTo(
                    targetValue = sheetHeightPx,
                    animationSpec = tween(220)
                )
                afterMove?.invoke()
                onDismiss()
            }
        }

        fun closeSheet() {
            dismissAnimated()
        }

        fun focusAndClose(nodeId: String) {
            onFocusNodeSelected(nodeId)
            dismissAnimated()
        }

        fun snapBack() {
            scope.launch {
                dragDirection = 0
                dragOffset.animateTo(
                    targetValue = 0f,
                    animationSpec = spring()
                )
            }
        }

        fun dismissByDrag() {
            scope.launch {
                dragDirection = 1
                dragOffset.animateTo(
                    targetValue = sheetHeightPx,
                    animationSpec = tween(180)
                )
                closeSheet()
            }
        }

        val dragIconRes = when (dragDirection) {
            1 -> R.drawable.keyboard_arrow_down_24px
            -1 -> R.drawable.keyboard_arrow_up_24px
            else -> R.drawable.keyboard_arrow_down_24px
        }

        ModalBottomSheet(
            onDismissRequest = { closeSheet() },
            sheetState = sheetState,
            sheetGesturesEnabled = false,
            dragHandle = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationY = dragOffset.value
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        vertical = if (isPortrait) 16.dp else 0.dp,
                        horizontal = if (isPortrait) 16.dp else 60.dp
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .pointerInput(sheetHeightPx) {
                            detectVerticalDragGestures(
                                onVerticalDrag = { change, dragAmount ->
                                    change.consume()

                                    dragDirection = when {
                                        dragAmount > 0f -> 1
                                        dragAmount < 0f -> -1
                                        else -> dragDirection
                                    }

                                    scope.launch {
                                        val next = (dragOffset.value + dragAmount)
                                            .coerceIn(0f, sheetHeightPx)
                                        dragOffset.snapTo(next)
                                    }
                                },
                                onDragEnd = {
                                    if (dragOffset.value > dismissThresholdPx) {
                                        dismissByDrag()
                                    } else {
                                        snapBack()
                                    }
                                },
                                onDragCancel = {
                                    snapBack()
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = { closeSheet() }) {
                        Icon(
                            painter = painterResource(dragIconRes),
                            contentDescription = "Закрыть",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                PrimaryTabRow(selectedTabIndex = windowVm.selectedTab) {
                    Tab(
                        selected = windowVm.selectedTab == 0,
                        onClick = { windowVm.selectTab(0) },
                        text = { Text("Источник") }
                    )
                    Tab(
                        selected = windowVm.selectedTab == 1,
                        onClick = { windowVm.selectTab(1) },
                        text = { Text("Узлы") }
                    )
                    Tab(
                        selected = windowVm.selectedTab == 2,
                        onClick = { windowVm.selectTab(2) },
                        text = { Text("Связи") }
                    )
                }

                Spacer(Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    when (windowVm.selectedTab) {

                        0 -> {
                            when (val currentFile = windowVm.navigationStack.lastOrNull()) {

                                null -> {
                                    AnalysisFileHierarchyDisplay(
                                        fileHierarchy = graphSource,
                                        modifier = Modifier.fillMaxSize(),
                                        onFileClick = { clicked ->
                                            windowVm.openFileInfo(clicked)
                                            dragDirection = 0
                                        }
                                    )
                                }

                                else -> {
                                    val selectedNode = findInFileHierarchyByUri(
                                        node = graphSource,
                                        targetUri = currentFile.uri.toUri()
                                    )

                                    val children = selectedNode?.children.orEmpty()

                                    Column(modifier = Modifier.fillMaxSize()) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            TextButton(
                                                onClick = {
                                                    windowVm.goBack()
                                                }
                                            ) {
                                                Text("Назад")
                                            }

                                            Spacer(Modifier.width(8.dp))

                                            Text(
                                                text = windowVm.navigationStack.joinToString(" / ") { it.name },
                                                style = MaterialTheme.typography.bodySmall,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        AnalysisFileInfoDisplay(
                                            fileInfo = currentFile,
                                            children = children,
                                            onSelect = { clicked ->
                                                windowVm.openFileInfo(clicked)
                                            },
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }
                        }

                        1 -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                item {
                                    Row {
                                        Text(
                                            text = "Всего: ",
                                            fontFamily = CustomTextStyles.Standard,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "${graph.nodes.size}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                    Spacer(Modifier.height(12.dp))
                                }

                                itemsIndexed(graph.nodes) { _, node ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .shadow(
                                                elevation = 1.5.dp,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .background(
                                                color = Color.White,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    windowVm.setInspectedNode(node.id)
                                                    focusAndClose(node.id)
                                                }
                                        ) {
                                            Text(node.name, style = MaterialTheme.typography.bodyLarge)
                                            Text(
                                                text = "id: ${node.id}",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                            Text(
                                                text = "type: ${node.type}",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }

                                        IconButton(onClick = {
                                            windowVm.inspectEdges(node.id)
                                        }) {
                                            Icon(
                                                painter = painterResource(R.drawable.search_24px),
                                                contentDescription = "Закрыть",
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(8.dp))
                                }
                            }
                        }

                        2 -> {
                            val inspectedNode = windowVm.inspectedNodeId?.let { nodeId ->
                                graph.nodes.firstOrNull { it.id == nodeId }
                            }

                            val shownEdges = if (windowVm.inspectedNodeId != null) {
                                graph.edges.filter { edge ->
                                    edge.from == windowVm.inspectedNodeId ||
                                            edge.to == windowVm.inspectedNodeId
                                }
                            } else {
                                graph.edges
                            }

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                item {
                                    if (inspectedNode != null) {
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 12.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            tonalElevation = 1.dp
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text(
                                                    text = inspectedNode.name,
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                                Text(
                                                    text = "id: ${inspectedNode.id}",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                                Text(
                                                    text = "type: ${inspectedNode.type}",
                                                    style = MaterialTheme.typography.bodySmall
                                                )

                                                Spacer(Modifier.height(8.dp))

                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    ComplexButton(
                                                        action = { focusAndClose(inspectedNode.id) },
                                                        modifier = Modifier.wrapContentSize(),
                                                    ) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Icon(
                                                                painter = painterResource(id = R.drawable.frame_inspect_24px),
                                                                contentDescription = "Выбрать новый файл",
                                                                tint = MaterialTheme.colorScheme.primary
                                                            )

                                                            Spacer(modifier = Modifier.width(16.dp))

                                                            Text(
                                                                "Показать",
                                                                fontFamily = FontFamily.SansSerif
                                                            )
                                                        }
                                                    }

                                                    TextButton(
                                                        onClick = {
                                                            windowVm.clearInspectedNode()
                                                        }
                                                    ) {
                                                        Text("Очистить выбор")
                                                    }
                                                }
                                            }
                                        }

                                        Text(
                                            text = "Связи узла",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Spacer(Modifier.height(8.dp))
                                    } else {
                                        Row {
                                            Text(
                                                text = "Всего: ",
                                                fontFamily = CustomTextStyles.Standard,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = "${graph.edges.size}",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                        Spacer(Modifier.height(8.dp))
                                    }
                                }

                                items(shownEdges) { edge ->
                                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                        Text(
                                            text = "${edge.from} → ${edge.to}",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            text = "type: ${edge.type}",
                                            style = MaterialTheme.typography.bodySmall
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
}