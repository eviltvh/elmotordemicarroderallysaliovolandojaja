package com.bynd.esp32dashboard.data

/**
 * Validación de la configuración de pines.
 * Reglas del blueprint:
 *  - ADC2 → no disponible con WiFi activo
 *  - 34, 35, 36, 39 → solo INPUT
 *  - 6–11 → reservados flash, NUNCA usar
 *  - 0, 2, 12 → afectan boot
 *  - 1, 3 → serial
 *  - No puede haber dos componentes en el mismo pin
 *  -bynd
 */
object PinValidator {

    sealed class Issue(val component: String, val message: String) {
        class Error(component: String, message: String) : Issue(component, message)
        class Warning(component: String, message: String) : Issue(component, message)
    }

    data class ValidationResult(
        val issues: List<Issue>
    ) {
        val errors get() = issues.filterIsInstance<Issue.Error>()
        val warnings get() = issues.filterIsInstance<Issue.Warning>()
        val isValid get() = errors.isEmpty()
    }

    fun validate(a: PinAssignment): ValidationResult {
        val issues = mutableListOf<Issue>()

        // Cada componente con su función requerida
        val checks: List<Triple<String, Int?, PinFunction>> = listOf(
            Triple("Servo", a.servo, PinFunction.PWM_OUT),
            Triple("HC-SR04 Trig", a.trig, PinFunction.DIGITAL_OUT),
            Triple("HC-SR04 Echo", a.echo, PinFunction.DIGITAL_IN),
            Triple("Sensor de Agua", a.water, PinFunction.ANALOG_IN_ADC1),
            Triple("LDR", a.ldr, PinFunction.ANALOG_IN_ADC1),
            Triple("Temperatura", a.temp, PinFunction.DIGITAL_IN)
        )

        for ((name, gpio, fn) in checks) {
            if (gpio == null) continue
            val spec = Pins.byGpio(gpio)
            if (spec == null) {
                issues += Issue.Error(name, "Pin GPIO $gpio no es válido")
                continue
            }
            if (fn !in spec.supports) {
                val msg = when (fn) {
                    PinFunction.ANALOG_IN_ADC1 -> "Este pin no es ADC1 — no funciona con WiFi"
                    PinFunction.PWM_OUT -> "Este pin no soporta PWM/output"
                    PinFunction.DIGITAL_OUT -> "Este pin no puede ser salida"
                    PinFunction.DIGITAL_IN -> "Este pin no soporta entrada digital"
                }
                issues += Issue.Error(name, msg)
            }
            when (spec.warning) {
                PinWarning.BOOT_SENSITIVE ->
                    issues += Issue.Warning(name, "Pin $gpio puede causar problemas al encender")
                PinWarning.SERIAL_RESERVED ->
                    issues += Issue.Warning(name, "Pin $gpio reservado para serial")
                PinWarning.ADC2_WIFI_CONFLICT ->
                    issues += Issue.Error(name, "Pin $gpio (ADC2) no funciona con WiFi activo")
                PinWarning.NONE -> Unit
            }
        }

        // Conflictos: mismo pin asignado a más de un componente
        val pairs = listOf(
            "Servo" to a.servo,
            "HC-SR04 Trig" to a.trig,
            "HC-SR04 Echo" to a.echo,
            "Sensor de Agua" to a.water,
            "LDR" to a.ldr,
            "Temperatura" to a.temp
        ).filter { it.second != null }

        val grouped = pairs.groupBy { it.second }
        for ((gpio, members) in grouped) {
            if (members.size > 1 && gpio != null) {
                val names = members.joinToString(", ") { it.first }
                members.forEach { (name, _) ->
                    val others = members.filter { it.first != name }.joinToString(", ") { it.first }
                    issues += Issue.Error(name, "Pin $gpio en conflicto con $others")
                }
            }
        }

        return ValidationResult(issues)
    }

    fun usedPins(a: PinAssignment, exclude: String): Set<Int> {
        val list = listOf(
            "Servo" to a.servo,
            "HC-SR04 Trig" to a.trig,
            "HC-SR04 Echo" to a.echo,
            "Sensor de Agua" to a.water,
            "LDR" to a.ldr,
            "Temperatura" to a.temp
        )
        return list.filter { it.first != exclude }.mapNotNull { it.second }.toSet()
    }
}
