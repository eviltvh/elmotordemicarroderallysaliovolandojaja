package com.bynd.esp32dashboard.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * Singleton del cliente Retrofit.
 * Se reconstruye con [rebuild] cuando el usuario cambia la IP/puerto del ESP32.
 * -bynd
 */
object ApiClient {

    @Volatile
    private var current: ESP32Api? = null

    @Volatile
    private var currentBaseUrl: String = ""

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
    }

    private val okHttp: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    fun api(ip: String, port: Int): ESP32Api {
        val baseUrl = "http://$ip:$port/"
        val cached = current
        if (cached != null && currentBaseUrl == baseUrl) return cached
        return rebuild(baseUrl)
    }

    @Synchronized
    private fun rebuild(baseUrl: String): ESP32Api {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttp)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        val api = retrofit.create(ESP32Api::class.java)
        current = api
        currentBaseUrl = baseUrl
        return api
    }
}
