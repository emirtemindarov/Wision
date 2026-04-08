package app.emirtemindarov.p1.render

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import app.emirtemindarov.p1.render.data.RenderNode

@Stable
class CameraState(
    scale: Float = 1f,
    offset: Offset = Offset.Zero
) {
    var scale by mutableFloatStateOf(scale)
    var offset by mutableStateOf(offset)
}

fun Modifier.cameraGestures(
    camera: CameraState,
    minScale: Float = 0.5f,
    maxScale: Float = 3f
): Modifier = pointerInput(Unit) {

    detectTransformGestures { centroid, pan, zoom, _ ->

        val oldScale = camera.scale
        val newScale = (oldScale * zoom).coerceIn(minScale, maxScale)

        val worldCentroid = (centroid - camera.offset) / oldScale

        camera.scale = newScale

        camera.offset =
            centroid - worldCentroid * newScale + pan
    }
}

fun centerCameraOnNode(
    camera: CameraState,
    viewport: Size
) {
    camera.offset = Offset(
        viewport.width / 2f,
        viewport.height / 2f
    )
}