package app.emirtemindarov.p1.components.dividers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AutoDivider(
    modifier: Modifier = Modifier,
    padding: Dp = 36.dp,
    thickness: Dp = 1.dp,
    color: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
) {

    BoxWithConstraints(modifier = modifier) {

        val isVertical = maxHeight < maxWidth

        if (isVertical) {
            // vertical divider
            // FIXME
            Spacer(
                modifier = Modifier
                    .width(thickness)
                    .height(maxHeight - padding)
                    .background(color)
            )
        } else {
            // horizontal divider
            // FIXME
            Spacer(
                modifier = Modifier
                    .height(thickness)
                    .width(maxWidth - padding)
                    .background(color)
            )
        }
    }
}
