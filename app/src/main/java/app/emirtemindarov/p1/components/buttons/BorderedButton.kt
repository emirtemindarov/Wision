package app.emirtemindarov.p1.components.buttons

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BorderedButton(
    action: () -> Unit,
    content: @Composable () -> Unit
) {
    Button(
        enabled = true,
        onClick = action,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .border(
                2.dp,
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(10.dp)
            )
            .size(164.dp)
    ) {
        content.invoke()
    }
}