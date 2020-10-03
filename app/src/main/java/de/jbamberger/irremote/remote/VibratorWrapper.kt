package de.jbamberger.irremote.remote

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import java.util.function.Consumer

class VibratorWrapper(context: Context) {

    private val vibrator: Vibrator
    private val vibrateImpl: Consumer<Long>

    init {
        vibrator = context.getSystemService(Vibrator::class.java)
        vibrateImpl = if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Consumer { duration ->
                    vibrator.vibrate(VibrationEffect.createOneShot(
                            duration, VibrationEffect.DEFAULT_AMPLITUDE))
                }
            } else {
                Consumer { duration ->
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(duration)
                }
            }
        } else {
            Consumer { }
        }
    }

    fun vibrate(duration: Long = 50) {
        vibrateImpl.accept(duration)
    }
}