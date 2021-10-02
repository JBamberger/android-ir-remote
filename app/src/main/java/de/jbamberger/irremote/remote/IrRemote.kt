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

import android.hardware.ConsumerIrManager
import timber.log.Timber

data class IrRemote(
        private val remoteDef: RemoteDefinition,
        private val irManager: ConsumerIrManager,
        private val vibrator: VibratorWrapper) {

    val layout: IrRemoteLayout
        get() = remoteDef.layout
    val commandDefs: IrCommandSet
        get() = remoteDef.commandDefs

    fun sendCommand(command: String) {
        val code = remoteDef.commandDefs.codeMap[command]
                ?: throw IllegalArgumentException("Unknown command name $command.")
        val frequency = remoteDef.commandDefs.frequency

        vibrator.vibrate()
        irManager.transmit(frequency, code)
        Timber.d("The required frequency range is not supported.")
    }
}