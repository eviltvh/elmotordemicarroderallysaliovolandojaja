package com.bynd.esp32dashboard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bynd.esp32dashboard.ui.components.*
import com.bynd.esp32dashboard.ui.theme.AppColors
import com.bynd.esp32dashboard.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    onOpenConfig: () -> Unit,
    vm: DashboardViewModel = viewModel()
) {
    val ui by vm.ui.collectAsState()
    val snap = ui.snapshot

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.ScreenBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ConnectionHeader(
                state = ui.connection,
                ip = ui.ip,
                pollingEnabled = ui.pollingEnabled,
                onTogglePolling = vm::setPolling
            )

            SectionHeader("Control de Actuadores")
            ServoControl(
                name = "Servo Motor",
                angle = snap.servo.angle,
                enabled = ui.servoEnabled,
                onToggle = vm::setServoEnabled,
                onLocalChange = vm::updateLocalServoAngle,
                onRelease = vm::sendServoAngle
            )

            SectionHeader("Monitoreo de Sensores")
            UltrasonicCard(
                distanceCm = snap.ultrasonic.distanceCm,
                enabled = ui.ultrasonicEnabled,
                onToggle = vm::setUltrasonicEnabled
            )
            WaterCard(
                state = snap.water.state,
                percent = snap.water.percent,
                enabled = ui.waterEnabled,
                onToggle = vm::setWaterEnabled
            )
            LdrCard(
                percent = snap.ldr.percent,
                state = snap.ldr.state,
                enabled = ui.ldrEnabled,
                onToggle = vm::setLdrEnabled
            )
            TemperatureCard(
                currentC = snap.temperature.current,
                history = snap.temperature.history,
                isMock = snap.temperature.pin == null,
                enabled = ui.temperatureEnabled,
                onToggle = vm::setTemperatureEnabled
            )

            ui.errorMessage?.let { msg ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppColors.Error.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        "⚠️ $msg",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.Error
                    )
                }
            }
        }

        // FAB para abrir config sin shake
        FloatingActionButton(
            onClick = onOpenConfig,
            containerColor = AppColors.Primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Settings, contentDescription = "Configuración")
        }
    }
}
