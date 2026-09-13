package com.pandora.app.feature.proposal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandora.app.core.designsystem.theme.ApricotContainer
import com.pandora.app.core.designsystem.theme.ApricotFixed
import com.pandora.app.core.designsystem.theme.BottomSheetTopShape
import com.pandora.app.core.designsystem.theme.CardShape
import com.pandora.app.core.designsystem.theme.CeruleanFixed
import com.pandora.app.core.designsystem.theme.IrisFixed
import com.pandora.app.core.designsystem.theme.IrisPrimary
import com.pandora.app.core.designsystem.theme.OnApricotFixed
import com.pandora.app.core.designsystem.theme.OnCeruleanFixed
import com.pandora.app.core.designsystem.theme.OnIrisFixed
import com.pandora.app.core.designsystem.theme.OutlineHairline
import com.pandora.app.core.designsystem.theme.PandoraTypography
import com.pandora.app.core.designsystem.theme.PorcelainCanvas
import com.pandora.app.core.designsystem.theme.PorcelainContainer
import com.pandora.app.core.designsystem.theme.PorcelainContainerHigh
import com.pandora.app.core.designsystem.theme.PorcelainContainerLow
import com.pandora.app.core.designsystem.theme.PorcelainSheetWhite
import com.pandora.app.core.designsystem.theme.TextOutline
import com.pandora.app.core.designsystem.theme.TextPrimary
import com.pandora.app.core.designsystem.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AiProposalBottomSheet(
    onDismiss: () -> Unit,
    onApprove: (selectedFolders: List<String>, selectedTags: List<String>) -> Unit,
    onDecline: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var folder1Checked by remember { mutableStateOf(true) }
    var folder2Checked by remember { mutableStateOf(true) }

    var tag1Selected by remember { mutableStateOf(true) }
    var tag2Selected by remember { mutableStateOf(true) }
    var tag3Selected by remember { mutableStateOf(true) }
    var tag4Selected by remember { mutableStateOf(true) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = BottomSheetTopShape,
        containerColor = PorcelainSheetWhite,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 4.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(TextOutline.copy(alpha = 0.4f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Section: AI Transparency & User Agency
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9999.dp))
                            .background(PorcelainContainerHigh)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = IrisPrimary,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "AI Suggestion · Review & Approve",
                            style = PandoraTypography.labelMedium,
                            color = IrisPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "STEP 1 OF 1",
                        style = PandoraTypography.labelSmall,
                        color = TextOutline,
                        letterSpacing = 0.5.sp
                    )
                }

                Text(
                    text = "Nothing changes without your confirmation. Edit, adjust paths, or curate proposed placements.",
                    style = PandoraTypography.bodySmall,
                    color = TextSecondary
                )
            }

            // Captured Item Preview Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CardShape)
                    .background(PorcelainContainerLow)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Article,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "medium.com/engineering",
                            style = PandoraTypography.labelSmall,
                            color = TextSecondary
                        )
                    }
                    Text(
                        text = "5 min read",
                        style = PandoraTypography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "State Management in Modern Distributed Android Apps",
                    style = PandoraTypography.headlineSmall,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "“Unidirectional data flow combined with local SQLite persistence guarantees deterministic UI rendering during network degradation...”",
                    style = PandoraTypography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2
                )
            }

            // Target Directory Stack
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                            imageVector = Icons.Default.FolderSpecial,
                            contentDescription = null,
                            tint = IrisPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Target Directory Stack",
                            style = PandoraTypography.labelLarge,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "Multi-destination",
                        style = PandoraTypography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                // Folder Row 1
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PorcelainContainer)
                        .clickable { folder1Checked = !folder1Checked }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = folder1Checked,
                            onCheckedChange = { folder1Checked = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = IrisPrimary,
                                checkmarkColor = PorcelainSheetWhite
                            )
                        )
                        Column {
                            Text(
                                text = "Technical Architecture / Android",
                                style = PandoraTypography.labelMedium,
                                color = TextPrimary
                            )
                            Text(
                                text = "24 articles · primary repository",
                                style = PandoraTypography.bodySmall,
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    IconButton(onClick = { }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Folder Row 2
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PorcelainContainer)
                        .clickable { folder2Checked = !folder2Checked }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = folder2Checked,
                            onCheckedChange = { folder2Checked = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = IrisPrimary,
                                checkmarkColor = PorcelainSheetWhite
                            )
                        )
                        Column {
                            Text(
                                text = "Mobile Guidelines",
                                style = PandoraTypography.labelMedium,
                                color = TextPrimary
                            )
                            Text(
                                text = "Team reference stack",
                                style = PandoraTypography.bodySmall,
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    IconButton(onClick = { }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Suggested Taxonomy Tags
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Suggested Taxonomy",
                    style = PandoraTypography.labelLarge,
                    color = TextPrimary
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ProposalTagPill(
                        name = "#android-architecture",
                        isSelected = tag1Selected,
                        onClick = { tag1Selected = !tag1Selected },
                        bg = IrisFixed,
                        fg = OnIrisFixed
                    )
                    ProposalTagPill(
                        name = "#reactive-state",
                        isSelected = tag2Selected,
                        onClick = { tag2Selected = !tag2Selected },
                        bg = CeruleanFixed,
                        fg = OnCeruleanFixed
                    )
                    ProposalTagPill(
                        name = "#concurrency",
                        isSelected = tag3Selected,
                        onClick = { tag3Selected = !tag3Selected },
                        bg = ApricotFixed,
                        fg = OnApricotFixed
                    )
                    ProposalTagPill(
                        name = "#performance",
                        isSelected = tag4Selected,
                        onClick = { tag4Selected = !tag4Selected },
                        bg = PorcelainContainerHigh,
                        fg = TextPrimary
                    )
                }
            }

            // AI Archival Rationale
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PorcelainContainerLow)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(IrisFixed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = IrisPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Column {
                    Text(
                        text = "Archival Rationale",
                        style = PandoraTypography.labelSmall,
                        color = IrisPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Matched because of frequent mentions of Kotlin Flow, MVI patterns, and local offline cache architecture.",
                        style = PandoraTypography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // Action Buttons Row
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Approve & Save CTA
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(9999.dp))
                        .background(IrisPrimary)
                        .clickable {
                            val folders = mutableListOf<String>()
                            if (folder1Checked) folders.add("Technical Architecture")
                            if (folder2Checked) folders.add("Mobile Guidelines")
                            val tags = mutableListOf<String>()
                            if (tag1Selected) tags.add("android-architecture")
                            if (tag2Selected) tags.add("reactive-state")
                            if (tag3Selected) tags.add("concurrency")
                            if (tag4Selected) tags.add("performance")
                            onApprove(folders, tags)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TaskAlt,
                        contentDescription = null,
                        tint = PorcelainSheetWhite,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = "Approve & Save",
                        style = PandoraTypography.labelLarge,
                        color = PorcelainSheetWhite,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Decline & Organize Manually CTA
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(9999.dp))
                        .background(PorcelainContainerLow)
                        .clickable(onClick = onDecline),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = "Decline & Organize Manually",
                        style = PandoraTypography.labelLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ProposalTagPill(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    bg: Color,
    fg: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(if (isSelected) bg else PorcelainContainer)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.size(13.dp)
            )
        }
        Text(
            text = name,
            style = PandoraTypography.labelSmall,
            color = if (isSelected) fg else TextSecondary,
            fontWeight = FontWeight.SemiBold
        )
    }
}
