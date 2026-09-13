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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandora.app.core.designsystem.theme.IrisPrimary
import com.pandora.app.core.designsystem.theme.PandoraTypography
import com.pandora.app.core.designsystem.theme.PorcelainCanvas
import com.pandora.app.core.designsystem.theme.PorcelainContainer
import com.pandora.app.core.designsystem.theme.PorcelainContainerHighest
import com.pandora.app.core.designsystem.theme.PorcelainSheetWhite
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
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PorcelainCanvas)
    ) {
        // 1. Segmented Navigation Pill Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(9999.dp))
                .background(PorcelainContainer)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
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
                    OrganizeTab.COLLECTIONS -> "4"
                    OrganizeTab.TAGS -> "32"
                    else -> null
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(9999.dp))
                        .background(if (isSelected) PorcelainSheetWhite else Color.Transparent)
                        .clickable { viewModel.setActiveTab(tab) }
                        .padding(vertical = 8.dp, horizontal = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = tab.label,
                        tint = if (isSelected) IrisPrimary else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = tab.label,
                        style = PandoraTypography.labelMedium,
                        color = if (isSelected) IrisPrimary else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                    if (badgeCount != null) {
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = badgeCount,
                            style = PandoraTypography.labelSmall,
                            color = TextSecondary,
                            fontSize = 9.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(9999.dp))
                                .background(PorcelainContainerHighest)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
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
                contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section: Curated Collections Spotlight
                item {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Curated Collections",
                                    style = PandoraTypography.headlineSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "SPOTLIGHT",
                                    style = PandoraTypography.labelSmall,
                                    color = TextOutline,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        CollectionsSpotlightCarousel(
                            collections = state.collections,
                            onCollectionClick = onCollectionClick,
                            onNewCollectionClick = { }
                        )
                    }
                }

                // Section: Library Folders Header & New Folder Action
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Library Folders",
                                style = PandoraTypography.headlineSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${state.folders.size} root directories · 154 total items",
                                style = PandoraTypography.bodySmall,
                                color = TextSecondary
                            )
                        }

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(9999.dp))
                                .background(IrisPrimary)
                                .clickable { }
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreateNewFolder,
                                contentDescription = "New Folder",
                                tint = PorcelainSheetWhite,
                                modifier = Modifier.size(16.dp)
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
                    Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                        FolderAccordionItem(
                            folderWithSubfolders = folderWithSubfolders,
                            isExpanded = state.expandedFolderIds.contains(folderWithSubfolders.folder.id),
                            onToggle = { viewModel.toggleFolderExpanded(folderWithSubfolders.folder.id) },
                            onFolderClick = onFolderClick
                        )
                    }
                }

                // Section: Recent Tags Cloud
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
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
                                    imageVector = Icons.Default.Label,
                                    contentDescription = null,
                                    tint = IrisPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Recent Tags",
                                    style = PandoraTypography.headlineSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "${state.tags.size.coerceAtLeast(32)} tags indexed",
                                style = PandoraTypography.labelSmall,
                                color = TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        TagCloudSection(
                            tags = state.tags,
                            onTagClick = { }
                        )
                    }
                }
            }
        }
    }
}
