package com.bynd.esp32dashboard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.bynd.esp32dashboard.ui.theme.AppColors

@Composable
fun LdrCard(
    percent: Int,
    state: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    AppCard {
        CardHeader(
            icon = Icons.Default.WbSunny,
            iconTint = AppColors.Warning,
            iconBg = androidx.compose.ui.graphics.Color(0xFFFFF4E5),
            title = "Sensor de Luz (LDR)",
            subtitle = "Nivel de iluminación",
            enabled = enabled,
            onToggle = onToggle
        )
        Spacer(Modifier.height(12.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier.size(140.dp),
                contentAlignment = Alignment.Center
            ) {
                DonutChart(
                    progress = percent / 100f,
                    color = AppColors.Primary,
                    track = AppColors.SliderTrack,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    "$percent%",
                    style = MaterialTheme.typography.titleLarge,
                    color = AppColors.Primary
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            StatusPill(
                text = state.replaceFirstChar { it.uppercase() },
                color = AppColors.Warning,
                bg = androidx.compose.ui.graphics.Color(0xFFFFF4E5)
            )
        }
    }
}

@Composable
fun DonutChart(
    progress: Float,
    color: androidx.compose.ui.graphics.Color,
    track: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    val sweep = (progress.coerceIn(0f, 1f)) * 360f
    val strokeWidth = 14.dp
    Canvas(modifier = modifier) {
        val sw = strokeWidth.toPx()
        val arcSize = Size(size.width - sw, size.height - sw)
        val topLeft = Offset(sw / 2, sw / 2)
        drawArc(
            color = track,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = sw)
        )
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = sweep,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = sw, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        )
    }
}
