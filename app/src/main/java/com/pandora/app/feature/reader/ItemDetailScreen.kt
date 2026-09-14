package com.pandora.app.feature.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandora.app.core.designsystem.theme.ApricotFixed
import com.pandora.app.core.designsystem.theme.ApricotOrange
import com.pandora.app.core.designsystem.theme.BadgeShape
import com.pandora.app.core.designsystem.theme.ButtonShape
import com.pandora.app.core.designsystem.theme.CardShape
import com.pandora.app.core.designsystem.theme.CeruleanDark
import com.pandora.app.core.designsystem.theme.CeruleanFixed
import com.pandora.app.core.designsystem.theme.DarkCapsuleSurface
import com.pandora.app.core.designsystem.theme.IrisFixed
import com.pandora.app.core.designsystem.theme.IrisPrimary
import com.pandora.app.core.designsystem.theme.OnCeruleanFixed
import com.pandora.app.core.designsystem.theme.OutlineHairline
import com.pandora.app.core.designsystem.theme.PandoraTypography
import com.pandora.app.core.designsystem.theme.PorcelainCanvas
import com.pandora.app.core.designsystem.theme.PorcelainContainer
import com.pandora.app.core.designsystem.theme.PorcelainContainerHigh
import com.pandora.app.core.designsystem.theme.PorcelainContainerHighest
import com.pandora.app.core.designsystem.theme.PorcelainContainerLow
import com.pandora.app.core.designsystem.theme.PorcelainSheetWhite
import com.pandora.app.core.designsystem.theme.Spacing
import com.pandora.app.core.designsystem.theme.TagChipShape
import com.pandora.app.core.designsystem.theme.TextPrimary
import com.pandora.app.core.designsystem.theme.TextSecondary
import com.pandora.app.core.designsystem.theme.TextTertiary

