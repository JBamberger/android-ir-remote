package de.jbamberger.irremote

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

interface VibrationAdapter {
    fun vibrate()
}

fun getVibrationAdapter(vibrator: Vibrator?, pulseLength: Long = 50): VibrationAdapter {
    return when {
        vibrator == null || !vibrator.hasVibrator() -> object : VibrationAdapter {
            override fun vibrate() {}
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> object : VibrationAdapter {
            private val effect =
                    VibrationEffect.createOneShot(pulseLength, VibrationEffect.DEFAULT_AMPLITUDE)

            override fun vibrate() {
                vibrator.vibrate(effect)
            }
        }
        else -> object : VibrationAdapter {
            override fun vibrate() {
                @Suppress("DEPRECATION") vibrator.vibrate(pulseLength)
            }
        }
    }
}
