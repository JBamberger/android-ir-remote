/*
 *    Copyright 2021 Jannik Bamberger
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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