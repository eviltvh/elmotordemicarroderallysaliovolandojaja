package com.bynd.esp32dashboard.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bynd.esp32dashboard.data.ConnectionState
import com.bynd.esp32dashboard.data.DashboardSnapshot
import com.bynd.esp32dashboard.data.PreferencesManager
import com.bynd.esp32dashboard.network.ApiClient
import com.bynd.esp32dashboard.network.ESP32Api
import com.bynd.esp32dashboard.network.ServoAngleRequest
import com.bynd.esp32dashboard.network.ToggleRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class DashboardUiState(
    val ip: String = "",
    val port: Int = 80,
    val connection: ConnectionState = ConnectionState.DESCONECTADO,
    val pollingEnabled: Boolean = true,
    val servoEnabled: Boolean = true,
    val ultrasonicEnabled: Boolean = true,
    val waterEnabled: Boolean = true,
    val ldrEnabled: Boolean = true,
    val temperatureEnabled: Boolean = true,
    val snapshot: DashboardSnapshot = DashboardSnapshot(),
    val errorMessage: String? = null
)

/**
 * ViewModel del dashboard.
 * Orquesta polling cada 3s, control del servo y toggles de sensores. -bynd
 */
class DashboardViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = PreferencesManager(app)

    private val _ui = MutableStateFlow(DashboardUiState())
    val ui = _ui.asStateFlow()

    private var pollingJob: Job? = null

    init {
        // Cargar IP/puerto guardados
        viewModelScope.launch {
            combine(prefs.ip, prefs.port) { ip, port -> ip to port }
                .collect { (ip, port) ->
                    _ui.value = _ui.value.copy(ip = ip, port = port)
                    restartPolling()
                }
        }
    }

    private fun api(): ESP32Api {
        val s = _ui.value
        return ApiClient.api(s.ip, s.port)
    }

    fun setPolling(enabled: Boolean) {
        _ui.value = _ui.value.copy(pollingEnabled = enabled)
        if (enabled) restartPolling() else pollingJob?.cancel()
    }

    fun setServoEnabled(v: Boolean) {
        _ui.value = _ui.value.copy(servoEnabled = v)
        viewModelScope.launch { runCatching { api().toggleServo(ToggleRequest(v)) } }
    }
    fun setUltrasonicEnabled(v: Boolean) { _ui.value = _ui.value.copy(ultrasonicEnabled = v) }
    fun setWaterEnabled(v: Boolean)      { _ui.value = _ui.value.copy(waterEnabled = v) }
    fun setLdrEnabled(v: Boolean)        { _ui.value = _ui.value.copy(ldrEnabled = v) }
    fun setTemperatureEnabled(v: Boolean){ _ui.value = _ui.value.copy(temperatureEnabled = v) }

    /** Solo se llama al soltar el slider, no en cada onChange. */
    fun sendServoAngle(angle: Int) {
        viewModelScope.launch {
            runCatching { api().postServo(ServoAngleRequest(angle)) }
                .onSuccess { state ->
                    val snap = _ui.value.snapshot.copy(servo = state)
                    _ui.value = _ui.value.copy(snapshot = snap)
                }
        }
    }

    /** Optimistic update local mientras el slider se mueve, sin hacer red. */
    fun updateLocalServoAngle(angle: Int) {
        val cur = _ui.value.snapshot
        _ui.value = _ui.value.copy(snapshot = cur.copy(servo = cur.servo.copy(angle = angle)))
    }

    fun manualRefresh() {
        viewModelScope.launch { fetchOnce() }
    }

    private fun restartPolling() {
        pollingJob?.cancel()
        if (!_ui.value.pollingEnabled) return
        pollingJob = viewModelScope.launch {
            while (true) {
                fetchOnce()
                delay(3_000)
            }
        }
    }

    private suspend fun fetchOnce() {
        val s = _ui.value
        _ui.value = s.copy(connection = ConnectionState.CARGANDO)
        try {
            val a = api()
            // Status primero — nos dice si está vivo
            val status = a.status()
            val servo = if (s.servoEnabled) runCatching { a.getServo() }.getOrNull() else null
            val us    = if (s.ultrasonicEnabled) runCatching { a.getUltrasonic() }.getOrNull() else null
            val water = if (s.waterEnabled) runCatching { a.getWater() }.getOrNull() else null
            val ldr   = if (s.ldrEnabled) runCatching { a.getLdr() }.getOrNull() else null
            val temp  = if (s.temperatureEnabled) runCatching { a.getTemperature() }.getOrNull() else null

            val snap = s.snapshot.copy(
                servo = servo ?: s.snapshot.servo,
                ultrasonic = us ?: s.snapshot.ultrasonic,
                water = water ?: s.snapshot.water,
                ldr = ldr ?: s.snapshot.ldr,
                temperature = temp ?: s.snapshot.temperature,
                lastUpdate = System.currentTimeMillis()
            )
            _ui.value = _ui.value.copy(
                snapshot = snap,
                connection = if (status.connected) ConnectionState.CONECTADO else ConnectionState.DESCONECTADO,
                errorMessage = null
            )
        } catch (t: Throwable) {
            _ui.value = _ui.value.copy(
                connection = ConnectionState.DESCONECTADO,
                errorMessage = t.message
            )
        }
    }

    fun reloadConnection() {
        viewModelScope.launch {
            val ip = prefs.ip.first()
            val port = prefs.port.first()
            _ui.value = _ui.value.copy(ip = ip, port = port)
            restartPolling()
        }
    }
}
