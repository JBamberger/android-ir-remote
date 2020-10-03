package de.jbamberger.irremote

import android.annotation.SuppressLint
import android.content.Intent
import android.hardware.ConsumerIrManager
import android.os.*
import android.util.AttributeSet
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        startActivity(Intent(this, TestActivity::class.java))
//        return

        setContentView(R.layout.activity_main)

        remoteLayout = findViewById(R.id.main_layout)
        irManager = this.getSystemService(ConsumerIrManager::class.java)
        vibrator = this.getSystemService(Vibrator::class.java)
        val errorText = findViewById<TextView>(R.id.error_text)

        if (irManager == null || !irManager!!.hasIrEmitter()) {
            Toast.makeText(this, "This device does not support Infrared communication.",
                    Toast.LENGTH_LONG).show()
        } else {
            errorText.visibility = View.GONE
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
                    if (vibrator.hasVibrator()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(
                                    50, VibrationEffect.DEFAULT_AMPLITUDE))
                        } else {
                            @Suppress("DEPRECATION")
                            vibrator.vibrate(50)
                        }
                    }
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
            remotes!!.keys.forEach(Consumer { menu.add(it) })
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
        remoteLayout!!.removeAllViews()
        initMenu = true
        if (def == null) {
            return
        }
        this.commands = def.commandDefs

        for (row in def.layout.controls) {
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
                                exec.execute { runIR(control.command) }
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

    private fun loadRemotes() = exec.execute {
        try {
            resources.openRawResource(R.raw.remotes)
                    .use {
                        val remotes = RemoteParser().parse(Utils.readString(it))
                        runOnUiThread { onRemotesReady(remotes) }
                    }
        } catch (e: IOException) {
            e.printStackTrace()
            runOnUiThread { this.onRemotesLoadingFailed() }
        }
    }
}
