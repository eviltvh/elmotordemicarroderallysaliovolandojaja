package com.bynd.esp32dashboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.bynd.esp32dashboard.data.ConnectionState
import com.bynd.esp32dashboard.ui.theme.AppColors

private data class HeaderStyle(
    val icon: ImageVector,
    val iconTint: Color,
    val iconBg: Color,
    val statusText: String,
    val statusColor: Color
)

@Composable
fun ConnectionHeader(
    state: ConnectionState,
    ip: String,
    pollingEnabled: Boolean,
    onTogglePolling: (Boolean) -> Unit
) {
    val style = when (state) {
        ConnectionState.CONECTADO -> HeaderStyle(
            Icons.Default.Wifi, AppColors.Primary, AppColors.PrimaryLight,
            "Conectado · $ip", AppColors.Success
        )
        ConnectionState.RECONECTANDO -> HeaderStyle(
            Icons.Default.Wifi, AppColors.Warning, Color(0xFFFFF4E5),
            "Reconectando…", AppColors.Warning
        )
        ConnectionState.CARGANDO -> HeaderStyle(
            Icons.Default.Wifi, AppColors.Primary, AppColors.PrimaryLight,
            "Conectando…", AppColors.TextSecondary
        )
        else -> HeaderStyle(
            Icons.Default.WifiOff, AppColors.TextSecondary, AppColors.IconBg,
            "Desconectado", AppColors.TextSecondary
        )
    }
    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(style.iconBg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) { Icon(style.icon, contentDescription = null, tint = style.iconTint) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("ESP32 Dashboard", style = MaterialTheme.typography.titleLarge)
                Text(style.statusText, style = MaterialTheme.typography.bodySmall, color = style.statusColor)
            }
            Switch(
                checked = pollingEnabled,
                onCheckedChange = onTogglePolling,
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
