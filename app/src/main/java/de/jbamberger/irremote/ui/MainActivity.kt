package de.jbamberger.irremote.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.jbamberger.irremote.R
import de.jbamberger.irremote.remote.IrRemoteProvider
import de.jbamberger.irremote.remote.MissingHardwareFeatureException


class MainActivity : AppCompatActivity() {

    private var remoteProvider: IrRemoteProvider? = null
    private lateinit var viewModel: IrRemotesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remote)

        viewModel = ViewModelProvider(this).get(IrRemotesViewModel::class.java)

        try {
            remoteProvider = IrRemoteProvider(this)
            viewModel.remotes = remoteProvider!!.getRemotes()
        } catch (e: MissingHardwareFeatureException) {
            // TODO: add appropriate error handling
            e.printStackTrace()
            return
        }

        val sectionsPagerAdapter = RemotesPagerAdapter(supportFragmentManager, lifecycle)
        sectionsPagerAdapter.setRemotes(remoteProvider!!.getRemotes())

        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter

        val tabLayout: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = sectionsPagerAdapter.getPageTitle(position)
        }.attach()

    }
}
