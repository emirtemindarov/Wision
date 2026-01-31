package app.emirtemindarov.p1

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    navController: NavHostController,
    content: @Composable (innerPadding: PaddingValues) -> Unit
) {

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val destination = backStackEntry?.destination

    val isSavedTabSelected =
        destination?.hierarchy?.any {
            it.route == Screen.SavedRoot::class.qualifiedName
        } == true

    val isAnalysisTabSelected =
        destination?.hierarchy?.any {
            it.route == Screen.AnalysisRoot::class.qualifiedName
        } == true

    val regularScaffoldScreens = setOf(
        Screen.FileSelectionScreen::class.qualifiedName!!,
        Screen.FileDetailsScreen::class.qualifiedName!!,
        Screen.FolderSelectionScreen::class.qualifiedName!!,
        Screen.FolderDetailsScreen::class.qualifiedName!!,
    )

    val analysisScaffoldScreens = setOf(
        Screen.FileAnalysisScreen::class.qualifiedName!!,
        Screen.FolderAnalysisScreen::class.qualifiedName!!
    )

    val homeScreenTopAppBar: @Composable () -> Unit = {
        TopAppBar(
            // без отступа сверху под системную панель, только на HomeScreen
            windowInsets = WindowInsets(top = 0),

            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                //titleContentColor = MaterialTheme.colorScheme.onSurface,
            ),
            title = {
                /*Text(
                    text = "Начальный экран",
                    style = MaterialTheme.typography.titleLarge
                )*/
            },
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
                .offset(x = (-12).dp, y = (-20).dp)
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

    val homeScreenBottomBar: @Composable () -> Unit = {}    // отсутствует

    val regularTopAppBar: @Composable () -> Unit = {

        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                //containerColor = MaterialTheme.colorScheme.onPrimary,
                titleContentColor = MaterialTheme.colorScheme.primary,
                //actionIconContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            title = {
                /*Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )*/
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_left_alt_24px),
                        contentDescription = "Назад",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            actions = {
                var menuExpanded by remember { mutableStateOf(false) } // состояние для контекстного меню справа сверху

                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Меню",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }


                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Настройки") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            // TODO: переход в настройки
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("О приложении") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            // TODO: открыть экран About
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Выход") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            // TODO: выйти из приложения или выйти из аккаунта
                        }
                    )
                }

            }
        )
    }

    val regularFloatingActionButton: @Composable () -> Unit = {}    // отсутствует

    val regularBottomBar: @Composable () -> Unit = {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.onPrimary,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            // FIXME при быстром многократном переходе не успевает обновиться текст заголовка
            //  (видно по шрифту что свойства меняются а текст в итоге не соответствует вкладке)
            //  (возвращается к нормальному состоянию при медленном переключении)
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
            /*navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_left_alt_24px),
                        contentDescription = "Назад",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },*/
            actions = {
                var menuExpanded by remember { mutableStateOf(false) } // состояние для контекстного меню справа сверху

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
                // TODO сделвть скролл и временные разделы по дням
                Icon(
                    painter = painterResource(id = R.drawable.view_comfy_alt_24px),
                    contentDescription = "Меню",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                /*DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Настройки") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            // TODO: переход в настройки
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("О приложении") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            // TODO: открыть экран About
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Выход") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            // TODO: выйти из приложения или выйти из аккаунта
                        }
                    )
                }*/

            }
        )
    }

    val savedProjectsFloatingActionButton: @Composable () -> Unit = {}    // отсутствует

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

    val analysisTopAppBar: @Composable () -> Unit = {}    // отсутствует

    val analysisFloatingActionButton: @Composable () -> Unit = {}    // отсутствует

    val analysisBottomBar: @Composable () -> Unit = {}    // отсутствует


    var topAppBarPlaceholder: @Composable (() -> Unit)
    var floatingActionButtonPlaceholder: @Composable (() -> Unit)
    var bottomBarPlaceholder: @Composable (() -> Unit)

    Log.i("currentRoute", currentRoute.toString())










    Scaffold(
        topBar = {
            when {
                currentRoute == Screen.HomeScreen::class.qualifiedName!! -> {
                    homeScreenTopAppBar()
                }

                regularScaffoldScreens.any { baseRoute ->
                    currentRoute?.startsWith(baseRoute) == true
                } -> {
                    regularTopAppBar()
                }

                analysisScaffoldScreens.any { baseRoute ->
                    currentRoute?.startsWith(baseRoute) == true
                } -> {
                    analysisTopAppBar()
                }

                currentRoute?.startsWith(
                    Screen.SavedScreen::class.qualifiedName!!
                ) == true -> {
                    savedProjectsTopAppBar()
                }
            }
        },

        floatingActionButton = {
            when {
                currentRoute == Screen.HomeScreen::class.qualifiedName!! -> {
                    homeScreenFloatingActionButton()
                }
            }
        },

        bottomBar = {
            when {
                regularScaffoldScreens.any { baseRoute ->
                    currentRoute?.startsWith(baseRoute) == true
                } -> {
                    regularBottomBar()
                }

                currentRoute?.startsWith(
                    Screen.SavedScreen::class.qualifiedName!!
                ) == true -> {
                    savedProjectsBottomBar()
                }
            }
        },

        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        val appliedPadding = if (
            (regularScaffoldScreens + analysisScaffoldScreens).any { baseRoute ->
                currentRoute?.startsWith(baseRoute) == true
            }
        ) {
            PaddingValues(
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                top = 0.dp,
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                bottom = innerPadding.calculateBottomPadding()
            )
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
