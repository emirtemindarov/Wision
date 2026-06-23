package app.emirtemindarov.p1.screens

import android.content.pm.ActivityInfo
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
import app.emirtemindarov.p1.components.FileInfoDisplay
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.currentBackStackEntryAsState
import app.emirtemindarov.p1.Environment
import app.emirtemindarov.p1.LockOrientationOnScreen
import app.emirtemindarov.p1.R
import app.emirtemindarov.p1.Screen
import app.emirtemindarov.p1.animations.FileSelectionAnimationV6
import app.emirtemindarov.p1.components.AltTopBarTitle
import app.emirtemindarov.p1.components.FakeTopBarTitle
import app.emirtemindarov.p1.components.buttons.BorderedButton
import app.emirtemindarov.p1.components.buttons.ComplexButton
import app.emirtemindarov.p1.components.buttons.OpenDialogButton
import app.emirtemindarov.p1.components.buttons.SimpleButton
import app.emirtemindarov.p1.components.buttons.ToolButton
import app.emirtemindarov.p1.components.buttons.ToolButtonWithBottomDialog
import app.emirtemindarov.p1.components.buttons.ToolButtonWithSimpleTopRightDialog
import app.emirtemindarov.p1.components.buttons.ToolButtonWithTopDialog
import app.emirtemindarov.p1.components.dividers.AutoDivider
import app.emirtemindarov.p1.components.dividers.HorizontalDivider
import app.emirtemindarov.p1.components.dividers.VerticalDivider
import app.emirtemindarov.p1.mvvm.data.FileHierarchy
import app.emirtemindarov.p1.mvvm.singleFile.SingleFileViewModel

// При выборе файла не переходит на FileDetailsScreen, отображая все здесь
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileSelectionScreen(
    navController: NavHostController,
    singleFileViewModel: SingleFileViewModel,
) {

    LockOrientationOnScreen(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {

        singleFileViewModel.debug()

        Log.d("FileSelectionScreen", singleFileViewModel.getCurrentlyViewedFile()?.name.orEmpty())

        val context = LocalContext.current

        val uiState by singleFileViewModel.state.collectAsState()
        val singleFile = uiState.singleFile

        val fileLoading = remember { mutableStateOf(false) }

        // обработчик системной кнопки "Назад"
        BackHandler {
            Log.i("BackHandler", "from FileSelectionScreen")
            navController.popBackStack()
        }

        // обработчик выбора новой папки
        val fileLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->

            fileLoading.value = true

            uri?.let {
                val file = DocumentFile.fromSingleUri(context, it)

                file?.let {
                    Log.i("folderLauncher", "$file")

                    singleFileViewModel.loadAndSetSingleFile(context, file)
                }
            }
        }

        //  кнопка выбора нового файла - иконка
        val newFileIcon: @Composable () -> Unit = {
            ToolButtonWithSimpleTopRightDialog(
                enabled = true,
                confirmText = "Выбрать новый файл",
                onConfirm = {
                    fileLauncher.launch(arrayOf("*/*"))
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.reset_focus_24px),
                    contentDescription = "Выбрать новый файл",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // кнопка выбора нового файла - иконка с текстом
        val newFileButton: @Composable () -> Unit = {
            ComplexButton(
                action = { fileLauncher.launch(arrayOf("*/*")) },
                modifier = Modifier.wrapContentSize(),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.reset_focus_24px),
                        contentDescription = "Выбрать новый файл",
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        "Выбрать новый файл",
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {

            // под scaffold.topbar
            AltTopBarTitle(
                fileInfo = uiState.singleFile,
                modifier = Modifier.wrapContentHeight().fillMaxWidth(),
                buttons = singleFile?.let {
                    listOf(
                        newFileIcon            //R.drawable.reset_focus_24px
                    )
                }.orEmpty()
            )

            // "истинное" содержимое скаффолда
            val arrangement =
                if (singleFile == null) Arrangement.Center
                else Arrangement.Top

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = arrangement,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (Environment.DEBUG) {
                    Text("FileSelectionScreen")
                }

                // файл не выбран
                singleFile ?: run {
                    if (fileLoading.value) {
                        Spacer(modifier = Modifier.height(180.dp))
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(180.dp))
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.file_selection),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }

                    Spacer(modifier = Modifier.height(50.dp))

                    newFileButton()

                    Spacer(modifier = Modifier.height(150.dp))
                }


                // файл выбран
                singleFile?.let { file ->

                    /*newFileButton()

                HorizontalDivider(top = 24.dp, padding = 48.dp)*/

                    FileInfoDisplay(
                        fileInfo = file,
                        fileStructure = singleFileViewModel,
                        navController = navController,
                    )
                }
            }
        }
    }
}