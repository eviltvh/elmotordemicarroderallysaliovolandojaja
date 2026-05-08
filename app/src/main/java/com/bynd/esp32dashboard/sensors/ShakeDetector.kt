package com.bynd.esp32dashboard.sensors

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

/**
 * Detector de shake gesture según el blueprint:
 *  - umbral ≥ 2.5G en cualquier eje
 *  - duración mínima 300 ms de movimiento continuo
 *  - cooldown de 2 segundos post-trigger
 * -bynd
 */
class ShakeDetector(
    private val onShake: () -> Unit
) : SensorEventListener {

    companion object {
        private const val SHAKE_THRESHOLD_GS = 2.5f
        private const val MIN_DURATION_MS = 300L
        private const val COOLDOWN_MS = 2_000L
        private const val GRAVITY = SensorManager.GRAVITY_EARTH
    }

    private var continuousStart: Long = 0
    private var lastTrigger: Long = 0

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return
        val (x, y, z) = Triple(event.values[0], event.values[1], event.values[2])
        val gForce = sqrt(x * x + y * y + z * z) / GRAVITY

        val now = System.currentTimeMillis()
        if (gForce >= SHAKE_THRESHOLD_GS) {
            if (continuousStart == 0L) continuousStart = now
            val duration = now - continuousStart
            val sinceLast = now - lastTrigger
            if (duration >= MIN_DURATION_MS && sinceLast >= COOLDOWN_MS) {
                lastTrigger = now
                continuousStart = 0
                onShake()
            }
        } else {
            continuousStart = 0
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
