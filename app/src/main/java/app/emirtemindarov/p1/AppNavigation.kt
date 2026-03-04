package app.emirtemindarov.p1

import android.util.Log
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import androidx.navigation.toRoute
import app.emirtemindarov.p1.assistant.AssistantViewModel
import app.emirtemindarov.p1.mvvm.originalroot.OriginalRootViewModel
import app.emirtemindarov.p1.mvvm.savedprojects.SavedProjectsViewModel
import app.emirtemindarov.p1.mvvm.singleFile.SingleFileViewModel
import app.emirtemindarov.p1.screens.FileAnalysisScreen
import app.emirtemindarov.p1.screens.FileDetailsScreen
import app.emirtemindarov.p1.screens.FileSelectionScreen
import app.emirtemindarov.p1.screens.FolderAnalysisScreen
import app.emirtemindarov.p1.screens.FolderDetailsScreen
import app.emirtemindarov.p1.screens.FolderSelectionScreen
import app.emirtemindarov.p1.screens.SavedScreen

// TODO блокировать кнопки при переходе между экранами ? (системного решения кажется нет, но возможно есть специальная библиотека)
//  (чтобы например нельзя было нажать кнопку выбора файла/папки при уходе с fileSelectionScreen)
//  (проблема - старый экран затухает, но его кнопки ненадолго продолжают быть активными)

// TODO возможно сделать передвижение влево-вправо у экранов

