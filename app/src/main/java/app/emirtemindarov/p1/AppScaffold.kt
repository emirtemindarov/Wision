package app.emirtemindarov.p1

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.runtime.saveable.rememberSaveable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    navController: NavHostController,
    content: @Composable (innerPadding: PaddingValues) -> Unit
) {

    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    val currentRoute = backStackEntry?.destination?.route

    var scaffoldRoute by rememberSaveable { mutableStateOf(currentRoute) }

    var fabRoute by rememberSaveable { mutableStateOf(currentRoute) }

    val isCurrentAnalysis =
        currentRoute?.startsWith(Screen.AnalysisScreen::class.qualifiedName!!) == true

    val isScaffoldAnalysis =
        scaffoldRoute?.startsWith(Screen.AnalysisScreen::class.qualifiedName!!) == true

    val regularScaffoldScreens = setOf(
        Screen.FileSelectionScreen::class.qualifiedName!!,
        Screen.FileDetailsScreen::class.qualifiedName!!,
        Screen.FolderSelectionScreen::class.qualifiedName!!,
        Screen.FolderDetailsScreen::class.qualifiedName!!,
    )

    val analysisScaffoldScreens = setOf(
        Screen.AnalysisScreen::class.qualifiedName!!,
    )

    DisposableEffect(backStackEntry) {
        val entry = backStackEntry ?: return@DisposableEffect onDispose {}

        val observer = LifecycleEventObserver { _, event ->
            val targetRoute = entry.destination.route

            val isTargetHome =
                targetRoute == Screen.HomeScreen::class.qualifiedName!!

            val isFabHome =
                fabRoute == Screen.HomeScreen::class.qualifiedName!!

            val isTargetRegular =
                regularScaffoldScreens.any { targetRoute?.startsWith(it) == true }

            val isTargetAnalysis =
                targetRoute?.startsWith(Screen.AnalysisScreen::class.qualifiedName!!) == true

            val isTargetSaved =
                targetRoute?.startsWith(Screen.SavedScreen::class.qualifiedName!!) == true

            val isScaffoldRegular =
                regularScaffoldScreens.any { scaffoldRoute?.startsWith(it) == true }

            val isScaffoldSaved =
                scaffoldRoute?.startsWith(Screen.SavedScreen::class.qualifiedName!!) == true

            // раннее обновление
            if (
            // выход из analysis
                (isScaffoldAnalysis && !isTargetAnalysis) ||

                // regular → saved
                (isScaffoldRegular && isTargetSaved) ||

                // saved → regular
                (isScaffoldSaved && isTargetRegular)
            ) {
                scaffoldRoute = targetRoute
            }
            else if (event == Lifecycle.Event.ON_RESUME) {
                scaffoldRoute = targetRoute
            }

            // FAB логика
            if (isFabHome && !isTargetHome) {
                // уходим с Home → скрыть сразу
                fabRoute = targetRoute
            }
            else if (event == Lifecycle.Event.ON_RESUME && isTargetHome) {
                // приходим на Home → показать только после RESUME
                fabRoute = targetRoute
            }
        }

        entry.lifecycle.addObserver(observer)

        onDispose {
            entry.lifecycle.removeObserver(observer)
        }
    }

    val isSavedTabSelected =
        destination?.hierarchy?.any {
            it.route == Screen.SavedRoot::class.qualifiedName
        } == true

    val isAnalysisTabSelected =
        destination?.hierarchy?.any {
            it.route == Screen.AnalysisRoot::class.qualifiedName
        } == true

    val homeScreenTopAppBar: @Composable () -> Unit = {
        TopAppBar(
            // без отступа сверху под системную панель, только на HomeScreen
            //windowInsets = WindowInsets(top = 0),

            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                //titleContentColor = MaterialTheme.colorScheme.onSurface,
            ),
            title = {},
            navigationIcon = {
                IconButton(onClick = {}/*TODO onMenuClick*/) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Меню",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            actions = {
                IconButton(onClick = {}/*TODO onHelpClick*/) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = "Помощь",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )
    }

    val homeScreenFloatingActionButton: @Composable () -> Unit = {
        FloatingActionButton(
            modifier = Modifier
                .offset(
                    x = (-12).dp,
                    y = (64).dp
                )
                .wrapContentSize(),
            onClick = {
                navController.navigateTab(Screen.SavedRoot)
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.folder_check_2_24px),
                contentDescription = "К сохраненным",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    val homeScreenBottomBar: @Composable () -> Unit = {}    // отсутствует НЕ ИСПОЛЬЗОВАТЬ ПУСТЫМ

    val regularTopAppBar: @Composable () -> Unit = {

        CenterAlignedTopAppBar(
            modifier = Modifier/*.width(64.dp)*/,
            colors = TopAppBarDefaults.topAppBarColors(
                //containerColor = Color.Transparent,
                containerColor = MaterialTheme.colorScheme.onPrimary,
                titleContentColor = MaterialTheme.colorScheme.primary,
                //actionIconContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            title = {},
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.keyboard_arrow_left_24px),
                        contentDescription = "Назад",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        )
    }

    val regularFloatingActionButton: @Composable () -> Unit = {}    // отсутствует НЕ ИСПОЛЬЗОВАТЬ ПУСТЫМ

    val regularBottomBar: @Composable () -> Unit = {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.onPrimary,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            NavigationBarItem(
                selected = isAnalysisTabSelected,
                onClick = {
                    navController.navigateTab(Screen.AnalysisRoot)
                },
                icon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Анализ проекта"
                    )
                },
                colors = NavigationBarItemColors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    selectedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.secondary,
                    unselectedTextColor = MaterialTheme.colorScheme.secondary,
                    disabledIconColor = MaterialTheme.colorScheme.primary,
                    disabledTextColor = MaterialTheme.colorScheme.primary
                ),
                label = { Text("Анализ") }
            )
            NavigationBarItem(
                selected = isSavedTabSelected,
                onClick = {
                    navController.navigateTab(Screen.SavedRoot)
                },
                icon = {
                    Icon(
                        Icons.AutoMirrored.Filled.List,
                        contentDescription = "Сохраненные проекты"
                    )
                },
                colors = NavigationBarItemColors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    selectedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.secondary,
                    unselectedTextColor = MaterialTheme.colorScheme.secondary,
                    disabledIconColor = MaterialTheme.colorScheme.primary,
                    disabledTextColor = MaterialTheme.colorScheme.primary
                ),
                label = { Text("Сохраненные") }
            )
        }
    }

    val savedProjectsTopAppBar: @Composable () -> Unit = {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.onPrimary,
                titleContentColor = MaterialTheme.colorScheme.primary,
                //actionIconContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            title = {
                Text(
                    text = "Сохраненные",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            actions = {
                // TODO реализовать поисковую систему (окно поиска будет отдельным экраном с динамическими подсказками?)
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Меню",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // TODO поэкспериментировать с альтернативными представлениями списка проанализированных проектов
                // TODO проверять дату и поправлять на недавнее (1 минуту назад, 2 минуты назад) вместо formatDate
                // TODO сделвть временные разделы по дням
                Icon(
                    painter = painterResource(id = R.drawable.view_comfy_alt_24px),
                    contentDescription = "Меню",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

            }
        )
    }

    val savedProjectsFloatingActionButton: @Composable () -> Unit = {}    // отсутствует НЕ ИСПОЛЬЗОВАТЬ ПУСТЫМ

    val savedProjectsBottomBar: @Composable () -> Unit = {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.onPrimary,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            NavigationBarItem(
                selected = isAnalysisTabSelected,
                onClick = {
                    navController.navigateTab(Screen.AnalysisRoot)
                },
                icon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Анализ проекта"
                    )
                },
                colors = NavigationBarItemColors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    selectedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.secondary,
                    unselectedTextColor = MaterialTheme.colorScheme.secondary,
                    disabledIconColor = MaterialTheme.colorScheme.primary,
                    disabledTextColor = MaterialTheme.colorScheme.primary
                ),
                label = { Text("Анализ") }
            )
            NavigationBarItem(
                selected = isSavedTabSelected,
                onClick = {
                    navController.navigateTab(Screen.SavedRoot)
                },
                icon = {
                    Icon(
                        Icons.AutoMirrored.Filled.List,
                        contentDescription = "Сохраненные проекты"
                    )
                },
                colors = NavigationBarItemColors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    selectedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.secondary,
                    unselectedTextColor = MaterialTheme.colorScheme.secondary,
                    disabledIconColor = MaterialTheme.colorScheme.primary,
                    disabledTextColor = MaterialTheme.colorScheme.primary
                ),
                label = { Text("Сохраненные") }
            )
        }
    }

    Log.i("selectedTabCheckSuccess", (currentRoute?.startsWith(Screen.SavedRoot::class.qualifiedName!!) == true).toString())

    val analysisTopAppBar: @Composable () -> Unit = {}    // отсутствует НЕ ИСПОЛЬЗОВАТЬ ПУСТЫМ

    val analysisFloatingActionButton: @Composable () -> Unit = {}    // отсутствует НЕ ИСПОЛЬЗОВАТЬ ПУСТЫМ

    val analysisBottomBar: @Composable () -> Unit = {}    // отсутствует НЕ ИСПОЛЬЗОВАТЬ ПУСТЫМ


    var topAppBarPlaceholder: @Composable (() -> Unit)         // НЕ ИСПОЛЬЗОВАТЬ
    var floatingActionButtonPlaceholder: @Composable (() -> Unit)      // НЕ ИСПОЛЬЗОВАТЬ
    var bottomBarPlaceholder: @Composable (() -> Unit)        // НЕ ИСПОЛЬЗОВАТЬ

    Log.i("currentRoute", currentRoute.toString())










    Scaffold(
        topBar = {
            when {
                scaffoldRoute == Screen.HomeScreen::class.qualifiedName!! -> {
                    homeScreenTopAppBar()
                }

                regularScaffoldScreens.any { baseRoute ->
                    scaffoldRoute?.startsWith(baseRoute) == true
                } -> {
                    regularTopAppBar()
                }

                /*currentRoute == Screen.AnalysisScreen::class.qualifiedName!! -> {
                    TopAppBar(
                        title = {},
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            navigationIconContentColor = Color.Transparent,
                            titleContentColor = Color.Transparent,
                            actionIconContentColor = Color.Transparent
                        )
                    )
                }*/

                scaffoldRoute?.startsWith(
                    Screen.SavedScreen::class.qualifiedName!!
                ) == true -> {
                    savedProjectsTopAppBar()
                }

                /*else -> {
                    // для непредусмотренных ситуаций
                    TopAppBar(
                        title = {},
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            navigationIconContentColor = Color.Transparent,
                            titleContentColor = Color.Transparent,
                            actionIconContentColor = Color.Transparent
                        )
                    )
                }*/
            }
        },

        floatingActionButton = {
            when {
                fabRoute == Screen.HomeScreen::class.qualifiedName!! -> {
                    homeScreenFloatingActionButton()
                }
            }
        },

        bottomBar = {
            when {
                scaffoldRoute?.startsWith(Screen.HomeScreen::class.qualifiedName!!) == true -> {
                    // отсутствует
                    NavigationBar(
                        containerColor = Color.Transparent,
                        contentColor = Color.Transparent
                    ) { }
                }

               /* currentRoute?.startsWith(Screen.AnalysisScreen::class.qualifiedName!!) == true -> {
                    // отсутствует
                    NavigationBar(
                        containerColor = Color.Transparent,
                        contentColor = Color.Transparent
                    ) { }
                }*/

                regularScaffoldScreens.any { baseRoute ->
                    scaffoldRoute?.startsWith(baseRoute) == true
                } -> {
                    regularBottomBar()
                }

                scaffoldRoute?.startsWith(Screen.SavedScreen::class.qualifiedName!!) == true -> {
                    savedProjectsBottomBar()
                }

                /*else -> {
                    // для непредусмотренных ситуаций
                    NavigationBar(
                        containerColor = Color.Transparent,
                        contentColor = Color.Transparent
                    ) {}
                }*/
            }
        },

        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        val appliedPadding = if (
            (regularScaffoldScreens/* + analysisScaffoldScreens*/).any { baseRoute ->
                scaffoldRoute?.startsWith(baseRoute) == true
            }
        ) {
            innerPadding
            /*PaddingValues(    // TODO не использовать
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                top = 0.dp,
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                bottom = innerPadding.calculateBottomPadding()
            )*/
        } else {
            innerPadding
        }

        content(appliedPadding)
    }

}

private fun NavHostController.navigateTab(route: Any) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
