package com.pandora.app.feature.reader

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pandora.app.core.database.entity.ItemType
import com.pandora.app.core.designsystem.theme.ApricotContainer
import com.pandora.app.core.designsystem.theme.ApricotFixed
import com.pandora.app.core.designsystem.theme.ApricotOrange
import com.pandora.app.core.designsystem.theme.BadgeShape
import com.pandora.app.core.designsystem.theme.ButtonShape
import com.pandora.app.core.designsystem.theme.CardShape
import com.pandora.app.core.designsystem.theme.CeruleanDark
import com.pandora.app.core.designsystem.theme.CeruleanFixed
import com.pandora.app.core.designsystem.theme.IrisFixed
import com.pandora.app.core.designsystem.theme.IrisPrimary
import com.pandora.app.core.designsystem.theme.OnApricotFixedVariant
import com.pandora.app.core.designsystem.theme.OnCeruleanFixed
import com.pandora.app.core.designsystem.theme.OutlineHairline
import com.pandora.app.core.designsystem.theme.PandoraTypography
import com.pandora.app.core.designsystem.theme.PorcelainCanvas
import com.pandora.app.core.designsystem.theme.PorcelainContainer
import com.pandora.app.core.designsystem.theme.PorcelainContainerHigh
import com.pandora.app.core.designsystem.theme.PorcelainContainerHighest
import com.pandora.app.core.designsystem.theme.PorcelainContainerLow
import com.pandora.app.core.designsystem.theme.PorcelainSheetWhite
import com.pandora.app.core.designsystem.theme.QuoteItalicStyle
import com.pandora.app.core.designsystem.theme.Spacing
import com.pandora.app.core.designsystem.theme.TagChipShape
import com.pandora.app.core.designsystem.theme.TextPrimary
import com.pandora.app.core.designsystem.theme.TextSecondary
import com.pandora.app.core.designsystem.theme.TextTertiary
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ItemDetailScreen(
    viewModel: ItemDetailViewModel,
    onBackClick: () -> Unit,
    onRelatedItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val itemWithRelations = state.itemWithRelations
    val context = LocalContext.current

    var showMenu by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showFolderDialog by remember { mutableStateOf(false) }
    var showTagsDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Dialog state holders
    var editTitle by remember { mutableStateOf("") }
    var editContent by remember { mutableStateOf("") }
    var newFolderName by remember { mutableStateOf("") }
    var newTagName by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PorcelainCanvas)
    ) {
        // Top Navigation Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PorcelainCanvas)
                .statusBarsPadding()
                .height(56.dp)
                .padding(horizontal = Spacing.Small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = "Artifact Reader",
                    style = PandoraTypography.headlineMedium,
                    fontSize = 19.sp,
                    color = TextPrimary
                )
            }

            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More Options",
                        tint = TextPrimary
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(PorcelainSheetWhite)
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit Title & Content") },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = IrisPrimary) },
                        onClick = {
                            showMenu = false
                            if (itemWithRelations != null) {
                                editTitle = itemWithRelations.item.title
                                editContent = itemWithRelations.item.fullContent.ifBlank { itemWithRelations.item.excerpt }
                                showEditDialog = true
                            }
                        }
                    )

                    DropdownMenuItem(
                        text = { Text(if (state.isFavorite) "Remove from Favorites" else "Add to Favorites") },
                        leadingIcon = {
                            Icon(
                                imageVector = if (state.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (state.isFavorite) ApricotOrange else TextSecondary
                            )
                        },
                        onClick = {
                            showMenu = false
                            viewModel.toggleFavorite()
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Move / Assign Folder") },
                        leadingIcon = { Icon(Icons.Default.Folder, contentDescription = null, tint = IrisPrimary) },
                        onClick = {
                            showMenu = false
                            showFolderDialog = true
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Manage Tags") },
                        leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null, tint = IrisPrimary) },
                        onClick = {
                            showMenu = false
                            showTagsDialog = true
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Copy Content / Link") },
                        leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null, tint = TextSecondary) },
                        onClick = {
                            showMenu = false
                            if (itemWithRelations != null) {
                                val textToCopy = itemWithRelations.item.sourceUrl
                                    ?: itemWithRelations.item.fullContent.ifBlank { itemWithRelations.item.excerpt.ifBlank { itemWithRelations.item.title } }
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Pandora Item", textToCopy)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Share Memory") },
                        leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, tint = TextSecondary) },
                        onClick = {
                            showMenu = false
                            if (itemWithRelations != null) {
                                val item = itemWithRelations.item
                                val shareText = "${item.title}\n\n${item.sourceUrl ?: item.fullContent.ifBlank { item.excerpt }}"
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Memory"))
                            }
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Delete Memory", color = Color(0xFFBA1A1A)) },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFBA1A1A)) },
                        onClick = {
                            showMenu = false
                            showDeleteConfirmDialog = true
                        }
                    )
                }
            }
        }

        if (state.isLoading || itemWithRelations == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = IrisPrimary)
            }
        } else {
            val item = itemWithRelations.item

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = Spacing.Medium, vertical = Spacing.Small),
                verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
            ) {
                // Meta Badges & Action Bar
                item {
                    val typeLabel = when (item.itemType) {
                        ItemType.IMAGE -> "Photo Capture"
                        ItemType.ARTICLE -> "Web Article"
                        ItemType.NOTE -> "Vault Note"
                        ItemType.DOCUMENT -> "Document"
                        ItemType.VOICE -> "Voice Memo"
                    }
                    val typeBg = when (item.itemType) {
                        ItemType.IMAGE -> ApricotFixed
                        ItemType.ARTICLE -> CeruleanFixed
                        ItemType.NOTE -> IrisFixed
                        ItemType.DOCUMENT -> CeruleanFixed
                        ItemType.VOICE -> ApricotFixed
                    }
                    val typeText = when (item.itemType) {
                        ItemType.IMAGE -> OnApricotFixedVariant
                        ItemType.ARTICLE -> OnCeruleanFixed
                        ItemType.NOTE -> IrisPrimary
                        ItemType.DOCUMENT -> OnCeruleanFixed
                        ItemType.VOICE -> OnApricotFixedVariant
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall)
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(BadgeShape)
                                    .background(typeBg)
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(
                                    text = typeLabel,
                                    style = PandoraTypography.labelSmall,
                                    color = typeText,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .clip(BadgeShape)
                                    .background(PorcelainContainer)
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = CeruleanDark,
                                    modifier = Modifier.size(11.dp)
                                )
                                Text(
                                    text = "Offline Vault",
                                    style = PandoraTypography.labelSmall,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall)
                        ) {
                            IconButton(
                                onClick = { viewModel.toggleFavorite() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PorcelainContainerLow)
                            ) {
                                Icon(
                                    imageVector = if (state.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (state.isFavorite) ApricotOrange else TextSecondary,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "${item.title}\n\n${item.sourceUrl ?: item.fullContent.ifBlank { item.excerpt }}"
                                        )
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, "Share Memory")
                                    context.startActivity(shareIntent)
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PorcelainContainerLow)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }
                    }
                }

                // Editorial Headline & Metadata
                item {
                    val dateStr = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(item.createdAt))

                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall)) {
                        Text(
                            text = item.title,
                            style = PandoraTypography.headlineLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 22.sp,
                            lineHeight = 30.sp
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall)
                        ) {
                            if (!item.domain.isNullOrBlank() || !item.capturedFromApp.isNullOrBlank()) {
                                Text(
                                    text = item.domain ?: item.capturedFromApp ?: "Vault Asset",
                                    style = PandoraTypography.bodySmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(text = "•", color = TextTertiary)
                            }
                            Text(text = dateStr, style = PandoraTypography.bodySmall, color = TextTertiary)
                        }
                    }
                }

                // Main Visual Content Box (Image / Note Body / Article Embed / Document)
                item {
                    val hasValidFile = !item.localFilePath.isNullOrBlank() && File(item.localFilePath).exists()

                    if (item.itemType == ItemType.IMAGE && hasValidFile) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .shadow(1.dp, CardShape)
                                .clip(CardShape)
                                .background(PorcelainContainer)
                                .border(1.dp, OutlineHairline, CardShape)
                        ) {
                            AsyncImage(
                                model = File(item.localFilePath!!),
                                contentDescription = item.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else if (item.itemType == ItemType.NOTE || item.itemType == ItemType.VOICE) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(1.dp, CardShape)
                                .clip(CardShape)
                                .background(PorcelainSheetWhite)
                                .border(1.dp, OutlineHairline, CardShape)
                                .padding(Spacing.Medium)
                        ) {
                            Text(
                                text = item.fullContent.ifBlank { item.excerpt.ifBlank { item.title } },
                                style = QuoteItalicStyle,
                                fontSize = 15.sp,
                                lineHeight = 24.sp,
                                color = TextPrimary
                            )
                        }
                    } else if (item.itemType == ItemType.ARTICLE && !item.sourceUrl.isNullOrBlank()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(1.dp, CardShape)
                                .clip(CardShape)
                                .background(PorcelainSheetWhite)
                                .border(1.dp, OutlineHairline, CardShape)
                                .padding(Spacing.Medium),
                            verticalArrangement = Arrangement.spacedBy(Spacing.Small)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.domain ?: "Source Article",
                                    style = PandoraTypography.labelMedium,
                                    color = IrisPrimary,
                                    fontWeight = FontWeight.Bold
                                )

                                Row(
                                    modifier = Modifier
                                        .clip(ButtonShape)
                                        .background(PorcelainContainer)
                                        .clickable {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.sourceUrl))
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Cannot open URL", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.OpenInNew,
                                        contentDescription = "Open",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "Open Link",
                                        style = PandoraTypography.labelSmall,
                                        color = TextPrimary
                                    )
                                }
                            }

                            if (item.fullContent.isNotBlank()) {
                                Text(
                                    text = item.fullContent,
                                    style = PandoraTypography.bodyMedium,
                                    color = TextPrimary,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    } else if (item.itemType == ItemType.DOCUMENT) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(1.dp, CardShape)
                                .clip(CardShape)
                                .background(PorcelainSheetWhite)
                                .border(1.dp, OutlineHairline, CardShape)
                                .padding(Spacing.Medium),
                            verticalArrangement = Arrangement.spacedBy(Spacing.MediumSmall)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
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
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(CeruleanFixed),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Description,
                                            contentDescription = null,
                                            tint = Color(0xFF0284C7),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = item.title,
                                            style = PandoraTypography.headlineSmall,
                                            fontSize = 15.sp,
                                            color = TextPrimary
                                        )
                                        val sizeKb = item.fileSizeBytes / 1024
                                        val sizeText = if (sizeKb > 1024) "${sizeKb / 1024} MB" else if (sizeKb > 0) "$sizeKb KB" else "PDF Document"
                                        Text(
                                            text = "$sizeText • Offline Vault",
                                            style = PandoraTypography.bodySmall,
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                if (hasValidFile) {
                                    Button(
                                        onClick = {
                                            try {
                                                val file = File(item.localFilePath!!)
                                                val uri = androidx.core.content.FileProvider.getUriForFile(
                                                    context,
                                                    "${context.packageName}.fileprovider",
                                                    file
                                                )
                                                val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                                                    setDataAndType(uri, "application/pdf")
                                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                }
                                                context.startActivity(Intent.createChooser(viewIntent, "Open Document"))
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Cannot open document viewer", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        shape = ButtonShape,
                                        colors = ButtonDefaults.buttonColors(containerColor = IrisPrimary)
                                    ) {
                                        Text("Open File", style = PandoraTypography.labelSmall)
                                    }
                                }
                            }

                            if (item.fullContent.isNotBlank() && item.fullContent != item.title) {
                                Text(
                                    text = item.fullContent,
                                    style = PandoraTypography.bodyMedium,
                                    color = TextPrimary,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(1.dp, CardShape)
                                .clip(CardShape)
                                .background(PorcelainContainer)
                                .border(1.dp, OutlineHairline, CardShape)
                                .padding(Spacing.Medium)
                        ) {
                            Text(
                                text = item.fullContent.ifBlank { item.excerpt.ifBlank { item.title } },
                                style = PandoraTypography.bodyMedium,
                                color = TextPrimary
                            )
                        }
                    }
                }

                // Dynamic Organization Hub (Folders + Tags)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, CardShape)
                            .clip(CardShape)
                            .background(PorcelainSheetWhite)
                            .border(1.dp, OutlineHairline, CardShape)
                            .padding(Spacing.Medium),
                        verticalArrangement = Arrangement.spacedBy(Spacing.MediumSmall)
                    ) {
                        // Folders Row
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "FOLDERS",
                                    style = PandoraTypography.labelSmall,
                                    color = TextSecondary,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "+ Add Folder",
                                    style = PandoraTypography.labelSmall,
                                    color = IrisPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { showFolderDialog = true }
                                )
                            }

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(Spacing.Small),
                                verticalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val folders = itemWithRelations.folders
                                if (folders.isEmpty()) {
                                    Row(
                                        modifier = Modifier
                                            .clip(TagChipShape)
                                            .background(PorcelainContainerHigh)
                                            .padding(horizontal = 8.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FolderOpen,
                                            contentDescription = null,
                                            tint = IrisPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "Inbox / Vault",
                                            style = PandoraTypography.labelMedium,
                                            color = TextPrimary
                                        )
                                    }
                                } else {
                                    folders.forEach { folder ->
                                        Row(
                                            modifier = Modifier
                                                .clip(TagChipShape)
                                                .background(PorcelainContainerHigh)
                                                .padding(horizontal = 8.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.FolderOpen,
                                                contentDescription = null,
                                                tint = IrisPrimary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = folder.name,
                                                style = PandoraTypography.labelMedium,
                                                color = TextPrimary
                                            )
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove",
                                                tint = TextTertiary,
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clickable { viewModel.removeFolder(folder.id) }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Tags Row
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "SEMANTIC TAGS",
                                    style = PandoraTypography.labelSmall,
                                    color = TextSecondary,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "+ Add Tag",
                                    style = PandoraTypography.labelSmall,
                                    color = IrisPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { showTagsDialog = true }
                                )
                            }

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall),
                                verticalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val tags = itemWithRelations.tags
                                if (tags.isEmpty()) {
                                    Text(
                                        text = "#vault",
                                        style = PandoraTypography.labelSmall,
                                        color = TextSecondary,
                                        modifier = Modifier
                                            .clip(TagChipShape)
                                            .background(IrisFixed)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                } else {
                                    tags.forEach { tag ->
                                        Row(
                                            modifier = Modifier
                                                .clip(TagChipShape)
                                                .background(IrisFixed)
                                                .padding(horizontal = 8.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "#${tag.name}",
                                                style = PandoraTypography.labelSmall,
                                                color = TextPrimary
                                            )
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove",
                                                tint = TextTertiary,
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .clickable { viewModel.removeTag(tag.id) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Related Items (if any exist in database)
                if (state.relatedItems.isNotEmpty()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.Small)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Other Items in Vault",
                                    style = PandoraTypography.headlineSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "${state.relatedItems.size} Items",
                                    style = PandoraTypography.labelSmall,
                                    color = IrisPrimary
                                )
                            }

                            state.relatedItems.forEach { related ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(1.dp, CardShape)
                                        .clip(CardShape)
                                        .background(PorcelainSheetWhite)
                                        .border(1.dp, OutlineHairline, CardShape)
                                        .clickable { onRelatedItemClick(related.item.id) }
                                        .padding(Spacing.Small),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(Spacing.Small),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(BadgeShape)
                                                .background(PorcelainContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            val icon = when (related.item.itemType) {
                                                ItemType.IMAGE -> Icons.Default.Edit
                                                ItemType.ARTICLE -> Icons.Default.Description
                                                else -> Icons.Default.FolderOpen
                                            }
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = IrisPrimary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Column {
                                            Text(
                                                text = related.item.title,
                                                style = PandoraTypography.headlineSmall,
                                                fontSize = 14.sp,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = related.item.excerpt.ifBlank { "Vault item" },
                                                style = PandoraTypography.bodySmall,
                                                color = TextSecondary,
                                                maxLines = 1
                                            )
                                        }
                                    }

                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = TextTertiary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 1. Edit Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Artifact", style = PandoraTypography.headlineSmall) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.Small)) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editContent,
                        onValueChange = { editContent = it },
                        label = { Text("Note Content / Text") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateTitleAndContent(editTitle, editContent)
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IrisPrimary)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 2. Move / Assign Folder Dialog
    if (showFolderDialog) {
        AlertDialog(
            onDismissRequest = { showFolderDialog = false },
            title = { Text("Assign to Folder", style = PandoraTypography.headlineSmall) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.Small)) {
                    Text("Choose an existing folder or create a new one:", style = PandoraTypography.bodySmall, color = TextSecondary)

                    state.availableFolders.forEach { folder ->
                        val isAssigned = itemWithRelations?.folders?.any { it.id == folder.id } == true
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isAssigned) IrisFixed else PorcelainContainer)
                                .clickable {
                                    if (isAssigned) {
                                        viewModel.removeFolder(folder.id)
                                    } else {
                                        viewModel.assignFolder(folder.id)
                                    }
                                }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(folder.name, style = PandoraTypography.bodyMedium, color = TextPrimary)
                            Text(if (isAssigned) "Assigned ✓" else "Tap to add", style = PandoraTypography.labelSmall, color = IrisPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = newFolderName,
                        onValueChange = { newFolderName = it },
                        label = { Text("Create new folder") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newFolderName.isNotBlank()) {
                            viewModel.createAndAssignFolder(newFolderName)
                            newFolderName = ""
                        }
                        showFolderDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IrisPrimary)
                ) {
                    Text("Done")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFolderDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // 3. Manage Tags Dialog
    if (showTagsDialog) {
        AlertDialog(
            onDismissRequest = { showTagsDialog = false },
            title = { Text("Manage Tags", style = PandoraTypography.headlineSmall) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.Small)) {
                    Text("Add semantic tags to organize this artifact:", style = PandoraTypography.bodySmall, color = TextSecondary)

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        state.availableTags.forEach { tag ->
                            val isAssigned = itemWithRelations?.tags?.any { it.id == tag.id } == true
                            Row(
                                modifier = Modifier
                                    .clip(TagChipShape)
                                    .background(if (isAssigned) IrisFixed else PorcelainContainer)
                                    .clickable {
                                        if (isAssigned) {
                                            viewModel.removeTag(tag.id)
                                        } else {
                                            viewModel.assignTag(tag.id)
                                        }
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("#${tag.name}", style = PandoraTypography.labelSmall, color = TextPrimary)
                                if (isAssigned) {
                                    Text("✓", color = IrisPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = newTagName,
                        onValueChange = { newTagName = it },
                        label = { Text("New tag name (e.g. #finance)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTagName.isNotBlank()) {
                            viewModel.createAndAssignTag(newTagName)
                            newTagName = ""
                        }
                        showTagsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IrisPrimary)
                ) {
                    Text("Done")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTagsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // 4. Delete Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete Artifact?", style = PandoraTypography.headlineSmall, color = Color(0xFFBA1A1A)) },
            text = {
                Text("This artifact will be permanently removed from your private vault and local storage.", style = PandoraTypography.bodyMedium)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        viewModel.deleteItem {
                            Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
                            onBackClick()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA1A1A))
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
