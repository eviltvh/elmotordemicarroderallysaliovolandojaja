package com.bynd.esp32dashboard.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bynd.esp32dashboard.data.PinAssignment
import com.bynd.esp32dashboard.data.PinValidator
import com.bynd.esp32dashboard.data.PreferencesManager
import com.bynd.esp32dashboard.network.ApiClient
import com.bynd.esp32dashboard.network.PinConfigPayload
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class PinConfigUiState(
    val ip: String = PreferencesManager.DEFAULT_IP,
    val port: String = PreferencesManager.DEFAULT_PORT.toString(),
    val pins: PinAssignment = PinAssignment(),
    val initialPins: PinAssignment = PinAssignment(),
    val initialIp: String = PreferencesManager.DEFAULT_IP,
    val initialPort: String = PreferencesManager.DEFAULT_PORT.toString(),
    val saving: Boolean = false,
    val saveResult: String? = null,
    val saveSuccess: Boolean? = null,
    val validation: PinValidator.ValidationResult = PinValidator.ValidationResult(emptyList())
) {
    val hasChanges: Boolean get() =
        pins != initialPins || ip != initialIp || port != initialPort
}

/**
 * ViewModel para la pantalla de configuración de pines (shake gesture). -bynd
 */
class PinConfigViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = PreferencesManager(app)
    private val _ui = MutableStateFlow(PinConfigUiState())
    val ui = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            val ip = prefs.ip.first()
            val port = prefs.port.first()
            val pins = prefs.pins.first()
            _ui.value = PinConfigUiState(
                ip = ip,
                port = port.toString(),
                pins = pins,
                initialPins = pins,
                initialIp = ip,
                initialPort = port.toString(),
                validation = PinValidator.validate(pins)
            )
        }
    }

    fun updateIp(ip: String) { _ui.value = _ui.value.copy(ip = ip) }
    fun updatePort(p: String) { _ui.value = _ui.value.copy(port = p) }

    fun updatePins(pins: PinAssignment) {
        _ui.value = _ui.value.copy(
            pins = pins,
            validation = PinValidator.validate(pins)
        )
    }

    fun discardChanges() {
        val s = _ui.value
        _ui.value = s.copy(
            ip = s.initialIp,
            port = s.initialPort,
            pins = s.initialPins,
            validation = PinValidator.validate(s.initialPins)
        )
    }

    fun save() {
        val s = _ui.value
        if (!s.validation.isValid) return
        val portInt = s.port.toIntOrNull() ?: return
        viewModelScope.launch {
            _ui.value = s.copy(saving = true, saveResult = null, saveSuccess = null)
            // 1) Guardar localmente y reconfigurar Retrofit
            prefs.setConnection(s.ip, portInt)
            prefs.setPins(s.pins)

            // 2) Enviar al ESP32
            try {
                val api = ApiClient.api(s.ip, portInt)
                api.postPinConfig(
                    PinConfigPayload(
                        servo = s.pins.servo,
                        trig = s.pins.trig,
                        echo = s.pins.echo,
                        water = s.pins.water,
                        ldr = s.pins.ldr,
                        temp = s.pins.temp
                    )
                )

                // 3) Polling rápido al /status durante max 10s
                val deadline = System.currentTimeMillis() + 10_000
                var ok = false
                while (System.currentTimeMillis() < deadline) {
                    val r = runCatching { api.status() }
                    if (r.getOrNull()?.connected == true) { ok = true; break }
                    delay(1_000)
                }

                _ui.value = _ui.value.copy(
                    saving = false,
                    saveResult = if (ok) "✅ Configuración guardada" else "⚠️ No se pudo reconectar — revisa el ESP32",
                    saveSuccess = ok,
                    initialIp = s.ip,
                    initialPort = s.port,
                    initialPins = s.pins
                )
            } catch (t: Throwable) {
                _ui.value = _ui.value.copy(
                    saving = false,
                    saveResult = "⚠️ Error de red: ${t.message ?: "desconocido"}",
                    saveSuccess = false
                )
            }
        }
    }

    fun clearResult() {
        _ui.value = _ui.value.copy(saveResult = null, saveSuccess = null)
    }
}
