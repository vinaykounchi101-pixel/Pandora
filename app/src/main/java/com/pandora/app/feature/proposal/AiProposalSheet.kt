package com.pandora.app.feature.proposal

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.pandora.app.core.database.entity.FolderEntity
import com.pandora.app.core.database.entity.ItemType
import com.pandora.app.core.designsystem.theme.ApricotFixed
import com.pandora.app.core.designsystem.theme.ApricotOrange
import com.pandora.app.core.designsystem.theme.BottomSheetTopShape
import com.pandora.app.core.designsystem.theme.CeruleanFixed
import com.pandora.app.core.designsystem.theme.IrisFixed
import com.pandora.app.core.designsystem.theme.IrisPrimary
import com.pandora.app.core.designsystem.theme.OutlineHairline
import com.pandora.app.core.designsystem.theme.PandoraTypography
import com.pandora.app.core.designsystem.theme.PorcelainContainer
import com.pandora.app.core.designsystem.theme.PorcelainContainerHigh
import com.pandora.app.core.designsystem.theme.PorcelainSheetWhite
import com.pandora.app.core.designsystem.theme.TagChipShape
import com.pandora.app.core.designsystem.theme.TextOutline
import com.pandora.app.core.designsystem.theme.TextPrimary
import com.pandora.app.core.designsystem.theme.TextSecondary
import com.pandora.app.core.util.DuplicateCheckResult
import com.pandora.app.core.util.DuplicateGuardHelper
import com.pandora.app.core.util.VoiceRecognitionHelper

data class QuickAddTypeOption(
    val type: ItemType,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val tagColor: Color,
    val iconTint: Color
)

private val QUICK_ADD_OPTIONS = listOf(
    QuickAddTypeOption(
        type = ItemType.NOTE,
        title = "Personal Note",
        subtitle = "Thoughts, reflections & ideas",
        icon = Icons.Default.Edit,
        tagColor = IrisFixed,
        iconTint = IrisPrimary
    ),
    QuickAddTypeOption(
        type = ItemType.IMAGE,
        title = "Image / Screenshot",
        subtitle = "Photos from device or gallery",
        icon = Icons.Default.Image,
        tagColor = ApricotFixed,
        iconTint = ApricotOrange
    ),
    QuickAddTypeOption(
        type = ItemType.DOCUMENT,
        title = "PDF / Document",
        subtitle = "PDF reports, contracts & files",
        icon = Icons.Default.PictureAsPdf,
        tagColor = CeruleanFixed,
        iconTint = Color(0xFF0284C7)
    ),
    QuickAddTypeOption(
        type = ItemType.ARTICLE,
        title = "Web Article / Link",
        subtitle = "URLs and web bookmarks",
        icon = Icons.Default.Link,
        tagColor = CeruleanFixed,
        iconTint = Color(0xFF0284C7)
    ),
    QuickAddTypeOption(
        type = ItemType.VOICE,
        title = "Voice Memo",
        subtitle = "Voice recording with transcription",
        icon = Icons.Default.Mic,
        tagColor = ApricotFixed,
        iconTint = ApricotOrange
    )
)

