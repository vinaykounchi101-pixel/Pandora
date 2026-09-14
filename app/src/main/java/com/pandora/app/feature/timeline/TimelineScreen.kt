package com.pandora.app.feature.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.pandora.app.core.database.entity.ItemType
import com.pandora.app.core.designsystem.component.FilterPillChip
import com.pandora.app.core.designsystem.theme.BadgeShape
import com.pandora.app.core.designsystem.theme.ButtonShape
import com.pandora.app.core.designsystem.theme.CardShape
import com.pandora.app.core.designsystem.theme.IrisPrimary
import com.pandora.app.core.designsystem.theme.OutlineHairline
import com.pandora.app.core.designsystem.theme.PandoraTypography
import com.pandora.app.core.designsystem.theme.PorcelainCanvas
import com.pandora.app.core.designsystem.theme.PorcelainContainer
import com.pandora.app.core.designsystem.theme.PorcelainContainerHigh
import com.pandora.app.core.designsystem.theme.PorcelainContainerLow
import com.pandora.app.core.designsystem.theme.PorcelainSheetWhite
import com.pandora.app.core.designsystem.theme.SegmentedPillShape
import com.pandora.app.core.designsystem.theme.Spacing
import com.pandora.app.core.designsystem.theme.TextPrimary
import com.pandora.app.core.designsystem.theme.TextSecondary
import com.pandora.app.core.designsystem.theme.TextTertiary
import com.pandora.app.feature.timeline.component.ArticleTile
import com.pandora.app.feature.timeline.component.FullWidthThoughtCard
import com.pandora.app.feature.timeline.component.HeroDiagramCard
import com.pandora.app.feature.timeline.component.NoteTile
import com.pandora.app.feature.timeline.component.PdfTile
import com.pandora.app.feature.timeline.component.VoiceMemoTile

