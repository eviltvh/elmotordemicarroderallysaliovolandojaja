package com.bynd.esp32dashboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bynd.esp32dashboard.ui.theme.AppColors

@Composable
fun UltrasonicCard(
    distanceCm: Double,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val nearAlert = enabled && distanceCm in 0.0..10.0
    AppCard {
        CardHeader(
            icon = Icons.Default.Sensors,
            iconTint = AppColors.Primary,
            iconBg = AppColors.PrimaryLight,
            title = "Sensor Ultrasónico",
            subtitle = "HC-SR04",
            enabled = enabled,
            onToggle = onToggle
        )
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                String.format("%.0f", distanceCm),
                style = MaterialTheme.typography.titleLarge,
                color = if (nearAlert) AppColors.Error else AppColors.Primary
            )
            Spacer(Modifier.width(4.dp))
            Text(
                "cm",
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.TextSecondary
            )
        }
        Spacer(Modifier.height(12.dp))
        ProgressBar(
            value = distanceCm.toFloat().coerceIn(0f, 400f),
            max = 400f,
            color = if (nearAlert) AppColors.Error else AppColors.Primary
        )
        if (nearAlert) {
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.Error.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Warning, contentDescription = null,
                    tint = AppColors.Error, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    "Objeto muy cercano (< 10 cm)",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.Error
                )
            }
        }
    }
}

@Composable
fun ProgressBar(value: Float, max: Float, color: androidx.compose.ui.graphics.Color) {
    val pct = (value / max).coerceIn(0f, 1f)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(AppColors.SliderTrack, RoundedCornerShape(4.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(pct)
                .background(color, RoundedCornerShape(4.dp))
        )
    }
}