@Composable
fun AppNavigation(
    navController: NavHostController,
    assistantViewModel: AssistantViewModel,
    savedProjectsViewModel: SavedProjectsViewModel,
    modifier: Modifier,     // хранится отступ под AppScaffold
) {

    val originalRootViewModel: OriginalRootViewModel = viewModel()

    val singleFileViewModel: SingleFileViewModel = viewModel()

    val backStackEntry by navController.currentBackStackEntryAsState()
    Log.i("backStackEntry", "$backStackEntry")

    NavHost(
        navController = navController,
        startDestination = Screen.AnalysisRoot,
        modifier = modifier,        // применяется отступ под AppScaffold (только здесь!)

        enterTransition = {
            val from = initialState.destination.route
            val to = targetState.destination.route
            Log.i("enter from-to", "from: $from | to $to")

            when {
                isAnalysis(from) && isSaved(to) -> {
                    Log.i("enter-1", "from tab1 to tab2")
                    slideInHorizontally { it }   // справа
                }

                isSaved(from) && isAnalysis(to) -> {
                    Log.i("enter-2", "from tab2 to tab1")
                    slideInHorizontally { -it }  // слева
                }

                else -> {
                    Log.i("enter-3", "3")
                    EnterTransition.None
                }
            }
        },

        exitTransition = {
            val from = initialState.destination.route
            val to = targetState.destination.route
            Log.i("exit from-to", "from: $from | to $to")

            when {
                isAnalysis(from) && isSaved(to) -> {
                    Log.i("exit-1", "from tab1 to tab2")
                    slideOutHorizontally { -it } // влево
                }

                isSaved(from) && isAnalysis(to) -> {
                    Log.i("exit-2", "from tab2 to tab1")
                    slideOutHorizontally { it }  // вправо
                }

                else -> {
                    Log.i("exit-3", "3")
                    ExitTransition.None
                }
            }
        },

        popEnterTransition = {
            val from = initialState.destination.route
            val to = targetState.destination.route
            Log.i("popEnter from-to", "from: $from | to $to")

            when {
                isSaved(from) && isAnalysis(to) -> {
                    slideInHorizontally { -it }
                }

                isAnalysis(from) && isSaved(to) -> {
                    slideInHorizontally { it }
                }

                else -> EnterTransition.None
            }
        },

        popExitTransition = {
            val from = initialState.destination.route
            val to = targetState.destination.route
            Log.i("popExit from-to", "from: $from | to $to")

            when {
                isSaved(from) && isAnalysis(to) -> {
                    slideOutHorizontally { it }
                }

                isAnalysis(from) && isSaved(to) -> {
                    slideOutHorizontally { -it }
                }

                else -> ExitTransition.None
            }
        }
    ) {

        // вкладка "Анализ"
        navigation<Screen.AnalysisRoot>(
            startDestination = Screen.HomeScreen,
        ) {
            composable<Screen.HomeScreen> {
                HomeScreen(
                    navController = navController,
                )
            }
            composable<Screen.FileSelectionScreen> {
                FileSelectionScreen(
                    navController = navController,
                    singleFileViewModel = singleFileViewModel,
                )
            }
            composable<Screen.FolderSelectionScreen> {
                FolderSelectionScreen(
                    navController = navController,
                    originalRootViewModel = originalRootViewModel,
                )
            }
            composable<Screen.FileDetailsScreen> {
                val args = it.toRoute<Screen.FileDetailsScreen>()
                FileDetailsScreen(
                    id = args.id,
                    originalRootViewModel = originalRootViewModel,
                    navController = navController,
                )
            }
            composable<Screen.FolderDetailsScreen> {
                val args = it.toRoute<Screen.FolderDetailsScreen>()
                FolderDetailsScreen(
                    id = args.id,
                    originalRootViewModel = originalRootViewModel,
                    navController = navController,
                )
            }
            composable<Screen.FileAnalysisScreen> {
                val args = it.toRoute<Screen.FileAnalysisScreen>()
                FileAnalysisScreen(
                    graphId = args.graphId,
                    fileInfo = args.fileInfo,
                    assistantViewModel = assistantViewModel,
                    navController = navController,
                )
            }
        }

        // Вкладка "Сохраненные"
        // чтобы перейти к вкладке из любого места где доступен navController
        //  нужно вызвать navController.navigate(Screen.SavedRoot)  не  navController.navigate(Screen.SavedScreen)   !!!
        navigation<Screen.SavedRoot>(
            startDestination = Screen.SavedScreen,
        ) {
            composable<Screen.SavedScreen> {
                SavedScreen(
                    savedProjectsViewModel = savedProjectsViewModel,
                    navController = navController,
                    onGraphClick = { graph ->
                        navController.navigate(
                            Screen.FileAnalysisScreen(  // !!!! FIXME либо определять на какой экран отправлять, либо унифицировать экран анализа для папок или файлов
                                graphId = graph.graphId,
                                fileInfo = null // или если есть
                            )
                        )
                    }
                )
            }
        }

        // можно перейти с любой вкладки
        composable<Screen.FileAnalysisScreen> {
            val args = it.toRoute<Screen.FileAnalysisScreen>()
            FileAnalysisScreen(
                graphId = args.graphId,
                fileInfo = args.fileInfo,
                assistantViewModel = assistantViewModel,
                navController = navController,
            )
        }
        // можно перейти с любой вкладки
        composable<Screen.FolderAnalysisScreen> {
            val args = it.toRoute<Screen.FolderAnalysisScreen>()
            FolderAnalysisScreen(
                graphId = args.graphId,
                fileHierarchyInfo = args.fileHierarchyInfo,
                assistantViewModel = assistantViewModel,
                navController = navController,
            )
        }

    }
}

private val tab1 = setOf(
    Screen.FileSelectionScreen::class.qualifiedName!!,
    Screen.FileDetailsScreen::class.qualifiedName!!,
    Screen.FolderSelectionScreen::class.qualifiedName!!,
    Screen.FolderDetailsScreen::class.qualifiedName!!,
)

private val tab2 = setOf(
    Screen.SavedScreen::class.qualifiedName!!,
)

private fun isAnalysis(route: String?) =
    tab1.any { tabRoute ->
        route?.startsWith(tabRoute) == true
    }

private fun isSaved(route: String?) =
    tab2.any { tabRoute ->
        route?.startsWith(tabRoute) == true
    }
