package com.pandora.app.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.pandora.app.core.designsystem.theme.ApricotContainer
import com.pandora.app.core.designsystem.theme.DarkCapsuleSurface
import com.pandora.app.core.designsystem.theme.InverseOnSurface
import com.pandora.app.core.designsystem.theme.PillShape
import com.pandora.app.core.designsystem.theme.PorcelainSheetWhite
import com.pandora.app.core.designsystem.theme.Spacing

@Composable
fun FloatingCaptureCapsule(
    onCameraClick: () -> Unit = {},
    onNoteClick: () -> Unit = {},
    onQuickAddClick: () -> Unit = {},
    onLinkClick: () -> Unit = {},
    onVoiceClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(elevation = 12.dp, shape = PillShape)
            .clip(PillShape)
            .background(DarkCapsuleSurface)
            .padding(horizontal = Spacing.Small, vertical = Spacing.ExtraSmall)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall)
        ) {
            // 1. Camera / Photo Capture
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onCameraClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Capture Photo",
                    tint = InverseOnSurface,
                    modifier = Modifier.size(18.dp)
                )
            }

            // 2. Note Quick Write
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onNoteClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Write Note",
                    tint = InverseOnSurface,
                    modifier = Modifier.size(18.dp)
                )
            }

            // 3. Center Primary Orange Action
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(ApricotContainer)
                    .clickable(onClick = onQuickAddClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Quick Add",
                    tint = PorcelainSheetWhite,
                    modifier = Modifier.size(24.dp)
                )
            }

            // 4. Link Ingestion
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onLinkClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = "Add Link",
                    tint = InverseOnSurface,
                    modifier = Modifier.size(18.dp)
                )
            }

            // 5. Voice Thought Recording
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onVoiceClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Memo",
                    tint = InverseOnSurface,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
