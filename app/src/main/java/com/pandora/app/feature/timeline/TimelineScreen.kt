package com.pandora.app.feature.timeline

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AutoAwesome
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandora.app.core.database.dao.ItemWithRelations
import com.pandora.app.core.database.entity.ItemType
import com.pandora.app.core.designsystem.component.FilterPillChip
import com.pandora.app.core.designsystem.theme.CardShape
import com.pandora.app.core.designsystem.theme.IrisFixed
import com.pandora.app.core.designsystem.theme.IrisPrimary
import com.pandora.app.core.designsystem.theme.OutlineHairline
import com.pandora.app.core.designsystem.theme.PandoraTypography
import com.pandora.app.core.designsystem.theme.PorcelainCanvas
import com.pandora.app.core.designsystem.theme.PorcelainContainer
import com.pandora.app.core.designsystem.theme.PorcelainContainerHigh
import com.pandora.app.core.designsystem.theme.PorcelainContainerLow
import com.pandora.app.core.designsystem.theme.PorcelainSheetWhite
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
        // 1. Search Bar & Time Horizon Toggles (from screen1.png)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Search Box
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .clip(RoundedCornerShape(9999.dp))
                    .background(PorcelainContainerHigh)
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TextTertiary,
                    modifier = Modifier.size(18.dp)
                )

                Box(modifier = Modifier.weight(1f)) {
                    if (state.searchQuery.isEmpty()) {
                        Text(
                            text = "Search memories, links, captures",
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
                        modifier = Modifier.size(24.dp)
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
                    .clip(RoundedCornerShape(9999.dp))
                    .background(PorcelainContainerHigh)
                    .padding(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(if (state.timeHorizon == TimeHorizon.MONTH) PorcelainSheetWhite else Color.Transparent)
                        .clickable { viewModel.setTimeHorizon(TimeHorizon.MONTH) }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "Month",
                        style = PandoraTypography.labelSmall,
                        color = if (state.timeHorizon == TimeHorizon.MONTH) IrisPrimary else TextSecondary,
                        fontWeight = if (state.timeHorizon == TimeHorizon.MONTH) FontWeight.Bold else FontWeight.Normal
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(if (state.timeHorizon == TimeHorizon.DAY) PorcelainSheetWhite else Color.Transparent)
                        .clickable { viewModel.setTimeHorizon(TimeHorizon.DAY) }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "Day",
                        style = PandoraTypography.labelSmall,
                        color = if (state.timeHorizon == TimeHorizon.DAY) IrisPrimary else TextSecondary,
                        fontWeight = if (state.timeHorizon == TimeHorizon.DAY) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // 2. Horizontal Filter Carousel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section: Today
                item {
                    TimelineSectionHeader(
                        title = "Today",
                        subtitle = "Oct 24",
                        itemCount = "4 items"
                    )
                }

                // Bento Hero Diagram
                val heroItem = state.items.find { it.item.itemType == ItemType.IMAGE }
                if (heroItem != null) {
                    item {
                        HeroDiagramCard(
                            itemWithRelations = heroItem,
                            onClick = { onItemClick(heroItem.item.id) },
                            onBookmarkClick = { viewModel.toggleFavorite(heroItem) },
                            onInspectClick = { onItemClick(heroItem.item.id) }
                        )
                    }
                }

                // Bento 2-Column Row: Article + Note
                val articleItem = state.items.find { it.item.itemType == ItemType.ARTICLE }
                val noteItem = state.items.find { it.item.itemType == ItemType.NOTE && it.item.title == "Quiet Architecture" }
                if (articleItem != null || noteItem != null) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Max),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (articleItem != null) {
                                ArticleTile(
                                    itemWithRelations = articleItem,
                                    onClick = { onItemClick(articleItem.item.id) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (noteItem != null) {
                                NoteTile(
                                    itemWithRelations = noteItem,
                                    onClick = { onItemClick(noteItem.item.id) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Section: Yesterday
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    TimelineSectionHeader(
                        title = "Yesterday",
                        subtitle = "Oct 23",
                        itemCount = "3 items"
                    )
                }

                // Full-width Thought Reflection
                val thoughtItem = state.items.find { it.item.title.contains("Cognitive Load") }
                if (thoughtItem != null) {
                    item {
                        FullWidthThoughtCard(
                            itemWithRelations = thoughtItem,
                            onClick = { onItemClick(thoughtItem.item.id) }
                        )
                    }
                }

                // Bento 2-Column Row: PDF + Voice Memo
                val pdfItem = state.items.find { it.item.itemType == ItemType.DOCUMENT }
                val voiceItem = state.items.find { it.item.itemType == ItemType.VOICE }
                if (pdfItem != null || voiceItem != null) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Max),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (pdfItem != null) {
                                PdfTile(
                                    itemWithRelations = pdfItem,
                                    onClick = { onItemClick(pdfItem.item.id) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (voiceItem != null) {
                                VoiceMemoTile(
                                    itemWithRelations = voiceItem,
                                    onClick = { onItemClick(voiceItem.item.id) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Section: Last Week Summarized Stacks
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    TimelineSectionHeader(
                        title = "Last Week",
                        subtitle = "Oct 17 - 21",
                        itemCount = "12 items"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ArchiveSummaryCard(
                            icon = Icons.Default.Image,
                            label = "8 Snaps",
                            modifier = Modifier.weight(1f)
                        )
                        ArchiveSummaryCard(
                            icon = Icons.Default.Link,
                            label = "3 Links",
                            modifier = Modifier.weight(1f)
                        )
                        ArchiveSummaryCard(
                            icon = Icons.Default.Archive,
                            label = "Archive",
                            modifier = Modifier.weight(1f)
                        )
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
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = PandoraTypography.headlineMedium,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                style = PandoraTypography.bodyMedium,
                color = TextTertiary
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(9999.dp))
                .background(PorcelainContainer)
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = itemCount,
                style = PandoraTypography.labelSmall,
                color = TextSecondary,
                fontSize = 11.sp
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
            .clip(RoundedCornerShape(16.dp))
            .background(PorcelainContainerLow)
            .border(1.dp, OutlineHairline, RoundedCornerShape(16.dp))
            .clickable { }
            .padding(vertical = 16.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = IrisPrimary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = PandoraTypography.labelSmall,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
    }
}
