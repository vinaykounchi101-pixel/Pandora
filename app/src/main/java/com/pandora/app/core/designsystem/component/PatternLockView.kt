package com.pandora.app.core.designsystem.component

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import com.pandora.app.core.designsystem.theme.ApricotOrange
import com.pandora.app.core.designsystem.theme.IrisPrimary
import com.pandora.app.core.designsystem.theme.PorcelainContainer
import com.pandora.app.core.designsystem.theme.TextOutline
import kotlin.math.pow
import kotlin.math.sqrt

data class PatternDot(
    val index: Int,
    val row: Int,
    val col: Int,
    val center: Offset = Offset.Zero
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PatternLockView(
    modifier: Modifier = Modifier,
    nodeColor: Color = TextOutline.copy(alpha = 0.6f),
    activeNodeColor: Color = IrisPrimary,
    lineColor: Color = ApricotOrange.copy(alpha = 0.85f),
    onPatternCompleted: (List<Int>) -> Unit
) {
    val selectedNodes = remember { mutableStateListOf<Int>() }
    var currentTouchPosition by remember { mutableStateOf<Offset?>(null) }
    var dotCenters by remember { mutableStateOf<List<Offset>>(emptyList()) }

    Box(
        modifier = modifier
            .size(280.dp)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .pointerInteropFilter { motionEvent ->
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                            val touchPos = Offset(motionEvent.x, motionEvent.y)
                            currentTouchPosition = touchPos

                            // Hit detection on dots
                            dotCenters.forEachIndexed { index, center ->
                                val distance = sqrt((touchPos.x - center.x).pow(2) + (touchPos.y - center.y).pow(2))
                                if (distance <= 40f && !selectedNodes.contains(index)) {
                                    selectedNodes.add(index)
                                }
                            }
                            true
                        }
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            if (selectedNodes.isNotEmpty()) {
                                onPatternCompleted(selectedNodes.toList())
                            }
                            selectedNodes.clear()
                            currentTouchPosition = null
                            true
                        }
                        else -> false
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val cellWidth = width / 2
            val cellHeight = height / 2

            // Calculate 3x3 dot centers
            val centers = (0..8).map { index ->
                val row = index / 3
                val col = index % 3
                Offset(col * cellWidth, row * cellHeight)
            }
            dotCenters = centers

            // Draw connecting lines between selected nodes
            if (selectedNodes.isNotEmpty()) {
                for (i in 0 until selectedNodes.size - 1) {
                    val start = centers[selectedNodes[i]]
                    val end = centers[selectedNodes[i + 1]]
                    drawLine(
                        color = lineColor,
                        start = start,
                        end = end,
                        strokeWidth = 6.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // Draw line to current touch point
                currentTouchPosition?.let { touchPos ->
                    val lastNodeCenter = centers[selectedNodes.last()]
                    drawLine(
                        color = lineColor,
                        start = lastNodeCenter,
                        end = touchPos,
                        strokeWidth = 5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }

            // Draw the 9 nodes
            centers.forEachIndexed { index, center ->
                val isSelected = selectedNodes.contains(index)
                val dotRadius = if (isSelected) 10.dp.toPx() else 7.dp.toPx()
                val outerRadius = 24.dp.toPx()

                if (isSelected) {
                    // Outer glow/ring
                    drawCircle(
                        color = activeNodeColor.copy(alpha = 0.25f),
                        radius = outerRadius,
                        center = center
                    )
                    drawCircle(
                        color = activeNodeColor,
                        radius = outerRadius,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                // Inner solid dot
                drawCircle(
                    color = if (isSelected) activeNodeColor else nodeColor,
                    radius = dotRadius,
                    center = center
                )
            }
        }
    }
}
