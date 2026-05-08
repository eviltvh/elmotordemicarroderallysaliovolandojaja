package com.bynd.esp32dashboard.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ESP32Api {

    @GET("status")
    suspend fun status(): StatusResponse

    @GET("servo")
    suspend fun getServo(): ServoState

    @POST("servo")
    suspend fun postServo(@Body req: ServoAngleRequest): ServoState

    @POST("servo/toggle")
    suspend fun toggleServo(@Body req: ToggleRequest): ServoState

    @GET("sensor/ultrasonic")
    suspend fun getUltrasonic(): UltrasonicReading

    @GET("sensor/water")
    suspend fun getWater(): WaterReading

    @GET("sensor/ldr")
    suspend fun getLdr(): LdrReading

    @GET("sensor/temperature")
    suspend fun getTemperature(): TemperatureReading

    @GET("config/pins")
    suspend fun getPinConfig(): PinConfigPayload

    @POST("config/pins")
    suspend fun postPinConfig(@Body cfg: PinConfigPayload): PinConfigPayload
}
