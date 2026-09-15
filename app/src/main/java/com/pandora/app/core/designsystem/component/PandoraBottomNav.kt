package com.pandora.app.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandora.app.core.designsystem.theme.IrisFixed
import com.pandora.app.core.designsystem.theme.IrisPrimary
import com.pandora.app.core.designsystem.theme.OutlineHairline
import com.pandora.app.core.designsystem.theme.PandoraTypography
import com.pandora.app.core.designsystem.theme.PillShape
import com.pandora.app.core.designsystem.theme.PorcelainCanvas
import com.pandora.app.core.designsystem.theme.Spacing
import com.pandora.app.core.designsystem.theme.TextSecondary
import com.pandora.app.core.designsystem.theme.TextTertiary

enum class NavDestination(val route: String, val label: String, val icon: ImageVector) {
    TIMELINE("timeline", "Timeline", Icons.Default.CalendarToday),
    ORGANIZE("organize", "Organize", Icons.Default.FolderOpen),
    SEARCH("search", "Search", Icons.Default.Search),
    PANDORA_AI("pandora_ai", "Pandora AI", Icons.Default.AutoAwesome)
}

@Composable
fun PandoraBottomNav(
    currentRoute: String,
    onNavigate: (NavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(PorcelainCanvas.copy(alpha = 0.98f))
            .border(width = 1.dp, color = OutlineHairline)
            .navigationBarsPadding()
            .padding(horizontal = Spacing.Small, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavDestination.entries.forEach { destination ->
            val isSelected = currentRoute == destination.route
            val pillColor by animateColorAsState(
                targetValue = if (isSelected) IrisFixed else Color.Transparent,
                label = "pillColor"
            )
            val contentColor by animateColorAsState(
                targetValue = if (isSelected) IrisPrimary else TextTertiary,
                label = "contentColor"
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onNavigate(destination) }
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 48.dp, height = 26.dp)
                        .clip(PillShape)
                        .background(pillColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label,
                        tint = contentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = destination.label,
                    style = PandoraTypography.labelSmall,
                    fontSize = 11.sp,
                    color = if (isSelected) IrisPrimary else TextSecondary,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