@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel,
    onItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PorcelainCanvas)
    ) {
        // 1. Search Bar & Time Horizon Toggles
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.Medium, vertical = Spacing.Small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
        ) {
            // Search Box
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(ButtonShape)
                    .background(PorcelainContainerHigh)
                    .padding(horizontal = Spacing.MediumSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TextTertiary,
                    modifier = Modifier.size(16.dp)
                )

                Box(modifier = Modifier.weight(1f)) {
                    if (state.searchQuery.isEmpty()) {
                        Text(
                            text = "Search memories, links, notes...",
                            style = PandoraTypography.bodyMedium,
                            color = TextTertiary,
                            fontSize = 13.sp
                        )
                    }
                    BasicTextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        textStyle = PandoraTypography.bodyMedium.copy(color = TextPrimary, fontSize = 13.sp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (state.searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.setSearchQuery("") },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Month / Day Toggle Capsule
            Row(
                modifier = Modifier
                    .clip(ButtonShape)
                    .background(PorcelainContainerHigh)
                    .padding(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(BadgeShape)
                        .background(if (state.timeHorizon == TimeHorizon.MONTH) PorcelainSheetWhite else Color.Transparent)
                        .clickable { viewModel.setTimeHorizon(TimeHorizon.MONTH) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Month",
                        style = PandoraTypography.labelSmall,
                        color = if (state.timeHorizon == TimeHorizon.MONTH) IrisPrimary else TextSecondary,
                        fontWeight = if (state.timeHorizon == TimeHorizon.MONTH) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(BadgeShape)
                        .background(if (state.timeHorizon == TimeHorizon.DAY) PorcelainSheetWhite else Color.Transparent)
                        .clickable { viewModel.setTimeHorizon(TimeHorizon.DAY) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Day",
                        style = PandoraTypography.labelSmall,
                        color = if (state.timeHorizon == TimeHorizon.DAY) IrisPrimary else TextSecondary,
                        fontWeight = if (state.timeHorizon == TimeHorizon.DAY) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // 2. Horizontal Filter Carousel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = Spacing.Medium, vertical = Spacing.ExtraSmall),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
        ) {
            TimelineFilter.entries.forEach { filter ->
                FilterPillChip(
                    label = filter.label,
                    isSelected = state.activeFilter == filter,
                    onClick = { viewModel.setFilter(filter) }
                )
            }
        }

        // 3. Bento Timeline Feed
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
                contentPadding = PaddingValues(start = Spacing.Medium, end = Spacing.Medium, top = Spacing.Small, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
            ) {
                // Dynamic Items Feed
                if (state.items.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(Spacing.Small)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = TextTertiary,
                                    modifier = Modifier.size(40.dp)
                                )
                                Text(
                                    text = if (state.searchQuery.isNotEmpty()) "No memories matching \"${state.searchQuery}\"" else "Your vault is empty",
                                    style = PandoraTypography.headlineSmall,
                                    color = TextSecondary,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Capture a note, link, or photo using the floating bar below",
                                    style = PandoraTypography.bodySmall,
                                    color = TextTertiary
                                )
                            }
                        }
                    }
                } else {
                    // Section: Recent Captures
                    item {
                        TimelineSectionHeader(
                            title = if (state.timeHorizon == TimeHorizon.MONTH) "This Month" else "Today",
                            subtitle = if (state.activeFilter == TimelineFilter.ALL) "Chronological Feed" else state.activeFilter.label,
                            itemCount = "${state.items.size} items"
                        )
                    }

                    // Dynamically Render Items
                    val items = state.items
                    var i = 0
                    while (i < items.size) {
                        val current = items[i]

                        if (current.item.itemType == ItemType.IMAGE) {
                            item(key = "item_${current.item.id}") {
                                HeroDiagramCard(
                                    itemWithRelations = current,
                                    onClick = { onItemClick(current.item.id) },
                                    onBookmarkClick = { viewModel.toggleFavorite(current) },
                                    onInspectClick = { onItemClick(current.item.id) }
                                )
                            }
                            i++
                        } else if (current.item.itemType == ItemType.NOTE && current.item.fullContent.length > 120) {
                            item(key = "item_${current.item.id}") {
                                FullWidthThoughtCard(
                                    itemWithRelations = current,
                                    onClick = { onItemClick(current.item.id) }
                                )
                            }
                            i++
                        } else if (i + 1 < items.size && items[i + 1].item.itemType != ItemType.IMAGE && !(items[i + 1].item.itemType == ItemType.NOTE && items[i + 1].item.fullContent.length > 120)) {
                            val next = items[i + 1]
                            item(key = "pair_${current.item.id}_${next.item.id}") {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(IntrinsicSize.Max),
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.MediumSmall)
                                ) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        DynamicItemTile(
                                            itemWithRelations = current,
                                            onClick = { onItemClick(current.item.id) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fillMaxHeight()
                                        )
                                    }
                                    Box(modifier = Modifier.weight(1f)) {
                                        DynamicItemTile(
                                            itemWithRelations = next,
                                            onClick = { onItemClick(next.item.id) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fillMaxHeight()
                                        )
                                    }
                                }
                            }
                            i += 2
                        } else {
                            item(key = "single_${current.item.id}") {
                                DynamicItemTile(
                                    itemWithRelations = current,
                                    onClick = { onItemClick(current.item.id) }
                                )
                            }
                            i++
                        }
                    }

                    // Section: Vault Stacks Overview
                    item {
                        Spacer(modifier = Modifier.height(Spacing.Small))
                        TimelineSectionHeader(
                            title = "Vault Overview",
                            subtitle = "Categorized Stacks",
                            itemCount = "${state.totalCount.coerceAtLeast(state.items.size)} total"
                        )
                        Spacer(modifier = Modifier.height(Spacing.Small))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
                        ) {
                            val noteCount = state.items.count { it.item.itemType == ItemType.NOTE }
                            val articleCount = state.items.count { it.item.itemType == ItemType.ARTICLE }
                            val imageCount = state.items.count { it.item.itemType == ItemType.IMAGE }

                            ArchiveSummaryCard(
                                icon = Icons.Default.Image,
                                label = "$imageCount Snaps",
                                modifier = Modifier.weight(1f)
                            )
                            ArchiveSummaryCard(
                                icon = Icons.Default.Link,
                                label = "$articleCount Links",
                                modifier = Modifier.weight(1f)
                            )
                            ArchiveSummaryCard(
                                icon = Icons.Default.Archive,
                                label = "$noteCount Notes",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineSectionHeader(
    title: String,
    subtitle: String,
    itemCount: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
        ) {
            Text(
                text = title,
                style = PandoraTypography.headlineMedium,
                fontSize = 19.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                style = PandoraTypography.bodySmall,
                color = TextTertiary
            )
        }

        Box(
            modifier = Modifier
                .clip(BadgeShape)
                .background(PorcelainContainer)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = itemCount,
                style = PandoraTypography.labelSmall,
                color = TextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun ArchiveSummaryCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(CardShape)
            .background(PorcelainContainerLow)
            .border(1.dp, OutlineHairline, CardShape)
            .clickable { }
            .padding(vertical = Spacing.Medium, horizontal = Spacing.Small),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = IrisPrimary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(Spacing.ExtraSmall))
        Text(
            text = label,
            style = PandoraTypography.labelSmall,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun DynamicItemTile(
    itemWithRelations: com.pandora.app.core.database.dao.ItemWithRelations,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (itemWithRelations.item.itemType) {
        ItemType.ARTICLE -> ArticleTile(itemWithRelations = itemWithRelations, onClick = onClick, modifier = modifier)
        ItemType.NOTE -> NoteTile(itemWithRelations = itemWithRelations, onClick = onClick, modifier = modifier)
        ItemType.DOCUMENT -> PdfTile(itemWithRelations = itemWithRelations, onClick = onClick, modifier = modifier)
        ItemType.VOICE -> VoiceMemoTile(itemWithRelations = itemWithRelations, onClick = onClick, modifier = modifier)
        else -> NoteTile(itemWithRelations = itemWithRelations, onClick = onClick, modifier = modifier)
    }
}

