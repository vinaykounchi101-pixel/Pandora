package com.pandora.app.feature.timeline.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Screenshot
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandora.app.core.database.dao.ItemWithRelations
import com.pandora.app.core.designsystem.theme.ApricotFixed
import com.pandora.app.core.designsystem.theme.BadgeShape
import com.pandora.app.core.designsystem.theme.ButtonShape
import com.pandora.app.core.designsystem.theme.CardShape
import com.pandora.app.core.designsystem.theme.DarkCapsuleSurface
import com.pandora.app.core.designsystem.theme.InverseOnSurface
import com.pandora.app.core.designsystem.theme.IrisPrimary
import com.pandora.app.core.designsystem.theme.OnApricotFixedVariant
import com.pandora.app.core.designsystem.theme.OutlineHairline
import com.pandora.app.core.designsystem.theme.PandoraTypography
import com.pandora.app.core.designsystem.theme.PorcelainContainer
import com.pandora.app.core.designsystem.theme.PorcelainContainerHigh
import com.pandora.app.core.designsystem.theme.PorcelainContainerLow
import com.pandora.app.core.designsystem.theme.PorcelainSheetWhite
import com.pandora.app.core.designsystem.theme.QuoteItalicStyle
import com.pandora.app.core.designsystem.theme.Spacing
import com.pandora.app.core.designsystem.theme.TagChipShape
import com.pandora.app.core.designsystem.theme.TextPrimary
import com.pandora.app.core.designsystem.theme.TextSecondary
import com.pandora.app.core.designsystem.theme.TextTertiary

@Composable
fun HeroDiagramCard(
    itemWithRelations: ItemWithRelations,
    onClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onInspectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val item = itemWithRelations.item

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(1.dp, CardShape)
            .clip(CardShape)
            .background(PorcelainSheetWhite)
            .border(1.dp, OutlineHairline, CardShape)
            .clickable(onClick = onClick)
            .padding(Spacing.Medium)
    ) {
        // Top Meta Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(BadgeShape)
                        .background(ApricotFixed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Screenshot,
                        contentDescription = null,
                        tint = OnApricotFixedVariant,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = item.capturedFromApp ?: "Captured from Chrome",
                    style = PandoraTypography.labelSmall,
                    color = TextSecondary
                )
            }
            Text(
                text = "11:42 AM",
                style = PandoraTypography.bodySmall,
                color = TextTertiary,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(Spacing.Small))

        // Title
        Text(
            text = item.title,
            style = PandoraTypography.headlineSmall,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            lineHeight = 23.sp
        )

        Spacer(modifier = Modifier.height(Spacing.MediumSmall))

        // Embedded Diagram Canvas Mock
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PorcelainContainer)
                .border(1.dp, OutlineHairline, RoundedCornerShape(10.dp))
                .padding(Spacing.MediumSmall)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Pandora - Chronological Timeline Architecture",
                    style = PandoraTypography.labelMedium,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(Spacing.ExtraSmall))
                Text(
                    text = "[ Ingestion ] ➔ [ SQLite + FTS5 ] ➔ [ Private Vault ]",
                    style = PandoraTypography.bodySmall,
                    color = IrisPrimary,
                    fontSize = 11.sp
                )
            }

            // Inspect Overlay Button
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clip(BadgeShape)
                    .background(DarkCapsuleSurface.copy(alpha = 0.9f))
                    .clickable(onClick = onInspectClick)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ZoomIn,
                    contentDescription = "Inspect",
                    tint = InverseOnSurface,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "Inspect Diagram",
                    style = PandoraTypography.labelSmall,
                    color = InverseOnSurface,
                    fontSize = 10.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.MediumSmall))

        // Bottom Tags & Folders Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall)
            ) {
                itemWithRelations.folders.firstOrNull()?.let { folder ->
                    Row(
                        modifier = Modifier
                            .clip(TagChipShape)
                            .background(PorcelainContainerHigh)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            tint = IrisPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = folder.name,
                            style = PandoraTypography.labelSmall,
                            color = IrisPrimary,
                            fontSize = 10.sp
                        )
                    }
                }
                itemWithRelations.tags.firstOrNull()?.let { tag ->
                    Text(
                        text = "#${tag.name}",
                        style = PandoraTypography.labelSmall,
                        color = TextSecondary,
                        fontSize = 10.sp,
                        modifier = Modifier
                            .clip(BadgeShape)
                            .background(PorcelainContainer)
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(if (item.isFavorite) ApricotFixed else PorcelainContainerHigh)
                    .clickable(onClick = onBookmarkClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (item.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = if (item.isFavorite) OnApricotFixedVariant else TextSecondary,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}

@Composable
fun FullWidthThoughtCard(
    itemWithRelations: ItemWithRelations,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val item = itemWithRelations.item

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(1.dp, CardShape)
            .clip(CardShape)
            .background(PorcelainSheetWhite)
            .border(1.dp, OutlineHairline, CardShape)
            .clickable(onClick = onClick)
            .padding(Spacing.Medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.title,
                style = PandoraTypography.headlineSmall,
                color = TextPrimary,
                fontSize = 17.sp
            )
            Text(
                text = "5:18 PM",
                style = PandoraTypography.bodySmall,
                color = TextTertiary,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(Spacing.Small))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(PorcelainContainerLow)
                .padding(Spacing.MediumSmall)
        ) {
            Text(
                text = item.excerpt.ifBlank { item.fullContent },
                style = QuoteItalicStyle,
                color = TextPrimary,
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(Spacing.MediumSmall))

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            itemWithRelations.folders.forEach { folder ->
                Row(
                    modifier = Modifier
                        .clip(TagChipShape)
                        .background(PorcelainContainer)
                        .padding(horizontal = 7.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = folder.name,
                        style = PandoraTypography.labelSmall,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
