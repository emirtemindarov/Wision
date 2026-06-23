package app.emirtemindarov.p1.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

/**
 * Адаптация LazyColumn для случая "один большой item с вложенностями".
 * - Сохраняет горизонтальный скролл (передаётся извне).
 * - Измеряет высоту вложенного контента и корректно вычисляет isAtTop/isAtBottom.
 * - Накладывает TopFade / BottomFade / PassiveScrollbar поверх списка.
 *
 * Параметры:
 * - horizontalScrollState: состояние горизонтального скролла (если нужен горизонтальный скролл)
 * - modifier: модификаторы контейнера
 * - content: содержимое единственного item (обычно твой FileHierarchyDisplay)
 */
@Composable
fun LazyColumnWithItem(
    modifier: Modifier = Modifier,
    horizontalScrollState: androidx.compose.foundation.ScrollState = rememberScrollState(),
    content: @Composable () -> Unit
) {
    val listState = rememberLazyListState()

    // Измеренная высота вложенного контента (в px)
    var contentHeightPx by remember { mutableStateOf(0) }

    // Высота viewport (в px) — получаем через BoxWithConstraints эквивалент (LocalDensity + fillMaxSize)
    val density = LocalDensity.current
    var viewportHeightPx by remember { mutableIntStateOf(0) }

    // Флаги положения списка
    val isAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 &&
                    listState.firstVisibleItemScrollOffset == 0
        }
    }

    val isAtBottom by remember {
        derivedStateOf {
            // Для single-item: проскроллено = firstVisibleItemScrollOffset (поскольку index == 0)
            // Если есть несколько элементов — fallback: используем layoutInfo как раньше
            val layoutInfo = listState.layoutInfo
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()

            val usingLayoutInfo = lastVisible != null && layoutInfo.totalItemsCount > 1

            if (usingLayoutInfo) {
                // стандартная проверка для обычных списков
                lastVisible.index == layoutInfo.totalItemsCount - 1 &&
                        lastVisible.offset + lastVisible.size <= layoutInfo.viewportEndOffset
            } else {
                // single-item fallback: сравниваем измеренную высоту контента и viewport
                if (contentHeightPx == 0 || viewportHeightPx == 0) {
                    // пока не измерили — считаем, что не внизу (покажем fade)
                    false
                } else {
                    val scrolled = listState.firstVisibleItemIndex * 0 + listState.firstVisibleItemScrollOffset
                    // если проскроллено + высота viewport >= высота контента => внизу
                    (scrolled + viewportHeightPx) >= contentHeightPx
                }
            }
        }
    }

    // Контейнер: Box, внутри LazyColumn с state и горизонтальным скроллом
    Box(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { size: IntSize ->
                // сохраняем высоту viewport в px
                viewportHeightPx = size.height
            }
    ) {
        // Сам LazyColumn: один item, внутри которого мы измеряем высоту контента
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(horizontalScrollState)
                .padding(start = 32.dp, end = 0.dp)
        ) {
            item {
                // Обёртка, чтобы измерить реальную высоту вложенного контента
                Column(
                    modifier = Modifier
                        .onSizeChanged { intSize ->
                            // сохраняем высоту содержимого в px
                            contentHeightPx = intSize.height
                            Log.d("LazyColumnWithItem", "contentHeightPx = $contentHeightPx, viewportHeightPx = $viewportHeightPx")
                        }
                ) {
                    content()
                }
            }
        }

        // Наложение эффектов - те же компоненты, что и в LazyColumnFadeContainer
        TopFade(
            visible = !isAtTop,
            modifier = Modifier.align(androidx.compose.ui.Alignment.TopCenter)
        )

        BottomFade(
            visible = !isAtBottom,
            modifier = Modifier.align(androidx.compose.ui.Alignment.BottomCenter)
        )

        PassiveScrollbar(
            listState = listState,
            modifier = Modifier.align(androidx.compose.ui.Alignment.CenterEnd).padding(end = 8.dp)
        )
    }
}
