package app.emirtemindarov.p1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import app.emirtemindarov.p1.assistant.AssistantViewModel
import app.emirtemindarov.p1.assistant.AssistantViewModelFactory
import app.emirtemindarov.p1.mvvm.savedprojects.SavedProjectsViewModel
import app.emirtemindarov.p1.room.AppDatabase
import app.emirtemindarov.p1.ui.theme.P1Theme

class MainActivity : ComponentActivity() {

    // TODO разобраться где лучше объявлять важные переменные
    //private val assistantViewModel: AssistantViewModel by viewModels()  ?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())

        setContent {

            val db = AppDatabase.get(applicationContext)
            val graphDao = db.graphDao()

            val savedProjectsViewModel = remember {
                SavedProjectsViewModel(graphDao)
            }

            val assistantViewModel: AssistantViewModel = viewModel(
                factory = AssistantViewModelFactory(graphDao)
            )

            val navController = rememberNavController()

            P1Theme {
                Surface {
                    Log.i("checkpoint Main", "checkpoint Main")

                    AppScaffold(
                        navController = navController,
                    ) { innerPadding ->

                        AppNavigation(
                            navController = navController,
                            assistantViewModel = assistantViewModel,
                            savedProjectsViewModel = savedProjectsViewModel,
                            modifier = Modifier.padding(innerPadding),
                        )
                    }
                }
            }
        }
    }
}
