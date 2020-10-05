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