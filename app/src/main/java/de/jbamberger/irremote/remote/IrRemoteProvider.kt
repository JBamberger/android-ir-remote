package de.jbamberger.irremote.remote

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.ConsumerIrManager
import de.jbamberger.irremote.R
import timber.log.Timber

class MissingHardwareFeatureException(message: String?) : Exception(message)

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

class IrRemoteProvider(context: Context) {

    private val irManager: ConsumerIrManager
    private val vibrator: VibratorWrapper
    private val remotes: Map<String, IrRemote>

    init {
        irManager = context.getSystemService(ConsumerIrManager::class.java)
                ?: throw MissingHardwareFeatureException("Device has no IR blaster.")
        vibrator = VibratorWrapper(context)

        if (!irManager.hasIrEmitter()) {
            throw MissingHardwareFeatureException("Device has no IR blaster.")
        }
        context.resources.openRawResource(R.raw.remotes).use {
            remotes = RemoteParser()
                    .parse(Utils.readString(it))
                    .filter { entry -> isFrequencySupported(entry.value.commandDefs.frequency) }
                    .mapValues { entry -> IrRemote(entry.value, irManager, vibrator) }
        }
    }

    fun getRemotes(): Map<String, IrRemote> {
        return remotes
    }

    private fun isFrequencySupported(frequency: Int): Boolean {
        for (range in irManager.carrierFrequencies) {
            if (range.minFrequency <= frequency && frequency <= range.maxFrequency) {
                return true
            }
        }
        return false
    }
}