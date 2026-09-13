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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandora.app.core.database.dao.ItemWithRelations
import com.pandora.app.core.designsystem.theme.ApricotFixed
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
            .padding(16.dp)
    ) {
        // Top Meta Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(ApricotFixed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Screenshot,
                        contentDescription = null,
                        tint = OnApricotFixedVariant,
                        modifier = Modifier.size(16.dp)
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
                color = TextTertiary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Title
        Text(
            text = item.title,
            style = PandoraTypography.headlineSmall,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Embedded Diagram Canvas Mock
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PorcelainContainer)
                .border(1.dp, OutlineHairline, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Pandora - Chronological Timeline Architecture",
                    style = PandoraTypography.labelMedium,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "[ API Gateway ] ➔ [ Ingestion Service ] ➔ [ Event Bus ] ➔ [ Vault ]",
                    style = PandoraTypography.bodySmall,
                    color = IrisPrimary,
                    fontSize = 11.sp
                )
            }

            // Inspect Overlay Button
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clip(RoundedCornerShape(6.dp))
                    .background(DarkCapsuleSurface.copy(alpha = 0.85f))
                    .clickable(onClick = onInspectClick)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ZoomIn,
                    contentDescription = "Inspect",
                    tint = InverseOnSurface,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "Inspect Diagram",
                    style = PandoraTypography.labelSmall,
                    color = InverseOnSurface,
                    fontSize = 10.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bottom Tags & Folders Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                itemWithRelations.folders.firstOrNull()?.let { folder ->
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9999.dp))
                            .background(PorcelainContainerHigh)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            tint = IrisPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = folder.name,
                            style = PandoraTypography.labelSmall,
                            color = IrisPrimary
                        )
                    }
                }
                itemWithRelations.tags.firstOrNull()?.let { tag ->
                    Text(
                        text = "#${tag.name}",
                        style = PandoraTypography.labelSmall,
                        color = TextSecondary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PorcelainContainer)
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (item.isFavorite) ApricotFixed else PorcelainContainerHigh)
                    .clickable(onClick = onBookmarkClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (item.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = if (item.isFavorite) OnApricotFixedVariant else TextSecondary,
                    modifier = Modifier.size(16.dp)
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
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.title,
                style = PandoraTypography.headlineSmall,
                color = TextPrimary
            )
            Text(
                text = "5:18 PM",
                style = PandoraTypography.bodySmall,
                color = TextTertiary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(PorcelainContainerLow)
                .padding(14.dp)
        ) {
            Text(
                text = item.excerpt.ifBlank { item.fullContent },
                style = QuoteItalicStyle,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            itemWithRelations.folders.forEach { folder ->
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(PorcelainContainer)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = folder.name,
                        style = PandoraTypography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
