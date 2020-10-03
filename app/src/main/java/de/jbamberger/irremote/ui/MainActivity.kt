package de.jbamberger.irremote.ui

import android.content.Intent
import android.hardware.ConsumerIrManager
import android.os.*
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import de.jbamberger.irremote.R
import de.jbamberger.irremote.remote.IrRemoteProvider
import de.jbamberger.irremote.remote.MissingHardwareFeatureException
import de.jbamberger.irremote.remote.RemoteParser
import de.jbamberger.irremote.remote.Utils
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.Executors
import java.util.function.Consumer


class MainActivity : AppCompatActivity() {

    private val exec = Executors.newSingleThreadExecutor()
    private var remoteLayout: TableLayout? = null
    private var initMenu = false
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

    private fun onRemotesLoadingFailed() {
        Toast.makeText(this, "Could not load remotes", Toast.LENGTH_LONG).show()
    }


    private fun initUi(remoteDefinition: RemoteParser.RemoteDefinition?) {
        remoteLayout!!.removeAllViews()
        initMenu = true
        if (remoteDefinition == null) {
            return
        }

        for (row in remoteDefinition.layout.controls) {
            val tr = TableRow(this)
            val params = TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT, 1.0f)
            tr.layoutParams = params
            for (control in row) {
                val b = Button(this)
                val buttonParams = TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1.0f)
                b.layoutParams = buttonParams
                if (control == null) {
                    b.visibility = View.INVISIBLE
                } else {
                    b.setOnTouchListener(
                            object : View.OnTouchListener {
                                private var handler: Handler? = null

                                private val action = object : Runnable {
                                    override fun run() {
                                        exec.execute { remoteProvider?.sendIrCode(remoteDefinition.commandDefs, control.command) }
                                        handler!!.postDelayed(this, 300)
                                    }
                                }

                                override fun onTouch(v: View, event: MotionEvent): Boolean {
                                    when (event.action) {
                                        MotionEvent.ACTION_DOWN -> {
                                            if (handler == null) {
                                                handler = Handler(Looper.getMainLooper())
                                                handler!!.post(action)
                                            }
                                            return true
                                        }
                                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                                            handler?.removeCallbacks(action)
                                            handler = null
                                            return true
                                        }
                                    }
                                    return false
                                }
                            })
                    b.tag = control.command
                    b.text = control.name
                }
                tr.addView(b)
            }
            with(remoteLayout!!) {
                isMeasureWithLargestChildEnabled = true
                addView(tr)
            }
        }
    }
}
