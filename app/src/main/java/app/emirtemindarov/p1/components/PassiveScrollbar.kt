package app.emirtemindarov.p1.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun PassiveScrollbar(
    listState: LazyListState,
    modifier: Modifier = Modifier,
    hideDelayMillis: Long = 250
) {
    val layoutInfo = listState.layoutInfo    // FIXME .layoutInfo - Reading a value annotated with @FrequentlyChangingValue inside composition
    val visibleItems = layoutInfo.visibleItemsInfo
    val totalItems = layoutInfo.totalItemsCount

    if (totalItems == 0 || visibleItems.isEmpty()) return

    // ---------- размеры ----------
    val viewportHeight = layoutInfo.viewportEndOffset.toFloat()

    val estimatedItemSize = visibleItems
        .map { it.size }
        .average()
        .toFloat()
        .coerceAtLeast(1f)

    val totalContentHeight = estimatedItemSize * totalItems

    // если нечего скроллить
    if (totalContentHeight <= viewportHeight) return

    // ---------- позиция ----------
    val scrollOffsetPx =
        (listState.firstVisibleItemIndex * estimatedItemSize) +
                listState.firstVisibleItemScrollOffset

    val scrollProgress =
        (scrollOffsetPx / (totalContentHeight - viewportHeight))
            .coerceIn(0f, 1f)

    val thumbHeightFraction =
        (viewportHeight / totalContentHeight)
            .coerceIn(0.08f, 0.6f)

    // ---------- Видимость с задержкой ----------
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            visible = true
        } else {
            delay(hideDelayMillis)
            if (!listState.isScrollInProgress) {
                visible = false
            }
        }
    }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        label = "scrollbarAlpha"
    )

    // ---------- UI ----------
    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(4.dp)
            .padding(end = 2.dp)
            .graphicsLayer { this.alpha = alpha }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(thumbHeightFraction)
                .align(Alignment.TopCenter)
                .offset {
                    IntOffset(
                        x = 0,
                        y = ((viewportHeight * (1f - thumbHeightFraction)) * scrollProgress).toInt()
                    )
                }
                .background(
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                    RoundedCornerShape(2.dp)
                )
        )
    }
}

@Composable
fun TopFade(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        label = "topFade"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(28.dp)
            .graphicsLayer { this.alpha = alpha }
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
fun BottomFade(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        label = "bottomFade"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(28.dp)
            .graphicsLayer { this.alpha = alpha }
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    )
}
