package app.emirtemindarov.p1

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun LockOrientationOnScreen(
    orientation: Int,
    content: @Composable () -> Unit
) {
    val activity = LocalContext.current.findActivity() ?: return

    DisposableEffect(orientation) {
        activity.requestedOrientation = orientation
        onDispose { }
    }

    content()
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}