@Composable
fun ItemDetailScreen(
    viewModel: ItemDetailViewModel,
    onBackClick: () -> Unit,
    onRelatedItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val itemWithRelations = state.itemWithRelations
    val context = androidx.compose.ui.platform.LocalContext.current

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

            IconButton(
                onClick = { },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = TextSecondary
                )
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
                                    .background(CeruleanFixed)
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = null,
                                    tint = OnCeruleanFixed,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Web Article",
                                    style = PandoraTypography.labelSmall,
                                    color = OnCeruleanFixed,
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
                                    val sendIntent = android.content.Intent().apply {
                                        action = android.content.Intent.ACTION_SEND
                                        putExtra(
                                            android.content.Intent.EXTRA_TEXT,
                                            "${item.title}\n\n${item.sourceUrl ?: item.fullContent.ifBlank { item.excerpt }}"
                                        )
                                        type = "text/plain"
                                    }
                                    val shareIntent = android.content.Intent.createChooser(sendIntent, "Share Memory")
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

                // Editorial Headline
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall)) {
                        Text(
                            text = item.title,
                            style = PandoraTypography.headlineLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 24.sp,
                            lineHeight = 32.sp
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall)
                        ) {
                            Text(
                                text = "from ${item.domain ?: "distributedsystems.io"}",
                                style = PandoraTypography.bodySmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                            Text(text = "•", color = TextTertiary)
                            Text(text = "Oct 24, 10:15 AM", style = PandoraTypography.bodySmall, color = TextTertiary)
                            Text(text = "•", color = TextTertiary)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = TextTertiary,
                                    modifier = Modifier.size(11.dp)
                                )
                                Text(
                                    text = "${item.readingTimeMinutes.coerceAtLeast(6)} min read",
                                    style = PandoraTypography.bodySmall,
                                    color = TextTertiary
                                )
                            }
                        }
                    }
                }

                // Featured Snapshot Card
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(135.dp)
                            .shadow(1.dp, CardShape)
                            .clip(CardShape)
                            .background(PorcelainContainer)
                            .border(1.dp, OutlineHairline, CardShape)
                            .padding(Spacing.Medium)
                    ) {
                        Column(modifier = Modifier.align(Alignment.Center)) {
                            Text(
                                text = "Minimalist Architectural Schema",
                                style = PandoraTypography.headlineSmall,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(Spacing.ExtraSmall))
                            Text(
                                text = "Distributed mesh network topologies & resilient microservices",
                                style = PandoraTypography.bodySmall,
                                color = TextSecondary
                            )
                        }

                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .clip(BadgeShape)
                                .background(DarkCapsuleSurface.copy(alpha = 0.9f))
                                .clickable { }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = "Inspect Source",
                                style = PandoraTypography.labelSmall,
                                color = PorcelainSheetWhite,
                                fontSize = 10.sp
                            )
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = null,
                                tint = PorcelainSheetWhite,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                }

                // Organization Hub (Folders + Tags)
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
                            Text(
                                text = "FOLDERS",
                                style = PandoraTypography.labelSmall,
                                color = TextSecondary,
                                letterSpacing = 0.5.sp
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(Spacing.Small),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
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
                                        text = "Tech Architecture",
                                        style = PandoraTypography.labelMedium,
                                        color = TextPrimary
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .clip(TagChipShape)
                                        .background(PorcelainContainerLow)
                                        .clickable { }
                                        .padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = IrisPrimary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "Add to Folder",
                                        style = PandoraTypography.labelMedium,
                                        color = IrisPrimary
                                    )
                                }
                            }
                        }

                        // Tags Row
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall)) {
                            Text(
                                text = "SEMANTIC TAGS",
                                style = PandoraTypography.labelSmall,
                                color = TextSecondary,
                                letterSpacing = 0.5.sp
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "#distributedsystems",
                                    style = PandoraTypography.labelSmall,
                                    color = TextPrimary,
                                    modifier = Modifier
                                        .clip(TagChipShape)
                                        .background(IrisFixed)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                                Text(
                                    text = "#reliability",
                                    style = PandoraTypography.labelSmall,
                                    color = TextPrimary,
                                    modifier = Modifier
                                        .clip(TagChipShape)
                                        .background(ApricotFixed)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                                Text(
                                    text = "#cloud",
                                    style = PandoraTypography.labelSmall,
                                    color = TextPrimary,
                                    modifier = Modifier
                                        .clip(TagChipShape)
                                        .background(CeruleanFixed)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }

                // Key Highlights Numbered Breakdown
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, CardShape)
                            .clip(CardShape)
                            .background(PorcelainContainerLow)
                            .border(1.dp, OutlineHairline, CardShape)
                            .padding(Spacing.Medium),
                        verticalArrangement = Arrangement.spacedBy(Spacing.Small)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 3.dp, height = 15.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(IrisPrimary)
                                )
                                Text(
                                    text = "Key Highlights",
                                    style = PandoraTypography.headlineSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Text(
                                text = "3 Highlights",
                                style = PandoraTypography.labelSmall,
                                color = TextSecondary,
                                modifier = Modifier
                                    .clip(BadgeShape)
                                    .background(PorcelainContainerHighest)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        HighlightNumberItem(
                            number = "01",
                            boldPrefix = "Loose coupling guarantees bounded blast radius:",
                            text = "Systems must assume independent node failure as regular operational telemetry rather than edge cases."
                        )
                        HighlightNumberItem(
                            number = "02",
                            boldPrefix = "Graceful degradation over binary uptime:",
                            text = "Serve stale cached state with eventual consistency rather than stalling inbound client handshakes."
                        )
                        HighlightNumberItem(
                            number = "03",
                            boldPrefix = "Backpressure orchestration:",
                            text = "Saturated consumers must push flow control upstream before internal message broker buffers undergo fatal memory evictions."
                        )
                    }
                }

                // AI Quiet Librarian Card
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, CardShape)
                            .clip(CardShape)
                            .background(PorcelainContainerHigh)
                            .border(1.dp, OutlineHairline, CardShape)
                            .padding(Spacing.Medium),
                        verticalArrangement = Arrangement.spacedBy(Spacing.Small)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(IrisPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = PorcelainSheetWhite,
                                    modifier = Modifier.size(17.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Ask AI about this article",
                                    style = PandoraTypography.headlineSmall,
                                    fontSize = 15.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Explore architecture trade-offs or surface cross-links with your notes.",
                                    style = PandoraTypography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.Small),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(ButtonShape)
                                    .background(IrisPrimary)
                                    .clickable { }
                                    .padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = PorcelainSheetWhite,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "Start chat",
                                    style = PandoraTypography.labelMedium,
                                    color = PorcelainSheetWhite,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(ButtonShape)
                                    .background(PorcelainContainer)
                                    .clickable { }
                                    .padding(horizontal = 10.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = "Generate 3 Flashcards",
                                    style = PandoraTypography.labelMedium,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                // Related Items in Library
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.Small)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Related Items in Library",
                                style = PandoraTypography.headlineSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "${state.relatedItems.size} Linked Artifacts",
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
                                        Icon(
                                            imageVector = Icons.Default.FolderSpecial,
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
                                            text = related.item.excerpt.ifBlank { "Linked personal reflection" },
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

@Composable
fun HighlightNumberItem(
    number: String,
    boldPrefix: String,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(ButtonShape)
            .background(PorcelainSheetWhite)
            .padding(Spacing.MediumSmall),
        horizontalArrangement = Arrangement.spacedBy(Spacing.Small),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = number,
            style = PandoraTypography.headlineMedium,
            color = IrisPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Column {
            Text(
                text = "$boldPrefix $text",
                style = PandoraTypography.bodyMedium,
                color = TextPrimary,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }
    }
}
