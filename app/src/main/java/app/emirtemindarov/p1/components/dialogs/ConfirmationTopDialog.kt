package app.emirtemindarov.p1.components.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import app.emirtemindarov.p1.R
import app.emirtemindarov.p1.components.buttons.ComplexButton

@Composable
fun ConfirmationTopDialog(
    title: String,
    text: String,
    confirmText: String,
    cancelText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onConfirm,
        /*properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )*/
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onConfirm() },
            contentAlignment = Alignment.TopCenter
        ) {

            Surface(
                shape = RoundedCornerShape(24.dp
                    /*bottomStart = 24.dp,
                    bottomEnd = 24.dp*/
                ),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 64.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            colors = ButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary,
                                containerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                disabledContentColor = Color.Transparent
                            )
                        ) {
                            Text(cancelText)
                        }

                        Spacer(Modifier.width(8.dp))

                        Button(
                            onClick = onConfirm,
                            colors = ButtonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.primary,
                                disabledContainerColor = Color.Transparent,
                                disabledContentColor = Color.Transparent
                            )
                        ) {
                            Text(confirmText)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OneButtonConfirmationTopRightDialog(
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onConfirm,
        /*properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )*/
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDismiss() },
            contentAlignment = Alignment.TopEnd
        ) {

            Surface(
                shape = AbsoluteRoundedCornerShape(
                    topLeft = 18.dp,
                    topRight = 0.dp,
                    bottomLeft = 18.dp,
                    bottomRight = 18.dp
                ),
                color = MaterialTheme.colorScheme.onPrimary,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(top = 64.dp)
            ) {

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(
                            12.dp,
                            20.dp
                        )
                ) {

                    ComplexButton(
                        action = onConfirm,
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
                                confirmText,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    }
                }

            }
        }
    }
}