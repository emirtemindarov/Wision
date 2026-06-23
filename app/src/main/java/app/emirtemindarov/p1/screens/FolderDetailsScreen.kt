package app.emirtemindarov.p1.screens

import android.content.pm.ActivityInfo
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import app.emirtemindarov.p1.BuildConfig
import app.emirtemindarov.p1.Environment
import app.emirtemindarov.p1.LockOrientationOnScreen
import app.emirtemindarov.p1.R
import app.emirtemindarov.p1.Screen
import app.emirtemindarov.p1.components.AltTopBarTitle
import app.emirtemindarov.p1.components.FakeTopBarTitle
import app.emirtemindarov.p1.components.FileInfoDisplay
import app.emirtemindarov.p1.components.buttons.ToolButton
import app.emirtemindarov.p1.mvvm.data.FileInfo
import app.emirtemindarov.p1.mvvm.originalroot.OriginalRootViewModel
import app.emirtemindarov.p1.utils.FileUtils

// Предшествует FolderAnalysisScreen / Вызывается только при удержании на папке из иерархии, после выбора папки
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderDetailsScreen(
    id: String,
    originalRootViewModel: OriginalRootViewModel,
    navController: NavHostController,
) {

    LockOrientationOnScreen(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {

        Log.d("FolderDetailsScreen", originalRootViewModel.getCurrentlyViewedFile()?.name.orEmpty())

        val uiState by originalRootViewModel.state.collectAsState()

        // Перехват системной кнопки "Назад"
        BackHandler {
            Log.i("back handler", "from Folder Details Screen")
            navController.popBackStack()
        }

        //  кнопка возврата на корневую папку - иконка
        val backToOriginalRootIcon: @Composable () -> Unit = {
            ToolButton(
                action = {
                    originalRootViewModel.getOriginalRootBackStackEntryId()?.let { route ->
                        navController.popBackStack(
                            route,
                            inclusive = false
                        )
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.home_24px),
                    contentDescription = "Выбрать новый файл",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {

            // scaffold.topAppBar
            AltTopBarTitle(
                fileInfo = uiState.currentlyViewedFile,
                modifier = Modifier.wrapContentHeight().fillMaxWidth(),
                buttons = originalRootViewModel.getOriginalRoot()?.let {
                    listOf(
                        backToOriginalRootIcon
                    )
                }.orEmpty()
            )

            // "истинное" содержимое скаффолда
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (Environment.DEBUG) {
                    Text("FolderDetailsScreen")
                }

                originalRootViewModel.getOriginalRoot()?.let { originalRoot ->

                    val selectedFolder = FileUtils.findInFileHierarchyByUri(
                        node = originalRoot,
                        targetUri = id.toUri()
                    )

                    Log.i("selectedFolder", "$selectedFolder")

                    selectedFolder?.let { folder ->

                        originalRootViewModel.setCurrentlyViewedFile(folder.fileInfo)

                        FileInfoDisplay(
                            fileInfo = folder.fileInfo,
                            children = folder.children,
                            fileStructure = originalRootViewModel,
                            navController = navController,
                        )

                    } ?: run {
                        Log.w("no fileInfo", "id = $id | originalRoot = $originalRoot")
                    }

                } ?: run {
                    Log.w("no fileInfo", "id = $id")
                }
            }
        }
    }
}