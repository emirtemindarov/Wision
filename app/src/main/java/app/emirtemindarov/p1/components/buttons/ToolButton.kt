package app.emirtemindarov.p1.components.buttons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import app.emirtemindarov.p1.components.dialogs.ConfirmationBottomDialog
import app.emirtemindarov.p1.components.dialogs.ConfirmationTopDialog
import app.emirtemindarov.p1.components.dialogs.OneButtonConfirmationTopRightDialog

@Composable
fun ToolButton(
    //enabled: Boolean,
    action: () -> Unit,
    content: @Composable () -> Unit
) {
    IconButton(onClick = action) {
        content.invoke()
    }
}

@Composable
fun ToolButtonWithBottomDialog(
    enabled: Boolean = true,
    dialogTitle: String,
    dialogText: String,
    confirmText: String = "Подтвердить",
    cancelText: String = "Отмена",
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    IconButton(
        enabled = enabled,
        onClick = { showDialog = true },
        modifier = modifier
    ) {
        content()
    }

    // --- диалог ---
    if (showDialog) {
        ConfirmationBottomDialog(
            title = dialogTitle,
            text = dialogText,
            confirmText = confirmText,
            cancelText = cancelText,
            onConfirm = {
                showDialog = false
                onConfirm()
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun ToolButtonWithTopDialog(
    enabled: Boolean = true,
    dialogTitle: String,
    dialogText: String,
    confirmText: String = "Отмена",
    cancelText: String = "Подтвердить",
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    IconButton(
        enabled = enabled,
        onClick = { showDialog = true },
        modifier = modifier
    ) {
        content()
    }

    // --- диалог ---
    if (showDialog) {
        ConfirmationTopDialog(
            title = dialogTitle,
            text = dialogText,
            confirmText = confirmText,
            cancelText = cancelText,
            onConfirm = {
                showDialog = false
            },
            onDismiss = {
                showDialog = false
                onConfirm()
            }
        )
    }
}

@Composable
fun ToolButtonWithSimpleTopRightDialog(
    enabled: Boolean = true,
    confirmText: String = "Подтвердить",
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    IconButton(
        enabled = enabled,
        onClick = { showDialog = true },
        modifier = modifier
    ) {
        content()
    }

    // --- диалог ---
    if (showDialog) {
        OneButtonConfirmationTopRightDialog(
            confirmText = confirmText,
            onConfirm = {
                showDialog = false
                onConfirm()
            },
            onDismiss = {
                showDialog = false
            }
        )
    }
}

