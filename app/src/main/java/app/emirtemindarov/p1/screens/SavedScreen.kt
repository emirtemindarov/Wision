package app.emirtemindarov.p1.screens

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import app.emirtemindarov.p1.LockOrientationOnScreen
import app.emirtemindarov.p1.R
import app.emirtemindarov.p1.Screen
import app.emirtemindarov.p1.mvvm.savedprojects.SavedProjectsStage
import app.emirtemindarov.p1.mvvm.savedprojects.SavedProjectsViewModel
import app.emirtemindarov.p1.room.GraphEntity
import app.emirtemindarov.p1.utils.FileUtils
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SavedScreen(
    savedProjectsViewModel: SavedProjectsViewModel,
    navController: NavHostController,
    onGraphClick: (GraphEntity) -> Unit = {},
) {

    LockOrientationOnScreen(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {

        val configuration = LocalConfiguration.current

        val isPortrait =
            configuration.orientation == Configuration.ORIENTATION_PORTRAIT

        /*val isLandscape =
            configuration.orientation == Configuration.ORIENTATION_LANDSCAPE*/

        savedProjectsViewModel.debug()

        val state by savedProjectsViewModel.state.collectAsState()
        //val graphs by savedProjectsViewModel.graphs.collectAsState()

        when (val stage = state.stage) {

            // список загружается
            SavedProjectsStage.Loading -> {
                SavedSkeletonList()
            }

            // список загружен
            is SavedProjectsStage.Success -> {
                if (isPortrait) {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(40.dp, 30.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(stage.graphs) { graph ->
                            SavedItem(
                                graph = graph,
                                onClick = { onGraphClick(graph) },
                            )
                        }
                    }

                } else {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(40.dp, 30.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(stage.graphs.chunked(2)) { rowItems ->

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {

                                rowItems.forEach { graph ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        SavedItem(
                                            graph = graph,
                                            onClick = { onGraphClick(graph) },
                                        )
                                    }
                                }

                                // если нечетное количество элементов — заполняем пустотой
                                if (rowItems.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            // если нет сохраненных (список пуст)
            SavedProjectsStage.Empty -> {
                EmptyPlaceholder()
            }

            // ошибка в бд
            is SavedProjectsStage.Error -> {
                Text(stage.message)    // TODO оформить показ ошибки
            }
        }
    }
}

// при SavedProjectsStage.Empty
@Composable
fun EmptyPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.saved_placeholder),
            contentDescription = null,
            tint = Color.Unspecified
        )

        Spacer(modifier = Modifier.height(80.dp))

        Text(
            text = "Проанализированные проекты\nбудут отображены здесь",
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Light,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,   // TODO полезная вещь!!!
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(80.dp))

    }
}

// элемент списка при SavedProjectsStage.Success
@Composable
private fun SavedItem(
    graph: GraphEntity,
    onClick: () -> Unit,
) {

    // TODO возможно будет более подходящей для "последнее изменение"
    val date = remember(graph.createdAt) {
        SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            .format(Date(graph.createdAt))
    }

    val cornerRadius = 15.dp

    Button(
        enabled = true,
        onClick = onClick,
        contentPadding = PaddingValues(0.dp),    // убирает предустановленный отступ Button
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(cornerRadius),
        modifier = Modifier
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(cornerRadius)
            )
            .fillMaxWidth()
            .height(86.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surface)
        ) {

            // картинка
            Column(
                modifier = Modifier.weight(0.285f).fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.schema_24px_weight_200),
                    contentDescription = "Схема",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(72.dp)
                )
            }

            VerticalDivider()

            // название и дата
            Column(
                modifier = Modifier
                    .weight(0.715f)
                    .fillMaxSize()
                    .padding(start = 18.dp, top = 12.dp, end = 18.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {

                // FIXME не сохраняется при выходе за экран при пролистывании
                val testGraphName = remember {
                    listOf(
                        "Папка с файлом",
                        "Laboratornaya_rabota1",
                        "Очень длинное название файла, превышающее ширину своего блока"
                    ).random()
                }

                // FIXME не сохраняется при выходе за экран при пролистывании
                val testLastModified = remember {
                    listOf(
                        "1 минуту назад",
                        "05.01.2026 17:58"
                    ).random()
                }

                // название
                Text(
                    text = graph.graphName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = -(0.1).sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,   // TODO полезная вещь!!!
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // последнее изменение
                Text(
                    text = FileUtils.formatDate(graph.lastModified),   // $date возможно будет более подходящей
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.7f),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = -(0.1).sp,
                    overflow = TextOverflow.Ellipsis,   // TODO полезная вещь!!!
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// при SavedProjectsStage.Loading    TODO обновить внешний вид
@Composable
fun SavedSkeletonList() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(40.dp, 30.dp),
        verticalArrangement = Arrangement.spacedBy(38.dp)
    ) {
        items(4) {
            SavedSkeletonItem()
        }
    }
}

// часть списка при SavedProjectsStage.Loading    TODO обновить внешний вид
@Composable
fun SavedSkeletonItem() {

    val cornerRadius = 15.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .border(
                1.dp,
                Color.LightGray,
                RoundedCornerShape(cornerRadius)
            )
            .clip(RoundedCornerShape(cornerRadius))
    ) {

        // картинка-заглушка
        Box(
            modifier = Modifier
                .weight(0.715f)
                .fillMaxWidth()
                .shimmer()
        )

        Column(
            modifier = Modifier
                .weight(0.285f)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Box(
                modifier = Modifier
                    .height(18.dp)
                    .fillMaxWidth(0.7f)
                    .shimmer()
            )

            Box(
                modifier = Modifier
                    .height(12.dp)
                    .fillMaxWidth(0.4f)
                    .shimmer()
            )
        }
    }
}

fun Modifier.shimmer(): Modifier = composed {

    val transition = rememberInfiniteTransition(label = "shimmer")

    // позиции перемещения слева направо
    val initialValue = -1200f
    val targetValue = 1200f

    val translate by transition.animateFloat(
        initialValue = initialValue,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 4000

                // движение
                initialValue at 0
                targetValue at 2400

                // пауза (значение не меняется)
                1600f at 3000
            },
            repeatMode = RepeatMode.Restart,
            /*initialStartOffset = StartOffset(
                offsetMillis = 1000,
                offsetType = StartOffsetType.Delay
            )*/
        ),
        label = "shimmer_translate"
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.LightGray.copy(alpha = 0.6f),
                Color.LightGray.copy(alpha = 0.3f),
                Color.LightGray.copy(alpha = 0.6f),
            ),
            start = Offset(translate, 0f),
            end = Offset(translate + 1200f, 0f)
        )
    )
}
