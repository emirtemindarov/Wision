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
import androidx.navigation.compose.currentBackStackEntryAsState
import app.emirtemindarov.p1.Environment
import app.emirtemindarov.p1.R
import app.emirtemindarov.p1.animations.FileAnalysisAnimation
import app.emirtemindarov.p1.assistant.AssistantStage
import app.emirtemindarov.p1.assistant.AssistantViewModel
import app.emirtemindarov.p1.components.buttons.GraphButton
import app.emirtemindarov.p1.components.GraphRenderV2
import app.emirtemindarov.p1.components.buttons.SimpleButton
import app.emirtemindarov.p1.mvvm.data.FileInfo
import app.emirtemindarov.p1.room.GraphLoadMode
import app.emirtemindarov.p1.utils.LogUtils.logLong
import app.emirtemindarov.p1.utils.toRenderGraph
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileAnalysisScreen(
    graphId: String?,
    fileInfo: String?,
    assistantViewModel: AssistantViewModel,
    navController: NavHostController,
) {

    // FIXME при изменение темы приложения, запрос срабатывает дополнительно !!!!!
    //  причем получается в итоге 3 запроса, судя по сохранениям
    LaunchedEffect(graphId, fileInfo) {

        val mode = when {
            graphId != null -> GraphLoadMode.FromDatabase(graphId)
            fileInfo != null -> GraphLoadMode.NewAnalysis(fileInfo)
            else -> error("Invalid navigation arguments")
        }

        //assistantViewModel.loadGraph(mode)
        assistantViewModel.responsesLoadGraph(mode)
    }

    Log.i("fileInfoFAS", "$fileInfo")

    val state by assistantViewModel.state.collectAsState()
    val stage = state.stage

    // Перехват системной кнопки "Назад"
    BackHandler {
        Log.i("BackHandlerFAS", "from FileAnalysisScreen")
        navController.popBackStack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onSurface),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (Environment.DEBUG) { Text("FileAnalysisScreen", color = Color.Green) }

        when (stage) {
            is AssistantStage.Loading -> {
                //Text("Загрузка...")
                FileAnalysisAnimation(Modifier.fillMaxSize())
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
                        .clipToBounds()       // обрезка
                        .background(Color(0xFFE3F2FD))
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
                            .align(Alignment.TopStart)    // закрепить в левом верхнем углу
                            .padding(16.dp)
                    ) {
                        SimpleButton(
                            enabled = true,
                            action = {
                                navController.popBackStack()
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.arrow_left_alt_24px),
                                contentDescription = "Назад",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        //Button(onClick = { /* действие 1 */ }) {
                        /*IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Назад",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }*/
                        //}
                        //Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Справа сверху
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)   // закрепляем в правом верхнем углу
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

            is AssistantStage.Error -> {
                Text("Ошибка: ${stage.message}")
            }
        }

    }

}