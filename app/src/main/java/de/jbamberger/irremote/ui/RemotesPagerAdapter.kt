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