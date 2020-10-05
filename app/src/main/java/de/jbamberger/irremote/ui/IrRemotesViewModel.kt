package de.jbamberger.irremote.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import de.jbamberger.irremote.remote.IrRemote
import de.jbamberger.irremote.remote.IrRemoteProvider
import de.jbamberger.irremote.remote.MissingHardwareFeatureException

class IrRemotesViewModel : ViewModel() {

    var remotes: Map<String, IrRemote>? = null


    fun getRemote(remoteName: String?): IrRemote? {
        return remotes?.get(remoteName)
    }
}