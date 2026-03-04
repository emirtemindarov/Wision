package app.emirtemindarov.p1.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import app.emirtemindarov.p1.R
import app.emirtemindarov.p1.mvvm.data.FileHierarchy
import app.emirtemindarov.p1.mvvm.data.FileInfo
import app.emirtemindarov.p1.ui.theme.CustomTextStyles

@Composable
fun FakeTopBarTitle(
    fileInfo: FileInfo?,
    //navController: NavHostController,
    modifier: Modifier = Modifier,
    buttons: List<@Composable () -> Unit> = emptyList()
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimary),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(bottom = 8.dp)
        ) {

            // заголовок
            fileInfo?.let { file ->

                var showFileInfo by remember { mutableStateOf(false) }
                if (showFileInfo) FileInfoWindow(file) { showFileInfo = false }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(10.dp))
                        .widthIn(min = 200.dp)
                        .clickable(    // или .indication(), если не нужно действие
                            // изменения цвета затемнения при нажатии кнопки
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(
                                bounded = true,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            )
                        ) {
                            Log.i("title pressed", "title pressed")
                            showFileInfo = true
                        }
                        .padding(12.dp)
                ) {
                    Text(
                        text = file.name,
                        fontFamily = CustomTextStyles.Standard,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 248.dp)
                    )
                }
            }


            // кнопки справа
            Row(
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                buttons.forEach { button ->
                    button()
                }
            }
        }
    }
}
