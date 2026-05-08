package com.bynd.esp32dashboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bynd.esp32dashboard.ui.theme.AppColors

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = AppColors.TextSecondary,
        modifier = Modifier.padding(start = 4.dp, top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) { content() }
    }
}

@Composable
fun CardHeader(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    subtitle: String? = null,
    enabled: Boolean,
    onToggle: ((Boolean) -> Unit)? = null
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconBg, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            subtitle?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary
                )
            }
        }
        if (onToggle != null) {
            Switch(
                checked = enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = AppColors.Primary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = AppColors.SliderTrack,
                    uncheckedBorderColor = AppColors.SliderTrack
                )
            )
        }
    }
}

@Composable
fun CenteredValue(text: String, color: Color = AppColors.Primary) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(
            text,
            color = color,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun StatBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = AppColors.TextSecondary)
        Text(
            value,
            color = color,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
fun StatRow(stats: List<Triple<String, String, Color>>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        stats.forEach { (label, value, color) -> StatBadge(label, value, color) }
    }
}
