package app.emirtemindarov.p1.screens

import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import app.emirtemindarov.p1.Environment
import app.emirtemindarov.p1.Environment.ALL_NODES_ROOT
import app.emirtemindarov.p1.LockOrientationOnScreen
import app.emirtemindarov.p1.R
import app.emirtemindarov.p1.animations.FileAnalysisAnimation
import app.emirtemindarov.p1.assistant.AssistantStage
import app.emirtemindarov.p1.assistant.AssistantViewModel
import app.emirtemindarov.p1.components.GraphControlsPanel
import app.emirtemindarov.p1.components.ZoomSliderCustom
import app.emirtemindarov.p1.components.buttons.CircleButton
import app.emirtemindarov.p1.components.buttons.GraphButton
import app.emirtemindarov.p1.render.CameraState
import app.emirtemindarov.p1.render.GraphRenderV3
import app.emirtemindarov.p1.render.NodeType
import app.emirtemindarov.p1.render.colorForType
import app.emirtemindarov.p1.room.GraphLoadMode
import app.emirtemindarov.p1.utils.LogUtils.logLong
import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration
import app.emirtemindarov.p1.components.FileInfoWindow
import app.emirtemindarov.p1.components.FileInfoWindowLocked

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    graphId: String?,
    fileInfo: String?,
    assistantViewModel: AssistantViewModel,
    navController: NavHostController,
    icons: Map<String, Drawable?>
) {

    LockOrientationOnScreen(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {

        val configuration = LocalConfiguration.current

        val isPortrait =
            configuration.orientation == Configuration.ORIENTATION_PORTRAIT

        /*val isLandscape =
            configuration.orientation == Configuration.ORIENTATION_LANDSCAPE*/

        assistantViewModel.debug()

        // FIXME при изменение темы приложения, запрос срабатывает дополнительно !!!!!
        LaunchedEffect(graphId, fileInfo) {

            val mode = when {
                graphId != null -> GraphLoadMode.FromDatabase(graphId)
                fileInfo != null -> GraphLoadMode.NewAnalysis(fileInfo)
                else -> error("Invalid navigation arguments")
            }

            assistantViewModel.loadGraph(mode)
        }

        Log.i("fileInfoFAS", "$fileInfo")

        val state by assistantViewModel.state.collectAsState()
        val stage = state.stage

        // Перехват системной кнопки "Назад"
        BackHandler {
            assistantViewModel.reset()
            Log.i("BackHandlerFAS", "from AnalysisScreen")
            navController.popBackStack()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (Environment.DEBUG) {
                Text("AnalysisScreen", color = Color.Green)
            }

            when (stage) {
                is AssistantStage.Idle -> {
                    //Spacer(modifier = Modifier.fillMaxSize())
                }

                is AssistantStage.Loading -> {
                    Loading(stage.mode)
                }

                is AssistantStage.Error -> {
                    Text("Ошибка: ${stage.message}")
                }

                is AssistantStage.Success -> {

                    logLong("RESPONSE_FULL", stage.graph.toString())

                    val graph = stage.graph
                    val graphSource = stage.graphSource

                    Log.i(
                        "GRAPH",
                        "Nodes: ${graph.nodes.size}, Edges: ${graph.edges.size}"
                    )

                    val camera = remember { CameraState() }

                    var viewportSize by remember { mutableStateOf(Size.Zero) }

                    var focusHistory by remember { mutableStateOf(listOf(ALL_NODES_ROOT)) }
                    var historyIndex by remember { mutableIntStateOf(0) }

                    val focusNodeId = focusHistory[historyIndex]

                    fun goToNode(id: String) {
                        if (focusNodeId == id) return

                        val newHistory = focusHistory.take(historyIndex + 1) + id
                        focusHistory = newHistory
                        historyIndex = newHistory.lastIndex
                    }

                    fun undo() {
                        if (historyIndex > 0) {
                            historyIndex--
                        }
                    }

                    fun redo() {
                        if (historyIndex < focusHistory.lastIndex) {
                            historyIndex++
                        }
                    }

                    fun zoom(camera: CameraState, factor: Float, viewport: Size) {
                        val center = Offset(viewport.width / 2, viewport.height / 2)

                        val oldScale = camera.scale
                        val newScale = (oldScale * factor).coerceIn(0.5f, 3f)

                        val worldCenter = (center - camera.offset) / oldScale

                        camera.scale = newScale
                        camera.offset = center - worldCenter * newScale
                    }

                    var fileInfoWindowNodeId by remember { mutableStateOf<String?>(null) }
                    var showFileInfoWindow by remember { mutableStateOf(false) }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            //.clipToBounds()       // обрезка
                            .background(MaterialTheme.colorScheme.surface)    // было 0xFFE3F2FD
                    ) {

                        GraphRenderV3(
                            graph = graph,
                            graphSource = graphSource,
                            camera = camera,
                            icons,
                            focusNodeId = focusNodeId,
                            onNodeFocusCallback = { goToNode(it) },
                            onViewportChanged = { viewportSize = it },
                            onDetailsClick = { nodeId ->
                                fileInfoWindowNodeId = nodeId
                                showFileInfoWindow = true
                            }
                        )

                        if (showFileInfoWindow) {
                            FileInfoWindowLocked(
                                graph = graph,
                                graphSource = graphSource,
                                isPortrait = isPortrait,
                                initialInspectedNodeId = fileInfoWindowNodeId,
                                onDismiss = {
                                    showFileInfoWindow = false
                                    fileInfoWindowNodeId = null
                                },
                                onFocusNodeSelected = { nodeId ->
                                    goToNode(nodeId)
                                }
                            )
                        }

                        // Слева сверху
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopStart)    // закрепить в левом верхнем углу
                                .padding(
                                    vertical = if (isPortrait) 40.dp else 32.dp,
                                    horizontal = if (isPortrait) 16.dp else 56.dp
                                )
                                .shadow(
                                    elevation = 1.5.dp,
                                    shape = CircleShape
                                )
                        ) {
                            CircleButton(
                                action = {
                                    assistantViewModel.reset()
                                    navController.popBackStack()
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.close_24px),
                                    contentDescription = "Закрыть",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // другая стрелка с отзеркаливанием
                            /*Button(onClick = { *//* действие 1 *//* }) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Назад",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))*/
                        }

                        // Справа сверху
                        if (isPortrait) {

                            Column(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(
                                        vertical = 40.dp,
                                        horizontal = 16.dp
                                    )
                            ) {

                                GraphControlsPanel {

                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) {
                                                showFileInfoWindow = true
                                                fileInfoWindowNodeId = null
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.code_xml_24px),
                                            contentDescription = "Источник графа",
                                            tint = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                GraphControlsPanel {

                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) { camera.scale = 1f; camera.offset = Offset.Zero },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.recenter_24px),
                                            contentDescription = "Вернуть в центр",
                                            tint = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                GraphControlsPanel {

                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) { zoom(camera, 1.2f, viewportSize) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.add_24px),
                                            contentDescription = "Приблизить",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Box(
                                        modifier = Modifier.width(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        ZoomSliderCustom(
                                            currentScale = camera.scale,
                                            onScaleRequested = { newScale ->
                                                val factor = newScale / camera.scale
                                                zoom(camera, factor, viewportSize)
                                            }
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) { zoom(camera, 1f / 1.2f, viewportSize) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.remove_24px),
                                            contentDescription = "Отдалить",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                GraphControlsPanel {

                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) { undo() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.undo_24px),
                                            contentDescription = "Назад",
                                            tint = MaterialTheme.colorScheme.onSurface.copy(
                                                alpha = if (historyIndex > 0) 1f else 0.3f
                                            ),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) { redo() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.redo_24px),
                                            contentDescription = "Вперед",
                                            tint = MaterialTheme.colorScheme.onSurface.copy(
                                                alpha = if (historyIndex < focusHistory.lastIndex) 1f else 0.3f
                                            ),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                        } else {

                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(
                                        vertical = 32.dp,
                                        horizontal = 56.dp
                                    )
                            ) {

                                // Левая колонка (1 кнопка)
                                Column {

                                    GraphControlsPanel {

                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null
                                                ) { showFileInfoWindow = true },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.code_xml_24px),
                                                contentDescription = "Источник графа",
                                                tint = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // Правая колонка (остальные кнопки)
                                Column {

                                    GraphControlsPanel {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null
                                                ) { camera.scale = 1f; camera.offset = Offset.Zero },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.recenter_24px),
                                                contentDescription = "Вернуть в центр",
                                                tint = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    GraphControlsPanel {

                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null
                                                ) { zoom(camera, 1.2f, viewportSize) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.add_24px),
                                                contentDescription = "Приблизить",
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        Box(
                                            modifier = Modifier.width(32.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            ZoomSliderCustom(
                                                currentScale = camera.scale,
                                                onScaleRequested = { newScale ->
                                                    val factor = newScale / camera.scale
                                                    zoom(camera, factor, viewportSize)
                                                }
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null
                                                ) { zoom(camera, 1f / 1.2f, viewportSize) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.remove_24px),
                                                contentDescription = "Отдалить",
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    GraphControlsPanel {

                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null
                                                ) { undo() },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.undo_24px),
                                                contentDescription = "Назад",
                                                tint = MaterialTheme.colorScheme.onSurface.copy(
                                                    alpha = if (historyIndex > 0) 1f else 0.3f
                                                ),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null
                                                ) { redo() },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.redo_24px),
                                                contentDescription = "Вперед",
                                                tint = MaterialTheme.colorScheme.onSurface.copy(
                                                    alpha = if (historyIndex < focusHistory.lastIndex) 1f else 0.3f
                                                ),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Слева снизу
                        var legendExpanded by remember { mutableStateOf(true) }

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(
                                    vertical = if (isPortrait) 56.dp else 16.dp,
                                    horizontal = if (isPortrait) 16.dp else 58.dp
                                )
                                .shadow(
                                    elevation = 1.5.dp,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .background(Color.White)
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {

                            // Кнопка сворачивания / разворачивания
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .size(88.dp,30.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { legendExpanded = !legendExpanded },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(
                                        id = if (legendExpanded)
                                            R.drawable.keyboard_arrow_down_24px
                                        else
                                            R.drawable.keyboard_arrow_up_24px
                                    ),
                                    contentDescription = "Свернуть/развернуть легенду",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            if (legendExpanded) {

                                @Composable
                                fun LegendItem(color: Color, label: String) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .background(color, shape = CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                LegendItem(colorForType(NodeType.FILE), "Файл")
                                LegendItem(colorForType(NodeType.FOLDER), "Папка")
                                LegendItem(colorForType(NodeType.CLASS), "Класс")
                                LegendItem(colorForType(NodeType.INTERFACE), "Интерфейс")
                                LegendItem(colorForType(NodeType.FUNCTION), "Функция")
                                LegendItem(colorForType(NodeType.VARIABLE), "Переменная")
                                LegendItem(colorForType(NodeType.BLOCK), "Блок")
                                LegendItem(colorForType(NodeType.OBJECT), "Объект")
                            }
                        }
                    }
                }

            }

        }

    }

}

// при AssistantStage.Loading
@Composable
private fun Loading(mode: GraphLoadMode) {
    when (mode) {
        is GraphLoadMode.FromDatabase -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),    // было 0xFFE3F2FD
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }

        is GraphLoadMode.NewAnalysis -> {
            FileAnalysisAnimation()
        }
    }
}
