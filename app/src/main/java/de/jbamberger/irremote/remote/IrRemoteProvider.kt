package de.jbamberger.irremote.remote

import android.content.Context
import android.hardware.ConsumerIrManager
import de.jbamberger.irremote.R

class MissingHardwareFeatureException(message: String?) : Exception(message)

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