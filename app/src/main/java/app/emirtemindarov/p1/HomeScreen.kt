package app.emirtemindarov.p1

import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import app.emirtemindarov.p1.components.buttons.BorderedButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
) {

    LockOrientationOnScreen(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {

        // TODO Toast "Нажмите еще раз чтобы выйти"    !!!!!!!!!!!

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(64.dp))

                Text(
                    text = "Выберите файл или папку",
                    fontSize = 24.sp,
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(96.dp))

                BorderedButton(
                    action = { navController.navigate(Screen.FileSelectionScreen) },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.draft_24px),
                        contentDescription = "К выбору файла",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(100.dp)
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                BorderedButton(
                    action = { navController.navigate(Screen.FolderSelectionScreen) }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.empty_folder_24px),
                        contentDescription = "К выбору папки",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(100.dp)
                    )
                }

                Spacer(modifier = Modifier.height(96.dp))

            }
        }
    }
}