package app.emirtemindarov.p1.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import app.emirtemindarov.p1.R
import app.emirtemindarov.p1.Screen
import app.emirtemindarov.p1.mvvm.savedprojects.SavedProjectsViewModel
import app.emirtemindarov.p1.room.GraphEntity
import app.emirtemindarov.p1.utils.FileUtils
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SavedScreen(
    viewModel: SavedProjectsViewModel,
    navController: NavHostController,
    onGraphClick: (GraphEntity) -> Unit = {},
) {

    val graphs by viewModel.graphs.collectAsState()

    // TODO если идет загрузка
    /*if (graphs.isLoading) {
        SavedFetchAnimation()
    }*/

    // если нет сохраненных
    if (graphs.isEmpty()) {
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
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(40.dp, 30.dp),
            verticalArrangement = Arrangement.spacedBy(38.dp)
        ) {
            items(graphs) { graph ->
                SavedItem(
                    graph = graph,
                    onClick = { onGraphClick(graph) },
                )
            }
        }
    }
}

@Composable
private fun SavedItem(
    graph: GraphEntity,
    onClick: () -> Unit,
) {

    // TODO возможно будет более подходящей для "gоследнее изменение"
    val date = remember(graph.createdAt) {
        SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            .format(Date(graph.createdAt))
    }

    val cornerRadius = 15.dp

    Button(
        enabled = true,
        onClick = onClick,
        contentPadding = PaddingValues(0.dp),    // TODO полезная вещь, так как Button имеет предустановленный отступ!!!
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
            .height(280.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surface)
        ) {

            // FIXME не сохраняется при выходе за экран при пролистывании
            val randomImageId = remember {
                listOf(
                    R.drawable.graph_image_test2,
                    R.drawable.graph_image_test3,
                    R.drawable.graph_image_test4,
                    R.drawable.graph_image_test5,
                ).random()
            }

            // картинка
            Column(modifier = Modifier.weight(0.715f)) {
                Image(
                    painter = painterResource(id = randomImageId),
                    contentDescription = "Рисунок графа",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop/*FillBounds*/  // TODO поэкспериментировать с вариантами
                )
            }

            // название и дата
            Column(
                modifier = Modifier
                    .weight(0.285f)
                    .padding(18.dp, 0.dp),
                verticalArrangement = Arrangement.Center,
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
