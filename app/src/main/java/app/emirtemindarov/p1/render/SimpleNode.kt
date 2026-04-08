package app.emirtemindarov.p1.render

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun SimpleNode(
    label: String,
    position: Offset
) {
    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    position.x.toInt(),
                    position.y.toInt()
                )
            }
            .background(Color.Blue, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(label, color = Color.White)
    }
}