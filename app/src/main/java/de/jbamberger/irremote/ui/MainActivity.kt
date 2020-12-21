package de.jbamberger.irremote.ui

import android.hardware.ConsumerIrManager
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.jbamberger.irremote.R
import de.jbamberger.irremote.VibrationAdapter
import de.jbamberger.irremote.getVibrationAdapter
import de.jbamberger.irremote.util.IOUtils
import de.jbamberger.irremote.util.RemoteParser
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.Executors
import java.util.function.Consumer


class MainActivity : AppCompatActivity() {

    private var remotes: Map<String, RemoteParser.RemoteDefinition>? = null
    private val exec = Executors.newSingleThreadExecutor()
    private var remoteLayout: TableLayout? = null
    private var commands: RemoteParser.IrDef? = null
    private var initMenu = false
    private var irManager: ConsumerIrManager? = null
    private lateinit var vibrator: VibrationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        remoteLayout = findViewById(R.id.main_layout)
        irManager = this.getSystemService(ConsumerIrManager::class.java)
        vibrator = getVibrationAdapter(this.getSystemService(Vibrator::class.java))

        if (irManager == null || !irManager!!.hasIrEmitter()) {
            Toast.makeText(this, "This device does not support infrared communication.",
                    Toast.LENGTH_LONG).show()
        } else {
            loadRemotes()
        }

    }

    fun runIR(command: String) {
        val frequency = commands!!.frequency
        val code = commands!!.codeMap[command]
        exec.execute {
            val irManager = this.irManager ?: return@execute

            val range = irManager.carrierFrequencies
            for (carrierFrequencyRange in range) {
                if (carrierFrequencyRange.minFrequency <= frequency
                        && frequency <= carrierFrequencyRange.maxFrequency) {
                    vibrator.vibrate()
                    irManager.transmit(frequency, code)
                    return@execute
                }
            }
            Timber.d("The required frequency range is not supported.")

        }
    }

    private fun onRemotesReady(remotes: Map<String, RemoteParser.RemoteDefinition>) {
        this.remotes = remotes
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (remotes != null) {
            menu.clear()
            remotes!!.keys.forEach(Consumer<String> { menu.add(it) })
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (remotes != null) {
            val definition = remotes!![item.title.toString()]
            initUi(definition)
        }


        return super.onOptionsItemSelected(item)
    }

    private fun onRemotesLoadingFailed() {
        Toast.makeText(this, "Could not load remotes", Toast.LENGTH_LONG).show()
    }

    private fun initUi(def: RemoteParser.RemoteDefinition?) {
        val handler = Handler()
        remoteLayout!!.removeAllViews()
        initMenu = true
        if (def == null) {
            return
        }
        this.commands = def.commandDefs

        for (row in def.layout.controls) {
            val tr = TableRow(this)
            for (control in row) {
                val button = Button(this)
                if (control == null) {
                    button.visibility = View.INVISIBLE
                } else {
                    button.setOnTouchListener(object : View.OnTouchListener {
                        private val action = object : Runnable {
                            override fun run() {
                                exec.execute { runIR(control.command) }
                                handler.postDelayed(this, 300)
                            }
                        }

                        override fun onTouch(v: View, event: MotionEvent): Boolean {
                            return when (event.action) {
                                MotionEvent.ACTION_DOWN -> {
                                    handler.post(action)
                                    true
                                }
                                MotionEvent.ACTION_UP -> {
                                    handler.removeCallbacks(action)
                                    v.performClick()
                                    true
                                }
                                MotionEvent.ACTION_CANCEL -> {
                                    handler.removeCallbacks(action)
                                    true
                                }
                                else -> false
                            }
                        }
                    })
                    button.tag = control.command
                    button.text = control.name
                }
                tr.addView(button)
            }
            remoteLayout!!.addView(tr)
        }
    }

    private fun loadRemotes() = exec.execute {
        try {
            resources.openRawResource(R.raw.remotes).use {
                val remotes = RemoteParser().parse(IOUtils.readString(it))
                runOnUiThread { onRemotesReady(remotes) }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            runOnUiThread { this.onRemotesLoadingFailed() }
        }
    }
}
