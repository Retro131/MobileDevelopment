package com.example.mobiledevelopment.game.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.compose.LifecycleResumeEffect

@Composable
fun rememberGravityTilt(
    enabled: Boolean
): State<Offset> {
    val context = LocalContext.current
    val view = LocalView.current

    val sensorManager = remember(context) {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val tilt = remember {
        mutableStateOf(Offset.Zero)
    }

    LifecycleResumeEffect(enabled, sensorManager, view) {
        val sensor = sensorManager.getDefaultSensor(
            Sensor.TYPE_GRAVITY
        )

        val listener = object : SensorEventListener {

            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]

                val screenGravity = when (view.display?.rotation) {
                    Surface.ROTATION_90 -> Offset(y, -x)
                    Surface.ROTATION_180 -> Offset(-x, -y)
                    Surface.ROTATION_270 -> Offset(-y, x)
                    else -> Offset(x, y)
                }

                val gravity = SensorManager.GRAVITY_EARTH

                tilt.value = Offset(
                    x = (-screenGravity.x / gravity)
                        .coerceIn(-1f, 1f),
                    y = (screenGravity.y / gravity)
                        .coerceIn(-1f, 1f)
                )
            }

            override fun onAccuracyChanged(
                sensor: Sensor?,
                accuracy: Int
            ) {
            }
        }

        if (enabled && sensor != null) {
            sensorManager.registerListener(
                listener,
                sensor,
                SensorManager.SENSOR_DELAY_GAME
            )
        }

        onPauseOrDispose {
            sensorManager.unregisterListener(listener)
            tilt.value = Offset.Zero
        }
    }

    return tilt
}