package app.emirtemindarov.p1.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GraphButton(
    action: () -> Unit,
    content: @Composable () -> Unit
) {
    Button(
        onClick = action,
        colors = ButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.onTertiary,
            disabledContentColor = MaterialTheme.colorScheme.tertiary
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onSurface
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 10.dp,
            disabledElevation = 2.dp,
            focusedElevation = 12.dp,
            hoveredElevation = 10.dp,
            pressedElevation = 2.dp
        ),
        modifier = Modifier.wrapContentSize()
    ) {
        content.invoke()
    }
}