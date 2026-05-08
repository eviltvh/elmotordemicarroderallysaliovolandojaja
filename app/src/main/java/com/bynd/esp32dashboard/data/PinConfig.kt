package com.bynd.esp32dashboard.data

/**
 * Tabla de capacidades de los GPIOs del ESP32 según el blueprint.
 * Es la fuente de verdad de la validación. -bynd
 */

enum class PinFunction { ANALOG_IN_ADC1, DIGITAL_IN, DIGITAL_OUT, PWM_OUT }

enum class PinWarning { NONE, BOOT_SENSITIVE, SERIAL_RESERVED, ADC2_WIFI_CONFLICT }

data class PinSpec(
    val gpio: Int,
    val supports: Set<PinFunction>,
    val notes: String,
    val warning: PinWarning = PinWarning.NONE
)

object Pins {

    // Lista derivada del blueprint del proyecto -bynd
    val ALL: List<PinSpec> = listOf(
        PinSpec(0, setOf(PinFunction.PWM_OUT, PinFunction.DIGITAL_IN, PinFunction.DIGITAL_OUT),
            "Boot fail si LOW al encender", PinWarning.BOOT_SENSITIVE),
        PinSpec(1, setOf(PinFunction.PWM_OUT, PinFunction.DIGITAL_IN, PinFunction.DIGITAL_OUT),
            "TX0 Serial — evitar", PinWarning.SERIAL_RESERVED),
        PinSpec(2, setOf(PinFunction.PWM_OUT, PinFunction.DIGITAL_IN, PinFunction.DIGITAL_OUT),
            "Must be LOW al flashear", PinWarning.BOOT_SENSITIVE),
        PinSpec(3, setOf(PinFunction.PWM_OUT, PinFunction.DIGITAL_IN, PinFunction.DIGITAL_OUT),
            "RX0 Serial — evitar", PinWarning.SERIAL_RESERVED),
        PinSpec(4, setOf(PinFunction.PWM_OUT, PinFunction.DIGITAL_IN, PinFunction.DIGITAL_OUT),
            "Seguro para uso general"),
        PinSpec(5, setOf(PinFunction.PWM_OUT, PinFunction.DIGITAL_IN, PinFunction.DIGITAL_OUT),
            "Trig HC-SR04 actual"),
        PinSpec(12, setOf(PinFunction.PWM_OUT, PinFunction.DIGITAL_IN, PinFunction.DIGITAL_OUT),
            "Boot fail si HIGH al encender", PinWarning.BOOT_SENSITIVE),
        PinSpec(13, setOf(PinFunction.PWM_OUT, PinFunction.DIGITAL_IN, PinFunction.DIGITAL_OUT),
            "Servo actual"),
        PinSpec(14, setOf(PinFunction.PWM_OUT, PinFunction.DIGITAL_IN, PinFunction.DIGITAL_OUT),
            "Genera PWM al boot"),
        PinSpec(15, setOf(PinFunction.PWM_OUT, PinFunction.DIGITAL_IN, PinFunction.DIGITAL_OUT),
            "Genera PWM al boot"),
        PinSpec(16, setOf(PinFunction.PWM_OUT, PinFunction.DIGITAL_IN, PinFunction.DIGITAL_OUT),
            "Seguro"),
        PinSpec(17, setOf(PinFunction.PWM_OUT, PinFunction.DIGITAL_IN, PinFunction.DIGITAL_OUT),
            "Seguro"),
        PinSpec(18, setOf(PinFunction.PWM_OUT, PinFunction.DIGITAL_IN, PinFunction.DIGITAL_OUT),
            "SPI CLK"),
        PinSpec(19, setOf(PinFunction.PWM_OUT, PinFunction.DIGITAL_IN, PinFunction.DIGITAL_OUT),
            "Echo HC-SR04 actual"),
        PinSpec(21, setOf(PinFunction.PWM_OUT, PinFunction.DIGITAL_IN, PinFunction.DIGITAL_OUT),
            "I2C SDA"),
        PinSpec(22, setOf(PinFunction.PWM_OUT, PinFunction.DIGITAL_IN, PinFunction.DIGITAL_OUT),
            "I2C SCL"),
        PinSpec(23, setOf(PinFunction.PWM_OUT, PinFunction.DIGITAL_IN, PinFunction.DIGITAL_OUT),
            "SPI MOSI"),
        PinSpec(25, setOf(PinFunction.PWM_OUT, PinFunction.DIGITAL_IN, PinFunction.DIGITAL_OUT),
            "DAC1 (ADC2 — no WiFi)"),
        PinSpec(26, setOf(PinFunction.PWM_OUT, PinFunction.DIGITAL_IN, PinFunction.DIGITAL_OUT),
            "DAC2 (ADC2 — no WiFi)"),
        PinSpec(27, setOf(PinFunction.PWM_OUT, PinFunction.DIGITAL_IN, PinFunction.DIGITAL_OUT),
            "ADC2 — no WiFi"),
        PinSpec(32, setOf(PinFunction.ANALOG_IN_ADC1, PinFunction.PWM_OUT,
            PinFunction.DIGITAL_IN, PinFunction.DIGITAL_OUT), "Sensor de Agua actual"),
        PinSpec(33, setOf(PinFunction.ANALOG_IN_ADC1, PinFunction.PWM_OUT,
            PinFunction.DIGITAL_IN, PinFunction.DIGITAL_OUT), "ADC1 safe con WiFi"),
        PinSpec(34, setOf(PinFunction.ANALOG_IN_ADC1, PinFunction.DIGITAL_IN),
            "LDR actual — INPUT ONLY"),
        PinSpec(35, setOf(PinFunction.ANALOG_IN_ADC1, PinFunction.DIGITAL_IN),
            "INPUT ONLY"),
        PinSpec(36, setOf(PinFunction.ANALOG_IN_ADC1, PinFunction.DIGITAL_IN),
            "INPUT ONLY (VP)"),
        PinSpec(39, setOf(PinFunction.ANALOG_IN_ADC1, PinFunction.DIGITAL_IN),
            "INPUT ONLY (VN)")
    )

    fun byGpio(gpio: Int?): PinSpec? = if (gpio == null) null else ALL.find { it.gpio == gpio }

    fun supports(gpio: Int, fn: PinFunction): Boolean =
        byGpio(gpio)?.supports?.contains(fn) ?: false

    fun candidates(fn: PinFunction): List<PinSpec> = ALL.filter { fn in it.supports }
}

/** Configuración local de pines en la app. */
data class PinAssignment(
    val servo: Int? = 13,
    val trig: Int? = 5,
    val echo: Int? = 19,
    val water: Int? = 32,
    val ldr: Int? = 34,
    val temp: Int? = null
)
