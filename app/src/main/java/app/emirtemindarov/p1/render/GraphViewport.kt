package app.emirtemindarov.p1.render

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

@Composable
fun GraphViewport(
    camera: CameraState,
    onSizeChanged: (Size) -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .size(1200.dp)
            .background(Color(0xFF0B0F14))
    ) {
        Column(
            modifier = Modifier
                .size(1100.dp)
                .clipToBounds()
                .cameraGestures(camera)
                .background(Color.Gray) // бесконечная область
        ) {

            var initialized by remember { mutableStateOf(false) }

            Column(modifier = Modifier
                .size(1000.dp)
                .onSizeChanged {
                    val size = Size(it.width.toFloat(), it.height.toFloat())
                    Log.i(
                        "size_graphViewport",
                        "$size | ${it.width} | ${it.height} | ${size * 2f}"
                    )
                    onSizeChanged(size)

                    if (!initialized) {
                        camera.offset = Offset(
                            size.width / 2f,   // ?
                            size.height / 2f   // ?
                        )
                        initialized = true
                    }
                }
                .graphicsLayer {   // !!!!! оставить как есть а скейлить все внутренности
                    translationX = camera.offset.x
                    translationY = camera.offset.y
                    scaleX = camera.scale
                    scaleY = camera.scale
                    transformOrigin = TransformOrigin(0f, 0f)
                },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
            }
        }
    }
}