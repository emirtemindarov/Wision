package app.emirtemindarov.p1.components.buttons

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.unit.dp

@Composable
fun SimpleButton(
    enabled: Boolean,
    action: () -> Unit,
    content: @Composable () -> Unit
) {
    Button(
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        onClick = action
    ) {
        content.invoke()
    }
}