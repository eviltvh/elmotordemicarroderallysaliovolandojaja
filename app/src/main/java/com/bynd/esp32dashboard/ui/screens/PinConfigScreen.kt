package com.bynd.esp32dashboard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bynd.esp32dashboard.data.PinAssignment
import com.bynd.esp32dashboard.data.PinFunction
import com.bynd.esp32dashboard.data.PinValidator
import com.bynd.esp32dashboard.ui.components.AppCard
import com.bynd.esp32dashboard.ui.components.PinSelector
import com.bynd.esp32dashboard.ui.components.SectionHeader
import com.bynd.esp32dashboard.ui.theme.AppColors
import com.bynd.esp32dashboard.viewmodel.PinConfigViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinConfigScreen(
    onClose: () -> Unit,
    vm: PinConfigViewModel = viewModel()
) {
    val ui by vm.ui.collectAsState()
    var showDiscardDialog by remember { mutableStateOf(false) }

    val tryClose: () -> Unit = {
        if (ui.hasChanges) showDiscardDialog = true else onClose()
    }

    Scaffold(
        modifier = Modifier.background(AppColors.ScreenBg),
        topBar = {
            Surface(color = AppColors.ScreenBg, shadowElevation = 0.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = tryClose) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar",
                            tint = AppColors.TextPrimary)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("⚙️ Configuración de Pines",
                        style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.weight(1f))
                    TextButton(
                        onClick = vm::save,
                        enabled = ui.validation.isValid && ui.hasChanges && !ui.saving
                    ) { Text("Guardar", color = AppColors.Primary) }
                }
            }
        },
        containerColor = AppColors.ScreenBg
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Aviso superior
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.Warning.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Warning, contentDescription = null,
                    tint = AppColors.Warning, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    "Cambiar pines reinicia la conexión con el ESP32",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.Warning
                )
            }

            SectionHeader("CONEXIÓN")
            AppCard {
                OutlinedTextField(
                    value = ui.ip,
                    onValueChange = vm::updateIp,
                    label = { Text("IP del ESP32") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = ui.port,
                    onValueChange = vm::updatePort,
                    label = { Text("Puerto") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            SectionHeader("ACTUADORES")
            ComponentCard("Servo Motor") {
                PinSelector(
                    label = "Pin de señal",
                    selected = ui.pins.servo,
                    requiredFunction = PinFunction.PWM_OUT,
                    usedPins = PinValidator.usedPins(ui.pins, "Servo"),
                    onSelected = { vm.updatePins(ui.pins.copy(servo = it)) },
                    isError = ui.validation.errors.any { it.component == "Servo" }
                )
                IssuesBlock(ui.validation, "Servo")
            }

            SectionHeader("SENSORES")
            ComponentCard("HC-SR04 (Ultrasónico)") {
                PinSelector(
                    label = "Trig (Output)",
                    selected = ui.pins.trig,
                    requiredFunction = PinFunction.DIGITAL_OUT,
                    usedPins = PinValidator.usedPins(ui.pins, "HC-SR04 Trig"),
                    onSelected = { vm.updatePins(ui.pins.copy(trig = it)) },
                    isError = ui.validation.errors.any { it.component == "HC-SR04 Trig" }
                )
                IssuesBlock(ui.validation, "HC-SR04 Trig")
                Spacer(Modifier.height(8.dp))
                PinSelector(
                    label = "Echo (Input)",
                    selected = ui.pins.echo,
                    requiredFunction = PinFunction.DIGITAL_IN,
                    usedPins = PinValidator.usedPins(ui.pins, "HC-SR04 Echo"),
                    onSelected = { vm.updatePins(ui.pins.copy(echo = it)) },
                    isError = ui.validation.errors.any { it.component == "HC-SR04 Echo" }
                )
                IssuesBlock(ui.validation, "HC-SR04 Echo")
            }

            ComponentCard("Sensor de Agua") {
                PinSelector(
                    label = "Pin (ADC1)",
                    selected = ui.pins.water,
                    requiredFunction = PinFunction.ANALOG_IN_ADC1,
                    usedPins = PinValidator.usedPins(ui.pins, "Sensor de Agua"),
                    onSelected = { vm.updatePins(ui.pins.copy(water = it)) },
                    isError = ui.validation.errors.any { it.component == "Sensor de Agua" }
                )
                IssuesBlock(ui.validation, "Sensor de Agua")
            }

            ComponentCard("LDR / Fotoresistencia") {
                PinSelector(
                    label = "Pin (ADC1)",
                    selected = ui.pins.ldr,
                    requiredFunction = PinFunction.ANALOG_IN_ADC1,
                    usedPins = PinValidator.usedPins(ui.pins, "LDR"),
                    onSelected = { vm.updatePins(ui.pins.copy(ldr = it)) },
                    isError = ui.validation.errors.any { it.component == "LDR" }
                )
                IssuesBlock(ui.validation, "LDR")
            }

            ComponentCard("Sensor de Temperatura") {
                Text(
                    "🔲 Placeholder — sin pin asignado por defecto",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary
                )
                Spacer(Modifier.height(8.dp))
                PinSelector(
                    label = "Pin",
                    selected = ui.pins.temp,
                    requiredFunction = PinFunction.DIGITAL_IN,
                    usedPins = PinValidator.usedPins(ui.pins, "Temperatura"),
                    onSelected = { vm.updatePins(ui.pins.copy(temp = it)) },
                    isError = ui.validation.errors.any { it.component == "Temperatura" }
                )
                IssuesBlock(ui.validation, "Temperatura")
            }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = vm::save,
                enabled = ui.validation.isValid && ui.hasChanges && !ui.saving,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
            ) {
                if (ui.saving) {
                    CircularProgressIndicator(
                        color = androidx.compose.ui.graphics.Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Guardando…")
                } else {
                    Text("Guardar y reconectar", fontWeight = FontWeight.SemiBold)
                }
            }

            // Resumen visual de errores totales
            if (ui.validation.errors.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppColors.Error.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        "${ui.validation.errors.size} error(es) — corrige antes de guardar",
                        color = AppColors.Error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }

    // Toast de resultado
    ui.saveResult?.let { result ->
        LaunchedEffect(result) {
            kotlinx.coroutines.delay(3_500)
            vm.clearResult()
            if (ui.saveSuccess == true) onClose()
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                color = if (ui.saveSuccess == true) AppColors.Success else AppColors.Warning,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 4.dp
            ) {
                Text(
                    result,
                    color = androidx.compose.ui.graphics.Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("¿Descartar cambios?") },
            text = { Text("Tienes cambios sin guardar.") },
            confirmButton = {
                TextButton(onClick = {
                    showDiscardDialog = false
                    vm.discardChanges()
                    onClose()
                }) { Text("Descartar", color = AppColors.Error) }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Seguir editando")
                }
            }
        )
    }
}

@Composable
private fun ComponentCard(title: String, content: @Composable () -> Unit) {
    AppCard {
        Text(title, style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun IssuesBlock(v: PinValidator.ValidationResult, component: String) {
    val errs = v.errors.filter { it.component == component }
    val warns = v.warnings.filter { it.component == component }
    if (errs.isEmpty() && warns.isEmpty()) return
    Spacer(Modifier.height(6.dp))
    errs.forEach {
        Text("🔴 ${it.message}",
            style = MaterialTheme.typography.bodySmall, color = AppColors.Error)
    }
    warns.forEach {
        Text("⚠️ ${it.message}",
            style = MaterialTheme.typography.bodySmall, color = AppColors.Warning)
    }
}
