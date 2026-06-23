package app.emirtemindarov.p1

import android.app.Activity
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import app.emirtemindarov.p1.screens.AnalysisScreen
import app.emirtemindarov.p1.screens.FileDetailsScreen
import app.emirtemindarov.p1.screens.FileSelectionScreen
import app.emirtemindarov.p1.screens.FolderDetailsScreen
import app.emirtemindarov.p1.screens.FolderSelectionScreen
import app.emirtemindarov.p1.screens.SavedScreen
import android.content.pm.ActivityInfo
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.animation.fadeOut
import androidx.compose.animation.fadeIn
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding

@Composable
fun AppNavigation(
    navController: NavHostController,
    assistantViewModel: AssistantViewModel,
    savedProjectsViewModel: SavedProjectsViewModel,
    innerPadding: PaddingValues,
    icons: Map<String, Drawable?>
) {

    val originalRootViewModel: OriginalRootViewModel = viewModel()

    val singleFileViewModel: SingleFileViewModel = viewModel()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    Log.i("backStackEntry", "$backStackEntry")

    NavHost(
        navController = navController,
        startDestination = Screen.AnalysisRoot,

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
                    fadeIn(tween(0))
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
                    fadeOut(tween(0))
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

                else -> fadeIn(tween(0))
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

                else -> fadeOut(tween(0))
            }
        }
    ) {

        // вкладка "Анализ"
        navigation<Screen.AnalysisRoot>(
            startDestination = Screen.HomeScreen,
        ) {
            composable<Screen.HomeScreen> {
                // Portrait
                Box(Modifier.padding(innerPadding)) {
                    HomeScreen(
                        navController = navController,
                    )
                }
            }
            composable<Screen.FileSelectionScreen> {
                // Portrait
                Box(Modifier.padding(innerPadding)) {
                    FileSelectionScreen(
                        navController = navController,
                        singleFileViewModel = singleFileViewModel,
                    )
                }
            }
            composable<Screen.FolderSelectionScreen> {
                // Portrait
                Box(Modifier.padding(innerPadding)) {
                    FolderSelectionScreen(
                        navController = navController,
                        originalRootViewModel = originalRootViewModel,
                    )
                }
            }
            composable<Screen.FileDetailsScreen> {
                val args = it.toRoute<Screen.FileDetailsScreen>()
                // Portrait
                Box(Modifier.padding(innerPadding)) {
                    FileDetailsScreen(
                        id = args.id,
                        originalRootViewModel = originalRootViewModel,
                        navController = navController,
                    )
                }
            }
            composable<Screen.FolderDetailsScreen> {
                val args = it.toRoute<Screen.FolderDetailsScreen>()
                // Portrait
                Box(Modifier.padding(innerPadding)) {
                    FolderDetailsScreen(
                        id = args.id,
                        originalRootViewModel = originalRootViewModel,
                        navController = navController,
                    )
                }
            }
        }

        // Вкладка "Сохраненные"
        // чтобы перейти к вкладке из любого места где доступен navController
        //  нужно вызвать navController.navigate(Screen.SavedRoot)  не  navController.navigate(Screen.SavedScreen)   !!!
        navigation<Screen.SavedRoot>(
            startDestination = Screen.SavedScreen,
        ) {
            composable<Screen.SavedScreen> {
                // Unspecified
                Box(Modifier.padding(innerPadding)) {
                    SavedScreen(
                        savedProjectsViewModel = savedProjectsViewModel,
                        navController = navController,
                        onGraphClick = { graph ->
                            navController.navigate(
                                Screen.AnalysisScreen(
                                    graphId = graph.graphId,
                                    fileHierarchyInfo = null // или если есть
                                )
                            )
                        }
                    )
                }
            }
        }

        // TODO !!!!!!  отсеивать FileInfo.uri (бесполезно для анализа и ест много токенов)  !!!!!
        // можно перейти с любой вкладки
        composable<Screen.AnalysisScreen> {
            val args = it.toRoute<Screen.AnalysisScreen>()
            // Unspecified
            AnalysisScreen(
                graphId = args.graphId,
                fileInfo = args.fileHierarchyInfo,
                assistantViewModel = assistantViewModel,
                navController = navController,
                icons = icons
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