private fun getFileDisplayNameAndSize(context: Context, uri: Uri): Pair<String, Long> {
    var name = "Document.pdf"
    var size = 0L
    try {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIdx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIdx = it.getColumnIndex(OpenableColumns.SIZE)
                if (nameIdx != -1) name = it.getString(nameIdx) ?: name
                if (sizeIdx != -1) size = it.getLong(sizeIdx)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return Pair(name, size)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AiProposalBottomSheet(
    onDismiss: () -> Unit,
    availableFolders: List<FolderEntity> = emptyList(),
    voiceHelper: VoiceRecognitionHelper? = null,
    duplicateGuardHelper: DuplicateGuardHelper? = null,
    onSaveUniversal: (
        itemType: ItemType,
        title: String,
        content: String,
        sourceUrl: String?,
        fileUri: Uri?,
        tags: List<String>,
        folderId: Long?
    ) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedType by remember { mutableStateOf(ItemType.NOTE) }
    var showTypeDropdown by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var sourceUrl by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf("") }
    var selectedFileSize by remember { mutableStateOf(0L) }

    var selectedFolderId by remember { mutableStateOf<Long?>(null) }
    var showFolderDropdown by remember { mutableStateOf(false) }

    var newTagInput by remember { mutableStateOf("") }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }

    var duplicateWarning by remember { mutableStateOf<String?>(null) }
    var isListening by remember { mutableStateOf(false) }

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedFileUri = uri
            val (name, size) = getFileDisplayNameAndSize(context, uri)
            selectedFileName = name
            selectedFileSize = size
            if (title.isBlank()) {
                title = name.substringBeforeLast(".")
            }
        }
    }

    // Document / PDF Picker Launcher
    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedFileUri = uri
            val (name, size) = getFileDisplayNameAndSize(context, uri)
            selectedFileName = name
            selectedFileSize = size
            if (title.isBlank()) {
                title = name.substringBeforeLast(".")
            }
        }
    }

    // Audio Dictation Permission Launcher
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

    // Duplicate check on link or title
    LaunchedEffect(sourceUrl, title) {
        if (duplicateGuardHelper != null) {
            if (selectedType == ItemType.ARTICLE && sourceUrl.isNotBlank()) {
                val result = duplicateGuardHelper.checkDuplicate(sourceUrl, null)
                duplicateWarning = when (result) {
                    is DuplicateCheckResult.DuplicateFound -> "This URL is already archived in your Vault."
                    is DuplicateCheckResult.None -> null
                }
            } else if (title.isNotBlank()) {
                val result = duplicateGuardHelper.checkDuplicate(null, title)
                duplicateWarning = when (result) {
                    is DuplicateCheckResult.DuplicateFound -> "An item with exact title is already in your Vault."
                    is DuplicateCheckResult.None -> null
                }
            } else {
                duplicateWarning = null
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            voiceHelper?.stopListening()
        }
    }

    val currentOption = QUICK_ADD_OPTIONS.first { it.type == selectedType }

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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Header: Title + AI Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick Add to Vault",
                    style = PandoraTypography.headlineMedium,
                    fontSize = 20.sp,
                    color = TextPrimary
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(PorcelainContainerHigh)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = IrisPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Vault Ingestion",
                        style = PandoraTypography.labelSmall,
                        color = IrisPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 1. Interactive Type Selector Dropdown Box
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "WHAT ARE YOU ADDING?",
                    style = PandoraTypography.labelSmall,
                    color = TextSecondary,
                    letterSpacing = 0.5.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, RoundedCornerShape(10.dp))
                            .clip(RoundedCornerShape(10.dp))
                            .background(PorcelainContainer)
                            .border(1.dp, IrisPrimary.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .clickable { showTypeDropdown = true }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(currentOption.tagColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = currentOption.icon,
                                    contentDescription = null,
                                    tint = currentOption.iconTint,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = currentOption.title,
                                    style = PandoraTypography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = currentOption.subtitle,
                                    style = PandoraTypography.bodySmall,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Type",
                            tint = TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showTypeDropdown,
                        onDismissRequest = { showTypeDropdown = false },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(PorcelainSheetWhite)
                    ) {
                        QUICK_ADD_OPTIONS.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(option.tagColor),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = option.icon,
                                                contentDescription = null,
                                                tint = option.iconTint,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = option.title,
                                                style = PandoraTypography.bodyMedium,
                                                fontWeight = if (option.type == selectedType) FontWeight.Bold else FontWeight.Normal,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = option.subtitle,
                                                style = PandoraTypography.bodySmall,
                                                color = TextSecondary,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    selectedType = option.type
                                    showTypeDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            // Duplicate Warning Banner
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
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
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

            // 2. Type-Specific Input Form
            when (selectedType) {
                ItemType.NOTE -> {
                    // Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Note Title (Optional)") },
                        placeholder = { Text("e.g. Design meeting takeaways") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IrisPrimary,
                            unfocusedBorderColor = PorcelainContainer
                        )
                    )

                    // Note Content with Voice Mic
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = content,
                            onValueChange = { content = it },
                            label = { Text(if (isListening) "🎙️ Listening... speak now" else "Write your note...") },
                            placeholder = { Text("Type note details, thoughts, or reflections...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isListening) ApricotOrange else IrisPrimary,
                                unfocusedBorderColor = PorcelainContainer
                            )
                        )

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
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .size(34.dp)
                                .background(if (isListening) ApricotOrange else PorcelainContainerHigh, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                                contentDescription = "Dictate",
                                tint = if (isListening) PorcelainSheetWhite else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                ItemType.IMAGE -> {
                    // Image Picker Area
                    if (selectedFileUri != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(PorcelainContainer)
                                .border(1.dp, OutlineHairline, RoundedCornerShape(10.dp))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    model = selectedFileUri,
                                    contentDescription = "Selected Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedFileName,
                                    style = PandoraTypography.labelMedium,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(
                                    onClick = {
                                        imagePickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                ) {
                                    Text("Change Photo", color = IrisPrimary)
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PorcelainContainer)
                                .border(1.dp, OutlineHairline, RoundedCornerShape(10.dp))
                                .clickable {
                                    imagePickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = null,
                                    tint = ApricotOrange,
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(
                                    text = "Tap to choose photo from device",
                                    style = PandoraTypography.labelMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "JPEG, PNG, WebP supported",
                                    style = PandoraTypography.bodySmall,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    // Image Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Photo Title / Description") },
                        placeholder = { Text("e.g. Architecture Blueprint") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IrisPrimary,
                            unfocusedBorderColor = PorcelainContainer
                        )
                    )

                    // Optional Notes
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Caption / Notes (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IrisPrimary,
                            unfocusedBorderColor = PorcelainContainer
                        )
                    )
                }

                ItemType.DOCUMENT -> {
                    // PDF / Document Picker Area
                    if (selectedFileUri != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(PorcelainContainer)
                                .border(1.dp, OutlineHairline, RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CeruleanFixed),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PictureAsPdf,
                                        contentDescription = null,
                                        tint = Color(0xFF0284C7),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = selectedFileName,
                                        style = PandoraTypography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        maxLines = 1
                                    )
                                    val sizeKb = selectedFileSize / 1024
                                    Text(
                                        text = if (sizeKb > 1024) "${sizeKb / 1024} MB" else "$sizeKb KB",
                                        style = PandoraTypography.bodySmall,
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            TextButton(onClick = { documentPickerLauncher.launch("*/*") }) {
                                Text("Change", color = IrisPrimary)
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PorcelainContainer)
                                .border(1.dp, OutlineHairline, RoundedCornerShape(10.dp))
                                .clickable { documentPickerLauncher.launch("*/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = Color(0xFF0284C7),
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(
                                    text = "Tap to choose PDF or Document",
                                    style = PandoraTypography.labelMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "PDF, EPUB, TXT, DOCX supported",
                                    style = PandoraTypography.bodySmall,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    // Document Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Document Title") },
                        placeholder = { Text("e.g. Q3 Strategic Plan") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IrisPrimary,
                            unfocusedBorderColor = PorcelainContainer
                        )
                    )

                    // Summary / Notes
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Document Summary / Notes (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IrisPrimary,
                            unfocusedBorderColor = PorcelainContainer
                        )
                    )
                }

                ItemType.ARTICLE -> {
                    // Web URL
                    OutlinedTextField(
                        value = sourceUrl,
                        onValueChange = { sourceUrl = it },
                        label = { Text("Web URL (https://...)") },
                        placeholder = { Text("https://example.com/article") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IrisPrimary,
                            unfocusedBorderColor = PorcelainContainer
                        )
                    )

                    // Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Article Title") },
                        placeholder = { Text("e.g. Distributed Systems Overview") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IrisPrimary,
                            unfocusedBorderColor = PorcelainContainer
                        )
                    )

                    // Excerpt / Notes
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Summary or Notes (Optional)") },
                        placeholder = { Text("Key insights or quotes...") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IrisPrimary,
                            unfocusedBorderColor = PorcelainContainer
                        )
                    )
                }

                ItemType.VOICE -> {
                    // Voice Record Area
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isListening) ApricotFixed else PorcelainContainer)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (isListening) ApricotOrange else PorcelainContainerHigh),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                                    contentDescription = null,
                                    tint = if (isListening) PorcelainSheetWhite else TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (isListening) "Recording in progress..." else "Voice Dictation Ready",
                                    style = PandoraTypography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (isListening) "Speak now..." else "Tap button to start speaking",
                                    style = PandoraTypography.bodySmall,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Button(
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
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isListening) ApricotOrange else IrisPrimary
                            )
                        ) {
                            Text(if (isListening) "Stop" else "Record")
                        }
                    }

                    // Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Voice Memo Title") },
                        placeholder = { Text("e.g. Brainstorming audio thought") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IrisPrimary,
                            unfocusedBorderColor = PorcelainContainer
                        )
                    )

                    // Transcript Editor
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Speech Transcript") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IrisPrimary,
                            unfocusedBorderColor = PorcelainContainer
                        )
                    )
                }
            }

            // 3. Assign to Folder (Optional)
            if (availableFolders.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "FOLDER ASSIGNMENT (OPTIONAL)",
                        style = PandoraTypography.labelSmall,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Box(modifier = Modifier.fillMaxWidth()) {
                        val activeFolder = availableFolders.find { it.id == selectedFolderId }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(PorcelainContainer)
                                .clickable { showFolderDropdown = true }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    contentDescription = null,
                                    tint = IrisPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = activeFolder?.name ?: "Inbox / Root (No Folder)",
                                    style = PandoraTypography.bodyMedium,
                                    color = TextPrimary
                                )
                            }
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondary)
                        }

                        DropdownMenu(
                            expanded = showFolderDropdown,
                            onDismissRequest = { showFolderDropdown = false },
                            modifier = Modifier.background(PorcelainSheetWhite)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Inbox / Root (No Folder)") },
                                onClick = {
                                    selectedFolderId = null
                                    showFolderDropdown = false
                                }
                            )
                            availableFolders.forEach { folder ->
                                DropdownMenuItem(
                                    text = { Text(folder.name) },
                                    leadingIcon = { Icon(Icons.Default.FolderOpen, contentDescription = null, tint = IrisPrimary) },
                                    onClick = {
                                        selectedFolderId = folder.id
                                        showFolderDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 4. Semantic Tags
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "SEMANTIC TAGS",
                    style = PandoraTypography.labelSmall,
                    color = TextSecondary,
                    letterSpacing = 0.5.sp,
                    fontWeight = FontWeight.Bold
                )

                if (selectedTags.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        selectedTags.forEach { tag ->
                            Row(
                                modifier = Modifier
                                    .clip(TagChipShape)
                                    .background(IrisFixed)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("#$tag", style = PandoraTypography.labelSmall, color = TextPrimary)
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove tag",
                                    tint = TextSecondary,
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clickable { selectedTags = selectedTags - tag }
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newTagInput,
                        onValueChange = { newTagInput = it },
                        placeholder = { Text("Add tag (e.g. work, design)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IrisPrimary,
                            unfocusedBorderColor = PorcelainContainer
                        )
                    )
                    Button(
                        onClick = {
                            if (newTagInput.isNotBlank()) {
                                selectedTags = selectedTags + newTagInput.trim().removePrefix("#").lowercase()
                                newTagInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PorcelainContainerHigh)
                    ) {
                        Text("Add", color = TextPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 5. Final Save Action Buttons
            val canSave = when (selectedType) {
                ItemType.NOTE -> title.isNotBlank() || content.isNotBlank()
                ItemType.IMAGE -> selectedFileUri != null || title.isNotBlank()
                ItemType.DOCUMENT -> selectedFileUri != null || title.isNotBlank()
                ItemType.ARTICLE -> sourceUrl.isNotBlank() || title.isNotBlank()
                ItemType.VOICE -> content.isNotBlank() || title.isNotBlank()
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel", color = TextSecondary)
                }

                Button(
                    onClick = {
                        val finalTitle = title.ifBlank {
                            when (selectedType) {
                                ItemType.NOTE -> "Personal Note"
                                ItemType.IMAGE -> selectedFileName.ifBlank { "Photo Capture" }
                                ItemType.DOCUMENT -> selectedFileName.ifBlank { "Saved Document" }
                                ItemType.ARTICLE -> sourceUrl.ifBlank { "Web Article" }
                                ItemType.VOICE -> "Voice Thought"
                            }
                        }
                        onSaveUniversal(
                            selectedType,
                            finalTitle,
                            content,
                            if (selectedType == ItemType.ARTICLE) sourceUrl else null,
                            selectedFileUri,
                            selectedTags.toList(),
                            selectedFolderId
                        )
                    },
                    modifier = Modifier.weight(2f),
                    colors = ButtonDefaults.buttonColors(containerColor = IrisPrimary),
                    enabled = canSave
                ) {
                    Text("Save to Vault", style = PandoraTypography.labelLarge)
                }
            }
        }
    }
}
