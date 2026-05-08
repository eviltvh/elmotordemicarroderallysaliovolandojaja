package com.bynd.esp32dashboard.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Mapeo 1:1 contra los endpoints del firmware ESP32 -bynd

@Serializable
data class StatusResponse(
    val connected: Boolean = true,
    val ip: String = "",
    val uptime: Long = 0
)

@Serializable
data class ServoState(
    val angle: Int = 90,
    val enabled: Boolean = true,
    val pin: Int = 13
)

@Serializable
data class ServoAngleRequest(
    val angle: Int
)

@Serializable
data class ToggleRequest(
    val enabled: Boolean
)

@Serializable
data class UltrasonicReading(
    @SerialName("distance_cm") val distanceCm: Double = 0.0,
    @SerialName("pin_trig") val pinTrig: Int = 5,
    @SerialName("pin_echo") val pinEcho: Int = 19
)

@Serializable
data class WaterReading(
    val raw: Int = 0,
    val percent: Int = 0,
    val state: String = "seco",
    val pin: Int = 32
)

@Serializable
data class LdrReading(
    val raw: Int = 0,
    val percent: Int = 0,
    val state: String = "oscuro",
    val pin: Int = 34
)

@Serializable
data class TemperatureReading(
    val current: Double? = null,
    val unit: String = "C",
    val history: List<Double> = emptyList(),
    val pin: Int? = null
)

@Serializable
data class PinConfigPayload(
    val servo: Int? = 13,
    val trig: Int? = 5,
    val echo: Int? = 19,
    val water: Int? = 32,
    val ldr: Int? = 34,
    val temp: Int? = null
)
