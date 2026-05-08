package com.bynd.esp32dashboard.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("esp32_prefs")

/**
 * Persistencia de la configuración del dashboard.
 * Guarda IP, puerto y la asignación actual de pines. -bynd
 */
class PreferencesManager(private val context: Context) {

    companion object {
        private val IP = stringPreferencesKey("ip")
        private val PORT = intPreferencesKey("port")
        private val PIN_SERVO = intPreferencesKey("pin_servo")
        private val PIN_TRIG = intPreferencesKey("pin_trig")
        private val PIN_ECHO = intPreferencesKey("pin_echo")
        private val PIN_WATER = intPreferencesKey("pin_water")
        private val PIN_LDR = intPreferencesKey("pin_ldr")
        private val PIN_TEMP = intPreferencesKey("pin_temp")

        const val DEFAULT_IP = "192.168.1.105"
        const val DEFAULT_PORT = 80
    }

    val ip: Flow<String> = context.dataStore.data.map { it[IP] ?: DEFAULT_IP }
    val port: Flow<Int> = context.dataStore.data.map { it[PORT] ?: DEFAULT_PORT }

    val pins: Flow<PinAssignment> = context.dataStore.data.map { p ->
        PinAssignment(
            servo = p[PIN_SERVO] ?: 13,
            trig = p[PIN_TRIG] ?: 5,
            echo = p[PIN_ECHO] ?: 19,
            water = p[PIN_WATER] ?: 32,
            ldr = p[PIN_LDR] ?: 34,
            temp = p[PIN_TEMP].takeIf { it != null && it >= 0 }
        )
    }

    suspend fun setConnection(ip: String, port: Int) {
        context.dataStore.edit { p ->
            p[IP] = ip
            p[PORT] = port
        }
    }

    suspend fun setPins(a: PinAssignment) {
        context.dataStore.edit { p ->
            a.servo?.let { p[PIN_SERVO] = it }
            a.trig?.let { p[PIN_TRIG] = it }
            a.echo?.let { p[PIN_ECHO] = it }
            a.water?.let { p[PIN_WATER] = it }
            a.ldr?.let { p[PIN_LDR] = it }
            p[PIN_TEMP] = a.temp ?: -1
        }
    }
}
