package com.bynd.esp32dashboard.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bynd.esp32dashboard.ui.theme.AppColors

/**
 * Control del servo motor.
 * El slider hace updates locales mientras se arrastra,
 * y solo manda el POST cuando el dedo se suelta. -bynd
 */
@Composable
fun ServoControl(
    name: String,
    angle: Int,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    onLocalChange: (Int) -> Unit,
    onRelease: (Int) -> Unit
) {
    var localAngle by remember(angle) { mutableIntStateOf(angle) }
    var dragging by remember { mutableStateOf(false) }

    AppCard {
        CardHeader(
            icon = Icons.Default.Speed,
            iconTint = AppColors.Primary,
            iconBg = AppColors.PrimaryLight,
            title = name,
            subtitle = "Ángulo: ${localAngle}°",
            enabled = enabled,
            onToggle = onToggle
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "${localAngle}°",
            style = MaterialTheme.typography.titleLarge,
            color = if (enabled) AppColors.Primary else AppColors.TextSecondary
        )
        Spacer(Modifier.height(4.dp))
        Slider(
            value = localAngle.toFloat(),
            onValueChange = {
                dragging = true
                localAngle = it.toInt()
                onLocalChange(localAngle)
            },
            onValueChangeFinished = {
                if (dragging) {
                    dragging = false
                    onRelease(localAngle)
                }
            },
            valueRange = 0f..180f,
            steps = 0,
            enabled = enabled,
            colors = SliderDefaults.colors(
                thumbColor = AppColors.CardBg,
                activeTrackColor = AppColors.Primary,
                inactiveTrackColor = AppColors.SliderTrack
            )
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("0°", style = MaterialTheme.typography.bodySmall, color = AppColors.TextSecondary)
            Text("180°", style = MaterialTheme.typography.bodySmall, color = AppColors.TextSecondary)
        }
    }
}
