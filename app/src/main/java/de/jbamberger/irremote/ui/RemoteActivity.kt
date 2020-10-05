package de.jbamberger.irremote.ui

import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import de.jbamberger.irremote.R
import de.jbamberger.irremote.remote.IrRemoteProvider
import de.jbamberger.irremote.remote.MissingHardwareFeatureException

class RemoteActivity : AppCompatActivity() {

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

        val sectionsPagerAdapter = RemotesPagerAdapter(this, supportFragmentManager)
        sectionsPagerAdapter.setRemotes(remoteProvider!!.getRemotes())

        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter

        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }
}