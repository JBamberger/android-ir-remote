package de.jbamberger.irremote.ui

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import de.jbamberger.irremote.remote.IrRemote

class RemotesPagerAdapter(fm: FragmentManager, lc: Lifecycle) : FragmentStateAdapter(fm, lc) {

    private var remoteNames: List<String> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setRemotes(remotes: Map<String, IrRemote>) {
        this.remoteNames = remotes.keys.toList()
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return remoteNames.size
    }

    override fun createFragment(position: Int): Fragment {
        return RemoteUiFragment.newInstance(remoteNames[position])
    }

    fun getPageTitle(position: Int): CharSequence {
        return remoteNames[position]
    }
}