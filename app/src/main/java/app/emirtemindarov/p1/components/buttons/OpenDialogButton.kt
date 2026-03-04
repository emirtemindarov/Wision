package app.emirtemindarov.p1.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.emirtemindarov.p1.components.dialogs.ConfirmationBottomDialog

@Composable
fun OpenDialogButton(
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

    Button(
        enabled = enabled,
        onClick = { showDialog = true },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.onTertiary,
            disabledContentColor = MaterialTheme.colorScheme.tertiary
        ),
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
    ) {
        content()
    }

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
