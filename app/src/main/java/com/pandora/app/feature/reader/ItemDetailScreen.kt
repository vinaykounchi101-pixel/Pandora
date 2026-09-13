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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandora.app.core.designsystem.theme.ApricotFixed
import com.pandora.app.core.designsystem.theme.ApricotOrange
import com.pandora.app.core.designsystem.theme.CardShape
import com.pandora.app.core.designsystem.theme.CeruleanDark
import com.pandora.app.core.designsystem.theme.CeruleanFixed
import com.pandora.app.core.designsystem.theme.CeruleanTertiary
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
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = "Note Detail",
                    style = PandoraTypography.headlineMedium,
                    fontSize = 20.sp,
                    color = TextPrimary
                )
            }

            IconButton(onClick = { }) {
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
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(CeruleanFixed)
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = null,
                                    tint = OnCeruleanFixed,
                                    modifier = Modifier.size(13.dp)
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
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(PorcelainContainer)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = CeruleanDark,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Offline Cached · App-Private",
                                    style = PandoraTypography.labelSmall,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
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
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(
                                onClick = { },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PorcelainContainerLow)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Editorial Headline
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = item.title,
                            style = PandoraTypography.headlineLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "saved from ${item.domain ?: "distributedsystems.io"}",
                                style = PandoraTypography.bodySmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                            Text(text = "•", color = TextTertiary)
                            Text(text = "Oct 24, 10:15 AM", style = PandoraTypography.bodySmall, color = TextTertiary)
                            Text(text = "•", color = TextTertiary)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = TextTertiary,
                                    modifier = Modifier.size(12.dp)
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
                            .height(150.dp)
                            .shadow(1.dp, CardShape)
                            .clip(CardShape)
                            .background(PorcelainContainer)
                            .border(1.dp, OutlineHairline, CardShape)
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.align(Alignment.Center)) {
                            Text(
                                text = "Minimalist Architectural Schema",
                                style = PandoraTypography.headlineSmall,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Distributed mesh network topologies & resilient microservices",
                                style = PandoraTypography.bodySmall,
                                color = TextSecondary
                            )
                        }

                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkCapsuleSurface.copy(alpha = 0.85f))
                                .clickable { }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
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
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                // Tactile Organization Hub (Folders + Tags)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, CardShape)
                            .clip(CardShape)
                            .background(PorcelainSheetWhite)
                            .border(1.dp, OutlineHairline, CardShape)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Folders Row
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "FOLDERS (${itemWithRelations.folders.size.coerceAtLeast(2)})",
                                    style = PandoraTypography.labelSmall,
                                    color = TextSecondary,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Organized in Personal Vault",
                                    style = PandoraTypography.labelSmall,
                                    color = IrisPrimary
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(PorcelainContainerHigh)
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FolderOpen,
                                        contentDescription = null,
                                        tint = IrisPrimary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = "Tech Architecture",
                                        style = PandoraTypography.labelMedium,
                                        color = TextPrimary
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(PorcelainContainerLow)
                                        .clickable { }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = IrisPrimary,
                                        modifier = Modifier.size(14.dp)
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
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "SEMANTIC TAGS",
                                style = PandoraTypography.labelSmall,
                                color = TextSecondary,
                                letterSpacing = 0.5.sp
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "#distributedsystems",
                                    style = PandoraTypography.labelSmall,
                                    color = TextPrimary,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(9999.dp))
                                        .background(IrisFixed)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                                Text(
                                    text = "#reliability",
                                    style = PandoraTypography.labelSmall,
                                    color = TextPrimary,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(9999.dp))
                                        .background(ApricotFixed)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                                Text(
                                    text = "#cloud",
                                    style = PandoraTypography.labelSmall,
                                    color = TextPrimary,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(9999.dp))
                                        .background(CeruleanFixed)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
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
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                Box(
                                    modifier = Modifier
                                        .size(width = 3.dp, height = 16.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(IrisPrimary)
                                )
                                Text(
                                    text = "Key Highlights",
                                    style = PandoraTypography.headlineSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "3 Parsed",
                                style = PandoraTypography.labelSmall,
                                color = TextSecondary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
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
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(IrisPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = PorcelainSheetWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Ask AI about this article",
                                    style = PandoraTypography.headlineSmall,
                                    fontSize = 16.sp,
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
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(IrisPrimary)
                                    .clickable { }
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = PorcelainSheetWhite,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Start fresh chat",
                                    style = PandoraTypography.labelMedium,
                                    color = PorcelainSheetWhite,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(PorcelainContainer)
                                    .clickable { }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
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
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Related Items in Library",
                                style = PandoraTypography.headlineSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "2 Linked Artifacts",
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
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(PorcelainContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FolderSpecial,
                                            contentDescription = null,
                                            tint = IrisPrimary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = related.item.title,
                                            style = PandoraTypography.headlineSmall,
                                            fontSize = 15.sp,
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
                                    modifier = Modifier.size(20.dp)
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
            .clip(RoundedCornerShape(10.dp))
            .background(PorcelainSheetWhite)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = number,
            style = PandoraTypography.headlineMedium,
            color = IrisPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
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
