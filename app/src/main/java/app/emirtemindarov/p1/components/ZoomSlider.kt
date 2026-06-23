package app.emirtemindarov.p1.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlin.math.max
import kotlin.math.min

/**
 * Вертикальный кастомный слайдер для зума.
 *
 * - currentScale: текущее значение масштаба
 * - onScaleRequested: колбэк с новым значением масштаба (newScale)
 * - minScale / maxScale: границы масштаба
 * - size: видимый размер компонента (ширина x высота)
 *
 * Визуал:
 * - трек: тёмная полоса (чёрный/оттенки)
 * - thumb: два круга: белый (чуть больше) под чёрным (основной)
 */
@Composable
fun ZoomSliderCustom(
    currentScale: Float,
    onScaleRequested: (Float) -> Unit,
    minScale: Float = 0.5f,
    maxScale: Float = 3f,
    height: Dp = 96.dp,
    width: Dp = 12.dp,
    modifier: Modifier = Modifier
) {
    val range = maxScale - minScale

    // 0f..1f — позиция ползунка
    var t by remember { mutableFloatStateOf(((currentScale - minScale) / range).coerceIn(0f, 1f)) }

    // синхронизация при внешнем изменении
    LaunchedEffect(currentScale) {
        val newT = ((currentScale - minScale) / range).coerceIn(0f, 1f)
        if (newT != t) t = newT
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            // ЭТО расширяет только input-зону
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val h = size.height
                    val newT = 1f - (offset.y / h)
                    t = newT.coerceIn(0f, 1f)
                    onScaleRequested(minScale + t * range)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val h = size.height
                        val newT = 1f - (offset.y / h)
                        t = newT.coerceIn(0f, 1f)
                        onScaleRequested(minScale + t * range)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        val h = size.height
                        val newT = 1f - (change.position.y / h)
                        t = newT.coerceIn(0f, 1f)
                        onScaleRequested(minScale + t * range)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = modifier
                .size(width, height)
                .drawBehind {
                    val w = size.width
                    val h = size.height

                    // трек
                    val trackWidth = w * 0.24f
                    val trackX = (w - trackWidth) / 2f
                    val radius = trackWidth / 2f

                    drawRoundRect(
                        color = Color.Black,
                        topLeft = Offset(trackX, 0f),
                        size = Size(trackWidth, h),
                        cornerRadius = CornerRadius(radius, radius)
                    )

                    // позиция шарика
                    val cy = (1f - t) * h
                    val cx = w / 2f

                    val blackR = w * 0.3f
                    val whiteR = blackR * 1.5f

                    // белый шарик (подложка)
                    drawCircle(
                        color = Color.White,
                        radius = whiteR,
                        center = Offset(cx, cy)
                    )

                    // чёрный шарик
                    drawCircle(
                        color = Color.Black,
                        radius = blackR,
                        center = Offset(cx, cy)
                    )
                },
            contentAlignment = Alignment.Center
        ) {}
    }
}
