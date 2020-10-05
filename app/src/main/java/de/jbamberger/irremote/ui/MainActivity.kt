package de.jbamberger.irremote.ui

import android.os.*
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import de.jbamberger.irremote.R
import de.jbamberger.irremote.remote.*
import java.util.concurrent.Executors
import java.util.function.Consumer


class MainActivity : AppCompatActivity() {


    private var remoteLayout: TableLayout? = null
    private var remoteProvider: IrRemoteProvider? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        startActivity(Intent(this, RemoteActivity::class.java))
//        return

        setContentView(R.layout.activity_main)

        remoteLayout = findViewById(R.id.main_layout)
        val errorText = findViewById<TextView>(R.id.error_text)

        try {
            remoteProvider = IrRemoteProvider(this)
            errorText.visibility = View.GONE
        } catch (e: MissingHardwareFeatureException) {
            errorText.visibility = View.VISIBLE
        }
    }


    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        remoteProvider?.getRemotes()?.let { remotes ->
            menu.clear()
            remotes.keys.forEach(Consumer { menu.add(it) })

        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val definition = remoteProvider?.getRemotes()?.get(item.title.toString())
        initUi(definition)
        return super.onOptionsItemSelected(item)
    }

    private fun initUi(remote: IrRemote?) {
        if (remote == null) {
            return
        }
        val inflater = IrRemoteUiInflater(remote)
        inflater.inflate(remoteLayout!!)
    }

}
