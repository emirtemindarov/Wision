package app.emirtemindarov.p1.animations

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun FolderSelectionAnimationV1(
    modifier: Modifier = Modifier,
    size: Dp = 400.dp
) {
    val filesCount = 7
    val density = LocalDensity.current
    val spacing = with(density) { 240.dp.toPx() } // больше отступ между файлами
    val halfWidth = with(density) { size.toPx() } / 2f

    // --- Этап 1: плавное появление при запуске ---
    var appeared by remember { mutableStateOf(false) }
    val appearAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1200, easing = LinearOutSlowInEasing),
        label = "appearAlpha"
    )
    val appearScale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.6f,
        animationSpec = tween(1000, easing = LinearEasing),
        label = "appearScale"
    )

    LaunchedEffect(Unit) {
        appeared = true
    }

    // --- Этап 2: бесконечное движение ---
    val infiniteTransition = rememberInfiniteTransition(label = "carousel")
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    Box(
        modifier = modifier
            .size(size)
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        for (i in 0 until filesCount) {
            val baseX = (i - filesCount / 2) * spacing
            val period = spacing * filesCount                     // длина «ленты» в пикселях
            val animatedX = baseX - offsetX * period

            // Зацикливание (карусель)
            val loopX = ((animatedX % period) + period) % period - period / 2

            // Нормализуем положение относительно центра
            val distFromCenter = abs(loopX)
            val norm = (1f - distFromCenter / halfWidth).coerceIn(0f, 1f)

            val scale = (0.6f + norm * 0.8f) * appearScale
            val alpha = (0.3f + norm * 0.7f) * appearAlpha

            val iconSize = 192.dp

            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = "File",
                modifier = Modifier
                    .size(iconSize)
                    .graphicsLayer {
                        translationX = loopX
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha * appearAlpha
                    },
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}