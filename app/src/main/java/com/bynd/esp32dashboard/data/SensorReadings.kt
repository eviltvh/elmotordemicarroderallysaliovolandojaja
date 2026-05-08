package com.bynd.esp32dashboard.data

import com.bynd.esp32dashboard.network.LdrReading
import com.bynd.esp32dashboard.network.ServoState
import com.bynd.esp32dashboard.network.TemperatureReading
import com.bynd.esp32dashboard.network.UltrasonicReading
import com.bynd.esp32dashboard.network.WaterReading

/**
 * Snapshot completo del estado del dashboard.
 * El timestamp permite detectar caché expirada (> 10 min). -bynd
 */
data class DashboardSnapshot(
    val servo: ServoState = ServoState(),
    val ultrasonic: UltrasonicReading = UltrasonicReading(),
    val water: WaterReading = WaterReading(),
    val ldr: LdrReading = LdrReading(),
    val temperature: TemperatureReading = TemperatureReading(),
    val lastUpdate: Long = 0L
) {
    fun isCacheStale(now: Long = System.currentTimeMillis(), maxAgeMs: Long = 10 * 60_000L): Boolean {
        if (lastUpdate == 0L) return true
        return now - lastUpdate > maxAgeMs
    }
}

enum class ConnectionState { CONECTADO, DESCONECTADO, CARGANDO, ERROR, RECONECTANDO }
