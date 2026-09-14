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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandora.app.core.database.dao.ItemWithRelations
import com.pandora.app.core.designsystem.theme.ApricotContainer
import com.pandora.app.core.designsystem.theme.ApricotFixed
import com.pandora.app.core.designsystem.theme.BadgeShape
import com.pandora.app.core.designsystem.theme.CardShape
import com.pandora.app.core.designsystem.theme.CeruleanFixed
import com.pandora.app.core.designsystem.theme.CeruleanTertiary
import com.pandora.app.core.designsystem.theme.IrisFixed
import com.pandora.app.core.designsystem.theme.IrisPrimary
import com.pandora.app.core.designsystem.theme.OnApricotFixed
import com.pandora.app.core.designsystem.theme.OnCeruleanFixed
import com.pandora.app.core.designsystem.theme.OnIrisFixed
import com.pandora.app.core.designsystem.theme.OutlineHairline
import com.pandora.app.core.designsystem.theme.PandoraTypography
import com.pandora.app.core.designsystem.theme.PorcelainContainer
import com.pandora.app.core.designsystem.theme.PorcelainSheetWhite
import com.pandora.app.core.designsystem.theme.QuoteItalicStyle
import com.pandora.app.core.designsystem.theme.Spacing
import com.pandora.app.core.designsystem.theme.TextPrimary
import com.pandora.app.core.designsystem.theme.TextSecondary
import com.pandora.app.core.designsystem.theme.TextTertiary

@Composable
fun ArticleTile(
    itemWithRelations: ItemWithRelations,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val item = itemWithRelations.item

    Column(
        modifier = modifier
            .shadow(1.dp, CardShape)
            .clip(CardShape)
            .background(PorcelainSheetWhite)
            .border(1.dp, OutlineHairline, CardShape)
            .clickable(onClick = onClick)
            .padding(Spacing.Medium),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
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
                    Text(
                        text = "${item.readingTimeMinutes.coerceAtLeast(4)} MIN READ",
                        style = PandoraTypography.labelSmall,
                        color = OnIrisFixed,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI Summary",
                    tint = IrisPrimary,
                    modifier = Modifier.size(13.dp)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.Small))

            Text(
                text = item.title,
                style = PandoraTypography.headlineMedium,
                fontSize = 15.sp,
                lineHeight = 21.sp,
                color = TextPrimary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(Spacing.ExtraSmall))

            Text(
                text = item.excerpt,
                style = PandoraTypography.bodySmall,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )
        }

        Column(modifier = Modifier.padding(top = Spacing.Small)) {
            Text(
                text = item.domain ?: "distributedsystems.io",
                style = PandoraTypography.labelSmall,
                color = TextTertiary,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun NoteTile(
    itemWithRelations: ItemWithRelations,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val item = itemWithRelations.item

    Column(
        modifier = modifier
            .shadow(1.dp, CardShape)
            .clip(CardShape)
            .background(PorcelainSheetWhite)
            .border(1.dp, OutlineHairline, CardShape)
            .clickable(onClick = onClick)
            .padding(Spacing.Medium),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(BadgeShape)
                        .background(ApricotFixed)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = OnApricotFixed,
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = "NOTE",
                        style = PandoraTypography.labelSmall,
                        color = OnApricotFixed,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "3:20 PM",
                    style = PandoraTypography.bodySmall,
                    color = TextTertiary,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(Spacing.Small))

            Text(
                text = item.fullContent.ifBlank { item.excerpt },
                style = QuoteItalicStyle,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = TextPrimary,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(
            modifier = Modifier.padding(top = Spacing.Small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = ApricotContainer,
                modifier = Modifier.size(11.dp)
            )
            Text(
                text = itemWithRelations.folders.firstOrNull()?.name ?: "Design Ops",
                style = PandoraTypography.labelSmall,
                color = ApricotContainer,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun PdfTile(
    itemWithRelations: ItemWithRelations,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val item = itemWithRelations.item

    Column(
        modifier = modifier
            .shadow(1.dp, CardShape)
            .clip(CardShape)
            .background(PorcelainSheetWhite)
            .border(1.dp, OutlineHairline, CardShape)
            .clickable(onClick = onClick)
            .padding(Spacing.Medium),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(BadgeShape)
                        .background(CeruleanFixed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = OnCeruleanFixed,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = "PDF",
                    style = PandoraTypography.labelSmall,
                    color = CeruleanTertiary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    modifier = Modifier
                        .clip(BadgeShape)
                        .background(CeruleanFixed)
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.Small))

            Text(
                text = item.title,
                style = PandoraTypography.headlineSmall,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(Spacing.ExtraSmall))

            Text(
                text = "2.4 MB • Encrypted",
                style = PandoraTypography.bodySmall,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }

        Row(
            modifier = Modifier.padding(top = Spacing.Small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Open Vault →",
                style = PandoraTypography.labelSmall,
                color = IrisPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun VoiceMemoTile(
    itemWithRelations: ItemWithRelations,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val item = itemWithRelations.item

    Column(
        modifier = modifier
            .shadow(1.dp, CardShape)
            .clip(CardShape)
            .background(PorcelainSheetWhite)
            .border(1.dp, OutlineHairline, CardShape)
            .clickable(onClick = onClick)
            .padding(Spacing.Medium),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(BadgeShape)
                        .background(ApricotFixed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = OnApricotFixed,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = "1:42",
                    style = PandoraTypography.labelSmall,
                    color = OnApricotFixed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    modifier = Modifier
                        .clip(BadgeShape)
                        .background(ApricotFixed)
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.Small))

            Text(
                text = item.title,
                style = PandoraTypography.headlineSmall,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(Spacing.ExtraSmall))

            Text(
                text = "Audio transcript ready",
                style = PandoraTypography.bodySmall,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }

        Row(
            modifier = Modifier.padding(top = Spacing.Small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = ApricotContainer,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "Listen",
                style = PandoraTypography.labelSmall,
                color = ApricotContainer,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
