package com.bynd.esp32dashboard.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AppColors.Primary,
    onPrimary = AppColors.CardBg,
    primaryContainer = AppColors.PrimaryLight,
    onPrimaryContainer = AppColors.Primary,
    secondary = AppColors.TempOrange,
    background = AppColors.ScreenBg,
    onBackground = AppColors.TextPrimary,
    surface = AppColors.CardBg,
    onSurface = AppColors.TextPrimary,
    surfaceVariant = AppColors.IconBg,
    onSurfaceVariant = AppColors.TextSecondary,
    error = AppColors.Error,
    outline = AppColors.Divider
)

@Composable
fun ESP32DashboardTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
