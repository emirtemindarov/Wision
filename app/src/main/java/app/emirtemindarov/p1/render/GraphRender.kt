package app.emirtemindarov.p1.render

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColor
import androidx.core.graphics.toColorInt
import app.emirtemindarov.p1.assistant.data.GraphModel
import app.emirtemindarov.p1.utils.buildColumns
import app.emirtemindarov.p1.utils.layout
import app.emirtemindarov.p1.utils.toRenderGraph

@Composable
fun GraphRenderV2(
    graph: GraphModel,
    camera: CameraState
) {
    var focusId by remember(graph) {
        mutableStateOf(graph.nodes.firstOrNull()?.id)
    }

    val renderGraph = remember(graph, focusId) {
        graph.toRenderGraph().copy(
            focusNodeId = focusId ?: graph.nodes.first().id
        )
    }

    var viewportSize by remember { mutableStateOf(Size.Zero) }

    val columns = remember(renderGraph) {
        buildColumns(renderGraph)
    }

    GraphViewport(
        camera = camera,
        onSizeChanged = { viewportSize = it }
    ) {

        Column(modifier = Modifier
            .size(900.dp)
            .background(Color.Green),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(modifier = Modifier.weight(0.3f).background(Color.Cyan)) {
                // TOP
                Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    ColumnContainer("TOP", columns.top) { node ->
                        GraphNodeView(
                            node = node,
                            camera = camera,
                            isSelected = false,
                            onClick = {
                                focusId = node.id
                                centerCameraOnNode(camera, viewportSize)
                            }
                        )
                    }
                }
            }

            Column(modifier = Modifier.weight(0.4f).background(Color.LightGray)) {
                // CENTER
                Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    ColumnContainer("CENTER", listOfNotNull(columns.center)) { node ->
                        GraphNodeView(
                            node = node,
                            camera = camera,
                            isSelected = true,
                            onClick = {
                                focusId = node.id
                                centerCameraOnNode(camera, viewportSize)
                            }
                        )
                    }
                }

                // LEFT
                Box(modifier = Modifier.align(Alignment.Start)) {
                    ColumnContainer("LEFT", columns.left) { node ->
                        GraphNodeView(
                            node = node,
                            camera = camera,
                            isSelected = false,
                            onClick = {
                                focusId = node.id
                                centerCameraOnNode(camera, viewportSize)
                            }
                        )
                    }
                }

                // RIGHT
                Box(modifier = Modifier.align(Alignment.End)) {
                    ColumnContainer("RIGHT", columns.right) { node ->
                        GraphNodeView(
                            node = node,
                            camera = camera,
                            isSelected = false,
                            onClick = {
                                focusId = node.id
                                centerCameraOnNode(camera, viewportSize)
                            }
                        )
                    }
                }
            }

            // BOTTOM CONTAINER
            Column(modifier = Modifier
                .weight(0.3f)
                .fillMaxWidth()
                .background(Color.DarkGray),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // BOTTOM
                Column(modifier = Modifier
                    .background(Color(0xFF86A4B4)),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    ColumnContainer("BOTTOM", columns.bottom) { node ->
                        GraphNodeView(
                            node = node,
                            camera = camera,
                            isSelected = false,
                            onClick = {
                                focusId = node.id
                                centerCameraOnNode(camera, viewportSize)
                            }
                        )
                    }
                }
            }
        }
    }
}