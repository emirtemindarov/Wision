package app.emirtemindarov.p1.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CircleButton(
    action: () -> Unit,
    content: @Composable () -> Unit
) {
    IconButton(
        onClick = action,
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color.White)
    ) {
        content()
    }
}