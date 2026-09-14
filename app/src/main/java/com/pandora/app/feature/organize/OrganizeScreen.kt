package com.pandora.app.feature.organize

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesomeMotion
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandora.app.core.designsystem.theme.BadgeShape
import com.pandora.app.core.designsystem.theme.ButtonShape
import com.pandora.app.core.designsystem.theme.CardShape
import com.pandora.app.core.designsystem.theme.IrisPrimary
import com.pandora.app.core.designsystem.theme.PandoraTypography
import com.pandora.app.core.designsystem.theme.PorcelainCanvas
import com.pandora.app.core.designsystem.theme.PorcelainContainer
import com.pandora.app.core.designsystem.theme.PorcelainContainerHighest
import com.pandora.app.core.designsystem.theme.PorcelainSheetWhite
import com.pandora.app.core.designsystem.theme.Spacing
import com.pandora.app.core.designsystem.theme.TextOutline
import com.pandora.app.core.designsystem.theme.TextPrimary
import com.pandora.app.core.designsystem.theme.TextSecondary
import com.pandora.app.feature.organize.component.CollectionsSpotlightCarousel
import com.pandora.app.feature.organize.component.FolderAccordionItem
import com.pandora.app.feature.organize.component.TagCloudSection

@Composable
fun OrganizeScreen(
    viewModel: OrganizeViewModel,
    onFolderClick: (Long) -> Unit,
    onCollectionClick: (Long) -> Unit,
    onTagClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var showNewFolderDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var showNewCollectionDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var newFolderName by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    var newCollectionName by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    var newCollectionDesc by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PorcelainCanvas)
    ) {
        // 1. Segmented Navigation Tab Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.Medium, vertical = Spacing.Small)
                .clip(ButtonShape)
                .background(PorcelainContainer)
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OrganizeTab.entries.forEach { tab ->
                val isSelected = state.activeTab == tab
                val icon = when (tab) {
                    OrganizeTab.FOLDERS -> Icons.Default.Folder
                    OrganizeTab.COLLECTIONS -> Icons.Default.AutoAwesomeMotion
                    OrganizeTab.TAGS -> Icons.Default.Tag
                }
                val badgeCount = when (tab) {
                    OrganizeTab.FOLDERS -> state.folders.size.toString()
                    OrganizeTab.COLLECTIONS -> state.collections.size.toString()
                    OrganizeTab.TAGS -> state.tags.size.toString()
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(BadgeShape)
                        .background(if (isSelected) PorcelainSheetWhite else Color.Transparent)
                        .clickable { viewModel.setActiveTab(tab) }
                        .padding(vertical = 6.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = tab.label,
                        tint = if (isSelected) IrisPrimary else TextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.size(Spacing.ExtraSmall))
                    Text(
                        text = tab.label,
                        style = PandoraTypography.labelMedium,
                        color = if (isSelected) IrisPrimary else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.size(Spacing.ExtraSmall))
                    Text(
                        text = badgeCount,
                        style = PandoraTypography.labelSmall,
                        color = TextSecondary,
                        fontSize = 9.sp,
                        modifier = Modifier
                            .clip(BadgeShape)
                            .background(PorcelainContainerHighest)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
        }

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = IrisPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = Spacing.Small, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
            ) {
                // Section: Curated Collections Spotlight
                if (state.activeTab == OrganizeTab.COLLECTIONS || state.activeTab == OrganizeTab.FOLDERS) {
                    item {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = Spacing.Medium),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
                                ) {
                                    Text(
                                        text = "Curated Collections",
                                        style = PandoraTypography.headlineSmall,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    )
                                    Text(
                                        text = "SPOTLIGHT",
                                        style = PandoraTypography.labelSmall,
                                        color = TextOutline,
                                        fontSize = 9.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(Spacing.Small))

                            CollectionsSpotlightCarousel(
                                collections = state.collections,
                                onCollectionClick = onCollectionClick,
                                onNewCollectionClick = { showNewCollectionDialog = true }
                            )
                        }
                    }
                }

                // Section: Library Folders Header & New Folder Action
                if (state.activeTab == OrganizeTab.FOLDERS) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Spacing.Medium),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Library Folders",
                                    style = PandoraTypography.headlineSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                                Text(
                                    text = "${state.folders.size} root directories",
                                    style = PandoraTypography.bodySmall,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .clip(ButtonShape)
                                    .background(IrisPrimary)
                                    .clickable { showNewFolderDialog = true }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CreateNewFolder,
                                    contentDescription = "New Folder",
                                    tint = PorcelainSheetWhite,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "New Folder",
                                    style = PandoraTypography.labelSmall,
                                    color = PorcelainSheetWhite,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Folder Accordion Items
                    items(state.folders) { folderWithSubfolders ->
                        Box(modifier = Modifier.padding(horizontal = Spacing.Medium)) {
                            FolderAccordionItem(
                                folderWithSubfolders = folderWithSubfolders,
                                isExpanded = state.expandedFolderIds.contains(folderWithSubfolders.folder.id),
                                onToggle = { viewModel.toggleFolderExpanded(folderWithSubfolders.folder.id) },
                                onFolderClick = onFolderClick
                            )
                        }
                    }
                }

                // Section: Recent Tags Cloud
                if (state.activeTab == OrganizeTab.TAGS || state.activeTab == OrganizeTab.FOLDERS) {
                    item {
                        Column(modifier = Modifier.padding(horizontal = Spacing.Medium)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Label,
                                        contentDescription = null,
                                        tint = IrisPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Recent Tags",
                                        style = PandoraTypography.headlineSmall,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    )
                                }
                                Text(
                                    text = "${state.tags.size} tags indexed",
                                    style = PandoraTypography.labelSmall,
                                    color = TextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(Spacing.Small))

                            TagCloudSection(
                                tags = state.tags,
                                onTagClick = onTagClick
                            )
                        }
                    }
                }
            }
        }
    }

    // New Folder Dialog
    if (showNewFolderDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showNewFolderDialog = false },
            title = { Text("Create New Folder", style = PandoraTypography.headlineSmall) },
            text = {
                androidx.compose.material3.OutlinedTextField(
                    value = newFolderName,
                    onValueChange = { newFolderName = it },
                    label = { Text("Folder Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        if (newFolderName.isNotBlank()) {
                            viewModel.createFolder(newFolderName.trim())
                            newFolderName = ""
                            showNewFolderDialog = false
                        }
                    }
                ) {
                    Text("Create", color = IrisPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showNewFolderDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = PorcelainSheetWhite,
            shape = CardShape
        )
    }

    // New Collection Dialog
    if (showNewCollectionDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showNewCollectionDialog = false },
            title = { Text("Create Curated Collection", style = PandoraTypography.headlineSmall) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    androidx.compose.material3.OutlinedTextField(
                        value = newCollectionName,
                        onValueChange = { newCollectionName = it },
                        label = { Text("Collection Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    androidx.compose.material3.OutlinedTextField(
                        value = newCollectionDesc,
                        onValueChange = { newCollectionDesc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        if (newCollectionName.isNotBlank()) {
                            viewModel.createCollection(newCollectionName.trim(), newCollectionDesc.trim())
                            newCollectionName = ""
                            newCollectionDesc = ""
                            showNewCollectionDialog = false
                        }
                    }
                ) {
                    Text("Create", color = IrisPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showNewCollectionDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = PorcelainSheetWhite,
            shape = CardShape
        )
    }
}
