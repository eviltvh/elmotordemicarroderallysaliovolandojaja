package com.bynd.esp32dashboard

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bynd.esp32dashboard.sensors.ShakeDetector
import com.bynd.esp32dashboard.ui.screens.DashboardScreen
import com.bynd.esp32dashboard.ui.screens.PinConfigScreen
import com.bynd.esp32dashboard.ui.theme.ESP32DashboardTheme
import com.bynd.esp32dashboard.viewmodel.DashboardViewModel
import com.bynd.esp32dashboard.viewmodel.PinConfigViewModel

class MainActivity : ComponentActivity() {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var shakeDetector: ShakeDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        setContent {
            ESP32DashboardTheme {
                val nav = rememberNavController()

                // Shake = abrir config -bynd
                DisposableEffect(Unit) {
                    val detector = ShakeDetector(onShake = {
                        runOnUiThread {
                            // Solo navegamos si estamos en el dashboard
                            val current = nav.currentBackStackEntry?.destination?.route
                            if (current == "dashboard") {
                                vibrateShort()
                                Toast.makeText(
                                    this@MainActivity,
                                    "⚙️ Configuración de pines",
                                    Toast.LENGTH_SHORT
                                ).show()
                                nav.navigate("config")
                            }
                        }
                    })
                    shakeDetector = detector
                    accelerometer?.let {
                        sensorManager.registerListener(detector, it,
                            SensorManager.SENSOR_DELAY_GAME)
                    }
                    onDispose {
                        sensorManager.unregisterListener(detector)
                        shakeDetector = null
                    }
                }

                NavHost(navController = nav, startDestination = "dashboard") {
                    composable("dashboard") {
                        val vm: DashboardViewModel = viewModel()
                        DashboardScreen(
                            onOpenConfig = { nav.navigate("config") },
                            vm = vm
                        )
                    }
                    composable("config") {
                        val cfgVm: PinConfigViewModel = viewModel()
                        PinConfigScreen(
                            onClose = {
                                nav.popBackStack()
                                // Forzar el dashboard a recargar la conexión
                            },
                            vm = cfgVm
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        shakeDetector?.let { d ->
            accelerometer?.let { s ->
                sensorManager.registerListener(d, s, SensorManager.SENSOR_DELAY_GAME)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        shakeDetector?.let { sensorManager.unregisterListener(it) }
    }

    @Suppress("DEPRECATION")
    private fun vibrateShort() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator.vibrate(VibrationEffect.createOneShot(80, 180))
        } else {
            val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(80, 180))
            } else {
                v.vibrate(80)
            }
        }
    }
}
