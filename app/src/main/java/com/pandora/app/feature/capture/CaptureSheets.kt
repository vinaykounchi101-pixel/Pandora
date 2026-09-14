package com.pandora.app.feature.capture

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.pandora.app.core.designsystem.theme.ApricotOrange
import com.pandora.app.core.designsystem.theme.BottomSheetTopShape
import com.pandora.app.core.designsystem.theme.ButtonShape
import com.pandora.app.core.designsystem.theme.IrisPrimary
import com.pandora.app.core.designsystem.theme.PandoraTypography
import com.pandora.app.core.designsystem.theme.PorcelainContainer
import com.pandora.app.core.designsystem.theme.PorcelainSheetWhite
import com.pandora.app.core.designsystem.theme.Spacing
import com.pandora.app.core.designsystem.theme.TagChipShape
import com.pandora.app.core.designsystem.theme.TextOutline
import com.pandora.app.core.designsystem.theme.TextPrimary
import com.pandora.app.core.designsystem.theme.TextSecondary
import com.pandora.app.core.util.DuplicateCheckResult
import com.pandora.app.core.util.DuplicateGuardHelper
import com.pandora.app.core.util.VoiceRecognitionHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickNoteBottomSheet(
    onDismiss: () -> Unit,
    onSaveNote: (title: String, content: String) -> Unit,
    voiceHelper: VoiceRecognitionHelper? = null,
    duplicateGuardHelper: DuplicateGuardHelper? = null
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var duplicateWarning by remember { mutableStateOf<String?>(null) }
    var isListening by remember { mutableStateOf(false) }

    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && voiceHelper != null) {
            isListening = true
            voiceHelper.startListening(
                onResult = { spokenText ->
                    content = if (content.isBlank()) spokenText else "$content $spokenText"
                    isListening = false
                },
                onError = { isListening = false }
            )
        }
    }

    LaunchedEffect(title) {
        if (title.isNotBlank() && duplicateGuardHelper != null) {
            val result = duplicateGuardHelper.checkDuplicate(null, title)
            duplicateWarning = when (result) {
                is DuplicateCheckResult.DuplicateFound -> "Existing note with exact title found in Vault."
                is DuplicateCheckResult.None -> null
            }
        } else {
            duplicateWarning = null
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            voiceHelper?.stopListening()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = BottomSheetTopShape,
        containerColor = PorcelainSheetWhite,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 4.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(TextOutline.copy(alpha = 0.4f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.Large, vertical = Spacing.Small)
                .padding(bottom = Spacing.Huge),
            verticalArrangement = Arrangement.spacedBy(Spacing.MediumSmall)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = ApricotOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Quick Note",
                        style = PandoraTypography.headlineSmall,
                        color = TextPrimary
                    )
                }

                IconButton(
                    onClick = {
                        if (isListening) {
                            voiceHelper?.stopListening()
                            isListening = false
                        } else {
                            val permissionCheck = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            )
                            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                isListening = true
                                voiceHelper?.startListening(
                                    onResult = { spokenText ->
                                        content = if (content.isBlank()) spokenText else "$content $spokenText"
                                        isListening = false
                                    },
                                    onError = { isListening = false }
                                )
                            } else {
                                recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            if (isListening) ApricotOrange else PorcelainContainer,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = "Voice Dictation",
                        tint = if (isListening) PorcelainSheetWhite else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            AnimatedVisibility(
                visible = duplicateWarning != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(TagChipShape)
                        .background(ApricotOrange.copy(alpha = 0.12f))
                        .padding(horizontal = Spacing.Medium, vertical = Spacing.Small)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = ApricotOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = duplicateWarning.orEmpty(),
                            style = PandoraTypography.bodySmall,
                            color = TextPrimary
                        )
                    }
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IrisPrimary,
                    unfocusedBorderColor = PorcelainContainer
                )
            )

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text(if (isListening) "Listening... speak now" else "What's on your mind?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isListening) ApricotOrange else IrisPrimary,
                    unfocusedBorderColor = PorcelainContainer
                )
            )

            Button(
                onClick = {
                    if (content.isNotBlank() || title.isNotBlank()) {
                        onSaveNote(title.ifBlank { "Personal Note" }, content)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = ButtonShape,
                colors = ButtonDefaults.buttonColors(containerColor = IrisPrimary)
            ) {
                Text("Save to Vault", style = PandoraTypography.labelLarge)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkPasteBottomSheet(
    onDismiss: () -> Unit,
    onSaveLink: (url: String, title: String) -> Unit,
    duplicateGuardHelper: DuplicateGuardHelper? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var url by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var duplicateWarning by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(url) {
        if (url.isNotBlank() && duplicateGuardHelper != null) {
            val result = duplicateGuardHelper.checkDuplicate(url, null)
            duplicateWarning = when (result) {
                is DuplicateCheckResult.DuplicateFound -> "This URL is already saved in your Vault."
                is DuplicateCheckResult.None -> null
            }
        } else {
            duplicateWarning = null
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = BottomSheetTopShape,
        containerColor = PorcelainSheetWhite,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 4.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(TextOutline.copy(alpha = 0.4f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.Large, vertical = Spacing.Small)
                .padding(bottom = Spacing.Huge),
            verticalArrangement = Arrangement.spacedBy(Spacing.MediumSmall)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
            ) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = null,
                    tint = IrisPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Save Web Link",
                    style = PandoraTypography.headlineSmall,
                    color = TextPrimary
                )
            }

            AnimatedVisibility(
                visible = duplicateWarning != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(TagChipShape)
                        .background(ApricotOrange.copy(alpha = 0.12f))
                        .padding(horizontal = Spacing.Medium, vertical = Spacing.Small)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = ApricotOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = duplicateWarning.orEmpty(),
                            style = PandoraTypography.bodySmall,
                            color = TextPrimary
                        )
                    }
                }
            }

            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("https://example.com") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IrisPrimary,
                    unfocusedBorderColor = PorcelainContainer
                )
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title / Summary") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IrisPrimary,
                    unfocusedBorderColor = PorcelainContainer
                )
            )

            Button(
                onClick = {
                    if (url.isNotBlank()) {
                        onSaveLink(url, title.ifBlank { url })
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = ButtonShape,
                colors = ButtonDefaults.buttonColors(containerColor = IrisPrimary)
            ) {
                Text("Archive Link", style = PandoraTypography.labelLarge)
            }
        }
    }
}
