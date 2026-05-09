package com.bynd.esp32dashboard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bynd.esp32dashboard.ui.theme.AppColors

@Composable
fun TemperatureCard(
    currentC: Double?,
    history: List<Double>,
    isMock: Boolean,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val displayHistory: List<Double> = if (history.isNotEmpty()) history
    else listOf(22.0, 23.5, 23.0, 26.1, 25.0, 26.8, 26.0)
    val displayCurrent = currentC ?: 26.0
    val labels = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
    val mn = displayHistory.minOrNull() ?: 0.0
    val mx = displayHistory.maxOrNull() ?: 0.0
    val avg = if (displayHistory.isNotEmpty()) displayHistory.average() else 0.0

    AppCard {
        CardHeader(
            icon = Icons.Default.Thermostat,
            iconTint = AppColors.TempOrange,
            iconBg = AppColors.TempOrangeLight,
            title = "Sensor de Temperatura",
            subtitle = if (isMock) "⚠️ Sin configurar — datos de ejemplo" else "Historial de lecturas",
            enabled = enabled,
            onToggle = onToggle
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "${"%.0f".format(displayCurrent)}°C",
            style = MaterialTheme.typography.titleLarge,
            color = AppColors.TempOrange,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        LineChartCanvas(
            data = displayHistory,
            labels = labels,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        )
        Spacer(Modifier.height(16.dp))
        StatRow(
            stats = listOf(
                Triple("Mínima", "${"%.0f".format(mn)}°C", AppColors.Primary),
                Triple("Promedio", "${"%.1f".format(avg)}°C", AppColors.Primary),
                Triple("Máxima", "${"%.0f".format(mx)}°C", AppColors.TempOrange)
            )
        )
        if (isMock) {
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.TempOrange.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Text(
                    "Asigna un pin desde la configuración para ver datos reales",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TempOrange
                )
            }
        }
    }
}

/**
 * Line chart hecho a mano con Canvas — sin librerías externas.
 * Variables renombradas para no chocar con Paint.color dentro de apply { }. -bynd
 */
@Composable
fun LineChartCanvas(
    data: List<Double>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    val lineColor: Color = AppColors.Primary
    val gridColor: Color = AppColors.Divider
    val labelArgb: Int = AppColors.TextSecondary.toArgb()

    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas
        val padLeft = 36f
        val padRight = 12f
        val padTop = 12f
        val padBottom = 28f
        val w = size.width - padLeft - padRight
        val h = size.height - padTop - padBottom
        if (w <= 0 || h <= 0) return@Canvas

        val mn = data.min()
        val mx = data.max()
        val mnRound = (mn - 1).toInt().coerceAtLeast(0).toDouble()
        val mxRound = (mx + 1).toInt().toDouble()
        val rangeRound = (mxRound - mnRound).takeIf { it > 0 } ?: 1.0

        // Grid horizontal — 4 líneas
        val gridLines = 4
        val nativeCanvasRef = drawContext.canvas.nativeCanvas
        val yPaint = android.graphics.Paint().apply {
            this.color = labelArgb
            textSize = 22f
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.RIGHT
        }

        for (i in 0..gridLines) {
            val y = padTop + h * i / gridLines
            drawLine(
                color = gridColor,
                start = Offset(padLeft, y),
                end = Offset(padLeft + w, y),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 6f))
            )
            val v = mxRound - (rangeRound * i / gridLines)
            nativeCanvasRef.drawText("${v.toInt()}", padLeft - 6f, y + 7f, yPaint)
        }

        val n = data.size
        if (n < 2) return@Canvas
        val stepX = w / (n - 1)
        val points = data.mapIndexed { idx, value ->
            val x = padLeft + idx * stepX
            val y = padTop + h * (1f - ((value - mnRound) / rangeRound).toFloat())
            Offset(x, y)
        }

        // Línea
        for (i in 0 until points.size - 1) {
            drawLine(
                color = lineColor,
                start = points[i],
                end = points[i + 1],
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }
        // Puntos (anillo blanco con borde de color)
        for (p in points) {
            drawCircle(color = Color.White, radius = 6f, center = p)
            drawCircle(
                color = lineColor,
                radius = 6f,
                center = p,
                style = Stroke(width = 2.5f)
            )
        }

        // Labels eje X
        val xPaint = android.graphics.Paint().apply {
            this.color = labelArgb
            textSize = 22f
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
        }
        labels.forEachIndexed { idx, lab ->
            if (idx >= n) return@forEachIndexed
            val x = padLeft + idx * stepX
            nativeCanvasRef.drawText(lab, x, size.height - 6f, xPaint)
        }
    }
}