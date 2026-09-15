package com.pandora.app.feature.ai

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandora.app.core.database.dao.CollectionWithItems
import com.pandora.app.core.database.dao.FolderWithSubfoldersAndItems
import com.pandora.app.core.database.dao.ItemWithRelations
import com.pandora.app.core.database.entity.ItemType
import com.pandora.app.core.designsystem.theme.ApricotFixed
import com.pandora.app.core.designsystem.theme.ApricotOrange
import com.pandora.app.core.designsystem.theme.BadgeShape
import com.pandora.app.core.designsystem.theme.ButtonShape
import com.pandora.app.core.designsystem.theme.CardShape
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
import com.pandora.app.core.designsystem.theme.PorcelainContainerLow
import com.pandora.app.core.designsystem.theme.PorcelainSheetWhite
import com.pandora.app.core.designsystem.theme.Spacing
import com.pandora.app.core.designsystem.theme.TagChipShape
import com.pandora.app.core.designsystem.theme.TextPrimary
import com.pandora.app.core.designsystem.theme.TextSecondary
import com.pandora.app.core.designsystem.theme.TextTertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PandoraAiScreen(
    viewModel: PandoraAiViewModel,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var inputQuery by remember { mutableStateOf("") }
    var showLinkDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.importAndAnalyzeMedia(uri)
        }
    }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val name = uri.lastPathSegment ?: "Document.pdf"
            viewModel.importAndAnalyzeDocument(uri, name)
        }
    }

    if (showLinkDialog) {
        PasteLinkDialog(
            onDismiss = { showLinkDialog = false },
            onSubmit = { url, title ->
                showLinkDialog = false
                viewModel.importAndAnalyzeLink(url, title)
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PorcelainCanvas)
            .imePadding()
    ) {
        if (state.selectedTarget == null) {
            // ==========================================
            // PHASE 1: CHOOSE TARGET (ARTIFACT / FOLDER / COLLECTION)
            // ==========================================
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = Spacing.Medium, vertical = Spacing.Small),
                verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
            ) {
                // Header Banner
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CardShape)
                            .background(PorcelainSheetWhite)
                            .border(1.dp, OutlineHairline, CardShape)
                            .padding(Spacing.Medium),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(IrisFixed),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = IrisPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Pandora AI Copilot",
                                    style = PandoraTypography.headlineSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Select an artifact, folder, or collection to summarize & chat",
                                    style = PandoraTypography.bodySmall,
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        if (!state.hasApiKey) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PorcelainContainerHigh)
                                    .clickable { onNavigateToSettings() }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Key, contentDescription = null, tint = ApricotOrange, modifier = Modifier.size(14.dp))
                                    Text("Set Gemini API Key for deep reasoning", style = PandoraTypography.labelSmall, color = TextPrimary)
                                }
                                Text("Settings →", style = PandoraTypography.labelSmall, color = IrisPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Scope Segmented Tabs (Artifacts | Folders | Collections)
                item {
                    val scopeTabs = listOf(
                        AiScopeTab.ARTIFACTS to "Artifacts",
                        AiScopeTab.FOLDERS to "Folders",
                        AiScopeTab.COLLECTIONS to "Collections"
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(PorcelainContainerHigh)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        scopeTabs.forEach { (tab, label) ->
                            val isSelected = state.activeScopeTab == tab
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) PorcelainSheetWhite else Color.Transparent)
                                    .clickable { viewModel.setScopeTab(tab) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    val icon = when (tab) {
                                        AiScopeTab.ARTIFACTS -> Icons.Default.Description
                                        AiScopeTab.FOLDERS -> Icons.Default.Folder
                                        AiScopeTab.COLLECTIONS -> Icons.Default.CollectionsBookmark
                                    }
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isSelected) IrisPrimary else TextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = label,
                                        style = PandoraTypography.labelSmall,
                                        color = if (isSelected) TextPrimary else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // TAB 1: ARTIFACTS
                if (state.activeScopeTab == AiScopeTab.ARTIFACTS) {
                    // Fast Upload / Ingest Actions (Photo | PDF | Paste Link)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                shape = ButtonShape,
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PorcelainContainerHigh)
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Photo", color = TextPrimary, style = PandoraTypography.labelSmall, fontSize = 11.sp)
                            }

                            Button(
                                onClick = { documentPickerLauncher.launch("*/*") },
                                modifier = Modifier.weight(1f),
                                shape = ButtonShape,
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PorcelainContainerHigh)
                            ) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("PDF", color = TextPrimary, style = PandoraTypography.labelSmall, fontSize = 11.sp)
                            }

                            Button(
                                onClick = { showLinkDialog = true },
                                modifier = Modifier.weight(1.1f),
                                shape = ButtonShape,
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PorcelainContainerHigh)
                            ) {
                                Icon(Icons.Default.AddLink, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Paste Link", color = TextPrimary, style = PandoraTypography.labelSmall, fontSize = 11.sp)
                            }
                        }
                    }

                    // Type Filter Chips
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall)
                        ) {
                            val filters = listOf(
                                null to "All Items",
                                ItemType.NOTE to "Notes",
                                ItemType.IMAGE to "Photos",
                                ItemType.DOCUMENT to "PDFs",
                                ItemType.ARTICLE to "Links / Web",
                                ItemType.VOICE to "Voice"
                            )
                            items(filters) { (type, label) ->
                                val isSelected = state.activeTypeFilter == type
                                Box(
                                    modifier = Modifier
                                        .clip(TagChipShape)
                                        .background(if (isSelected) IrisPrimary else PorcelainContainer)
                                        .clickable { viewModel.setTypeFilter(type) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = label,
                                        style = PandoraTypography.labelSmall,
                                        color = if (isSelected) PorcelainSheetWhite else TextPrimary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    // List of Vault Items to choose from
                    if (state.items.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Psychology,
                                        contentDescription = null,
                                        tint = TextTertiary,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Text("No matching artifacts in vault", style = PandoraTypography.bodyMedium, color = TextSecondary)
                                    Text("Save notes, PDFs, links, or photos using Quick Add (+) first.", style = PandoraTypography.bodySmall, color = TextTertiary)
                                }
                            }
                        }
                    } else {
                        items(state.items) { itemWithRelations ->
                            ArtifactSelectionCard(
                                itemWithRelations = itemWithRelations,
                                onSelect = { viewModel.selectItem(itemWithRelations) }
                            )
                        }
                    }
                }

                // TAB 2: FOLDERS
                if (state.activeScopeTab == AiScopeTab.FOLDERS) {
                    if (state.folders.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.FolderOpen, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(40.dp))
                                    Text("No folders created yet", style = PandoraTypography.bodyMedium, color = TextSecondary)
                                    Text("Create folders in Organize to cluster related documents.", style = PandoraTypography.bodySmall, color = TextTertiary)
                                }
                            }
                        }
                    } else {
                        items(state.folders) { folderWithItems ->
                            FolderSelectionCard(
                                folderWithItems = folderWithItems,
                                onSelect = { viewModel.selectFolder(folderWithItems) }
                            )
                        }
                    }
                }

                // TAB 3: COLLECTIONS
                if (state.activeScopeTab == AiScopeTab.COLLECTIONS) {
                    if (state.collections.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.CollectionsBookmark, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(40.dp))
                                    Text("No collections found", style = PandoraTypography.bodyMedium, color = TextSecondary)
                                    Text("Create collections in Organize to curate topic-based bundles.", style = PandoraTypography.bodySmall, color = TextTertiary)
                                }
                            }
                        }
                    } else {
                        items(state.collections) { collectionWithItems ->
                            CollectionSelectionCard(
                                collectionWithItems = collectionWithItems,
                                onSelect = { viewModel.selectCollection(collectionWithItems) }
                            )
                        }
                    }
                }
            }
        } else {
            // ==========================================
            // PHASE 2: ACTIVE SUMMARY & INTERACTIVE Q&A
            // ==========================================
            val selected = state.selectedTarget!!

            Column(modifier = Modifier.fillMaxSize()) {
                // Top Grounding Context Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PorcelainSheetWhite)
                        .border(1.dp, OutlineHairline)
                        .padding(horizontal = Spacing.Medium, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        val icon = when (selected) {
                            is AiTarget.ItemTarget -> when (selected.itemWithRelations.item.itemType) {
                                ItemType.IMAGE -> Icons.Default.Image
                                ItemType.DOCUMENT -> Icons.Default.PictureAsPdf
                                ItemType.ARTICLE -> Icons.Default.Link
                                ItemType.VOICE -> Icons.Default.Mic
                                ItemType.NOTE -> Icons.Default.Edit
                            }
                            is AiTarget.FolderTarget -> Icons.Default.Folder
                            is AiTarget.CollectionTarget -> Icons.Default.CollectionsBookmark
                        }
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(IrisFixed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, contentDescription = null, tint = IrisPrimary, modifier = Modifier.size(15.dp))
                        }
                        Column {
                            Text(
                                text = selected.title,
                                style = PandoraTypography.headlineSmall,
                                fontSize = 14.sp,
                                color = TextPrimary,
                                maxLines = 1
                            )
                            Text(
                                text = "Active Grounding: ${selected.typeLabel}",
                                style = PandoraTypography.labelSmall,
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    TextButton(onClick = { viewModel.clearSelection() }) {
                        Text("Switch Target", color = IrisPrimary, style = PandoraTypography.labelSmall)
                    }
                }

                // Chat Messages & Summary Lazy Column
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = Spacing.Medium, vertical = Spacing.Small),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Executive Summary Box
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(1.dp, CardShape)
                                .clip(CardShape)
                                .background(PorcelainSheetWhite)
                                .border(1.dp, IrisPrimary.copy(alpha = 0.3f), CardShape)
                                .padding(Spacing.Medium),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = IrisPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Executive Summary",
                                        style = PandoraTypography.headlineSmall,
                                        fontSize = 15.sp,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (state.summary != null) {
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("Pandora AI Summary", state.summary))
                                            Toast.makeText(context, "Summary copied", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondary, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }

                            if (state.isGeneratingSummary) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.padding(vertical = 12.dp)
                                ) {
                                    CircularProgressIndicator(
                                        color = IrisPrimary,
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Text(
                                        text = "Pandora AI is analyzing scoped content...",
                                        style = PandoraTypography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            } else {
                                Text(
                                    text = state.summary ?: "No summary available for this target.",
                                    style = PandoraTypography.bodyMedium,
                                    color = TextPrimary,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }

                    // Suggested Prompts
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "SUGGESTED QUESTIONS",
                                style = PandoraTypography.labelSmall,
                                color = TextSecondary,
                                letterSpacing = 0.5.sp,
                                fontSize = 10.sp
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val prompts = when (selected) {
                                    is AiTarget.FolderTarget -> listOf(
                                        "Synthesize all items in this folder",
                                        "What are the main themes across these files?",
                                        "Generate action items from this folder",
                                        "Find connections between these items"
                                    )
                                    is AiTarget.CollectionTarget -> listOf(
                                        "Summarize this entire collection",
                                        "What is the key takeaway of this bundle?",
                                        "Create a structured outline from these items",
                                        "List the top highlights"
                                    )
                                    is AiTarget.ItemTarget -> listOf(
                                        "What are the main key takeaways?",
                                        "Generate 3 action items from this",
                                        "Explain this in simple terms",
                                        "Summarize in 2 bullets"
                                    )
                                }
                                prompts.forEach { prompt ->
                                    Box(
                                        modifier = Modifier
                                            .clip(TagChipShape)
                                            .background(PorcelainContainerHigh)
                                            .clickable { viewModel.sendMessage(prompt) }
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(prompt, style = PandoraTypography.labelSmall, color = TextPrimary)
                                    }
                                }
                            }
                        }
                    }

                    // Chat History Thread
                    items(state.chatMessages) { chatMsg ->
                        if (chatMsg.isUser) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .clip(RoundedCornerShape(12.dp, 12.dp, 2.dp, 12.dp))
                                        .background(IrisPrimary)
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = chatMsg.text,
                                        style = PandoraTypography.bodyMedium,
                                        color = PorcelainSheetWhite,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(0.92f)
                                        .clip(RoundedCornerShape(12.dp, 12.dp, 12.dp, 2.dp))
                                        .background(PorcelainSheetWhite)
                                        .border(1.dp, OutlineHairline, RoundedCornerShape(12.dp, 12.dp, 12.dp, 2.dp))
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = IrisPrimary,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = "Pandora AI",
                                            style = PandoraTypography.labelSmall,
                                            color = IrisPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = chatMsg.text,
                                        style = PandoraTypography.bodyMedium,
                                        color = TextPrimary,
                                        lineHeight = 22.sp
                                    )
                                }
                            }
                        }
                    }

                    // Thinking Bubble
                    if (state.isSendingMessage) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PorcelainContainerHigh)
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                CircularProgressIndicator(color = IrisPrimary, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                                Text("Pandora AI is thinking...", style = PandoraTypography.bodySmall, color = TextSecondary)
                            }
                        }
                    }
                }

                // Bottom Chat Input Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PorcelainSheetWhite)
                        .border(1.dp, OutlineHairline)
                        .padding(horizontal = Spacing.Medium, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputQuery,
                        onValueChange = { inputQuery = it },
                        placeholder = { Text("Ask a question about this ${selected.typeLabel.lowercase()}...") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IrisPrimary,
                            unfocusedBorderColor = PorcelainContainer
                        )
                    )

                    IconButton(
                        onClick = {
                            if (inputQuery.isNotBlank()) {
                                viewModel.sendMessage(inputQuery.trim())
                                inputQuery = ""
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (inputQuery.isNotBlank()) IrisPrimary else PorcelainContainerHigh)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (inputQuery.isNotBlank()) PorcelainSheetWhite else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ArtifactSelectionCard(
    itemWithRelations: ItemWithRelations,
    onSelect: () -> Unit
) {
    val item = itemWithRelations.item
    val dateStr = SimpleDateFormat("MMM dd • h:mm a", Locale.getDefault()).format(Date(item.createdAt))

    val (badgeBg, badgeText, badgeLabel, icon) = when (item.itemType) {
        ItemType.NOTE -> Quadruple(IrisFixed, IrisPrimary, "NOTE", Icons.Default.Edit)
        ItemType.IMAGE -> Quadruple(ApricotFixed, OnApricotFixedVariant, "PHOTO", Icons.Default.Image)
        ItemType.DOCUMENT -> Quadruple(CeruleanFixed, OnCeruleanFixed, "DOCUMENT", Icons.Default.PictureAsPdf)
        ItemType.ARTICLE -> Quadruple(CeruleanFixed, OnCeruleanFixed, "LINK", Icons.Default.Link)
        ItemType.VOICE -> Quadruple(ApricotFixed, OnApricotFixedVariant, "VOICE", Icons.Default.Mic)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, CardShape)
            .clip(CardShape)
            .background(PorcelainSheetWhite)
            .border(1.dp, OutlineHairline, CardShape)
            .clickable(onClick = onSelect)
            .padding(Spacing.Medium),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(BadgeShape)
                    .background(badgeBg)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(icon, contentDescription = null, tint = badgeText, modifier = Modifier.size(11.dp))
                Text(badgeLabel, style = PandoraTypography.labelSmall, color = badgeText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Text(dateStr, style = PandoraTypography.labelSmall, color = TextTertiary, fontSize = 10.sp)
        }

        Text(
            text = item.title,
            style = PandoraTypography.headlineSmall,
            fontSize = 16.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        val snippet = item.fullContent.ifBlank { item.excerpt }
        if (snippet.isNotBlank()) {
            Text(
                text = snippet,
                style = PandoraTypography.bodySmall,
                color = TextSecondary,
                maxLines = 2,
                lineHeight = 17.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(ButtonShape)
                    .background(IrisFixed)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = IrisPrimary, modifier = Modifier.size(13.dp))
                Text("Analyze & Ask AI", style = PandoraTypography.labelSmall, color = IrisPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FolderSelectionCard(
    folderWithItems: FolderWithSubfoldersAndItems,
    onSelect: () -> Unit
) {
    val folder = folderWithItems.folder
    val itemCount = folderWithItems.items.size

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, CardShape)
            .clip(CardShape)
            .background(PorcelainSheetWhite)
            .border(1.dp, OutlineHairline, CardShape)
            .clickable(onClick = onSelect)
            .padding(Spacing.Medium),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(BadgeShape)
                    .background(IrisFixed)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(Icons.Default.Folder, contentDescription = null, tint = IrisPrimary, modifier = Modifier.size(11.dp))
                Text("FOLDER", style = PandoraTypography.labelSmall, color = IrisPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Text("$itemCount artifacts inside", style = PandoraTypography.labelSmall, color = TextTertiary, fontSize = 10.sp)
        }

        Text(
            text = folder.name,
            style = PandoraTypography.headlineSmall,
            fontSize = 16.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        if (folderWithItems.items.isNotEmpty()) {
            val previewNames = folderWithItems.items.take(3).joinToString(", ") { it.title }
            Text(
                text = "Includes: $previewNames${if (itemCount > 3) " +${itemCount - 3} more" else ""}",
                style = PandoraTypography.bodySmall,
                color = TextSecondary,
                maxLines = 1,
                fontSize = 12.sp
            )
        } else {
            Text(
                text = "Empty folder • AI will analyze folder structure",
                style = PandoraTypography.bodySmall,
                color = TextTertiary,
                fontSize = 12.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(ButtonShape)
                    .background(IrisFixed)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = IrisPrimary, modifier = Modifier.size(13.dp))
                Text("Analyze Folder & Ask AI", style = PandoraTypography.labelSmall, color = IrisPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CollectionSelectionCard(
    collectionWithItems: CollectionWithItems,
    onSelect: () -> Unit
) {
    val collection = collectionWithItems.collection
    val itemCount = collectionWithItems.items.size

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, CardShape)
            .clip(CardShape)
            .background(PorcelainSheetWhite)
            .border(1.dp, OutlineHairline, CardShape)
            .clickable(onClick = onSelect)
            .padding(Spacing.Medium),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(BadgeShape)
                    .background(CeruleanFixed)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(Icons.Default.CollectionsBookmark, contentDescription = null, tint = OnCeruleanFixed, modifier = Modifier.size(11.dp))
                Text("COLLECTION", style = PandoraTypography.labelSmall, color = OnCeruleanFixed, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Text("$itemCount items", style = PandoraTypography.labelSmall, color = TextTertiary, fontSize = 10.sp)
        }

        Text(
            text = collection.name,
            style = PandoraTypography.headlineSmall,
            fontSize = 16.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        if (collection.description.isNotBlank()) {
            Text(
                text = collection.description,
                style = PandoraTypography.bodySmall,
                color = TextSecondary,
                maxLines = 2,
                lineHeight = 17.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(ButtonShape)
                    .background(IrisFixed)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = IrisPrimary, modifier = Modifier.size(13.dp))
                Text("Analyze Collection & Ask AI", style = PandoraTypography.labelSmall, color = IrisPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PasteLinkDialog(
    onDismiss: () -> Unit,
    onSubmit: (url: String, title: String) -> Unit
) {
    var urlText by remember { mutableStateOf("") }
    var titleText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.AddLink, contentDescription = null, tint = IrisPrimary)
                Text("Analyze Web Link / Article", style = PandoraTypography.headlineSmall, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Enter a web link or article URL. Pandora AI will store and ground questions against it.",
                    style = PandoraTypography.bodySmall,
                    color = TextSecondary
                )
                OutlinedTextField(
                    value = urlText,
                    onValueChange = { urlText = it },
                    label = { Text("URL (e.g. https://example.com/article)") },
                    placeholder = { Text("https://...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IrisPrimary,
                        unfocusedBorderColor = PorcelainContainer
                    )
                )
                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text("Title (Optional)") },
                    placeholder = { Text("Article Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IrisPrimary,
                        unfocusedBorderColor = PorcelainContainer
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (urlText.isNotBlank()) {
                        onSubmit(urlText.trim(), titleText.trim())
                    }
                },
                enabled = urlText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = IrisPrimary)
            ) {
                Text("Analyze Link", color = PorcelainSheetWhite)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = PorcelainSheetWhite,
        shape = CardShape
    )
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
