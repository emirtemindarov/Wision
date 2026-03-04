package app.emirtemindarov.p1.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.documentfile.provider.DocumentFile
import app.emirtemindarov.p1.components.FileHierarchyDisplay
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.currentBackStackEntryAsState
import app.emirtemindarov.p1.BuildConfig
import app.emirtemindarov.p1.Environment
import app.emirtemindarov.p1.R
import app.emirtemindarov.p1.animations.FolderSelectionAnimationV1
import app.emirtemindarov.p1.components.FakeTopBarTitle
import app.emirtemindarov.p1.components.buttons.ComplexButton
import app.emirtemindarov.p1.components.buttons.OpenDialogButton
import app.emirtemindarov.p1.components.buttons.SimpleButton
import app.emirtemindarov.p1.components.buttons.ToolButton
import app.emirtemindarov.p1.components.buttons.ToolButtonWithBottomDialog
import app.emirtemindarov.p1.components.buttons.ToolButtonWithSimpleTopRightDialog
import app.emirtemindarov.p1.components.buttons.ToolButtonWithTopDialog
import app.emirtemindarov.p1.components.dividers.HorizontalDivider
import app.emirtemindarov.p1.mvvm.originalroot.OriginalRootViewModel
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderSelectionScreen(
    navController: NavHostController,
    originalRootViewModel: OriginalRootViewModel,
) {

    originalRootViewModel.debug()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    val currentRoute = destination?.route

    Log.d("FolderSelectionScreen", originalRootViewModel.getCurrentlyViewedFile()?.name.orEmpty())

    val context = LocalContext.current
    //val coroutineScope = rememberCoroutineScope()

    val uiState by originalRootViewModel.state.collectAsState()
    val currentHierarchy = uiState.originalRoot

    val folderLoading = remember { mutableStateOf(false) }

    // обработчик системной кнопки "Назад"
    BackHandler {
        Log.i("BackHandler", "from FileSelectionScreen")
        navController.popBackStack()
    }

    // обработчик выбора новой папки
    val folderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->

        folderLoading.value = true

        uri?.let {
            val documentFile = DocumentFile.fromTreeUri(context, it)

            documentFile?.let { folder ->
                Log.i("folderLauncher", "$folder")

                originalRootViewModel.loadAndSetOriginalRoot(
                    context,
                    folder,
                    currentRoute
                )
            }
        }
    }

    /*//  кнопка возврата на корневую папку - иконка
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
    }*/

    //  кнопка открытия новой папки - иконка
    val newFolderIcon: @Composable () -> Unit = {
        ToolButtonWithSimpleTopRightDialog(
            enabled = true,
            confirmText = "Выбрать новую папку",
            onConfirm = {
                folderLauncher.launch(null)
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.reset_focus_24px),
                contentDescription = "Выбрать новую папку",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

    // кнопка открытия новой папки - иконка с текстом
    val newFolderButton: @Composable () -> Unit = {
        ComplexButton(
            action = { folderLauncher.launch(null) },
            modifier = Modifier.wrapContentSize(),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.reset_focus_24px),
                    contentDescription = "Выбрать новую папку",
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    "Выбрать новую папку",
                    fontFamily = FontFamily.SansSerif
                )
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // заменяет scaffold.topbar
        FakeTopBarTitle(
            fileInfo = uiState.originalRoot?.fileInfo,
            modifier = Modifier.weight(0.125f),
            buttons = currentHierarchy?.let {
                listOf(
                    //backToOriginalRootIcon,    //R.drawable.home_24px
                    newFolderIcon,             //R.drawable.reset_focus_24px
                )
            }.orEmpty()
        )

        // "истинное" содержимое скаффолда
        val arrangement =
            if (currentHierarchy == null) Arrangement.Center
            else Arrangement.Top

        Column(
            modifier = Modifier
                .weight(0.875f)
                .fillMaxSize(),
            verticalArrangement = arrangement,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (Environment.DEBUG) {
                Text("FolderSelectionScreen")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // папка не выбрана
            currentHierarchy ?: run {
                if (folderLoading.value) {
                    Spacer(modifier = Modifier.height(180.dp))
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(180.dp))
                } else {
                    Icon(
                        painter = painterResource(R.drawable.folder_selection),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )

                    Spacer(modifier = Modifier.height(50.dp))

                    newFolderButton()

                    Spacer(modifier = Modifier.height(150.dp))
                }
            }

            // папка выбрана
            currentHierarchy?.let { currentHierarchy ->

                //newFolderButton()

                //HorizontalDivider(top = 24.dp, bottom = 24.dp, padding = 48.dp)

                Text(text = "Иерархия папки:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                // базовый вертикальный скролл
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    item {

                        Log.i("1currentHierarchy", "$currentHierarchy")
                        FileHierarchyDisplay(
                            fileHierarchy = currentHierarchy,
                            originalRootViewModel = originalRootViewModel,
                            navController = navController,
                            isRoot = true
                        )
                    }
                }
            }
        }
    }
}
