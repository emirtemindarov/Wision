package app.emirtemindarov.p1.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import app.emirtemindarov.p1.BuildConfig
import app.emirtemindarov.p1.Environment
import app.emirtemindarov.p1.R
import app.emirtemindarov.p1.animations.FileAnalysisAnimation
import app.emirtemindarov.p1.assistant.AssistantStage
import app.emirtemindarov.p1.assistant.AssistantViewModel
import app.emirtemindarov.p1.components.buttons.GraphButton
import app.emirtemindarov.p1.components.GraphRenderV2
import app.emirtemindarov.p1.components.buttons.SimpleButton
import app.emirtemindarov.p1.mvvm.data.FileHierarchy
import app.emirtemindarov.p1.room.GraphLoadMode
import app.emirtemindarov.p1.utils.LogUtils.logLong
import app.emirtemindarov.p1.utils.toRenderGraph
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderAnalysisScreen(
    graphId: String?,
    fileHierarchyInfo: String?,
    assistantViewModel: AssistantViewModel,
    navController: NavHostController,
) {
    assistantViewModel.debug()

    // FIXME при изменение темы приложения, запрос срабатывает дополнительно !!!!!
    LaunchedEffect(graphId, fileHierarchyInfo) {

        val mode = when {
            graphId != null -> GraphLoadMode.FromDatabase(graphId)
            fileHierarchyInfo != null -> GraphLoadMode.NewAnalysis(fileHierarchyInfo)
            else -> error("Invalid navigation arguments")
        }

        assistantViewModel.loadGraph(mode)
    }

    Log.i("fileInfoFoAS", "$fileHierarchyInfo")

    val state by assistantViewModel.state.collectAsState()
    val stage = state.stage

    // Перехват системной кнопки "Назад"
    BackHandler {
        assistantViewModel.reset()
        Log.i("BackHandlerFoAS", "from FolderAnalysisScreen")
        navController.popBackStack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (Environment.DEBUG) { Text("FolderAnalysisScreen", color = Color.Green) }

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

                Log.i(
                    "GRAPH",
                    "Nodes: ${graph.nodes.size}, Edges: ${graph.edges.size}"
                )

                val scale = remember { mutableFloatStateOf(1f) }
                val offset = remember { mutableStateOf(Offset.Zero) }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clipToBounds()      // обрезка
                        .background(MaterialTheme.colorScheme.surface)    // было 0xFFE3F2FD)
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale.floatValue = (scale.floatValue * zoom).coerceIn(0.3f, 4f)
                                offset.value += pan
                            }
                        }
                ) {

                    var layoutID by remember { mutableIntStateOf(1)}
                    if (layoutID > 4) { layoutID = 1 }

                    GraphRenderV2(
                        renderGraph = graph.toRenderGraph(layoutID),
                        scale = scale,
                        offset = offset,
                        mainElementName = graph.nodes.first().label,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Слева сверху
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopStart)   // закрепить в левом верхнем углу
                            .padding(16.dp)
                    ) {
                        SimpleButton(
                            enabled = true,
                            action = {
                                assistantViewModel.reset()
                                navController.popBackStack()
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.arrow_left_alt_24px),
                                contentDescription = "Назад",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    // Справа сверху
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd) // закрепляем в правом верхнем углу
                            .padding(16.dp)
                    ) {
                        GraphButton(
                            action = { scale.floatValue = 1f; offset.value = Offset.Zero }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.recenter_24px),
                                contentDescription = "Вернуть в начальную позицию",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        GraphButton(
                            action = { layoutID += 1; scale.floatValue = 1f; offset.value = Offset.Zero }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.rebase_24px),
                                contentDescription = "Перейти к следующей схеме и вернуть в начальную позицию",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        GraphButton(
                            action = { scale.floatValue = (scale.floatValue * 1.2f).coerceAtMost(4f) }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.zoom_in_24px),
                                contentDescription = "Приблизить",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        GraphButton(
                            action = { scale.floatValue = (scale.floatValue / 1.2f).coerceAtLeast(0.3f) }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.zoom_out_24px),
                                contentDescription = "Отдалить",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
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
