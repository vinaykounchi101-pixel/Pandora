package com.pandora.app.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pandora.app.core.designsystem.theme.IrisFixed
import com.pandora.app.core.designsystem.theme.IrisPrimary
import com.pandora.app.core.designsystem.theme.PandoraTypography
import com.pandora.app.core.designsystem.theme.PorcelainContainerHigh
import com.pandora.app.core.designsystem.theme.TextPrimary
import com.pandora.app.core.designsystem.theme.TextSecondary

@Composable
fun FilterPillChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    badgeCount: Int? = null
) {
    val backgroundColor = if (isSelected) IrisFixed else PorcelainContainerHigh
    val contentColor = if (isSelected) IrisPrimary else TextSecondary

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
        }

        Text(
            text = label,
            style = PandoraTypography.labelMedium,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(IrisPrimary)
            )
        } else if (badgeCount != null) {
            Text(
                text = "$badgeCount",
                style = PandoraTypography.labelSmall,
                color = TextSecondary.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun TaxonomyTagChip(
    tagName: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = PorcelainContainerHigh,
    textColor: Color = TextPrimary,
    borderColor: Color? = null,
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null
) {
    val baseModifier = modifier
        .clip(RoundedCornerShape(8.dp))
        .background(backgroundColor)
        .then(
            if (borderColor != null) Modifier.border(1.dp, borderColor, RoundedCornerShape(8.dp))
            else Modifier
        )
        .then(
            if (onClick != null) Modifier.clickable(onClick = onClick)
            else Modifier
        )
        .padding(horizontal = 10.dp, vertical = 4.dp)

    Row(
        modifier = baseModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
        }
        Text(
            text = if (tagName.startsWith("#")) tagName else "#$tagName",
            style = PandoraTypography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}
