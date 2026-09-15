package com.pandora.app.feature.timeline.component

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
    onFavoriteToggle: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val item = itemWithRelations.item
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

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
                        text = if (item.readingTimeMinutes > 0) "${item.readingTimeMinutes} MIN READ" else "ARTICLE",
                        style = PandoraTypography.labelSmall,
                        color = OnIrisFixed,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = TextTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    ItemCardDropdownMenu(
                        expanded = showMenu,
                        onDismiss = { showMenu = false },
                        isFavorite = item.isFavorite,
                        onFavoriteToggle = {
                            showMenu = false
                            onFavoriteToggle?.invoke()
                        },
                        onShare = {
                            showMenu = false
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "${item.title}\n${item.sourceUrl ?: ""}")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Article"))
                        },
                        onDelete = {
                            showMenu = false
                            onDeleteClick?.invoke()
                        }
                    )
                }
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

            if (item.excerpt.isNotBlank()) {
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
        }

        Column(modifier = Modifier.padding(top = Spacing.Small)) {
            val domainDisplay = item.domain ?: item.sourceUrl ?: "Saved Link"
            Text(
                text = domainDisplay,
                style = PandoraTypography.labelSmall,
                color = TextTertiary,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun NoteTile(
    itemWithRelations: ItemWithRelations,
    onClick: () -> Unit,
    onFavoriteToggle: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val item = itemWithRelations.item
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    val formattedTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(item.createdAt))

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

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formattedTime,
                        style = PandoraTypography.bodySmall,
                        color = TextTertiary,
                        fontSize = 10.sp
                    )
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Options",
                                tint = TextTertiary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        ItemCardDropdownMenu(
                            expanded = showMenu,
                            onDismiss = { showMenu = false },
                            isFavorite = item.isFavorite,
                            onFavoriteToggle = {
                                showMenu = false
                                onFavoriteToggle?.invoke()
                            },
                            onShare = {
                                showMenu = false
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "${item.title}\n\n${item.fullContent.ifBlank { item.excerpt }}")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Note"))
                            },
                            onDelete = {
                                showMenu = false
                                onDeleteClick?.invoke()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.Small))

            Text(
                text = item.fullContent.ifBlank { item.excerpt.ifBlank { item.title } },
                style = QuoteItalicStyle,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = TextPrimary,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }

        val folderName = itemWithRelations.folders.firstOrNull()?.name
        if (folderName != null) {
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
                    text = folderName,
                    style = PandoraTypography.labelSmall,
                    color = ApricotContainer,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun PdfTile(
    itemWithRelations: ItemWithRelations,
    onClick: () -> Unit,
    onFavoriteToggle: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val item = itemWithRelations.item
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    val sizeText = if (item.fileSizeBytes > 0) "${item.fileSizeBytes / 1024} KB" else "PDF Document"

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

                Row(verticalAlignment = Alignment.CenterVertically) {
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
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Options",
                                tint = TextTertiary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        ItemCardDropdownMenu(
                            expanded = showMenu,
                            onDismiss = { showMenu = false },
                            isFavorite = item.isFavorite,
                            onFavoriteToggle = {
                                showMenu = false
                                onFavoriteToggle?.invoke()
                            },
                            onShare = {
                                showMenu = false
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, item.title)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share PDF"))
                            },
                            onDelete = {
                                showMenu = false
                                onDeleteClick?.invoke()
                            }
                        )
                    }
                }
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
                text = "$sizeText • Offline Vault",
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
                text = "Open Document →",
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
    onFavoriteToggle: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val item = itemWithRelations.item
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    val durationText = if (item.durationSeconds > 0) {
        "${item.durationSeconds / 60}:${(item.durationSeconds % 60).toString().padStart(2, '0')}"
    } else "Voice"

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

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = durationText,
                        style = PandoraTypography.labelSmall,
                        color = OnApricotFixed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        modifier = Modifier
                            .clip(BadgeShape)
                            .background(ApricotFixed)
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Options",
                                tint = TextTertiary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        ItemCardDropdownMenu(
                            expanded = showMenu,
                            onDismiss = { showMenu = false },
                            isFavorite = item.isFavorite,
                            onFavoriteToggle = {
                                showMenu = false
                                onFavoriteToggle?.invoke()
                            },
                            onShare = {
                                showMenu = false
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "${item.title}\n${item.fullContent.ifBlank { item.excerpt }}")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Voice Memo"))
                            },
                            onDelete = {
                                showMenu = false
                                onDeleteClick?.invoke()
                            }
                        )
                    }
                }
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
                text = item.excerpt.ifBlank { "Audio recording in vault" },
                style = PandoraTypography.bodySmall,
                color = TextSecondary,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
                text = "Play",
                style = PandoraTypography.labelSmall,
                color = ApricotContainer,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ImageTile(
    itemWithRelations: ItemWithRelations,
    onClick: () -> Unit,
    onFavoriteToggle: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val item = itemWithRelations.item
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    val hasValidFile = !item.localFilePath.isNullOrBlank() && File(item.localFilePath).exists()

    Column(
        modifier = modifier
            .shadow(1.dp, CardShape)
            .clip(CardShape)
            .background(PorcelainSheetWhite)
            .border(1.dp, OutlineHairline, CardShape)
            .clickable(onClick = onClick)
            .padding(Spacing.Small),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // Visual Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(PorcelainContainer),
                contentAlignment = Alignment.Center
            ) {
                if (hasValidFile) {
                    AsyncImage(
                        model = File(item.localFilePath!!),
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = IrisPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "IMAGE CAPTURE",
                            style = PandoraTypography.labelSmall,
                            color = IrisPrimary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 3-dots on image overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable { showMenu = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    ItemCardDropdownMenu(
                        expanded = showMenu,
                        onDismiss = { showMenu = false },
                        isFavorite = item.isFavorite,
                        onFavoriteToggle = {
                            showMenu = false
                            onFavoriteToggle?.invoke()
                        },
                        onShare = {
                            showMenu = false
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, item.title)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Image"))
                        },
                        onDelete = {
                            showMenu = false
                            onDeleteClick?.invoke()
                        }
                    )
                }
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
                text = item.capturedFromApp ?: "Vault Asset",
                style = PandoraTypography.bodySmall,
                color = TextTertiary,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        val folderName = itemWithRelations.folders.firstOrNull()?.name
        if (folderName != null) {
            Row(
                modifier = Modifier.padding(top = Spacing.Small),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    tint = IrisPrimary,
                    modifier = Modifier.size(11.dp)
                )
                Text(
                    text = folderName,
                    style = PandoraTypography.labelSmall,
                    color = IrisPrimary,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun ItemCardDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier.background(PorcelainSheetWhite)
    ) {
        DropdownMenuItem(
            text = { Text(if (isFavorite) "Favorited" else "Favorite") },
            leadingIcon = {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isFavorite) ApricotContainer else TextSecondary
                )
            },
            onClick = onFavoriteToggle
        )
        DropdownMenuItem(
            text = { Text("Share") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = TextSecondary
                )
            },
            onClick = onShare
        )
        DropdownMenuItem(
            text = { Text("Delete", color = Color(0xFFBA1A1A)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color(0xFFBA1A1A)
                )
            },
            onClick = onDelete
        )
    }
}
