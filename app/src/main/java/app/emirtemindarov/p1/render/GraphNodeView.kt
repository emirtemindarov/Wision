package app.emirtemindarov.p1.render

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.emirtemindarov.p1.assistant.data.NodeType
import app.emirtemindarov.p1.render.data.RenderNode
import app.emirtemindarov.p1.utils.colorForType

@Composable
fun GraphNodeView(
    node: RenderNode,
    camera: CameraState,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val scale = camera.scale

    val showDetails = scale > 0.7f
    val showSignature = scale > 1.2f

    Card(
        modifier = Modifier
            .width(if (expanded) 220.dp else 140.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onDoubleTap = { expanded = !expanded }
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = colorForType(node.data.type)
        ),
        elevation = CardDefaults.cardElevation(
            if (isSelected) 10.dp else 4.dp
        )
    ) {

        Column(modifier = Modifier.padding(8.dp)) {

            Text(
                node.data.label,
                color = Color.White,
                fontSize = 12.sp
            )

            if (showDetails) {
                Text(
                    node.data.type.name,
                    fontSize = 10.sp,
                    color = Color.LightGray
                )
            }

            if (showSignature) {
                node.data.properties?.signature?.return_type?.let {
                    Text("→ $it", fontSize = 9.sp, color = Color.Gray)
                }
            }

            if (expanded) {
                Spacer(Modifier.height(4.dp))

                Text(
                    node.data.description ?: "",
                    fontSize = 9.sp,
                    color = Color.LightGray
                )
            }
        }
    }
}