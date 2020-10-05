package de.jbamberger.irremote.ui

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import de.jbamberger.irremote.remote.IrRemote

class RemotesPagerAdapter(private val context: Context, fm: FragmentManager)
    : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var remoteNames: List<String> = emptyList()

    fun setRemotes(remotes: Map<String, IrRemote>) {
        this.remoteNames = remotes.keys.toList()
        this.notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment {
        return RemoteUiFragment.newInstance(remoteNames[position])
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return remoteNames[position]
    }

    override fun getCount(): Int {
        return remoteNames.size
    }
}