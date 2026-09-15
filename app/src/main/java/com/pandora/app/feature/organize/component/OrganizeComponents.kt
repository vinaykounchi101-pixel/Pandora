package com.pandora.app.feature.organize.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SubdirectoryArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandora.app.core.database.dao.CollectionWithItems
import com.pandora.app.core.database.dao.FolderWithSubfoldersAndItems
import com.pandora.app.core.database.dao.TagWithItemCount
import com.pandora.app.core.designsystem.theme.ApricotFixed
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
import com.pandora.app.core.designsystem.theme.PorcelainContainerHigh
import com.pandora.app.core.designsystem.theme.PorcelainContainerLow
import com.pandora.app.core.designsystem.theme.PorcelainSheetWhite
import com.pandora.app.core.designsystem.theme.TextOutline
import com.pandora.app.core.designsystem.theme.TextPrimary
import com.pandora.app.core.designsystem.theme.TextSecondary
import com.pandora.app.core.designsystem.theme.TextTertiary

@Composable
fun CollectionsSpotlightCarousel(
    collections: List<CollectionWithItems>,
    onCollectionClick: (Long) -> Unit,
    onNewCollectionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        collections.forEach { collectionWithItems ->
            val collection = collectionWithItems.collection
            val bgTint = when (collection.colorToken) {
                "secondary" -> ApricotFixed
                "tertiary" -> CeruleanFixed
                else -> IrisFixed
            }
            val iconTint = when (collection.colorToken) {
                "secondary" -> OnApricotFixed
                "tertiary" -> CeruleanTertiary
                else -> IrisPrimary
            }

            Column(
                modifier = Modifier
                    .width(230.dp)
                    .height(170.dp)
                    .shadow(1.dp, CardShape)
                    .clip(CardShape)
                    .background(PorcelainSheetWhite)
                    .border(1.dp, OutlineHairline, CardShape)
                    .clickable { onCollectionClick(collection.id) }
                    .padding(16.dp),
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
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(bgTint),
                            contentAlignment = Alignment.Center
                        ) {
                            val icon = when (collection.iconName) {
                                "devices" -> Icons.Default.Devices
                                "insights" -> Icons.Default.Insights
                                else -> Icons.Default.Psychology
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconTint,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Text(
                            text = "2h ago",
                            style = PandoraTypography.labelSmall,
                            color = TextOutline,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = collection.name,
                        style = PandoraTypography.headlineMedium,
                        fontSize = 17.sp,
                        lineHeight = 22.sp,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = collection.description,
                        style = PandoraTypography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${collectionWithItems.items.size} artifacts",
                        style = PandoraTypography.labelSmall,
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(PorcelainContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // New Collection Card
        Column(
            modifier = Modifier
                .width(130.dp)
                .height(170.dp)
                .clip(CardShape)
                .background(PorcelainContainerLow)
                .border(1.dp, OutlineHairline, CardShape)
                .clickable(onClick = onNewCollectionClick)
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PorcelainSheetWhite)
                    .shadow(1.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Collection",
                    tint = IrisPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "New\nCollection",
                style = PandoraTypography.labelMedium,
                color = IrisPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun FolderAccordionItem(
    folderWithSubfolders: FolderWithSubfoldersAndItems,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onFolderClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val folder = folderWithSubfolders.folder
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "chevronRotation"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(1.dp, CardShape)
            .clip(CardShape)
            .background(PorcelainSheetWhite)
            .border(1.dp, OutlineHairline, CardShape)
    ) {
        // Parent Folder Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(IrisFixed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = IrisPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = folder.name,
                            style = PandoraTypography.labelLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (folderWithSubfolders.subfolders.isNotEmpty()) {
                            Text(
                                text = "${folderWithSubfolders.subfolders.size} Sub",
                                style = PandoraTypography.labelSmall,
                                color = OnIrisFixed,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(IrisFixed)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "${folderWithSubfolders.items.size} items",
                        style = PandoraTypography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            if (folderWithSubfolders.subfolders.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TextOutline,
                    modifier = Modifier
                        .size(22.dp)
                        .rotate(rotationAngle)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextOutline,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Subfolders Accordion
        AnimatedVisibility(visible = isExpanded && folderWithSubfolders.subfolders.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PorcelainContainerLow)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                folderWithSubfolders.subfolders.forEach { subfolder ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 18.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PorcelainSheetWhite)
                            .clickable { onFolderClick(subfolder.id) }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SubdirectoryArrowRight,
                                contentDescription = null,
                                tint = CeruleanTertiary,
                                modifier = Modifier.size(16.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = null,
                                tint = TextOutline,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = subfolder.name,
                                style = PandoraTypography.bodyMedium,
                                color = TextPrimary,
                                fontSize = 13.sp
                            )
                        }


                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagCloudSection(
    tags: List<TagWithItemCount>,
    onTagClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(1.dp, CardShape)
            .clip(CardShape)
            .background(PorcelainSheetWhite)
            .border(1.dp, OutlineHairline, CardShape)
            .padding(16.dp)
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.forEach { tagWithCount ->
                val tag = tagWithCount.tag
                val bgTint = if (tag.isAiGenerated) IrisFixed else when (tag.colorToken) {
                    "secondary" -> ApricotFixed
                    "tertiary" -> CeruleanFixed
                    else -> PorcelainContainerHigh
                }
                val textTint = if (tag.isAiGenerated) IrisPrimary else when (tag.colorToken) {
                    "secondary" -> OnApricotFixed
                    "tertiary" -> OnCeruleanFixed
                    else -> TextPrimary
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(bgTint)
                        .clickable { onTagClick(tag.name) }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    if (tag.isAiGenerated) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = IrisPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    Text(
                        text = "#${tag.name}",
                        style = PandoraTypography.labelMedium,
                        color = textTint,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${tagWithCount.itemCount}",
                        style = PandoraTypography.labelSmall,
                        color = textTint.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
