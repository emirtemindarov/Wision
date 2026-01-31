package app.emirtemindarov.p1.components.dividers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    start: Dp = 8.dp,
    end: Dp = 8.dp,
    padding: Dp = 36.dp,
    thickness: Dp = 1.dp,
    color: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
) {
    Spacer(modifier = Modifier.width(start))

    // FIXME
    BoxWithConstraints(modifier = modifier.fillMaxHeight()) {

        Spacer(
            modifier = Modifier
                .width(thickness)
                .height(maxHeight - padding)
                .background(color)
        )
    }

    Spacer(modifier = Modifier.width(end))
}

@Composable
fun HorizontalDivider(
    modifier: Modifier = Modifier,
    top: Dp = 8.dp,
    bottom: Dp = 8.dp,
    padding: Dp = 36.dp,
    thickness: Dp = 1.dp,
    color: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
) {
    Spacer(modifier = Modifier.height(top))

    // FIXME
    BoxWithConstraints(modifier = modifier) {

        Spacer(
            modifier = Modifier
                .height(thickness)
                .width(maxWidth - padding)
                .background(color)
        )
    }

    Spacer(modifier = Modifier.height(bottom))
}

