package app.emirtemindarov.p1.render

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.emirtemindarov.p1.render.data.RenderNode

@Composable
fun ColumnContainer(
    title: String,
    nodes: List<RenderNode>,
    content: @Composable (RenderNode) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .background(Color(0x22FFFFFF))
            .border(1.dp, Color.White)
            .padding(8.dp)
    ) {
        Column {

            Text(
                text = "$title (${nodes.size})",
                color = Color.White,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(8.dp))

            nodes.forEach {
                content(it)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}