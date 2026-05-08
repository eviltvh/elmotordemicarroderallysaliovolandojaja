package com.bynd.esp32dashboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bynd.esp32dashboard.ui.theme.AppColors

@Composable
fun WaterCard(
    state: String,
    percent: Int,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val (statusColor, statusBg) = when (state.lowercase()) {
        "mojado" -> AppColors.Primary to AppColors.PrimaryLight
        "húmedo", "humedo" -> AppColors.Success to AppColors.SuccessLight
        else -> AppColors.TextSecondary to AppColors.IconBg
    }
    AppCard {
        CardHeader(
            icon = Icons.Default.Opacity,
            iconTint = AppColors.Primary,
            iconBg = AppColors.PrimaryLight,
            title = "Sensor de Agua",
            subtitle = state.replaceFirstChar { it.uppercase() },
            enabled = enabled,
            onToggle = onToggle
        )
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusPill(text = state.replaceFirstChar { it.uppercase() }, color = statusColor, bg = statusBg)
            Spacer(Modifier.weight(1f))
            Text(
                "$percent%",
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.Primary
            )
        }
        Spacer(Modifier.height(10.dp))
        ProgressBar(value = percent.toFloat(), max = 100f, color = AppColors.Primary)
    }
}

@Composable
fun StatusPill(text: String, color: Color, bg: Color) {
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text, color = color, style = MaterialTheme.typography.bodySmall)
    }
}
