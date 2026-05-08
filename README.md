# ESP32 Dashboard

App Android (Kotlin + Jetpack Compose) para monitorear y controlar un ESP32 vía WiFi/HTTP REST.

Arquitectura:
- Kotlin 2.0 + Jetpack Compose (Material 3)
- ViewModel + StateFlow
- Retrofit + OkHttp + kotlinx.serialization
- DataStore Preferences para persistencia
- Custom Canvas para gráficas (sin libs externas)

## Vistas

**Dashboard** — pantalla principal con polling cada 3s:
- Header WiFi con estado y toggle de polling
- Servo motor (slider con `onValueChangeFinished` → manda POST solo al soltar)
- HC-SR04 ultrasónico con barra y alerta < 10cm
- Sensor de Agua (estado + porcentaje)
- LDR con donut chart
- Temperatura con line chart (datos mock si no hay pin asignado)

**Configuración de Pines** (acceso por shake gesture o el FAB ⚙️):
- Edición de IP/puerto del ESP32
- Selector de pin por componente con dropdowns inteligentes
- Validación en tiempo real según la tabla del blueprint:
  - ADC2 vs WiFi
  - Pines input-only (34, 35, 36, 39)
  - Conflictos de uso (mismo pin en dos componentes)
  - Pines sensibles al boot (0, 2, 12)
  - Pines serial (1, 3)

## Shake gesture

Implementado en `sensors/ShakeDetector.kt`:
- ≥ 2.5G en cualquier eje
- duración mínima 300ms
- cooldown 2s post-trigger

## Build

```bash
./gradlew assembleDebug
```

El ESP32 debe responder a los endpoints documentados en el blueprint
(`/status`, `/servo`, `/sensor/{ultrasonic,water,ldr,temperature}`,
`/config/pins`). El cleartext HTTP en LAN ya está habilitado en
`network_security_config.xml` para los rangos 192.168.x.x, 10.0.x.x y `esp32.local`.

— bynd
